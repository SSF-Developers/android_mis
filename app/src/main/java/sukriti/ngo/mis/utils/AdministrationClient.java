package sukriti.ngo.mis.utils;

import static sukriti.ngo.mis.AWSConfig.cognitoRegion;
import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.providerName;
import static sukriti.ngo.mis.AWSConfig.userPoolId;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidentityprovider.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidentityprovider.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidentityprovider.model.AdminDisableUserResult;
import com.amazonaws.services.cognitoidentityprovider.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidentityprovider.model.AdminEnableUserResult;
import com.amazonaws.services.cognitoidentityprovider.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidentityprovider.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;
import com.amazonaws.services.cognitoidentityprovider.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidentityprovider.model.ListUsersInGroupResult;
import com.amazonaws.services.cognitoidentityprovider.model.UserType;
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sukriti.ngo.mis.dataModel.CognitoUser;
import sukriti.ngo.mis.dataModel.CreateUserRequest;
import sukriti.ngo.mis.dataModel.ValidationResult;
import sukriti.ngo.mis.interfaces.AuthorisationHandler;
import sukriti.ngo.mis.ui.administration.interfaces.ClientSuperAdminRequestHandler;
import sukriti.ngo.mis.ui.administration.interfaces.CognitoUserDetailsRequestHandler;
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler;
import sukriti.ngo.mis.ui.login.data.UserProfile;

public class AdministrationClient {

    private static AmazonCognitoIdentityProvider identityProvider;
    private static boolean isProviderInitialized = false;

    private void initializeClient(Context context) {

        Log.i("__initializeClient", "creating...");
        String idToken = AuthenticationClient.getCurrSession().getIdToken().getJWTToken();
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context, // Context
                identityPoolID, // Identity Pool ID
                Regions.AP_SOUTH_1 // Region
        );
        Map<String, String> logins = new HashMap<>();
        logins.put(providerName, idToken);
        credentialsProvider.setLogins(logins);
        credentialsProvider.getCredentials();

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        identityProvider = new AmazonCognitoIdentityProviderClient(credentialsProvider, clientConfiguration);
        identityProvider.setRegion(Region.getRegion(cognitoRegion));
        isProviderInitialized = true;

