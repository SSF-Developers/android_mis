package sukriti.ngo.mis.utils;

import static sukriti.ngo.mis.AWSConfig.LAMBDA_TIMEOUT;
import static sukriti.ngo.mis.AWSConfig.cognitoRegion;
import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.lambdaAggregationBucketName;
import static sukriti.ngo.mis.AWSConfig.providerName;
import static sukriti.ngo.mis.utils.Utilities.getIotPolicyName;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import sukriti.ngo.mis.dataModel._Result;
import sukriti.ngo.mis.interfaces.LoginLambdaHandler;
import sukriti.ngo.mis.interfaces.LoginLambdaResultHandler;
import sukriti.ngo.mis.interfaces.RepositoryCallback;
import sukriti.ngo.mis.repository.DataRepository;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaRequest;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.GetClientListLambdaHandler;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.GetClientListResultHandler;
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessLambdaHandler;
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessLambdaRequest;
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessLambdaResult;
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessRequestHandler;
import sukriti.ngo.mis.ui.administration.lambda.DeleteUser.DeleteUserLambdaHandler;
import sukriti.ngo.mis.ui.administration.lambda.DeleteUser.DeleteUserLambdaRequest;
import sukriti.ngo.mis.ui.administration.lambda.DeleteUser.DeleteUserLambdaResult;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.ClientPermissionLambdaRequest;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.ClientPermissionLambdaResult;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.GetPermissionDataLambdaHandler;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.GetPermissionDataResultHandler;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionResult;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionResultHandler;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionsLambdaHandler;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionsRequest;
import sukriti.ngo.mis.ui.administration.lambda.listTeam.GetTeamLambdaHandler;
import sukriti.ngo.mis.ui.administration.lambda.listTeam.GetTeamLambdaRequest;
import sukriti.ngo.mis.ui.administration.lambda.listTeam.GetTeamLambdaResult;
import sukriti.ngo.mis.ui.dashboard.repository.MisCabinData;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.AggregateMisUserDataLambdaHandler;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.AggregateMisVendorDataLambdaHandler;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataLambdaRequest;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataLambdaResult;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataRequestHandler;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaResult;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishRequestHandler;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.PublishCommandLambdaHandler;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.PublishConfigLambdaHandler;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.PublishGenericConfigLambdaHandler;
import sukriti.ngo.mis.ui.login.data.LoginLambdaRequest;
import sukriti.ngo.mis.ui.login.data.LoginLambdaResult;
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.ui.management.EnrollDevice.Lambda.DDBSaveLambda.DynamoDbDataWriter;
import sukriti.ngo.mis.ui.management.EnrollDevice.Lambda.DDBSaveLambda.DynamoDbDataWriterResponseCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterCabinPayload;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterComplexPayload;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.UpdateComplexPayload;
import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseLambdaHandler;
import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.CreateNewPolicy.CreatePolicyLambdaHandler;
import sukriti.ngo.mis.ui.management.lambda.CreateNewPolicy.CreatePolicyResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.DeleteComplex.DeleteComplexLambda;
import sukriti.ngo.mis.ui.management.lambda.DeleteComplex.DeleteComplexResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.DeleteDevice.DeleteDeviceLambdaHandler;
import sukriti.ngo.mis.ui.management.lambda.DeleteDevice.DeleteDeviceResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaHandler;
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.DeletePolicy.DeletePolicyLambda;
import sukriti.ngo.mis.ui.management.lambda.DeletePolicy.DeletePolicyResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda.CreateQrLambda;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda.CreateQrRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda.CreateQrResponse;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda.CreateQrResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambda;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambda;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.RequestManagementLambda;
import sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails.EnterpriseDetailsLambda;
import sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails.EnterpriseDetailsResponse;
import sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails.EnterpriseDetailsResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.FetchClientLogo.FetchClientLogoLambda;
import sukriti.ngo.mis.ui.management.lambda.FetchClientLogo.FetchClientLogoResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.FetchWifiDetails.FetchWifiDetails;
import sukriti.ngo.mis.ui.management.lambda.FetchWifiDetails.FetchWifiDetailsResponse;
import sukriti.ngo.mis.ui.management.lambda.FetchWifiDetails.FetchWifiDetailsResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda.GetPolicyLambda;
import sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda.GetPolicyLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda.GetPolicyLambdaResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaHandler;
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseLambdaHandler;
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseResponse;
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.PatchDevice.PatchDeviceLambda;
import sukriti.ngo.mis.ui.management.lambda.PatchDevice.PatchDeviceLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.PatchEnterprise.PatchEnterpriseLambda;
import sukriti.ngo.mis.ui.management.lambda.PatchEnterprise.PatchEnterpriseLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.QrShare.QrShareLambda;
import sukriti.ngo.mis.ui.management.lambda.QrShare.QrShareLambdaResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.TogglePolicyState.TogglePolicyState;
import sukriti.ngo.mis.ui.management.lambda.TogglePolicyState.TogglePolicyStateResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete.UndoDeleteEnterprise;
import sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete.UndoEnterpriseDeleteLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete.UndoEnterpriseDeleteResponse;
import sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete.UndoEnterpriseDeleteResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.VerifyPackageName.VerifyPackageNameLambda;
import sukriti.ngo.mis.ui.management.lambda.VerifyPackageName.VerifyPackageNameResponseHandler;
import sukriti.ngo.mis.ui.profile.fragments.GetProfileDataLambdaHandler;
import sukriti.ngo.mis.ui.profile.fragments.GetProfileDataResultHandler;
import sukriti.ngo.mis.ui.profile.fragments.ProfileDataLambdaRequest;
import sukriti.ngo.mis.ui.profile.fragments.UserProfileDataLambdaResult;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DeleteScheduleReportUserLambdaResult;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DeleteScheduledReportUserLambda;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DeleteScheduledReportUserLambdaResultHandler;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DownloadReportLambdaHandler;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DownloadReportLambdaResult;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DownloadReportRequest;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DownloadReportResultHandler;
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaHandler;

