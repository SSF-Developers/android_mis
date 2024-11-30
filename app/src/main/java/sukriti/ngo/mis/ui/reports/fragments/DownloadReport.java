package sukriti.ngo.mis.ui.reports.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.databinding.FragmentDownloadReportBinding;
import sukriti.ngo.mis.interfaces.NavigationHandler;
import sukriti.ngo.mis.ui.profile.fragments.ProfileDataLambdaRequest;
import sukriti.ngo.mis.ui.reports.ReportsViewModel;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DeleteScheduleReportUserLambdaResult;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DeleteScheduledReportUserLambdaResultHandler;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DownloadReportLambdaResult;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DownloadReportRequest;
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DownloadReportResultHandler;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.UserAlertClient;



public class DownloadReport extends Fragment {
    ReportsViewModel viewModel;
    FragmentDownloadReportBinding binding;
    DatePickerDialog scheduleStartDatePickerDialog;
    DatePickerDialog scheduleEndDatePickerDialog;
    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;
    Resources resources;
    NavigationHandler navigationHandler;
    Long startDateEpoch, endDateEpoch, scheduleStartDateEpoch, scheduleEndDateEpoch;
    UserAlertClient userAlertClient;
    SharedPrefsClient sharedPrefsClient;
    LambdaClient lambdaClient;

    private static DownloadReport Instance = null;

    private DownloadReport() {
        // Required empty public constructor
    }

    public static DownloadReport getInstance() {
        if(Instance == null) {
            Instance = new DownloadReport();
        }

        return Instance;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDownloadReportBinding.inflate(getLayoutInflater());
        init();
        return binding.getRoot();
    }

