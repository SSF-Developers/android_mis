package sukriti.ngo.mis.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapperConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
//import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sukriti.ngo.mis.dataModel.ValidationResult;
import sukriti.ngo.mis.dataModel._Result;
import sukriti.ngo.mis.dataModel.dynamo_db.Country;
import sukriti.ngo.mis.dataModel.dynamo_db.PermissionTree;
import sukriti.ngo.mis.dataModel.dynamo_db.Team;
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess;
import sukriti.ngo.mis.interfaces.AuthorisationHandler;
import sukriti.ngo.mis.interfaces.RepositoryCallback;
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData;
import sukriti.ngo.mis.ui.administration.interfaces.TeamRequestHandler;
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler;
import sukriti.ngo.mis.ui.administration.interfaces.UserAccessTreeRequestHandler;
import sukriti.ngo.mis.ui.administration.lambda.listTeam.GetTeamLambdaRequest;
import sukriti.ngo.mis.ui.administration.lambda.listTeam.GetTeamLambdaResult;
import sukriti.ngo.mis.ui.login.data.UserProfile;

import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.providerName;


//https://public-apis.xyz/indian-cities-1423
public class AdministrationDbHelper {

    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dynamoDBClient;
    private Context context;
    public boolean hasValidAccessToken = false;
    private final String TAG = "AWSDynamoDbClient";
    private ValidationResult Result;

    public AdministrationDbHelper(Context context) {
        this.context = context;
    }

    public void Authorize(final AuthorisationHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                        Map<String, String> logins = new HashMap<String, String>();
                        logins.put(providerName, idToken);
                        credentialsProvider.setLogins(logins);
                        credentialsProvider.getCredentials();
                        dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
                        dynamoDBClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
                        hasValidAccessToken = true;
                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                Result = new ValidationResult(true, "");
                                callback.onResult(Result);
                            }
                        };
                    } catch (NotAuthorizedException e) {
                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                Result = new ValidationResult(false, e.getMessage());
                                callback.onResult(Result);
                            }
                        };
                    }


                } else {
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            Result = new ValidationResult(false, "Session Invalid");
                            callback.onResult(Result);
                        }
                    };
                }
                handler.post(returnCallback);
            }
        }).start();
    }

    public void getUserAccessTree(final String userName, final UserAccessTreeRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if (hasValidAccessToken) {
                DynamoDBMapper dynamoDBMapper = DynamoDBMapper.builder().dynamoDBClient(dynamoDBClient).build();
                UserAccess userAccess = dynamoDBMapper.load(UserAccess.class, userName);
                returnCallback = () -> {
                    Result = new ValidationResult(true, "");
                    callback.onSuccess(userAccess);
                };
            } else {
                returnCallback = () -> {
                    Result = new ValidationResult(false, "Session Invalid");
                    callback.onError("Session Invalid");
                };
            }
            Log.i("_getTeam", "returnCallback-2");
            handler.post(returnCallback);
        }).start();
    }

    public void updateUserAccessTree(final MemberDetailsData user, Country country, final RequestHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(context.getMainLooper());
                Runnable returnCallback;
                if (hasValidAccessToken) {
                    DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                            .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                            .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
                            .build();
                    DynamoDBMapper dynamoDBMapper = DynamoDBMapper.builder().dynamoDBClient(dynamoDBClient).build();
                    //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient, dynamoDBMapperConfig);
                    //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);

                    PermissionTree permissionTree = new PermissionTree();
                    permissionTree.setCountry(country);

                    UserAccess userAccess = new UserAccess();
                    userAccess.setUserId(user.cognitoUser.getUserName());
                    userAccess.setUserRole(user.cognitoUser.getRole());
                    userAccess.setPermissions(permissionTree);
                    dynamoDBMapper.save(userAccess);

                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
                        }
                    };
                } else {
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            Result = new ValidationResult(false, "Session Invalid");
                            callback.onError("Session Invalid");
                        }
                    };
                }
                handler.post(returnCallback);
            }
        }).start();
    }

    public void addTeamMember(Team teamMember, final RequestHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(context.getMainLooper());
                Runnable returnCallback;
                if (hasValidAccessToken) {
                    DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                            .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                            .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
                            .build();
                    DynamoDBMapper dynamoDBMapper = DynamoDBMapper.builder().dynamoDBClient(dynamoDBClient).build();
                    //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient, dynamoDBMapperConfig);
                    //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
                    dynamoDBMapper.save(teamMember);

                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
                        }
                    };
                } else {
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            Result = new ValidationResult(false, "Session Invalid");
                            callback.onError("Session Invalid");
                        }
                    };
                }
                handler.post(returnCallback);
            }
        }).start();
    }

    public void getTeam(final String adminName, final TeamRequestHandler callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(context.getMainLooper());
                Runnable returnCallback;
                if (hasValidAccessToken) {
                    Log.i(TAG, "run: hasValidAccessToken : "+hasValidAccessToken);
                    DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                            .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                            .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
                            .build();
                    DynamoDBMapper dynamoDBMapper = DynamoDBMapper.builder().dynamoDBClient(dynamoDBClient).build();
                    //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient, dynamoDBMapperConfig);
                    //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);

                    Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                    eav.put(":val1", new AttributeValue().withS(adminName));

                    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                            .withFilterExpression("admin = :val1").withExpressionAttributeValues(eav);
                    List<Team> itemList = dynamoDBMapper.scan(Team.class, scanExpression);
                    Log.i(TAG, "run: itemList : "+ new Gson().toJson(itemList));

                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            Result = new ValidationResult(true, "");
                            Log.i(TAG, "run: itemList success");
                            callback.onSuccess(itemList);
                        }
                    };
                } else {
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: Session Invalid");
                            Result = new ValidationResult(false, "Session Invalid");
                            callback.onError("Session Invalid");
                        }
                    };
                }
                handler.post(returnCallback);
            }
        }).start();
    }

//    public void _getUserAccessTree(final String userName, final UserAccessTreeRequestHandler callback) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final Handler handler = new Handler(context.getMainLooper());
//                Runnable returnCallback;
//                if (hasValidAccessToken) {
//                    DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
//                            .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
//                            .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
//                            .build();
//                    DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient, dynamoDBMapperConfig);
//                    //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
//
//                    UserAccess userAccess = dynamoDBMapper.query(UserAccess.class, userName);
//                    returnCallback = new Runnable() {
//                        @Override
//                        public void run() {
//                            Result = new ValidationResult(true, "");
//                            callback.onSuccess(userAccess);
//                        }
//                    };
//                } else {
//                    returnCallback = new Runnable() {
//                        @Override
//                        public void run() {
//                            Result = new ValidationResult(false, "Session Invalid");
//                            callback.onError("Session Invalid");
//                        }
//                    };
//                }
//                Log.i("_getTeam", "returnCallback-2");
//                handler.post(returnCallback);
//            }
//        }).start();
//    }

}