public class LambdaClient {

    private CognitoCachingCredentialsProvider credentialsProvider;
    private DataRepository dataRepository;
    private Context context;
    private LoginLambdaHandler lambdaHandler;
    private DefineAccessLambdaHandler mDefineAccessLambdaHandler;
    private AggregateMisUserDataLambdaHandler mAggregateMisUserDataLambdaHandler;
    private AggregateMisVendorDataLambdaHandler mAggregateMisVendorDataLambdaHandler;
    private PublishCommandLambdaHandler mPublishCommandLambdaHandler;
    private PublishConfigLambdaHandler mPublishConfigLambdaHandler;
    private PublishGenericConfigLambdaHandler mPublishGenericConfigLambdaHandler;
    private CreateTicketLambdaHandler mCreateTicketLambdaHandler;
    private GetClientListLambdaHandler mGetClientListLambdaHandler;
    private GetProfileDataLambdaHandler mGetProfileDataLambdaHandler;
    private GetPermissionDataLambdaHandler mGetPermissionDataLambdaHandler;
    private SubmitPermissionsLambdaHandler mSubmitPermissionsLambdaHandler;
    private DownloadReportLambdaHandler mDownloadReportLambdaHandler;
    private DeleteScheduledReportUserLambda mdeleteScheduledReportUserLambda;
    private ListEnterpriseLambdaHandler mListEnterpriseLambda;
    private ListDeviceLambdaHandler mListDeviceLambda;
    private CreateEnterpriseLambdaHandler mCreateEnterpriseLambda;
    private DeleteEnterpriseLambdaHandler mDeleteEnterpriseLambda;
    private DeleteDeviceLambdaHandler deleteDeviceLambda;
    private ListPolicyLambda mListPolicyLambda;
    private CreateQrLambda createQrLambda;
    private DynamoDbDataWriter dynamoDbDataWriter;
    private CreatePolicyLambdaHandler createPolicyLambdaHandler;
    private PatchDeviceLambda patchDeviceLambda;
    private PatchEnterpriseLambda patchEnterpriseLambda;
    private DeletePolicyLambda deletePolicyLambda;
    private GetPolicyLambda getPolicyLambda;
    private QrShareLambda qrShareLambda;
    private TogglePolicyState togglePolicyStateLambda;
    private DeleteComplexLambda deleteComplexLambda;
    private EnterpriseDetailsLambda enterpriseDetailsLambda;
    private UndoDeleteEnterprise undoEnterpriseDelete;
    private FetchClientLogoLambda fetchClientLogo;
    private FetchWifiDetails fetchWifiDetails;

    // management
    private ManagementLambda mManagementLambda;
    private SharedPrefsClient sharedPrefsClient;
    private VerifyPackageNameLambda verifyPackageNameLambda;

    public LambdaClient(Context context) {
        this.context = context;
        sharedPrefsClient = new SharedPrefsClient(context);
    }