    public void init() {
        viewModel = ViewModelProviders.of(this).get(ReportsViewModel.class);
        binding.scheduleReportLayout.spinner.setEnabled(false);
        resources = requireContext().getResources();
        sharedPrefsClient = new SharedPrefsClient(requireContext());
        lambdaClient = new LambdaClient(requireContext());
        checkPermissions();
        initiateScheduleStartDatePicker();
        initiateScheduleEndDatePicker();
        initiateStartDatePicker();
        initiateEndDatePicker();
        userAlertClient = new UserAlertClient(requireActivity());
        setClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Lambdas
    public void executeDownloadLambda() {
        userAlertClient.showWaitDialog("Getting data...");
        DownloadReportRequest request = new DownloadReportRequest();
        request.setBwtStats(binding.selectPastDateLayout.bwtCheckBox.isChecked());
        request.setUsageStats(binding.selectPastDateLayout.usageCheckBox.isChecked());
        request.setCollectionStats(binding.selectPastDateLayout.collectionCheckBox.isChecked());
        request.setFeedbackStats(binding.selectPastDateLayout.feedbackCheckBox.isChecked());
        request.setUpiStats(binding.selectPastDateLayout.upiCheckBox.isChecked());
        request.setStartDate(startDateEpoch);
        request.setEndDate(endDateEpoch);

        request.setUserName(sharedPrefsClient.getUserDetails().getUser().getUserName());
        request.setClientName(sharedPrefsClient.getUserDetails().getOrganisation().getClient());
        Log.i("complexList2", viewModel.getComplexList().toString());
        ArrayList<String> list = viewModel.getListOfComplex(sharedPrefsClient.getSelectionTree());
        if(list.size() == 0) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            builder.setMessage("Please select a unit");
            return;
        }
        request.setComplex(viewModel.getListOfComplex(sharedPrefsClient.getSelectionTree()));

        if(binding.scheduleReportLayout.radioButtonYes.isChecked()) {
            request.setEmail(binding.selectPastDateLayout.emailEditText.getText().toString());
            request.setSchedule(true);
            request.setRateValue(binding.scheduleReportLayout.rateEditText.getText().toString());
            request.setRateUnit("Days");
            request.setScheduleDuration(binding.scheduleReportLayout.spinner.getSelectedItem().toString());
            request.setScheduleStartDate(scheduleStartDateEpoch);
            request.setScheduleEndDate(scheduleEndDateEpoch);
        }

        Log.i("date", new Gson().toJson(request));
        DownloadReportResultHandler callback = new DownloadReportResultHandler() {
            @Override
            public void onSuccess(@NonNull DownloadReportLambdaResult result) {
                Log.i("downloadReportResult", "Success");
                userAlertClient.closeWaitDialog();
                if(result.getBody().getMessage().equals("User Already exist")) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                    builder.setTitle("User Already Exists");
                    builder.setMessage("Do you want to delete user?");
                    builder.setNegativeButton("No", (dialogInterface, i) -> {

                    });

                    builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        userAlertClient.showWaitDialog("Deleting User");
                        deleteScheduledUser();
                    });

                    builder.show();
                }
                else {
                    Executor executor = Executors.newCachedThreadPool();

                    Runnable runnable = () -> downloadAndSaveReport(result.getBody().getLink());

                    userAlertClient.showWaitDialog("Saving report to device");
                    executor.execute(runnable);
                    Log.i("downloadReportResult", new Gson().toJson(result));
                }

            }

            @Override
            public void onError(@NonNull String message) {
                Log.i("downloadReportResult", "Error " + message);
                userAlertClient.closeWaitDialog();
                userAlertClient.showDialogMessage("Error", message, false);
            }
        };
        lambdaClient.ExecuteDownloadReportLambda(request, callback);
    }

    private void deleteScheduledUser() {
        ProfileDataLambdaRequest profileDataLambdaRequest = new ProfileDataLambdaRequest("actionDeleteUserScheduler", sharedPrefsClient.getUserDetails().getUser().getUserName());

        lambdaClient.ExecuteDeleteScheduledReportUserLambda(profileDataLambdaRequest, new DeleteScheduledReportUserLambdaResultHandler() {
            @Override
            public void onSuccess(@NonNull DeleteScheduleReportUserLambdaResult result) {
                userAlertClient.closeWaitDialog();
                if(result.getMessage().equals("User deleted")) {
                    userAlertClient.showDialogMessage("User Deleted", "User Deleted Successfully", false);
                }
            }

            @Override
            public void onError(@NonNull String message) {
                userAlertClient.showDialogMessage("Error", message, false);
            }
        });

    }

    private void downloadAndSaveReport(String link) {
            Log.i("downloadError", "downloadAndSaveReport()");
            Log.i("downloadError", "Link -> " + link);

            try {
                URL url = new URL(link);
                Log.i("downloadError", "URL Created");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.i("downloadError", "Connection opened");
                connection.connect();



                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i("downloadError", "Connection response code = HTTP_OK");
                    InputStream inputStream = connection.getInputStream();
                    Log.i("downloadError", "Input Stream created");
                    File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".pdf");
                    if(!file.exists()) {
                        boolean fileCreated = file.createNewFile();
                        Log.i("downloadError", "File Created? " + fileCreated);
                    }
                    FileOutputStream outputStream = new FileOutputStream(file);
//                    FileOutputStream outputStream = new FileOutputStream("/Internal storage/Download/Report.pdf");
                    Log.i("downloadError", "Output Stream created");

                    byte[] buffer = new byte[1024];
                    int length;
                    Log.i("downloadError", "Starting file write");
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer,0,length);
                    }
                    Log.i("downloadError", "File written successfully");

                    outputStream.flush();
                    Log.i("downloadError", "output Stream flushed");
                    outputStream.close();
                    Log.i("downloadError", "output Stream closed");
                    inputStream.close();
                    Log.i("downloadError", "Input Stream closed");
                } else {
                    Log.i("downloadError", "Connection response !HTTP_OK");
                }

                userAlertClient.closeWaitDialog();
                connection.disconnect();
                Log.i("downloadError", "Connection disconnected");

            } catch (IOException e) {
                System.out.println(e.getMessage());
                userAlertClient.closeWaitDialog();
                userAlertClient.showDialogMessage("Error", e.getMessage(), false);
            }
        }

    // Data validation methods
    public boolean checkDownloadSectionData() {
        boolean valid;
        // If Date is not selected
        if (binding.selectPastDateLayout.startDatePickerButton.getText().toString().isEmpty()) {
            binding.selectPastDateLayout.startDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            binding.selectPastDateLayout.startDateErrorTV.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.selectPastDateLayout.startDateErrorTV.setVisibility(View.GONE);
        }

        if (binding.selectPastDateLayout.endDatePickerButton.getText().toString().isEmpty()) {
            binding.selectPastDateLayout.endDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            binding.selectPastDateLayout.endDateErrorTV.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.selectPastDateLayout.endDateErrorTV.setVisibility(View.GONE);
        }

        // If not at-least one of the checkbox is checked
        if(!binding.selectPastDateLayout.bwtCheckBox.isChecked() &&
                !binding.selectPastDateLayout.usageCheckBox.isChecked() &&
                !binding.selectPastDateLayout.feedbackCheckBox.isChecked() &&
                !binding.selectPastDateLayout.upiCheckBox.isChecked() &&
                !binding.selectPastDateLayout.collectionCheckBox.isChecked()
        ) {
            binding.selectPastDateLayout.statsErrorTV.setText(getString(R.string.pleaseSelectAtLeastOneOfTheStatus));
            binding.selectPastDateLayout.statsErrorTV.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.selectPastDateLayout.statsErrorTV.setVisibility(View.GONE);
        }

        // If no start date and end date is selected
        if(startDateEpoch == null && endDateEpoch == null) {
            binding.selectPastDateLayout.startDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            binding.selectPastDateLayout.endDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            return false;
        }
        // If start date is not selected
        else if(startDateEpoch == null) {
            binding.selectPastDateLayout.startDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            return false;
        }
        // If end date is not selected
        else if (endDateEpoch == null) {
            binding.selectPastDateLayout.endDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            return false;
        }

        else {
            Instant instant1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                instant1 = Instant.ofEpochMilli(startDateEpoch);
                Instant instant2 = Instant.ofEpochMilli(endDateEpoch);
                Log.i("date", "date " + instant2.isBefore(instant1));
                valid = instant1.isBefore(instant2);
                if(!valid) {
                    binding.selectPastDateLayout.startDateErrorTV.setText(getString(R.string.chooseAValidDate));
                    binding.selectPastDateLayout.startDateErrorTV.setVisibility(View.VISIBLE);
                    return false;
                } else {
                    binding.selectPastDateLayout.startDateErrorTV.setVisibility(View.GONE);
                }
            }
        }

       return true;

    }

    public boolean checkScheduleSectionData() {
        boolean valid;

        if(binding.scheduleReportLayout.rateEditText.getText().toString().isEmpty() ||
                binding.scheduleReportLayout.rateEditText.getText().toString().equals("0")
        ) {
            binding.scheduleReportLayout.rateErrorTV.setText(getString(R.string.pleaseEnterAValidRate));
            binding.scheduleReportLayout.rateErrorTV.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.scheduleReportLayout.rateErrorTV.setVisibility(View.GONE);
        }



        if(binding.scheduleReportLayout.scheduleStartDatePickerButton.getText().toString().isEmpty()) {
            binding.scheduleReportLayout.startDateErrorTV.setText(getString(R.string.chooseAValidDate));
            binding.scheduleReportLayout.startDateErrorTV.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.scheduleReportLayout.startDateErrorTV.setVisibility(View.GONE);
        }

        if(binding.scheduleReportLayout.scheduleEndDatePickerButton.getText().toString().isEmpty()) {
            binding.scheduleReportLayout.endDateErrorTV.setText(getString(R.string.chooseAValidDate));
            binding.scheduleReportLayout.endDateErrorTV.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.scheduleReportLayout.endDateErrorTV.setVisibility(View.GONE);
        }

        if (binding.selectPastDateLayout.emailEditText.getText().toString().isEmpty()) {
            binding.selectPastDateLayout.emailErrorTV.setText(getString(R.string.emailRequired));
            binding.selectPastDateLayout.emailErrorTV.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.selectPastDateLayout.emailErrorTV.setVisibility(View.GONE);
        }

        if(scheduleStartDateEpoch == null && scheduleEndDateEpoch == null) {
            binding.scheduleReportLayout.startDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            binding.scheduleReportLayout.endDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            return false;
        }
        else if(startDateEpoch == null) {
            binding.scheduleReportLayout.startDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            return false;
        }
        else if (endDateEpoch == null) {
            binding.scheduleReportLayout.endDateErrorTV.setText(getString(R.string.pleaseSelectADate));
            return false;
        }
        else {
            Instant instant1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                instant1 = Instant.ofEpochMilli(scheduleStartDateEpoch);
                Instant instant2 = Instant.ofEpochMilli(scheduleEndDateEpoch);

                Log.i("date", "Schedule date " + instant2.isBefore(instant1));
                valid = instant2.isAfter(instant1);
                if(!valid) {
                    binding.scheduleReportLayout.startDateErrorTV.setText(getString(R.string.chooseAValidDate));
                    binding.scheduleReportLayout.startDateErrorTV.setVisibility(View.VISIBLE);
                    return false;
                } else {
                    binding.selectPastDateLayout.startDateErrorTV.setVisibility(View.GONE);
                }
            }
        }

        return true;
    }

    // initializers
    public void initiateScheduleStartDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            String strDay;
            if(day < 10) {
                strDay = "0"+day;
            } else {
                strDay = String.valueOf(day);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            Log.i("date", "scheduleStartDateEpoch -> " + calendar.getTimeInMillis());
            Log.i("date", "scheduleStartDate -> " + calendar.getTime());
            scheduleStartDateEpoch = calendar.getTimeInMillis();
            String date = getMonth(month+1) + " " + strDay + " " + year;
            binding.scheduleReportLayout.scheduleStartDatePickerButton.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        scheduleStartDatePickerDialog = new DatePickerDialog(requireContext(), AlertDialog.THEME_HOLO_DARK, dateSetListener, year, month, day);
        calendar.set(year, month, day+1);
        scheduleStartDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
    }

    public void initiateScheduleEndDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            String strDay;
            if(day < 10) {
                strDay = "0"+day;
            } else {
                strDay = String.valueOf(day);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            Log.i("date", "scheduleEndDateEpoch -> " + calendar.getTimeInMillis());
            Log.i("date", "scheduleEndDate -> " + calendar.getTime());
            scheduleEndDateEpoch = calendar.getTimeInMillis();
            String date = getMonth(month+1) + " " + strDay + " " + year;
            binding.scheduleReportLayout.scheduleEndDatePickerButton.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        scheduleEndDatePickerDialog = new DatePickerDialog(requireContext(), AlertDialog.THEME_HOLO_DARK, dateSetListener, year, month, day);
        calendar.set(year, month, day+1);
        scheduleEndDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
    }

    public void initiateStartDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            String strDay;
            if(day < 10) {
                strDay = "0"+day;
            } else {
                strDay = String.valueOf(day);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            Log.i("date", "startDateEpoch -> " + calendar.getTimeInMillis());
            Log.i("date", "startDate -> " + calendar.getTime());
            startDateEpoch = calendar.getTimeInMillis();
            String date = getMonth(month+1) + " " + strDay + " " + year;
            binding.selectPastDateLayout.startDatePickerButton.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        startDatePickerDialog = new DatePickerDialog(requireContext(), AlertDialog.THEME_HOLO_DARK, dateSetListener, year, month, day);
        startDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    public void initiateEndDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            String strDay;
            if(day < 10) {
                strDay = "0"+day;
            } else {
                strDay = String.valueOf(day);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            endDateEpoch = calendar.getTimeInMillis();
            Log.i("date", "endDateEpoch -> " + calendar.getTimeInMillis());
            Log.i("date", "endDate -> " + calendar.getTime());
            String date = getMonth(month+1) + " " + strDay + " " + year;
            binding.selectPastDateLayout.endDatePickerButton.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        endDatePickerDialog = new DatePickerDialog(requireContext(), AlertDialog.THEME_HOLO_DARK, dateSetListener, year, month, day);
        endDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    // Utility Methods
    private void setClickListeners() {
        binding.scheduleReportLayout.scheduleStartDatePickerButton.setOnClickListener(view -> showScheduleStartDatePickerDialog());

        binding.scheduleReportLayout.scheduleEndDatePickerButton.setOnClickListener(view -> showScheduleEndDatePickerDialog());

        binding.selectPastDateLayout.startDatePickerButton.setOnClickListener(view -> showStartDatePickerDialog());

        binding.selectPastDateLayout.endDatePickerButton.setOnClickListener(view -> showEndDatePickerDialog());

        binding.scheduleReportLayout.radioButtonNo.setOnClickListener(view -> {
            Log.i("details", "No Button clicked");
            binding.scheduleReportLayout.rateTV.setTextColor(Color.GRAY);
            binding.scheduleReportLayout.rateEditText.setEnabled(false);
            binding.scheduleReportLayout.rateEditText.setHintTextColor(Color.GRAY);
            binding.scheduleReportLayout.reportDurationTV.setTextColor(Color.GRAY);
            binding.scheduleReportLayout.spinner.setEnabled(false);
            binding.scheduleReportLayout.scheduleStartDate.setTextColor(Color.GRAY);
            binding.scheduleReportLayout.scheduleStartDatePickerButton.setEnabled(false);
            binding.scheduleReportLayout.scheduleStartDatePickerButton.setTextColor(Color.GRAY);
            binding.scheduleReportLayout.scheduleEndDate.setTextColor(Color.GRAY);
            binding.scheduleReportLayout.scheduleEndDatePickerButton.setEnabled(false);
            binding.scheduleReportLayout.scheduleEndDatePickerButton.setTextColor(Color.GRAY);
        });

        binding.scheduleReportLayout.radioButtonYes.setOnClickListener(view -> {
            Log.i("details", "No Button clicked");
            binding.scheduleReportLayout.rateTV.setTextColor(Color.BLACK);
            binding.scheduleReportLayout.rateEditText.setEnabled(true);
            binding.scheduleReportLayout.rateEditText.setHintTextColor(Color.BLACK);
            binding.scheduleReportLayout.reportDurationTV.setTextColor(Color.BLACK);
            binding.scheduleReportLayout.spinner.setEnabled(true);
            binding.scheduleReportLayout.scheduleStartDate.setTextColor(Color.BLACK);
            binding.scheduleReportLayout.scheduleStartDatePickerButton.setEnabled(true);
            binding.scheduleReportLayout.scheduleStartDatePickerButton.setTextColor(Color.BLACK);
            binding.scheduleReportLayout.scheduleEndDatePickerButton.setEnabled(true);
            binding.scheduleReportLayout.scheduleEndDatePickerButton.setTextColor(Color.BLACK);
            binding.scheduleReportLayout.scheduleEndDate.setTextColor(Color.BLACK);
            binding.scheduleReportLayout.rateEditText.setTextColor(Color.BLACK);
        });

        binding.scheduleReportLayout.rateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.scheduleReportLayout.scheduleReportDetails.setText(resources.getString(R.string.scheduleDetails, editable.toString()));
            }
        });

        binding.download.setOnClickListener(view -> {
            if(isDataValid()) {
                executeDownloadLambda();
            }
        });

    }

    private void showScheduleStartDatePickerDialog() {
        scheduleStartDatePickerDialog.show();
    }

    private void showScheduleEndDatePickerDialog() {
        scheduleEndDatePickerDialog.show();
    }

    private void showStartDatePickerDialog() {
        startDatePickerDialog.show();
    }

    private void showEndDatePickerDialog() {
        endDatePickerDialog.show();
    }

    public void setNavigationHandler(NavigationHandler handler) {
        navigationHandler = handler;
    }

    public String getMonth(int n) {
        switch(n) {
            case 1:
                return "JAN";

            case 2:
                return "FEB";


            case 3:
                return "MAR";


            case 4:
                return "APR";


            case 5:
                return "MAY";


            case 6:
                return "JUN";


            case 7:
                return "JUL";


            case 8:
                return "AUG";


            case 9:
                return "SEP";


            case 10:
                return "OCT";


            case 11:
                return "NOV";


            case 12:
                return "DEC";
        }

        return "";
    }

    public boolean isDataValid() {

        if(checkDownloadSectionData()) {
            if(binding.scheduleReportLayout.radioButtonYes.isChecked()) {
                return checkScheduleSectionData();
            } else {
                return true;
            }
        }
        return false;
    }

    public void checkPermissions() {
        if(! (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100) {
            if(grantResults.length > 0) {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("downloadError", "Permission Granted");
                } else {
                    checkPermissions();
                }
            }
        }
    }

}