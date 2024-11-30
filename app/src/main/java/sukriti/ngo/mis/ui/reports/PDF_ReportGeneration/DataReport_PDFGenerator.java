package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import sukriti.ngo.mis.dataModel.dynamo_db.Country;
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure.StateStructure;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;
import sukriti.ngo.mis.utils.UserAlertClient;

public class DataReport_PDFGenerator {
    private Context context;
    ArrayList<UsageReportData> mData;




    public DataReport_PDFGenerator(Context context, ArrayList<UsageReportData> mData) {
        this.context = context;
        this.mData =mData;
    }

    public void createPdf(ArrayList<StateStructure> state, String dur){
        IText itext = new IText(context,"DataReport");
        itext.openPdf();
        itext.frontPage("Data Report",state,dur);


        for(int j = 0;j<mData.size();j++) {

            switch (dur) {
                case "Last 15 days":
                    if (j > 0 && j % 3 == 0) {
                        itext.areaBreak();
                    }
                    break;
                case "Last 7 days":
                    if (j > 0 && j % 4 == 0) {
                        itext.areaBreak();
                    }
                    break;
                case "Last 2 days":
                    if (j > 0 && j % 7 == 0) {
                        itext.areaBreak();
                    }
                    break;
                case "Yesterday":
                case "Today":
                    if (j > 0 && j % 9 == 0) {
                        itext.areaBreak();
                    }
                    break;
            }

            itext.addHeading(mData.get(j).getComplex().getName(),mData.get(j).getComplex().getAddress(),true,context);
            itext.drawDataReportRow(mData.get(j));



        }
        itext.closePdf();
    }


}