        Log.i("__initializeClient", "isProviderInitialized = true");
    }

    private void releaseClient() {
        if (isProviderInitialized) {
            identityProvider.shutdown();
            isProviderInitialized = false;
        }

    }

    public void createUser(Context context, CreateUserRequest createUserRequest, AuthorisationHandler callback) {
        new Thread(() -> {
            if (!isProviderInitialized)
                initializeClient(context);
            SharedPrefsClient sharedPrefsClient = new SharedPrefsClient(context);
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback = null;


            Map<String, String> lambdaParameters = new HashMap<>();
            lambdaParameters.put("groupName", getGroupName(UserProfile.Companion.getRole(createUserRequest.Role)));
            lambdaParameters.put("userPoolId", userPoolId);
            lambdaParameters.put("memberName", createUserRequest.UserName);
            lambdaParameters.put("memberRole", createUserRequest.Role);
            lambdaParameters.put("adminName", sharedPrefsClient.getUserDetails().getUser().getUserName());
            lambdaParameters.put("adminRole", UserProfile.Companion.getRoleName(sharedPrefsClient.getUserDetails().getRole()));
            lambdaParameters.put("assignedBy", sharedPrefsClient.getUserDetails().getUser().getUserName());

            ArrayList<AttributeType> attributeList = new ArrayList<>();
            attributeList.add(new AttributeType().withName("custom:custom:Organization").withValue(createUserRequest.OrganizationName));
            attributeList.add(new AttributeType().withName("custom:Role").withValue(createUserRequest.Role));
            attributeList.add(new AttributeType().withName("custom:ClientName").withValue(createUserRequest.ClientName));

            AdminCreateUserRequest request = new AdminCreateUserRequest()
                    .withUserPoolId(userPoolId)
                    .withUsername(createUserRequest.UserName)
                    .withTemporaryPassword(createUserRequest.Password)
                    .withClientMetadata(lambdaParameters)
                    //.withDesiredDeliveryMediums("EMAIL")
                    .withUserAttributes(attributeList)
                    .withMessageAction("SUPPRESS");

            try {
                AdminCreateUserResult result = identityProvider.adminCreateUser(request);
                Log.i("_createUser", "Result -> " + new Gson().toJson(result));
                Log.i("_createUser","adminCreateUser() - success");
                addUserToUserGroup(context,createUserRequest,callback);
//                returnCallback = () -> {
//                    callback.onResult(new ValidationResult(true, ""));
//                };

            } catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> callback.onResult(new ValidationResult(false, e.getMessage()));
            }

            //releaseClient();
            handler.post(returnCallback);
        }).start();
    }

    public void addUserToUserGroup(Context context, CreateUserRequest createUserRequest, AuthorisationHandler callback) {
        new Thread(() -> {
            if (!isProviderInitialized)
                initializeClient(context);
            SharedPrefsClient sharedPrefsClient = new SharedPrefsClient(context);
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String groupName = getGroupName(UserProfile.Companion.getRole(createUserRequest.Role));
            AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest()
                    .withUserPoolId(userPoolId)
                    .withUsername(createUserRequest.UserName)
                    .withGroupName(groupName);

            try {
                Log.i("_createUser","addUserToUserGroup()");
                identityProvider.adminAddUserToGroup(request);
                returnCallback = () -> {
                    Log.i("_createUser","addUserToUserGroup() - success");
                    callback.onResult(new ValidationResult(true, ""));
                };

            } catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> callback.onResult(new ValidationResult(false, e.getMessage()));
            }

            releaseClient();
            handler.post(returnCallback);
        }).start();
    }


    public void getUserDetails(Context context, String userName, CognitoUserDetailsRequestHandler callback) {
        new Thread(() -> {
            Runnable returnCallback;
            final Handler handler = new Handler(context.getMainLooper());

            try {
                if (!isProviderInitialized)
                    initializeClient(context);

                AdminGetUserRequest request = new AdminGetUserRequest()
                        .withUserPoolId(userPoolId)
                        .withUsername(userName);

                Log.i("__initializeClient", "adminGetUser; isProviderInitialized = " + isProviderInitialized);
                AdminGetUserResult result = identityProvider.adminGetUser(request);
                returnCallback = () -> {
                    Log.i("_getTeam", "returnCallback-1");
                    CognitoUser user = new CognitoUser(
                            result.getUsername(),
                            result.getUserStatus(),
                            result.getEnabled(),
                            getCognitoAttributeName("custom:Role", result.getUserAttributes()),
                            getCognitoAttributeName("custom:ClientName", result.getUserAttributes()),
                            getCognitoAttributeName("custom:custom:Organization", result.getUserAttributes()),
                            result.getUserLastModifiedDate().toString(),
                            result.getUserCreateDate().toString(),
                            getCognitoAttributeName("name", result.getUserAttributes()),
                            getCognitoAttributeName("gender", result.getUserAttributes()),
                            getCognitoAttributeName("address", result.getUserAttributes()),
                            getCognitoAttributeName("phone_number", result.getUserAttributes()),
                            getCognitoAttributeName("email", result.getUserAttributes()));
                    callback.onSuccess(user);
                };

            }
            catch (NotAuthorizedException e){
                e.printStackTrace();
                returnCallback = () -> {
                    Log.i("_getTeam", "returnCallback-3");
                    callback.onError(e.getMessage());
                };
            }
            catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> {
                    Log.i("_getTeam", "returnCallback-2");
                    callback.onError(e.getMessage() + "Login again with your credentials to continue.");
                };
            }
            releaseClient();
            Log.i("_getTeam", "returnCallback-0");
            handler.post(returnCallback);
        }).start();
    }

    public void disableUser(Context context, String userName, RequestHandler callback) {
        new Thread(() -> {
            if (!isProviderInitialized)
                initializeClient(context);

             //context.getMainLooper().quit();

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            AdminDisableUserRequest request = new AdminDisableUserRequest()
                    .withUsername(userName)
                    .withUserPoolId(userPoolId);

            try {
                AdminDisableUserResult result = identityProvider.adminDisableUser(request);
                returnCallback = callback::onSuccess;

            } catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> callback.onError( e.getMessage());
            }
            handler.post(returnCallback);
        }).start();
    }

    public void enableUser(Context context, String userName, RequestHandler callback) {
        new Thread(() -> {
            if (!isProviderInitialized)
                initializeClient(context);

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            AdminEnableUserRequest request = new AdminEnableUserRequest()
                    .withUsername(userName)
                    .withUserPoolId(userPoolId);

            try {
                AdminEnableUserResult result = identityProvider.adminEnableUser(request);
                returnCallback = callback::onSuccess;

            } catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> callback.onError(e.getMessage());
            }
            handler.post(returnCallback);
        }).start();
    }

    public void deleteUser(Context context, String userName, RequestHandler callback) {
        new Thread(() -> {
            if (!isProviderInitialized)
                initializeClient(context);

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            AdminDeleteUserRequest request = new AdminDeleteUserRequest()
                    .withUsername(userName)
                    .withUserPoolId(userPoolId);

            try {
                identityProvider.adminDeleteUser(request);
                returnCallback = callback::onSuccess;

            } catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> callback.onError(e.getMessage());
            }
            handler.post(returnCallback);
        }).start();
    }


    public void getClientSuperAdmin(Context context, String clientCode, ClientSuperAdminRequestHandler callback) {
        String tag = "getClientSuperAdmin";

        new Thread(() -> {
            if (!isProviderInitialized)
                initializeClient(context);

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            ListUsersInGroupRequest request = new ListUsersInGroupRequest()
                    .withUserPoolId(userPoolId)
                    .withGroupName("CLIENT_SUPER_ADMIN");

            try {
                ListUsersInGroupResult result = identityProvider.listUsersInGroup(request);
                List<UserType> userList = result.getUsers();

                returnCallback = () -> callback.onSuccess(false,null);

                for(UserType user: userList){
                    Log.i(tag,""+user.getUsername());
                    for(AttributeType attribute: user.getAttributes()){
                        if(attribute.getName().compareToIgnoreCase("custom:ClientName")==0)
                        {
                            Log.i(tag,""+attribute.getValue());
                            if(clientCode.compareToIgnoreCase(attribute.getValue())==0){
                                Log.i(tag,"duplicate request");
                                returnCallback = () -> callback.onSuccess(true,user);
                            }
                        }
                    }
                }

            } catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> callback.onError(e.getMessage());
            }
            handler.post(returnCallback);
        }).start();
    }

    private String getCognitoAttributeName(String key, List<AttributeType> list) {
        String value = "";
        for (AttributeType attr : list) {
            Log.i("getCognitoAttributeName", attr.getName() + " , " + attr.getValue());
            if (attr.getName().compareToIgnoreCase(key) == 0)
                value = attr.getValue();
        }
        return value;
    }

    private String getGroupName(UserProfile.Companion.UserRole role) {
        Log.i("getGroupName-1", role.name());
        String res;
        switch (role) {
            case SuperAdmin:
                res = "SUPER_ADMIN";
                break;
            case VendorAdmin:
                res = "VENDOR_ADMIN";
                break;
            case VendorManager:
                res = "VENDOR_MANAGER";
                break;
            case ClientAdmin:
                res = "CLIENT_ADMIN";
                break;
            case ClientManager:
                res = "CLIENT_MANAGER";
                break;
            case ClientSuperAdmin:
                res = "CLIENT_SUPER_ADMIN";
                break;
            default:
                res = "";
        }
        Log.i("getGroupName", res);
        return res;
    }

    public void createSuperAdmin(Context context) {
        AuthorisationHandler callback = Result -> {

        };

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String idToken = AuthenticationClient.getCurrSession().getIdToken().getJWTToken();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context, // Context
                    identityPoolID, // Identity Pool ID
                    Regions.AP_SOUTH_1 // Region
            );
            Map<String, String> logins = new HashMap<>();
            logins.put(providerName, idToken);
            credentialsProvider.setLogins(logins);
            credentialsProvider.getCredentials();

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(credentialsProvider, clientConfiguration);
            cipClient.setRegion(Region.getRegion(cognitoRegion));
            //userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cipClient);

            Map<String, String> lambdaParameters = new HashMap<>();
            lambdaParameters.put("", "");

            ArrayList<AttributeType> attributeList = new ArrayList<>();
            attributeList.add(new AttributeType().withName("gender").withValue("Male"));
            attributeList.add(new AttributeType().withName("address").withValue("Gurgaon"));
            attributeList.add(new AttributeType().withName("email").withValue("developer@sukriti.ngo"));
            attributeList.add(new AttributeType().withName("phone_number").withValue("+917508902000"));
            attributeList.add(new AttributeType().withName("custom:custom:Organization").withValue("Sukriti Social Foundation"));
            attributeList.add(new AttributeType().withName("custom:Role").withValue("Super Admin"));
            attributeList.add(new AttributeType().withName("custom:ClientName").withValue("SSF"));

            AdminCreateUserRequest request = new AdminCreateUserRequest()
                    .withUserPoolId(userPoolId)
                    .withUsername("ssf_super_admin")
                    .withTemporaryPassword("Changeme11!")
                    .withClientMetadata(lambdaParameters)
                    //.withDesiredDeliveryMediums("EMAIL")
                    .withUserAttributes(attributeList)
                    .withMessageAction("SUPPRESS");
            try {
                AdminCreateUserResult result = cipClient.adminCreateUser(request);
                returnCallback = () -> callback.onResult(new ValidationResult());

            } catch (AmazonClientException e) {
                e.printStackTrace();
                returnCallback = () -> callback.onResult(new ValidationResult());
            }

            handler.post(returnCallback);
        }).start();
    }
}