    public void ExecuteLoginLambda(final LoginLambdaResultHandler callback, UserProfile userProfile) {
        Log.d("_loginFlow", "ExecuteLoginLambda() called with: callback = [" + callback + "], userProfile = [" + userProfile + "]");
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    lambdaHandler = factory.build(LoginLambdaHandler.class);

                    String identityId = credentialsProvider.getIdentityId();
                    Log.i("__loginLambda", "identityId: " + identityId);

                    LoginLambdaRequest request = new LoginLambdaRequest(credentialsProvider.getIdentityId(), getIotPolicyName(userProfile));

                    LoginLambdaResult result = lambdaHandler.node_function_login(request);
                    returnCallback = () -> {
                        callback.onSuccess(result);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    Log.i("_loginFlow", "Error from executing lambda " + e.getMessage());
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
            }
            else {
                returnCallback = () -> {
                    Log.i("_loginFlow", "getCurrSession() = null");
                    callback.onError("Session Invalid");
                };
            }
            handler.post(returnCallback);
        }).start();
    }

    public void ExecuteDefineAccessLambda(DefineAccessLambdaRequest request, final DefineAccessRequestHandler callback) {
        //request = new DefineAccessLambdaRequest(credentialsProvider.getIdentityId());
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mDefineAccessLambdaHandler = factory.build(DefineAccessLambdaHandler.class);
//                    DefineAccessLambdaResult result = mDefineAccessLambdaHandler.mis_define_access(request);
                    Log.i("AccessDefine", "Request: "+new Gson().toJson(request));
                    Log.i("AccessDefine", "Executing lambda: ");
                    DefineAccessLambdaResult result = mDefineAccessLambdaHandler.mis_administration_defineAccess(request);
                    returnCallback = () -> {
                        Log.i("AccessDefine", "calling onSuccess() ");
                        callback.onSuccess(result);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    Log.i("AccessDefine", "Lambda Error " + e.getMessage());
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
            } else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }
            handler.post(returnCallback);
        }).start();
    }

    public void ExecuteAggregateMisUserDataLambda(CabinDataLambdaRequest request, final CabinDataRequestHandler callback) {
        dataRepository = new DataRepository((context));
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            returnCallback = () -> {
                callback.onError("void");
            };
            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("__ExecuteLambda", "" + new Gson().toJson(request));

                    LambdaInvokerFactory factory = initLambdaClient();
                    mAggregateMisUserDataLambdaHandler = factory.build(AggregateMisUserDataLambdaHandler.class);
                    CabinDataLambdaResult result = mAggregateMisUserDataLambdaHandler.aggregate_mis_user_data(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider.getCredentials());
                        S3Object fullObject = s3Client.getObject(new GetObjectRequest(lambdaAggregationBucketName, result.fileName));
                        Log.i("__ExecuteLambda", "Content-Type: " + fullObject.getObjectMetadata().getContentType());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fullObject.getObjectContent()));
                        String s3DocContent = ReadBigStringIn(reader);
                        Log.i("__ExecuteLambda", "Content-Type: " + s3DocContent);
                        dataRepository.insertBulkData(context, new Gson().fromJson(s3DocContent, MisCabinData.class));
                        returnCallback = () -> {
                            callback.onSuccess(result);
                        };
                    } else {
                        returnCallback = () -> {
                            callback.onError(result.error);
                        };
                    }


                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
                catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    };
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }
            handler.post(returnCallback);
        }).start();
    }

    public String ReadBigStringIn(BufferedReader buffIn) throws IOException {
//        StringBuilder everything = new StringBuilder();
//        String line;
//        while ((line = buffIn.readLine()) != null) {
//            everything.append(line);
//        }

        int i = 0;
        StringBuilder sb = new StringBuilder();
        while ((i = buffIn.read()) != -1) {
            char c = (char) i;
            if (c == '\n')
                break;
            sb.append(c);
        }

        return sb.toString();
    }

    public void ExecuteAggregateVendorUserDataLambda(CabinDataLambdaRequest request, final CabinDataRequestHandler callback) {
        dataRepository = new DataRepository((context));
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback = () -> {
                callback.onError("void");
            };

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("__ExecuteLambda", "" + new Gson().toJson(request));
                    LambdaInvokerFactory factory = initLambdaClient();
                    mAggregateMisVendorDataLambdaHandler = factory.build(AggregateMisVendorDataLambdaHandler.class);
                    CabinDataLambdaResult result = mAggregateMisVendorDataLambdaHandler.aggregate_mis_vendor_data(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider.getCredentials());
                        S3Object fullObject = s3Client.getObject(new GetObjectRequest(lambdaAggregationBucketName, result.fileName));
                        Log.i("__ExecuteLambda", "Content-Type: " + fullObject.getObjectMetadata().getContentType());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fullObject.getObjectContent()));
                        String s3DocContent = ReadBigStringIn(reader);
                        Log.i("__ExecuteLambda", "Content-Type: " + s3DocContent);
                        dataRepository.insertBulkData(context, new Gson().fromJson(s3DocContent, MisCabinData.class));
                        returnCallback = () -> callback.onSuccess(result);

                    } else {
                        returnCallback = () -> {
                            callback.onError(result.error);
                        };
                    }


                }
                catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
                catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
                catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    };
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }
            handler.post(returnCallback);
        }).start();
    }

    public void ExecutePublishCommandLambda(IotPublishLambdaRequest request, final IotPublishRequestHandler callback) {
        dataRepository = new DataRepository((context));
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback = () -> {
                callback.onError("void");
            };

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("__ExecuteLambda", "" + new Gson().toJson(request));
                    LambdaInvokerFactory factory = initLambdaClient();
                    Log.i("__ExecuteLambda", "lambda client initiated successfully");
                    mPublishCommandLambdaHandler = factory.build(PublishCommandLambdaHandler.class);
                    Log.i("__ExecuteLambda", "mPublishCommandLambdaHandler built successfully");
                    IotPublishLambdaResult result = mPublishCommandLambdaHandler.mis_publish_command(request);
                    Log.i("__ExecuteLambda", "mPublishCommandLambdaHandler.mis_publish_command(request)");

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        returnCallback = () -> {
                            callback.onSuccess(result);
                        };
                    } else {
                        returnCallback = () -> {
                            callback.onError(result.error);
                        };
                    }


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                } catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    };
                }
            } else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }
            handler.post(returnCallback);
        }).start();
    }

    public void ExecutePublishConfigLambda(IotPublishLambdaRequest request, final IotPublishRequestHandler callback) {
        dataRepository = new DataRepository((context));
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback = () -> {
                callback.onError("void");
            };

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("__ExecuteLambda", "" + new Gson().toJson(request));
                    LambdaInvokerFactory factory = initLambdaClient();
                    mPublishConfigLambdaHandler = factory.build(PublishConfigLambdaHandler.class);
                    IotPublishLambdaResult result = mPublishConfigLambdaHandler.mis_publish_config(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));


                    if (result.result == 1) {
                        //Success response
                        returnCallback = () -> callback.onSuccess(result);
                    }
                    else {
                        returnCallback = () -> callback.onError(result.error);
                    }


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
                catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
                catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    };
                }
            } else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }
            handler.post(returnCallback);
        }).start();
    }

    public void ExecutePublishGenericConfigLambda(IotPublishLambdaRequest request, final IotPublishRequestHandler callback) {
        dataRepository = new DataRepository((context));
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("__ExecuteLambda", "" + new Gson().toJson(request));
                    LambdaInvokerFactory factory = initLambdaClient();
                    mPublishGenericConfigLambdaHandler = factory.build(PublishGenericConfigLambdaHandler.class);
                    IotPublishLambdaResult result = mPublishGenericConfigLambdaHandler.mis_publish_config_generic(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));


                    if (result.result == 1) {
                        //Success response
                        returnCallback = () -> {
                            callback.onSuccess(result);
                        };
                    }
                    else {
                        returnCallback = () -> callback.onError(result.error);

                    }


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> callback.onError(e.getMessage());

                }
                catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> callback.onError(e.getMessage());

                }
                catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    };
                }
            } else {
                returnCallback = () -> callback.onError("Session Invalid");

            }
            handler.post(returnCallback);
        }).start();
    }

    public void ExecuteDeleteUserLambda(DeleteUserLambdaRequest request, final RepositoryCallback callback) {

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    DeleteUserLambdaHandler mDeleteUserLambdaHandler = factory.build(DeleteUserLambdaHandler.class);
                    DeleteUserLambdaResult result = mDeleteUserLambdaHandler.mis_delete_user(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(request));
                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        returnCallback = () -> callback.onComplete(new _Result.Success<String>(result.message));
                    } else {
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<String>(-1, result.message));
                        };
                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
                catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
                catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
            } else {
                returnCallback = () -> {
                    callback.onComplete(new _Result.Error<String>(-1, "Session Invaled"));
                };
                handler.post(returnCallback);
            }
        }).start();
    }

    public void ExecuteGetTeamLambda(GetTeamLambdaRequest request, final RepositoryCallback callback) {

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    GetTeamLambdaHandler mGetTeamLambdaHandler = factory.build(GetTeamLambdaHandler.class);
                    GetTeamLambdaResult result = mGetTeamLambdaHandler.mis_adminisatration_listTeam(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(request));
                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    returnCallback = () -> {
                        Log.i("getTeamLambda", "getTeamLambda: 4.1 \n Get Team success Lambda" + new Gson().toJson(result));
                        callback.onComplete(new _Result.Success<>(result));
                    };
                    handler.post(returnCallback);

                }
                catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
                catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
                catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
            }
            else {
                returnCallback = () -> {
                    callback.onComplete(new _Result.Error<String>(-1, "Session Invaled"));
                };
                handler.post(returnCallback);
            }
        }).start();
    }

    public void ExecuteGetClientListLambda( GetClientListResultHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if (AuthenticationClient.getCurrSession() != null) {
                Log.i("getClientList", "Current Session authenticated");
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    Log.i("getClientList", "Lambda factory created");
                    mGetClientListLambdaHandler = factory.build(GetClientListLambdaHandler.class);
                    Log.i("getClientList", "client handler build");
                    ClientListLambdaRequest request = new ClientListLambdaRequest();
                    ClientListLambdaResult result = mGetClientListLambdaHandler.mis_administration_listClients(request);


                    Log.i("getClientList", "result " + new Gson().toJson(result));

                    returnCallback = () -> {
                        Log.i("getClientList", "calling onSuccess()");
                        callback.onSuccess(result);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }
            handler.post(returnCallback);
        } ).start();
    }

    public void ExecuteProfileDataLambda(ProfileDataLambdaRequest request, GetProfileDataResultHandler callback) {
        Log.i("LambdaExecution", "Executing ExecuteProfileDataLambda");
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if(AuthenticationClient.getCurrSession() != null) {
                Log.i("myProfileData", "Current Session authenticated");
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    Log.i("myProfileData", "Lambda factory created");
                    mGetProfileDataLambdaHandler = factory.build(GetProfileDataLambdaHandler.class);
                    Log.i("myProfileData", "client handler build");
                    UserProfileDataLambdaResult result = mGetProfileDataLambdaHandler.mis_adminisatration_actions(request);
                    Log.i("myProfileData", "result " + new Gson().toJson(result));
                    returnCallback = () -> {
                        Log.i("getClientList", "calling onSuccess()");
                        callback.onSuccess(result);
                    };

                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);

        } ).start();
    }

    public void ExecuteDeleteScheduledReportUserLambda(ProfileDataLambdaRequest request, DeleteScheduledReportUserLambdaResultHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if(AuthenticationClient.getCurrSession() != null) {
                Log.i("myProfileData", "Current Session authenticated");
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    Log.i("myProfileData", "Lambda factory created");
                    mdeleteScheduledReportUserLambda = factory.build(DeleteScheduledReportUserLambda.class);
                    Log.i("myProfileData", "client handler build");
                    DeleteScheduleReportUserLambdaResult result = mdeleteScheduledReportUserLambda.mis_adminisatration_actions(request);
                    Log.i("myProfileData", "result " + new Gson().toJson(result));
                    returnCallback = () -> {
                        Log.i("getClientList", "calling onSuccess()");
                        callback.onSuccess(result);
                    };

                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onError(e.getMessage());
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);

        } ).start();
    }

    public void ExecuteGetPermissionDataLambda(ClientPermissionLambdaRequest request, GetPermissionDataResultHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                Log.i("myPermissionData", "Current Session authenticated");
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    Log.i("myPermissionData", "Lambda factory created");
                    mGetPermissionDataLambdaHandler = factory.build(GetPermissionDataLambdaHandler.class);
                    Log.i("myPermissionData", "client handler build");
                    ClientPermissionLambdaResult result = mGetPermissionDataLambdaHandler.mis_administration_getpermission_ui(request);
                    Log.i("myPermissionData", "result " + new Gson().toJson(result));
                    returnCallback = () -> {
                        Log.i("myPermissionData", "calling onSuccess()");
                        callback.onSuccess(result);
                    };

                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        Log.i("myPermissionData", "calling onSuccess()");
                        callback.onError(e.getMessage());
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);

        } ).start();
    }

    public void ExecuteSubmitPermissionsLambda(SubmitPermissionsRequest request, SubmitPermissionResultHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                Log.i("myPermissionData", "Current Session authenticated");
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    Log.i("myPermissionData", "Lambda factory created");
                    mSubmitPermissionsLambdaHandler = factory.build(SubmitPermissionsLambdaHandler.class);
                    Log.i("myPermissionData", "client handler build");
                    SubmitPermissionResult result = mSubmitPermissionsLambdaHandler.mis_administration_ui(request);
                    Log.i("myPermissionData", "result " + new Gson().toJson(result));
                    returnCallback = () -> {
                        Log.i("myPermissionData", "calling onSuccess()");
                        callback.onSuccess(result);
                    };

                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        Log.i("myPermissionData", "calling onSuccess()");
                        callback.onError(e.getMessage());
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);

        } ).start();

    }
    public void ExecuteDownloadReportLambda(DownloadReportRequest request, DownloadReportResultHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                Log.i("myPermissionData", "Current Session authenticated");
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    Log.i("myPermissionData", "Lambda factory created");
                    mDownloadReportLambdaHandler = factory.build(DownloadReportLambdaHandler.class);
                    Log.i("myPermissionData", "client handler build");
                    DownloadReportLambdaResult result = mDownloadReportLambdaHandler.mis_report_api(request);
                    Log.i("myPermissionData", "result " + new Gson().toJson(result));
                    returnCallback = () -> {
                        Log.i("myPermissionData", "calling onSuccess()");
                        callback.onSuccess(result);
                    };

                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        Log.i("myPermissionData", "calling onSuccess()");
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);

        } ).start();

    }

    public void ExecuteListEnterpriseLambda(ListEnterpriseResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mListEnterpriseLambda = factory.build(ListEnterpriseLambdaHandler.class);
                    ListEnterpriseLambdaRequest request = new ListEnterpriseLambdaRequest("ssf-admin-399810");
                    ListEnterpriseResponse response = mListEnterpriseLambda.List_Enterprises_Android_Management(request);

                    Log.i("listEnterprise", "Response -> " + new Gson().toJson(response));
                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Sesion Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteListDeviceLambda(ListDeviceLambdaRequest request, ListDeviceResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mListDeviceLambda = factory.build(ListDeviceLambdaHandler.class);
                    ListDeviceLambdaResponse response = mListDeviceLambda.Android_Management_Device(request);
                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException exception) {
                    ListDeviceLambdaResponse resp = new ListDeviceLambdaResponse();
                    resp.setBody(new ArrayList<>());
                    resp.setStatusCode(400);
                    returnCallback = () -> {
                        callback.onSuccess(resp);
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }


    public void ExecuteCreateEnterpriseLambda( CreateEnterpriseResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mCreateEnterpriseLambda = factory.build(CreateEnterpriseLambdaHandler.class);
                    CreateEnterpriseLambdaResponse response = mCreateEnterpriseLambda.Create_Enterprises_API();
                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteDeleteEnterpriseLambda(DeleteEnterpriseLambdaRequest request, DeleteEnterpriseResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mDeleteEnterpriseLambda = factory.build(DeleteEnterpriseLambdaHandler.class);
                    DeleteEnterpriseLambdaResponse response = mDeleteEnterpriseLambda.Soft_delete_enterprise(request);
                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteUndoEnterpriseDeleteLambda(UndoEnterpriseDeleteLambdaRequest request, UndoEnterpriseDeleteResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    undoEnterpriseDelete = factory.build(UndoDeleteEnterprise.class);
                    UndoEnterpriseDeleteResponse response = undoEnterpriseDelete.undo_soft_delete_enterprise(request);
                    Log.i("undoEnterpriseDelete", "Response : " + new Gson().toJson(response));
                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteDeleteDeviceLambda(JsonObject request, DeleteDeviceResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    deleteDeviceLambda = factory.build(DeleteDeviceLambdaHandler.class);
                    JsonObject response = deleteDeviceLambda.Android_Management_Device(request);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (AmazonClientException exception) {
                    returnCallback = () -> {
                        if(exception.getMessage() != null) {
                            callback.onError(exception.getMessage());
                        }
                        else {
                            callback.onError("Internet issue");
                        }
                    };
                }
                catch (JsonSyntaxException exception) {
                    returnCallback = () -> {
                        if(exception.getMessage() != null) {
                            callback.onError(exception.getMessage());
                        }
                        else {
                            callback.onError("Internet issue");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecutePatchDeviceLambda(JsonObject request, PatchDeviceLambdaResponse callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    patchDeviceLambda = factory.build(PatchDeviceLambda.class);
                    JsonObject response = patchDeviceLambda.Android_Management_Device(request);
                    Log.i("patchDevice", new Gson().toJson(response));
                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecutePatchEnterpriseLambda(JsonObject request, PatchEnterpriseLambdaResponse callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    patchEnterpriseLambda = factory.build(PatchEnterpriseLambda.class);
                    JsonObject response = patchEnterpriseLambda.Android_Management_Device(request);
                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        } else {
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteManagementLambda(ManagementLambdaRequest request, ListIotStateDistrictCityResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mManagementLambda = factory.build(ManagementLambda.class);
                    ListIotStateDistrictCityLambdaResponse response = new ListIotStateDistrictCityLambdaResponse();
                    JsonObject res = mManagementLambda.Enterprise_Crud_Iot_ComplexTree(request);
                    Log.i("jsonRes", res.toString());

                    returnCallback = () -> {
                        Log.i("manage_res", new Gson().toJson(res));
                        callback.onSuccess(res);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("manage_res", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("manage_res", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteManagementLambda(RequestManagementLambda request, ListIotStateDistrictCityResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mManagementLambda = factory.build(ManagementLambda.class);
                    ListIotStateDistrictCityLambdaResponse response = new ListIotStateDistrictCityLambdaResponse();
                    JsonObject res = mManagementLambda.Enterprise_Crud_Iot_ComplexTree(request);
                    Log.i("jsonRes", res.toString());

                    returnCallback = () -> {
                        Log.i("manage_res", new Gson().toJson(res));
                        callback.onSuccess(res);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("manage_res", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("manage_res", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteManagementLambda(RegisterComplexPayload request, ListIotStateDistrictCityResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mManagementLambda = factory.build(ManagementLambda.class);
                    ListIotStateDistrictCityLambdaResponse response = new ListIotStateDistrictCityLambdaResponse();
                    JsonObject res = mManagementLambda.Enterprise_Crud_Iot_ComplexTree(request);
                    Log.i("jsonRes", res.toString());

                    returnCallback = () -> {
                        Log.i("manage_res", new Gson().toJson(res));
                        callback.onSuccess(res);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("manage_res", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("manage_res", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteIotCabinLambda(ManagementLambdaRequest request, ListIotStateDistrictCityResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mManagementLambda = factory.build(ManagementLambda.class);
                    ListIotStateDistrictCityLambdaResponse response = new ListIotStateDistrictCityLambdaResponse();
                    JsonObject res = mManagementLambda.Enterprise_Crud_Iot_Cabin(request);
                    Log.i("jsonRes", res.toString());

                    returnCallback = () -> {
                        Log.i("manage_res", new Gson().toJson(res));
                        callback.onSuccess(res);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("manage_res", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("manage_res", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }

    public void ExecuteIotCabinLambda(RegisterCabinPayload request, ListIotStateDistrictCityResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mManagementLambda = factory.build(ManagementLambda.class);
                    ListIotStateDistrictCityLambdaResponse response = new ListIotStateDistrictCityLambdaResponse();
                    JsonObject res = mManagementLambda.Enterprise_Crud_Iot_Cabin(request);
                    Log.i("jsonRes", res.toString());

                    returnCallback = () -> {
                        Log.i("manage_res", new Gson().toJson(res));
                        callback.onSuccess(res);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("manage_res", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("manage_res", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }

    public void ExecuteIotCabinLambda(JsonObject request, ListIotStateDistrictCityResponseHandler callback) {
        String TAG = "userTypeListCallback";
        Log.d(TAG, "ExecuteIotCabinLambda() called with: request = [" + request + "]");
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    if(mManagementLambda == null) {
                        mManagementLambda = factory.build(ManagementLambda.class);
                    }
                    JsonObject res = mManagementLambda.Enterprise_Crud_Iot_Cabin(request);
                    Log.i(TAG, res.toString());

                    returnCallback = () -> {
                        Log.i(TAG, "Returning payload: " + new Gson().toJson(res));
                        callback.onSuccess(res);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i(TAG, new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i(TAG, "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }
    public void ExecuteManagementLambda(UpdateComplexPayload request, ListIotStateDistrictCityResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mManagementLambda = factory.build(ManagementLambda.class);
                    ListIotStateDistrictCityLambdaResponse response = new ListIotStateDistrictCityLambdaResponse();
                    JsonObject res = mManagementLambda.Enterprise_Crud_Iot_ComplexTree(request);
                    Log.i("jsonRes", res.toString());

                    returnCallback = () -> {
                        Log.i("manage_res", new Gson().toJson(res));
                        callback.onSuccess(res);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("manage_res", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("manage_res", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }
    public void ExecuteListPolicyLambda(ListPolicyLambdaRequest request, ListPolicyLambdaResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    mListPolicyLambda = factory.build(ListPolicyLambda.class);
                    ListPolicyLambdaResponse response = mListPolicyLambda.Enterprises_List_Policies_Android_Management(request);
                    Log.i("listPolicy", new Gson().toJson(response) );

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("listPolicy", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("listPolicy", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException exception) {

                    Log.i("jsonSyntax", Objects.requireNonNull(exception.getMessage()));
                    returnCallback = () -> {
                      callback.onError("Json Syntax Error");
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }
    public void ExecuteCreateQrLambda(CreateQrRequest request, CreateQrResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    createQrLambda = factory.build(CreateQrLambda.class);
                    CreateQrResponse response = createQrLambda.Create_QR_API(request);
                    Log.i("createQr", new Gson().toJson(response) );

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("listPolicy", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("listPolicy", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }
    public void ExecuteDynamoDbDataWriter(JsonObject request, DynamoDbDataWriterResponseCallback callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    dynamoDbDataWriter = factory.build(DynamoDbDataWriter.class);
                    JsonObject response = dynamoDbDataWriter.Enrollment_device_crud_details(request);
                    Log.i("dynamoDbDataWriter", new Gson().toJson(response) );

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("listPolicy", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("listPolicy", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }
    public void ExecuteCreatePolicyLambda(JsonObject request, CreatePolicyResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("CreatePolicyLambda", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    createPolicyLambdaHandler = factory.build(CreatePolicyLambdaHandler.class);
                    JsonObject response = createPolicyLambdaHandler.Enterprises_Create_Policies_Android_Management(request);
                    Log.i("createNewPolicy", response.toString());

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("listPolicy", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("listPolicy", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }

    public void ExecuteVerifyPackageNameLambda(JsonObject request, VerifyPackageNameResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("VerifyPackageName", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    verifyPackageNameLambda = factory.build(VerifyPackageNameLambda.class);
                    JsonObject response = verifyPackageNameLambda.VerifyPackageName(request);
                    Log.i("VerifyPackageName", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("VerifyPackageName", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("VerifyPackageName", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }

    public void ExecuteDeletePolicyLambda(JsonObject request, DeletePolicyResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("deletePolicy", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    deletePolicyLambda = factory.build(DeletePolicyLambda.class);
                    JsonObject response = deletePolicyLambda.Enterprises_Delete_Policies_Android_Management(request);
                    Log.i("deletePolicy", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("deletePolicy", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("deletePolicy", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }

    public void ExecuteGetPolicyLambda(JsonObject request, GetPolicyLambdaResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("deletePolicy", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    getPolicyLambda = factory.build(GetPolicyLambda.class);
                    GetPolicyLambdaResponse response = getPolicyLambda.Enterprises_get_Policies_Android_Management(request);
                    Log.i("deletePolicy", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("deletePolicy", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("deletePolicy", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException e) {
                    returnCallback = () -> {
                      if(e.getMessage() != null) {
                          callback.onError(e.getMessage());
                      }
                      else {
                          callback.onError("Json Syntax Exception");
                      }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();

    }

    public void ExecuteShareQrLambda(JsonObject request, QrShareLambdaResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("shareQr", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    qrShareLambda = factory.build(QrShareLambda.class);
                    JsonObject response = qrShareLambda.shareQR(request);
                    Log.i("shareQr", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("shareQr", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("shareQr", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        }
                        else {
                            callback.onError("Json Syntax Exception");
                        }
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteTogglePolicyStateLambda(JsonObject request, TogglePolicyStateResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("togglePolicy", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    togglePolicyStateLambda = factory.build(TogglePolicyState.class);
                    JsonObject response = togglePolicyStateLambda.Android_Management_Device(request);
                    Log.i("togglePolicy", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("togglePolicy", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("togglePolicy", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        }
                        else {
                            callback.onError("Json Syntax Exception");
                        }
                    };
                }
                catch (AmazonClientException exception) {
                    returnCallback = () -> {
                      callback.onError("Network Issue. Please try again");
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteDeleteComplexLambda(JsonObject request, DeleteComplexResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("deleteComplex", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    deleteComplexLambda = factory.build(DeleteComplexLambda.class);
                    JsonObject response = deleteComplexLambda.Enterprise_Crud_Iot_ComplexTree(request);
                    Log.i("deleteComplex", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("deleteComplex", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("deleteComplex", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        }
                        else {
                            callback.onError("Json Syntax Exception");
                        }
                    };
                }
                catch (AmazonClientException exception) {
                    returnCallback = () -> {
                        callback.onError("Network Issue. Please try again");
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteEnterpriseDetailsLambda(JsonObject request, EnterpriseDetailsResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("enterpriseDetails", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    enterpriseDetailsLambda = factory.build(EnterpriseDetailsLambda.class);
                    JsonObject response = enterpriseDetailsLambda.Get_Enterprises_Android_Management(request);
                    Log.i("enterpriseDetails", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("enterpriseDetails", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("enterpriseDetails", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        }
                        else {
                            callback.onError("Json Syntax Exception");
                        }
                    };
                }
                catch (AmazonClientException exception) {
                    returnCallback = () -> {
                        callback.onError("Network Issue. Please try again");
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteFetchClientLogoLambda(JsonObject request, FetchClientLogoResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
                    Log.i("fetchClientLogo", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    fetchClientLogo = factory.build(FetchClientLogoLambda.class);
                    JsonObject response = fetchClientLogo.fetchClientLogo(request);
                    Log.i("fetchClientLogo", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("fetchClientLogo", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("fetchClientLogo", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        }
                        else {
                            callback.onError("Json Syntax Exception");
                        }
                    };
                }
                catch (AmazonClientException exception) {
                    returnCallback = () -> {
                        callback.onError("Network Issue. Please try again");
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    public void ExecuteFetchWifiDetailsLambda(FetchWifiDetailsResponseHandler callback) {
        new Thread( () -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if(AuthenticationClient.getCurrSession() != null) {
                try {
//                    Log.i("fetchWifiDetails", "Request: " + request);
                    LambdaInvokerFactory factory = initLambdaClient();
                    fetchWifiDetails = factory.build(FetchWifiDetails.class);
                    FetchWifiDetailsResponse response = fetchWifiDetails.fetchWifiDetails();
                    Log.i("fetchWifiDetails", "Response: "+ response);

                    returnCallback = () -> {
                        callback.onSuccess(response);
                    };
                }
                catch (LambdaFunctionException | NotAuthorizedException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            Log.i("fetchWifiDetails", new Gson().toJson(e.getMessage()));
                            callback.onError(e.getMessage());
                        } else {
                            Log.i("fetchWifiDetails", "Something went wrong");
                            callback.onError("Something went wrong");
                        }
                    };
                }
                catch (JsonSyntaxException e) {
                    returnCallback = () -> {
                        if(e.getMessage() != null) {
                            callback.onError(e.getMessage());
                        }
                        else {
                            callback.onError("Json Syntax Exception");
                        }
                    };
                }
                catch (AmazonClientException exception) {
                    returnCallback = () -> {
                        callback.onError("Network Issue. Please try again");
                    };
                }
            }
            else {
                returnCallback = () -> {
                    callback.onError("Session Invalid");
                };
            }

            handler.post(returnCallback);
        } ) .start();
    }

    private LambdaInvokerFactory initLambdaClient() {
        String idToken = AuthenticationClient.getCurrSession().getIdToken().getJWTToken();
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                identityPoolID,
                cognitoRegion
        );
        Map<String, String> logins = new HashMap<String, String>();
        logins.put(providerName, idToken);
        credentialsProvider.setLogins(logins);
        credentialsProvider.getCredentials();

        ClientConfiguration config = new ClientConfiguration();
        config.setSocketTimeout(LAMBDA_TIMEOUT);

        LambdaInvokerFactory factory = new LambdaInvokerFactory(context,
                Regions.AP_SOUTH_1, credentialsProvider, config);

        return factory;
    }

}
