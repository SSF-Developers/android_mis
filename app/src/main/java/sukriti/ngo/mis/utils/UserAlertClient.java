package sukriti.ngo.mis.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import sukriti.ngo.mis.communication.SimpleHandler;
import sukriti.ngo.mis.interfaces.DialogActionHandler;
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler;
import sukriti.ngo.mis.interfaces.NavigationHandler;
import sukriti.ngo.mis.interfaces.SimpleSelectionHandler;

public class UserAlertClient {
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private LoadingScreen loadingScreen;
    private final String TAG = "UserAlertClient";
    private Activity activityRef;
    private Lifecycle lifecycle = null;


    public UserAlertClient(Activity activity) {
        activityRef = activity;
    }


    //FragmentIllegalState-Fix
    public UserAlertClient(Activity activity, Lifecycle lifecycle) {
        activityRef = activity;
        this.lifecycle = lifecycle;
        this.lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if(event == Lifecycle.Event.ON_PAUSE) {
                    if(userDialog != null)
                        userDialog.dismiss();
                    if(waitDialog != null) {
                        Log.e("_illegalState","closed!!");
                            waitDialog.dismiss();
                    }
                }
            }
        });
//        Log.e("_illegalState","User Alert Client : "+lifecycle.getCurrentState());
    }

    public void showWaitScreen(String message) {
        closeWaitScreen();
        loadingScreen = new LoadingScreen(activityRef, message);
        loadingScreen.show();
    }

    public void closeWaitScreen() {
        try {
            loadingScreen.dismiss();
        } catch (Exception e) {
            //
        }
    }

    public void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(activityRef);
        waitDialog.setTitle(message);
        waitDialog.setCancelable(false);
        waitDialog.show();
    }

    //With Should Exit
    public void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(activityRef);
        builder.setTitle(title).setMessage(body).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exit)
                        activityRef.onBackPressed();

                } catch (Exception e) {
                    // Log failure
                    Log.e(TAG, " -- Dialog dismiss failed");

                }
            }
        });
        if (exit)
            builder.setCancelable(false);
        userDialog = builder.create();
        userDialog.show();
    }

    //Optional Fragment Navigation
    public void showDialogMessage(String title, String body, final int navigateTo, final NavigationHandler navigationHandler) {
        final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(activityRef);
        builder
                .setTitle(title)
                .setMessage(body)
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            userDialog.dismiss();
                            activityRef.onBackPressed();
                        } catch (Exception e) {
                            // Log failure
                            Log.e(TAG, " -- Dialog dismiss failed");

                        }
                    }
                })
                .setPositiveButton("Define", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            userDialog.dismiss();
                            navigationHandler.navigateTo(navigateTo);

                        } catch (Exception e) {
                            // Log failure
                            Log.e(TAG, " -- Dialog dismiss failed");

                        }
                    }
                });
        builder.setCancelable(false);
        userDialog = builder.create();
        userDialog.show();
    }

    //With Intent Navigation
    public void showDialogMessage(String title, String body, final Intent intent) {
        final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(activityRef);
        builder.setTitle(title).setMessage(body).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    activityRef.startActivity(intent);
                    activityRef.finish();

                } catch (Exception e) {
                    // Log failure
                    Log.e(TAG, " -- Dialog dismiss failed");

                }
            }
        });
        builder.setCancelable(false);
        userDialog = builder.create();
        userDialog.show();
    }

    //With Callback
    public void showDialogMessage(String title, String body, DialogActionHandler dialogActionHandler) {
        final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(activityRef);
        builder.setTitle(title)
                .setMessage(body)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogActionHandler.onPositiveAction();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogActionHandler.onNegativeAction();
                    }
                });
        builder.setCancelable(false);
        userDialog = builder.create();
        userDialog.show();
    }

    //With Callback
    public void showDialogMessage(String title, String body, DialogSingleActionHandler dialogActionHandler) {
        final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(activityRef);
        builder.setTitle(title)
                .setMessage(body)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogActionHandler.onAction();
                    }
                });
        builder.setCancelable(false);
        userDialog = builder.create();
        userDialog.show();
    }

    public void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    public void showSingleSelectionDialog(String title, String selection, String[] items, SimpleSelectionHandler callback) {
        AlertDialog.Builder alertDialog = new MaterialAlertDialogBuilder(activityRef);
        alertDialog.setTitle(title);

        int checkedItem = -1;
        int index = 0;
        for (String item : items) {
            if (item.compareToIgnoreCase(selection) == 0)
                checkedItem = index;
            index++;
        }
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onSelect(items[which]);
                userDialog.dismiss();
            }
        });
        userDialog = alertDialog.create();
        userDialog.setCanceledOnTouchOutside(true);
        userDialog.show();
    }

    public void showConfirmActionDialog(FragmentManager fragmentManager,String title,String description, String actionName, SimpleHandler callback ) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("ConfirmAction");
        if (prev != null) {
            transaction.remove(prev);
        }
        transaction.addToBackStack(null);
        ConfirmAction confirmActionFragment = ConfirmAction.newInstance();
        confirmActionFragment.setUp(title, description, actionName);
        confirmActionFragment.show(transaction, "ConfirmAction");
        confirmActionFragment.setListener(callback);
    }

}
