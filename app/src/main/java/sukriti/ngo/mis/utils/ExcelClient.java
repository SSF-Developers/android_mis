package sukriti.ngo.mis.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

//import com.opencsv.CSVWriter;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.repository.data.DailyUsageCount;
import sukriti.ngo.mis.ui.complexes.data.DisplayBwtProfile;
import sukriti.ngo.mis.ui.complexes.data.DisplayResetProfile;
import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;
import sukriti.ngo.mis.ui.reports.data.UsageReportRawData;

public class ExcelClient {

    private static final String rowBreak = "\r\n";

    public static String usageProfileToCsv(List<DisplayUsageProfile> gridData, List<String> titles) {
        String data = "";
        for (String item : titles)
            data += item + ",";
        data += rowBreak;

        for (DisplayUsageProfile item : gridData) {
            data += item.getDate() + ",";
            data += item.getTime() + ",";
            data += Nomenclature.getCabinType(item.getUsageProfile().getSHORT_THING_NAME()) + ",";
            data += item.getUsageProfile().getDuration() + ",";
            data += item.getUsageProfile().getAmountcollected() + ",";
            data += item.getUsageProfile().getFeedback() + ",";
            data += item.getUsageProfile().getEntrytype() + ",";
            data += item.getUsageProfile().getAirdryer() + ",";
            data += item.getUsageProfile().getFantime() + ",";
            data += item.getUsageProfile().getFloorclean() + ",";
            data += item.getUsageProfile().getFullflush() + ",";
            data += item.getUsageProfile().getManualflush() + ",";
            data += item.getUsageProfile().getLighttime() + ",";
            data += item.getUsageProfile().getMiniflush() + ",";
            data += item.getUsageProfile().getPreflush() + ",";
            data += item.getUsageProfile().getRFID() + ",";
            data += item.getUsageProfile().getCLIENT() + ",";
            data += item.getUsageProfile().getCOMPLEX() + ",";
            data += item.getUsageProfile().getSTATE() + ",";
            data += item.getUsageProfile().getDISTRICT() + ",";
            data += item.getUsageProfile().getCITY() + ",";
            data += rowBreak;
        }

        return data;
    }

    public static String usageReportToCsv(List<UsageReportData> gridData, List<String> titles) {
        String data = "";
        for (String item : titles)
            data += item + ",";
        data += rowBreak;

        for (UsageReportData item : gridData) {
            List<UsageReportRawData> fileItem = Utilities.getUsageReportRawData(item);
            for (UsageReportRawData rowItem : fileItem) {
                data += item.getComplex().getName() + ",";
                data += rowItem.getCabinType() + ",";
                data += rowItem.getCabinCount() + ",";
                data += rowItem.getUsage() + ",";
                data += rowItem.getFeedback() + ",";
                data += rowItem.getCollection() + ",";
                data += rowBreak;
            }
        }

        return data;
    }

    public static String dateDataToCsv(List<UsageReportData> gridData, List<String> titles) {
        String data = "";
        for (String item : titles)
            data += item + ",";
        data += rowBreak;

        int dateIndex = 0;
        for (UsageReportData complexItem : gridData) {
            dateIndex = 0;
            for (DailyUsageCount rowItem : complexItem.getUsageComparison().getFemale()) {

                data += complexItem.getComplex().getName()+",";

                data += complexItem.getUsageComparison().getMale().get(dateIndex).date + ",";

                data += complexItem.getUsageComparison().getMale().get(dateIndex).count + ",";
                data += complexItem.getCollectionComparison().getMale().get(dateIndex).amount + ",";

                data += complexItem.getUsageComparison().getFemale().get(dateIndex).count + ",";
                data += complexItem.getCollectionComparison().getFemale().get(dateIndex).amount + ",";

                data += complexItem.getUsageComparison().getPd().get(dateIndex).count + ",";
                data += complexItem.getCollectionComparison().getPd().get(dateIndex).amount + ",";

                data += complexItem.getUsageComparison().getMur().get(dateIndex).count + ",";
                data += complexItem.getCollectionComparison().getMur().get(dateIndex).amount + ",";

                data += rowBreak;
                dateIndex++;
            }
        }

        return data;
    }
    public static String bwtProfileToCsv(List<DisplayBwtProfile> gridData, List<String> titles) {
        String data = "";
        for (String item : titles)
            data += item + ",";
        data += rowBreak;


        for (DisplayBwtProfile item : gridData) {
            data += item.getDate() + ",";
            data += item.getTime() + ",";
            data += Nomenclature.getCabinType(item.getBwtProfile().getSHORT_THING_NAME()) + ",";
            data += rowBreak;
        }

        return data;
    }

    public static String resetProfileToCsv(List<DisplayResetProfile> gridData, List<String> titles) {
        String data = "";
        for (String item : titles)
            data += item + ",";
        data += rowBreak;
        for (DisplayResetProfile item : gridData) {
            data += item.getDate() + ",";
            data += item.getTime() + ",";
            data += Nomenclature.getCabinType(item.getResetProfile().getSHORT_THING_NAME()) + ",";
            data += item.getResetProfile().getUserId() + ",";
            data += item.getResetProfile().getBoardId() + ",";
            data += item.getResetProfile().getResetsource() + ",";
            data += item.getResetProfile().getCLIENT() + ",";
            data += item.getResetProfile().getCOMPLEX() + ",";
            data += item.getResetProfile().getSTATE() + ",";
            data += item.getResetProfile().getDISTRICT() + ",";
            data += item.getResetProfile().getCITY() + ",";
            data += rowBreak;
        }

        return data;
    }


    public static void downlaod(Context context, String fileName, List<DisplayUsageProfile> gridData) throws IOException {
        fileName = "test";
        File root = context.getExternalFilesDir(null);
        File dir = new File(root, "mis");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir, fileName + ".csv");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(new Gson().toJson(gridData).getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
            Log.i("_download", "" + file.getAbsolutePath());
        }
    }


    public static void email(Context context, String fileName) {

        fileName = "test";
        File root = context.getExternalFilesDir(null);
        File dir = new File(root, "mis");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir, fileName + ".csv");

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"email@example.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");

        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }

    public static void createFile(Fragment activity) {

        final int CREATE_FILE = 1;

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/excel");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.csv");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, CREATE_FILE);
    }


}
