package sukriti.ngo.mis.ui.administration;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

import sukriti.ngo.mis.dataModel.CognitoUser;
import sukriti.ngo.mis.dataModel.ThingGroupDetails;
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess;
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData;
import sukriti.ngo.mis.ui.administration.interfaces.CognitoUserDetailsRequestHandler;
import sukriti.ngo.mis.ui.administration.interfaces.DetailedTeamRequestHandler;
import sukriti.ngo.mis.ui.administration.interfaces.UserAccessTreeRequestHandler;
import sukriti.ngo.mis.utils.AdministrationClient;
import sukriti.ngo.mis.utils.AdministrationDbHelper;

import static java.lang.Thread.sleep;
import static sukriti.ngo.mis.utils.AWSIotProvisioningClient.POOLING_SLEEP_SHORT;

public class ViewModelHelper {
    private ThingGroupDetails thingGroupDetails;
    private boolean hasIteratedAllChildren, isIterationComplete;
    private int detailsErrCount, detailsSuccessCount, iterationIndex;
    private String TAG = "ViewModelHelper";

    public void getCognitoDetails(final Context context, final List<MemberDetailsData> list, final AdministrationClient administrationClient, final DetailedTeamRequestHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable returnCallback;

                if (list.size() > 0) {
                    hasIteratedAllChildren = false;
                    detailsErrCount = 0;
                    detailsSuccessCount = 0;
                    iterationIndex = 0;
                    while (!hasIteratedAllChildren) {
                        isIterationComplete = false;
                        Log.d(TAG, "GroupName " + list.get(iterationIndex).team.getMember());
                        administrationClient.getUserDetails(context, list.get(iterationIndex).team.getMember(), new CognitoUserDetailsRequestHandler() {
                            @Override
                            public void onSuccess(CognitoUser user) {
                                detailsSuccessCount++;
                                isIterationComplete = true;
                                list.get(iterationIndex).cognitoUser = user;
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

                returnCallback = new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(list);
                    }
                };


                handler.post(returnCallback);
            }
        }).start();
    }

    public void getAccessTree(final Context context, final List<MemberDetailsData> list, final AdministrationDbHelper misDynamoDbClient, final DetailedTeamRequestHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable returnCallback;

                if (list.size() > 0) {
                    hasIteratedAllChildren = false;
                    detailsErrCount = 0;
                    detailsSuccessCount = 0;
                    iterationIndex = 0;
                    while (!hasIteratedAllChildren) {
                        isIterationComplete = false;
                        Log.d(TAG, "GroupName " + list.get(iterationIndex).team.getMember());

                        misDynamoDbClient.getUserAccessTree(list.get(iterationIndex).team.getMember(), new UserAccessTreeRequestHandler() {
                            @Override
                            public void onSuccess(UserAccess userAccess) {
                                detailsSuccessCount++;
                                isIterationComplete = true;
                                if(userAccess == null) {
                                    //User Access Not Defined
                                }else {
                                    list.get(iterationIndex).userAccess = userAccess.getPermissions().getCountry();
                                }

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

                returnCallback = new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(list);
                    }
                };


                handler.post(returnCallback);
            }
        }).start();
    }
}
