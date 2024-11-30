package sukriti.ngo.mis.utils;

import static java.lang.Thread.sleep;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_CODE;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_COMPLEX_ADDR;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_COMPLEX_CITY_CODE;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_COMPLEX_CLNT;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_COMPLEX_COCO;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_COMPLEX_LATT;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_COMPLEX_LONG;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_COMPLEX_UUID;
import static sukriti.ngo.mis.AWSConfig._ATTRIBUTE_NAME;
import static sukriti.ngo.mis.AWSConfig.endPointMumbai;
import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.providerName;
import static sukriti.ngo.mis.utils.Utilities.getCityIndex;
import static sukriti.ngo.mis.utils.Utilities.getDistrictIndex;
import static sukriti.ngo.mis.utils.Utilities.getStateIndex;
import static sukriti.ngo.mis.utils.Utilities.getTrimmedClientSuperAdminAccessTree;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.DescribeThingGroupRequest;
import com.amazonaws.services.iot.model.DescribeThingGroupResult;
import com.amazonaws.services.iot.model.DescribeThingRequest;
import com.amazonaws.services.iot.model.DescribeThingResult;
import com.amazonaws.services.iot.model.GroupNameAndArn;
import com.amazonaws.services.iot.model.ListThingGroupsRequest;
import com.amazonaws.services.iot.model.ListThingGroupsResult;
import com.amazonaws.services.iot.model.ListThingsInThingGroupRequest;
import com.amazonaws.services.iot.model.ListThingsInThingGroupResult;
import com.amazonaws.services.iot.model.ResourceNotFoundException;
import com.amazonaws.services.iot.model.ThingGroupMetadata;
import com.amazonaws.services.iot.model.ThingGroupProperties;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sukriti.ngo.mis.dataModel.ThingDetails;
import sukriti.ngo.mis.dataModel.ThingGroupDetails;
import sukriti.ngo.mis.dataModel.ValidationResult;
import sukriti.ngo.mis.dataModel._Result;
import sukriti.ngo.mis.dataModel.dynamo_db.City;
import sukriti.ngo.mis.dataModel.dynamo_db.Complex;
import sukriti.ngo.mis.dataModel.dynamo_db.Country;
import sukriti.ngo.mis.dataModel.dynamo_db.District;
import sukriti.ngo.mis.dataModel.dynamo_db.State;
import sukriti.ngo.mis.interfaces.AuthorisationHandler;
import sukriti.ngo.mis.interfaces.GetThingDetailsHandler;
import sukriti.ngo.mis.interfaces.GetThingGroupChildrenHandler;
import sukriti.ngo.mis.interfaces.GetThingGroupDetailsHandler;
import sukriti.ngo.mis.interfaces.GetThingsInThingGroupHandler;
import sukriti.ngo.mis.interfaces.RepositoryCallback;
import sukriti.ngo.mis.ui.administration.data.RawComplex;
import sukriti.ngo.mis.ui.administration.data.RawNonComplex;
import sukriti.ngo.mis.ui.administration.data.RawProvisioningTreeNode;
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler;
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData;
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.BwtCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.FwcCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.MurCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.MwcCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.PwcCabin;
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler;
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaRequest;
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaResult;
import sukriti.ngo.mis.ui.complexes.lambda.CabinDetailsLambdaClient;

public class AWSIotProvisioningClient {
    public static final int POOLING_SLEEP_SHORT = 400; //Milli-Seconds
    private AWSIotClient awsIotClient;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private Context context;
    public boolean hasValidAccessToken = false;
    private final String TAG = "ProvisioningClient";
    private ValidationResult Result;

    private int iterationIndex;
    private int detailsErrCount;
    private int detailsSuccessCount;
    private boolean hasIteratedAllChildren = false;
    private boolean isIterationComplete = false;

    public AWSIotProvisioningClient(Context context) {
        this.context = context;
    }

    private void signOut() {
        credentialsProvider.clearCredentials();
        awsIotClient.shutdown();
        hasValidAccessToken = false;
    }


    public void Authorize(final AuthorisationHandler callback) {
        new Thread(() -> {

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    String idToken = AuthenticationClient.getCurrSession().getIdToken().getJWTToken();
                    credentialsProvider = new CognitoCachingCredentialsProvider(
                            context, // Context
                            identityPoolID, // Identity Pool ID
                            Regions.AP_SOUTH_1 // Region
                    );
                    Map<String, String> logins = new HashMap<>();
                    logins.put(providerName, idToken);
                    credentialsProvider.setLogins(logins);
                    credentialsProvider.getCredentials();
                    awsIotClient = new AWSIotClient(credentialsProvider);
                    awsIotClient.setEndpoint(endPointMumbai);

                    hasValidAccessToken = true;

                    returnCallback = () -> {
                        Result = new ValidationResult(true, "");
                        callback.onResult(Result);
                    };
                } catch (NotAuthorizedException e) {
                    returnCallback = () -> {
                        Result = new ValidationResult(false, e.getMessage());
                        callback.onResult(Result);
                    };
                }

            } else {
                returnCallback = () -> {
                    Result = new ValidationResult(false, "Session Invalid");
                    callback.onResult(Result);
                    Log.d(TAG, "AWSSecretKey ");
                };
            }
            handler.post(returnCallback);
        }).start();
    }

    private ArrayList<ThingGroupDetails> childrenList;
    private ThingGroupDetails thingGroupDetails;

    public void getChildrenThingGroups(final String Parent, final GetThingGroupChildrenHandler callback) {

        new Thread(() -> {
            Log.i("CreateUser", "getChildrenThingGroups run()");
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable returnCallback;
            if (hasValidAccessToken) {
                Log.i("CreateUser", "hasValidAccessToken");
                try {
                    ListThingGroupsResult result = awsIotClient.listThingGroups(new ListThingGroupsRequest()
                            .withRecursive(false)
                            .withParentGroup(Parent));
                    Log.i("CreateUser", "thing groups listed successfully");
                    List<GroupNameAndArn> list = result.getThingGroups();


                    childrenList = new ArrayList<>();
                    if (list.size() > 0) {
                        Log.i("CreateUser", "thing group list size > 0");
                        hasIteratedAllChildren = false;
                        detailsErrCount = 0;
                        detailsSuccessCount = 0;
                        iterationIndex = 0;
                        while (!hasIteratedAllChildren) {
                            isIterationComplete = false;
                            getThingGroupDetails(list.get(iterationIndex).getGroupName(), new GetThingGroupDetailsHandler() {
                                @Override
                                public void onResult(String ThingGroupName, String Description, Map<String, String> Attributes, ThingGroupMetadata metadata) {
                                    thingGroupDetails = new ThingGroupDetails();
                                    thingGroupDetails.Name = ThingGroupName;
                                    thingGroupDetails.Description = Description;
                                    thingGroupDetails.Parent = Parent;
                                    thingGroupDetails.AttributesMap = Attributes;
                                    childrenList.add(thingGroupDetails);
                                    detailsSuccessCount++;
                                    isIterationComplete = true;
                                }

                                @Override
                                public void onError(String message) {
                                    detailsErrCount++;
                                    Log.i(TAG, detailsErrCount + ":" + message);
                                    isIterationComplete = true;
                                }
                            });
                            while (!isIterationComplete) {
                                try {
                                    sleep(POOLING_SLEEP_SHORT);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if ((detailsSuccessCount + detailsErrCount) == list.size())
                                hasIteratedAllChildren = true;
                            else
                                iterationIndex++;
                        }
                    }

                    if (childrenList.size() == 0) {
                        returnCallback = () -> callback.onError("No child elements found for the thing group '" + Parent + "'");
                    } else {
                        returnCallback = () -> callback.onResult(Parent, childrenList, 1);
                    }

                } catch (ResourceNotFoundException e) {
                    returnCallback = () -> {
                        ValidationResult Result = new ValidationResult(false, "Cannot find the requested thing group under '" + Parent + "'");
                        callback.onError(Result.getMessage());
                    };
                }
            } else {
                returnCallback = () -> {
                    ValidationResult Result = new ValidationResult(false, "Invalid user session");
                    callback.onError(Result.getMessage());
                };
            }
            handler.post(returnCallback);
        }).start();
    } // Quick Config

    public void getProvisioningTree(final ProvisioningTreeRequestHandler callback) {
        final String tag = "ProvisioningTree";
        //RawProvisioningTree
        ArrayList<RawProvisioningTreeNode> rawProvisioningTree = new ArrayList<>();
        //Provisioning Tree
        Country country = new Country();
        //Helper Data Structures
        HashMap<String, RawNonComplex> stateHash = new HashMap<>();
        HashMap<String, RawNonComplex> districtHash = new HashMap<>();
        HashMap<String, RawNonComplex> cityHash = new HashMap<>();

        new Thread(() -> {
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable returnCallback;
            if (hasValidAccessToken) {
                try {
                    ListThingGroupsResult result = awsIotClient.listThingGroups(new ListThingGroupsRequest()
                            .withRecursive(true)
                            .withMaxResults(250)
                            .withParentGroup("INDIA"));
                    List<GroupNameAndArn> list = result.getThingGroups();

                    if (list.size() > 0) {
                        hasIteratedAllChildren = false;
                        detailsErrCount = 0;
                        detailsSuccessCount = 0;
                        iterationIndex = 0;
                        while (!hasIteratedAllChildren) {
                            isIterationComplete = false;
                            getThingGroupDetails(list.get(iterationIndex).getGroupName(), new GetThingGroupDetailsHandler() {
                                @Override
                                public void onResult(String ThingGroupName, String Description, Map<String, String> Attributes, ThingGroupMetadata metadata) {

//                                        Iterator it = Attributes.entrySet().iterator();
//                                        while (it.hasNext()) {
//                                            Map.Entry pair = (Map.Entry) it.next();
//
//                                        }

//                                        for(GroupNameAndArn details: meta data.getRootToParentThingGroups()){
//                                            Log.i(tag,"getRootToParentThingGroups");
//                                            Log.i(tag,""+details.getGroupName());
//                                        }
                                    if (Attributes.get(_ATTRIBUTE_NAME) != null) {
                                        //THIS IS A NON-COMPLEX
                                        RawNonComplex nonComplex = new RawNonComplex(metadata.getParentGroupName(), Attributes.get(_ATTRIBUTE_NAME), Attributes.get(_ATTRIBUTE_CODE));
                                        RawProvisioningTreeNode node = new RawProvisioningTreeNode(false, null, nonComplex);
                                        rawProvisioningTree.add(node);
                                    }
                                    else {
                                        RawComplex complex = new RawComplex(Attributes.get(_ATTRIBUTE_COMPLEX_CITY_CODE), ThingGroupName, Attributes.get(_ATTRIBUTE_COMPLEX_UUID),
                                                Attributes.get(_ATTRIBUTE_COMPLEX_ADDR), Attributes.get(_ATTRIBUTE_COMPLEX_LATT), Attributes.get(_ATTRIBUTE_COMPLEX_LONG),
                                                Attributes.get(_ATTRIBUTE_COMPLEX_COCO), Attributes.get(_ATTRIBUTE_COMPLEX_CLNT));
                                        RawProvisioningTreeNode node = new RawProvisioningTreeNode(true, complex, null);
                                        rawProvisioningTree.add(node);
                                    }

                                    detailsSuccessCount++;
                                    isIterationComplete = true;
                                }

                                @Override
                                public void onError(String message) {
                                    detailsErrCount++;
                                    Log.i(TAG, detailsErrCount + ":" + message);
                                    isIterationComplete = true;
                                }
                            });
                            while (!isIterationComplete) {
                                try {
                                    sleep(POOLING_SLEEP_SHORT);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if ((detailsSuccessCount + detailsErrCount) == list.size())
                                hasIteratedAllChildren = true;
                            else
                                iterationIndex++;
                        }


                        //Pass: States
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (!node.isComplex() && node.getNonComplex().getParent().compareToIgnoreCase("INDIA") == 0) {
                                State state = new State(node.getNonComplex().getName(), node.getNonComplex().getCode(), 0, new ArrayList<>());
                                if (country.getStates() == null)
                                    country.setStates(new ArrayList<>());
                                country.getStates().add(state);
                                stateHash.put(node.getNonComplex().getCode(), node.getNonComplex());
                            }
                        }


                        //Pass: Districts
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (!node.isComplex()) {
                                if (stateHash.get(node.getNonComplex().getParent()) != null) {
                                    RawNonComplex parentState = stateHash.get(node.getNonComplex().getParent());

                                    int stateIndex = getStateIndex(parentState.getName(), country.getStates());
                                    District district = new District(node.getNonComplex().getName(), node.getNonComplex().getCode(), 0, new ArrayList<>());
                                    country.getStates().get(stateIndex).getDistricts().add(district);
                                    districtHash.put(node.getNonComplex().getCode(), node.getNonComplex());
                                }
                            }
                        }


                        //Pass: Cities
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (!node.isComplex() && districtHash.get(node.getNonComplex().getParent()) != null) {
                                RawNonComplex parentDistrict = districtHash.get(node.getNonComplex().getParent());
                                RawNonComplex parentState = stateHash.get(parentDistrict.getParent());

                                int stateIndex = getStateIndex(parentState.getName(), country.getStates());
                                int districtIndex = getDistrictIndex(parentDistrict.getName(), country.getStates().get(stateIndex).getDistricts());

                                City city = new City(node.getNonComplex().getName(), node.getNonComplex().getCode(), 0, new ArrayList<>());
                                country.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().add(city);
                                cityHash.put(node.getNonComplex().getCode(), node.getNonComplex());
                            }
                        }

                        //Pass: Complexes
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (node.isComplex() && cityHash.get(node.getComplex().getParent()) != null) {

                                RawNonComplex parentCity = cityHash.get(node.getComplex().getParent());
                                RawNonComplex parentDistrict = districtHash.get(parentCity.getParent());
                                RawNonComplex parentState = stateHash.get(parentDistrict.getParent());

                                int stateIndex = getStateIndex(parentState.getName(), country.getStates());
                                int districtIndex = getDistrictIndex(parentDistrict.getName(), country.getStates().get(stateIndex).getDistricts());
                                int cityIndex = getCityIndex(parentCity.getName(), country.getStates().get(stateIndex).getDistricts().
                                        get(districtIndex).getCities());

                                Complex complex = new Complex(node.getComplex().getName(), node.getComplex().getAddress(), node.getComplex().getUuid(),
                                        node.getComplex().getCoco(), node.getComplex().getLat(), node.getComplex().getLon());
                                complex.setClient(node.getComplex().getClient());
                                country.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().get(cityIndex).getComplexes().add(complex);
                            } else {
                                Log.e(tag, "Complex Pass - Null Block");
                            }
                        }

/*
                            Iterator it = stateHash.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                System.out.println(pair.getKey() + " = " + pair.getValue());
                                Log.i(tag + "-state", pair.getKey().toString());
                            }

                            it = districtHash.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                System.out.println(pair.getKey() + " = " + pair.getValue());
                                Log.i(tag + "-dist", pair.getKey().toString());
                            }

                            it = cityHash.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                System.out.println(pair.getKey() + " = " + pair.getValue());
                                Log.i(tag + "-city", pair.getKey().toString());
                            }
*/

                    }

                    if (rawProvisioningTree.size() == 0) {
                        returnCallback = () -> callback.onError("No child elements found for the thing group '" + "INDIA" + "'");
                    } else {
                        returnCallback = () -> callback.onSuccess(country);
                    }

                } catch (ResourceNotFoundException e) {
                    returnCallback = () -> {
                        ValidationResult Result = new ValidationResult(false, "Cannot find the requested thing group under '" + "INDIA" + "'");
                        callback.onError(Result.getMessage());
                    };
                }
            } else {
                returnCallback = () -> {
                    ValidationResult Result = new ValidationResult(false, "Invalid user session");
                    callback.onError(Result.getMessage());
                };
            }
            handler.post(returnCallback);
        }).start();
    } // Define Access

    private Country trimmedAccessTree;

    public void getClientProvisioningTree(final ProvisioningTreeRequestHandler callback, String ClientCode) {
        final String tag = "__CSAAccessTree";
        //RawProvisioningTree
        ArrayList<RawProvisioningTreeNode> rawProvisioningTree = new ArrayList<>();
        //Provisioning Tree
        Country country = new Country();
        trimmedAccessTree = new Country();
        //Helper Data Structures
        HashMap<String, RawNonComplex> stateHash = new HashMap<>();
        HashMap<String, RawNonComplex> districtHash = new HashMap<>();
        HashMap<String, RawNonComplex> cityHash = new HashMap<>();

        new Thread(() -> {
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable returnCallback;
            if (hasValidAccessToken) {
                try {
                    Log.i(tag, "start");
                    ListThingGroupsRequest listThingGroupsRequest = new ListThingGroupsRequest()
                            .withRecursive(true)
                            .withMaxResults(250)
                            .withParentGroup("INDIA");
                    Log.i(tag,"max: "+listThingGroupsRequest.getMaxResults());
                    ListThingGroupsResult result = awsIotClient.listThingGroups(listThingGroupsRequest);
                    List<GroupNameAndArn> list = result.getThingGroups();
                    Log.e(tag, "empty slots as per max limit for listThingGroups(INDIA): "+list.size()+"/250");

                    Log.i(tag, "recursive provisioning thing group list fetched");

                    if (list.size() > 0) {
                        hasIteratedAllChildren = false;
                        detailsErrCount = 0;
                        detailsSuccessCount = 0;
                        iterationIndex = 0;
                        while (!hasIteratedAllChildren) {
                            isIterationComplete = false;
                            Log.d(tag, "GroupName " + list.get(iterationIndex).getGroupName());
                            getThingGroupDetails(list.get(iterationIndex).getGroupName(), new GetThingGroupDetailsHandler() {
                                @Override
                                public void onResult(String ThingGroupName, String Description, Map<String, String> Attributes, ThingGroupMetadata metadata) {

                                    if (Attributes.get(_ATTRIBUTE_NAME) != null) {
                                        //THIS IS A NON-COMPLEX
                                        RawNonComplex nonComplex = new RawNonComplex(metadata.getParentGroupName(), Attributes.get(_ATTRIBUTE_NAME), Attributes.get(_ATTRIBUTE_CODE));
                                        RawProvisioningTreeNode node = new RawProvisioningTreeNode(false, null, nonComplex);
                                        rawProvisioningTree.add(node);
                                    } else {
                                        Log.d(tag, "Complex-Client " + Attributes.get(_ATTRIBUTE_COMPLEX_CLNT));
                                        if (Attributes.get(_ATTRIBUTE_COMPLEX_CLNT).compareToIgnoreCase(ClientCode) == 0) {
                                            Log.d(tag, "Filter!! " + Attributes.get(_ATTRIBUTE_COMPLEX_CLNT) + " - " + ClientCode);
                                            RawComplex complex = new RawComplex(Attributes.get(_ATTRIBUTE_COMPLEX_CITY_CODE), ThingGroupName, Attributes.get(_ATTRIBUTE_COMPLEX_UUID),
                                                    Attributes.get(_ATTRIBUTE_COMPLEX_ADDR), Attributes.get(_ATTRIBUTE_COMPLEX_LATT), Attributes.get(_ATTRIBUTE_COMPLEX_LONG),
                                                    Attributes.get(_ATTRIBUTE_COMPLEX_COCO), Attributes.get(_ATTRIBUTE_COMPLEX_CLNT));
                                            RawProvisioningTreeNode node = new RawProvisioningTreeNode(true, complex, null);
                                            rawProvisioningTree.add(node);
                                        }
                                    }

                                    detailsSuccessCount++;
                                    isIterationComplete = true;
                                }

                                @Override
                                public void onError(String message) {
                                    detailsErrCount++;
                                    Log.i(TAG, detailsErrCount + ":" + message);
                                    isIterationComplete = true;
                                }
                            });
                            while (!isIterationComplete) {
                                try {
                                    sleep(POOLING_SLEEP_SHORT);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if ((detailsSuccessCount + detailsErrCount) == list.size())
                                hasIteratedAllChildren = true;
                            else
                                iterationIndex++;
                        }
                        Log.i(tag, "recursive provisioning thing group individual details fetched");

                        //Pass: States
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (!node.isComplex() && node.getNonComplex().getParent().compareToIgnoreCase("INDIA") == 0) {
                                State state = new State(node.getNonComplex().getName(), node.getNonComplex().getCode(), 0, new ArrayList<>());
                                if (country.getStates() == null)
                                    country.setStates(new ArrayList<>());
                                country.getStates().add(state);
                                stateHash.put(node.getNonComplex().getCode(), node.getNonComplex());
                            }
                        }
                        Log.i(tag, "Completed States Pass");

                        //Pass: Districts
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (!node.isComplex()) {
                                if (stateHash.get(node.getNonComplex().getParent()) != null) {
                                    RawNonComplex parentState = stateHash.get(node.getNonComplex().getParent());

                                    int stateIndex = getStateIndex(parentState.getName(), country.getStates());
                                    District district = new District(node.getNonComplex().getName(), node.getNonComplex().getCode(), 0, new ArrayList<>());
                                    country.getStates().get(stateIndex).getDistricts().add(district);
                                    districtHash.put(node.getNonComplex().getCode(), node.getNonComplex());
                                }
                            }
                        }
                        Log.i(tag, "Completed District Pass");

                        //Pass: Cities
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (!node.isComplex() && districtHash.get(node.getNonComplex().getParent()) != null) {
                                RawNonComplex parentDistrict = districtHash.get(node.getNonComplex().getParent());
                                RawNonComplex parentState = stateHash.get(parentDistrict.getParent());

                                int stateIndex = getStateIndex(parentState.getName(), country.getStates());
                                int districtIndex = getDistrictIndex(parentDistrict.getName(), country.getStates().get(stateIndex).getDistricts());

                                City city = new City(node.getNonComplex().getName(), node.getNonComplex().getCode(), 0, new ArrayList<>());
                                country.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().add(city);
                                cityHash.put(node.getNonComplex().getCode(), node.getNonComplex());
                            }
                        }
                        Log.i(tag, "Completed City Pass");

                        //Pass: Complexes
                        for (RawProvisioningTreeNode node : rawProvisioningTree) {
                            if (node.isComplex() && cityHash.get(node.getComplex().getParent()) != null) {

                                RawNonComplex parentCity = cityHash.get(node.getComplex().getParent());
                                RawNonComplex parentDistrict = districtHash.get(parentCity.getParent());
                                RawNonComplex parentState = stateHash.get(parentDistrict.getParent());

                                int stateIndex = getStateIndex(parentState.getName(), country.getStates());
                                int districtIndex = getDistrictIndex(parentDistrict.getName(), country.getStates().get(stateIndex).getDistricts());
                                int cityIndex = getCityIndex(parentCity.getName(), country.getStates().get(stateIndex).getDistricts().
                                        get(districtIndex).getCities());

                                Complex complex = new Complex(node.getComplex().getName(), node.getComplex().getAddress(), node.getComplex().getUuid(),
                                        node.getComplex().getCoco(), node.getComplex().getLat(), node.getComplex().getLon());
                                complex.setClient(node.getComplex().getClient());
                                Log.i("__ClientRequest", "Client: " + complex.getClient());

                                country.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().get(cityIndex).getComplexes().add(complex);
                                Log.d(tag, "Filter!! " + complex.getName());
                            } else {
                                Log.i(tag, "Complex Pass - Null Block");
                            }
                        }

                        trimmedAccessTree = getTrimmedClientSuperAdminAccessTree(country); //country;//
                    }


                    if (rawProvisioningTree.size() == 0) {
                        returnCallback = () -> callback.onError("No child elements found for the thing group '" + "INDIA" + "'");
                    } else {
                        returnCallback = () -> callback.onSuccess(trimmedAccessTree);
                    }

                }
                catch (ResourceNotFoundException e) {
                    returnCallback = () -> {
                        ValidationResult Result = new ValidationResult(false, "Cannot find the requested thing group under '" + "INDIA" + "'");
                        callback.onError(Result.getMessage());
                    };
                }
            }

            else {
                returnCallback = () -> {
                    ValidationResult Result = new ValidationResult(false, "Invalid user session");
                    callback.onError(Result.getMessage());
                };
            }
            handler.post(returnCallback);
        }).start();
    } // Define Access, Member Access

    private Map<String, String> attributes;
    private String description;

    public void getThingGroupDetails(final String ThingGroupName, final GetThingGroupDetailsHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable returnCallback;
            if (hasValidAccessToken) {
                try {
                    DescribeThingGroupResult result = awsIotClient.describeThingGroup(new DescribeThingGroupRequest().withThingGroupName(ThingGroupName));
                    ThingGroupProperties properties = result.getThingGroupProperties();
                    ThingGroupMetadata metadata = result.getThingGroupMetadata();
                    attributes = properties.getAttributePayload().getAttributes();
                    description = properties.getThingGroupDescription();
                    returnCallback = () -> callback.onResult(ThingGroupName, description, attributes, metadata);
                } catch (ResourceNotFoundException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        ValidationResult Result = new ValidationResult(false, "Cannot find the requested thing group '" + ThingGroupName + "' on AWS.");
                        callback.onError(Result.getMessage());
                    };
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        ValidationResult Result = new ValidationResult(false, "Cannot find 'ThingGroupProperties' for thing group under '" + ThingGroupName + "'");
                        callback.onError(Result.getMessage());

                    };
                }
                catch (AmazonClientException e){
                    e.printStackTrace();
                    returnCallback = () -> {
                        ValidationResult Result = new ValidationResult(false, "Could not connect to AWS."+ e.getMessage());
                        callback.onError(Result.getMessage());
                    };
                }
            } else {
                returnCallback = () -> {
                    ValidationResult Result = new ValidationResult(false, "Invalid user session");
                    callback.onError(Result.getMessage());
                };
            }
            handler.post(returnCallback);
        }).start();
    } // Quick Config, Define Access, Member Access

    private ArrayList<ThingDetails> thingList;

    public void getThingsInThingGroup(final String ThingGroupName, final GetThingsInThingGroupHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable returnCallback;
            if (hasValidAccessToken) {
                try {
                    ListThingsInThingGroupResult result = awsIotClient.listThingsInThingGroup(new ListThingsInThingGroupRequest()
                            .withThingGroupName(ThingGroupName)
                            .withRecursive(false));
                    List<String> list = result.getThings();

                    thingList = new ArrayList<>();
                    if (list.size() > 0) {
                        hasIteratedAllChildren = false;
                        detailsErrCount = 0;
                        detailsSuccessCount = 0;
                        iterationIndex = 0;
                        while (!hasIteratedAllChildren) {
                            isIterationComplete = false;
                            getThingDetails(list.get(iterationIndex), new GetThingDetailsHandler() {
                                @Override
                                public void onResult(ThingDetails thingDetails) {
                                    thingDetails.ThingGroup = ThingGroupName;
                                    thingList.add(thingDetails);
                                    detailsSuccessCount++;
                                    isIterationComplete = true;
                                }

                                @Override
                                public void onError(String message) {
                                    detailsErrCount++;
                                    Log.i(TAG, detailsErrCount + ":" + message);
                                    isIterationComplete = true;
                                }
                            });
                            while (!isIterationComplete) {
                                try {
                                    sleep(POOLING_SLEEP_SHORT);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if ((detailsSuccessCount + detailsErrCount) == list.size())
                                hasIteratedAllChildren = true;
                            else
                                iterationIndex++;
                        }
                    }


                    returnCallback = () -> callback.onResult(thingList);

                } catch (ResourceNotFoundException e) {
                    returnCallback = () -> callback.onError("Thing Group '" + ThingGroupName + "' not listed.");
                }
            } else {
                returnCallback = () -> callback.onError("Invalid user session");
            }
            handler.post(returnCallback);
        }).start();
    } // Raise Ticket

    private ThingDetails thingDetails;

    public void getThingDetails(final String ThingName, final GetThingDetailsHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable returnCallback;
            if (hasValidAccessToken) {
                try {
                    DescribeThingResult result = awsIotClient.describeThing(new DescribeThingRequest()
                            .withThingName(ThingName));
                    thingDetails = new ThingDetails();
                    thingDetails.Arn = result.getThingArn();
                    thingDetails.Id = result.getThingId();
                    thingDetails.Name = result.getThingName();
                    thingDetails.DefaultClientId = result.getDefaultClientId();
                    thingDetails.ThingType = result.getThingTypeName();
                    thingDetails.BillingGroup = result.getBillingGroupName();
                    thingDetails.Version = result.getVersion();
                    thingDetails.AttributesMap = result.getAttributes();

                    returnCallback = () -> callback.onResult(thingDetails);
                } catch (ResourceNotFoundException e) {
                    returnCallback = () -> callback.onError("Requested thing '" + ThingName + "' not found.");
                }
            } else {
                returnCallback = () -> callback.onError("Invalid user session");
            }
            handler.post(returnCallback);
        }).start();
    } // Raise Ticket

    public void getComplexDetails(String complexName, ComplexDetailsRequestHandler fragmentCallback) {
//        Log.i(tag, "getComplexDetails() - " + complexName);
        ComplexCompositionLambdaRequest request = new ComplexCompositionLambdaRequest(complexName);
        RepositoryCallback<ComplexCompositionLambdaResult> callback = new RepositoryCallback<ComplexCompositionLambdaResult>() {
            @Override
            public void onComplete(_Result<ComplexCompositionLambdaResult> result) {
                Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 5");
                Log.i("cabinDetails", "onComplete: 5 :" + new Gson().toJson(result));
                if (result instanceof _Result.Success) {
                    ComplexCompositionLambdaResult data = ((_Result.Success<ComplexCompositionLambdaResult>) result).data;
                    ComplexDetailsData complexDetails = new ComplexDetailsData();
                    CabinDetailsData cabinDetails;
                    Log.i("connection", "onComplete: 6 :result success : " + new Gson().toJson(((_Result.Success<ComplexCompositionLambdaResult>) result).data.getComplexComposition()));
                    if (data.complexComposition.murCabins.size() > 0) {
                        for (MurCabin detail : data.complexComposition.murCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail);
                            complexDetails.murCabins.add(cabinDetails);
                            complexDetails.totalCabins++;
                        }
                    }
                    if (data.complexComposition.mwcCabins.size() > 0) {
                        for (MwcCabin detail : data.complexComposition.mwcCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail);
                            complexDetails.mwcCabins.add(cabinDetails);
                            complexDetails.totalCabins++;
                        }
                    }
                    if (data.complexComposition.fwcCabins.size() > 0) {
                        for (FwcCabin detail : data.complexComposition.fwcCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail);
                            complexDetails.fwcCabins.add(cabinDetails);
                            complexDetails.totalCabins++;
                        }
                    }
                    if (data.complexComposition.pwcCabins.size() > 0) {
                        for (PwcCabin detail : data.complexComposition.pwcCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail);
                            complexDetails.pwcCabins.add(cabinDetails);
                            complexDetails.totalCabins++;
                        }
                    }
                    if (data.complexComposition.bwtCabins.size() > 0) {
                        for (BwtCabin detail : data.complexComposition.bwtCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail);
                            complexDetails.bwtCabins.add(cabinDetails);
                            complexDetails.totalCabins++;
                        }
                    }

                    if(data.complexComposition.murCabins.size() > 0) {
                        complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails, data.complexComposition.murCabins.get(0));
                    } else if (data.complexComposition.mwcCabins.size() > 0) {
                        complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails, data.complexComposition.mwcCabins.get(0));
                    } else if (data.complexComposition.fwcCabins.size() > 0) {
                        complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails, data.complexComposition.fwcCabins.get(0));
                    } else if (data.complexComposition.pwcCabins.size() > 0) {
                        complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails, data.complexComposition.pwcCabins.get(0));
                    } else if (data.complexComposition.bwtCabins.size() > 0) {
                        complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails, data.complexComposition.bwtCabins.get(0));
                    }

                    Log.i("connection", "onComplete: 6 :result success : " + new Gson().toJson(complexDetails));
                    fragmentCallback.onSuccess(complexDetails);
                } else {
                    _Result.Error err = (_Result.Error) result;
                    fragmentCallback.onError(err.message);
                }

            }
        };
        CabinDetailsLambdaClient cabinDetailsLambdaClient = new CabinDetailsLambdaClient(context);
        cabinDetailsLambdaClient.ExecuteComplexCompositionLambda(request, callback);
    }


}

