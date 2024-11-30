package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.util.Log;

import android.view.View;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;


import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure.StateStructure;
import sukriti.ngo.mis.ui.reports.chartClient.CollectionComparisonChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.FeedbackDistributionChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.UpiComparisionChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.UsageComparisonChartClient;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;
import sukriti.ngo.mis.utils.Utilities;


public class PdfGenerator {
    private Context context;
    int width;
    int height;
    ArrayList<UsageReportData> mData;

    ArrayList<Bitmap> bmpUsageGraph = new ArrayList<>();
    ArrayList<Bitmap> bmpFeedbackGraph = new ArrayList<>();
    ArrayList<Bitmap> bmpCollectionGraph = new ArrayList<>();
    ArrayList<Bitmap> bmpNewTicketGraph = new ArrayList<>();
    ArrayList<Bitmap> bmpUpiCollectionGraph = new ArrayList<>();


    public PdfGenerator(Context context, ArrayList<UsageReportData> mData) {

        this.context = context;
        this.mData = mData;
    }

    @SuppressLint("InflateParams")
    public void createView() {

        for (int i = 0; i < mData.size(); i++) {
            UsageReportData complexUsageData = mData.get(i);

            //bar chart view
            View raw_bar_graph = View.inflate(context, R.layout.raw_bar_chart, null);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            raw_bar_graph.measure(widthMeasureSpec, heightMeasureSpec);
            raw_bar_graph.layout(0, 0, raw_bar_graph.getMeasuredWidth(), raw_bar_graph.getMeasuredHeight());
            BarChart barChart = raw_bar_graph.findViewById(R.id.raw_bar).findViewById(R.id.chart);

            //bar chart view
            View raw_feedback_bar_graph = View.inflate(context, R.layout.raw_feedback_graph, null);
            int widthMeasureSpecFed = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightMeasureSpecFed = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            raw_feedback_bar_graph.measure(widthMeasureSpecFed, heightMeasureSpecFed);
            raw_feedback_bar_graph.layout(0, 0, raw_feedback_bar_graph.getMeasuredWidth(), raw_feedback_bar_graph.getMeasuredHeight());
            BarChart fedBarChart = raw_feedback_bar_graph.findViewById(R.id.raw_bar).findViewById(R.id.chart);

            //line chart view
            View raw_line_graph = View.inflate(context, R.layout.raw_line_chart, null);
            int widthMeasure = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightMeasure = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            raw_line_graph.measure(widthMeasure, heightMeasure);
            raw_line_graph.layout(0, 0, raw_line_graph.getMeasuredWidth(), raw_line_graph.getMeasuredHeight());
            LineChart lineChart = raw_line_graph.findViewById(R.id.raw_line).findViewById(R.id.chart);


            UsageComparisonChartClient mUsageComparisonChartClient = new UsageComparisonChartClient(context, barChart, complexUsageData);
            mUsageComparisonChartClient.setFormatter();
            bmpUsageGraph.add(getBitmapOfViewGraph(raw_bar_graph));

            FeedbackDistributionChartClient mFeedbackDistributionChartClient = new FeedbackDistributionChartClient(context, fedBarChart, complexUsageData);
            mFeedbackDistributionChartClient.setFormatter();
            bmpFeedbackGraph.add(getBitmapOfViewGraph(raw_feedback_bar_graph));

            CollectionComparisonChartClient mCollectionComparisonChartClient = new CollectionComparisonChartClient(context, barChart, complexUsageData);
            mCollectionComparisonChartClient.setFormatter();
            bmpCollectionGraph.add(getBitmapOfViewGraph(raw_bar_graph));

            UpiComparisionChartClient upiComparisionChartClient = new UpiComparisionChartClient(context, barChart, complexUsageData);
            upiComparisionChartClient.setFormatter();
            bmpUpiCollectionGraph.add(getBitmapOfViewGraph(raw_bar_graph));

//                        TicketResolutionTimelineChartClient mTicketResolutionTimelineChartClient = new TicketResolutionTimelineChartClient(context,lineChart,complexUsageData);
//                        mTicketResolutionTimelineChartClient.setFormatter();
//                        bmpNewTicketGraph.add(getBitmapOfViewGraph(raw_line_graph));
        }
    }


    private Bitmap getBitmapOfViewGraph(View view) {
        Log.i("ggg", "getBitmapOfViewGraph: width :" + width);
        Log.i("ggg", "getBitmapOfViewGraph: height :" + height);


        Bitmap returnedBitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) bgDrawable.draw(canvas);
        else canvas.drawColor(Color.WHITE);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return returnedBitmap;
    }

    public void createPdf(ArrayList<StateStructure> state, String dur) {
        IText itext = new IText(context, "GraphicalReport");
        itext.openPdf();
        itext.frontPage("Graphical Report", state, dur);

        for (int j = 0; j < mData.size(); j++) {
            String subhead =mData.get(j).getComplex().getAddress();
            if(subhead == null){
                subhead = "";
            }
            itext.addHeading(mData.get(j).getComplex().getName(),subhead , false, context);

            itext.addSubHeading("Data Report");
            itext.drawGraphicalReportTable();
            itext.drawGraphicalReportTableCell(Utilities.getUsageReportRawData(mData.get(j)));

            itext.addSubHeading("Usage Comparison");
            itext.drawImage(bmpUsageGraph.get(j));

            itext.addSubHeading("Feedback Distribution");
            itext.drawImage(bmpFeedbackGraph.get(j));

            itext.addSubHeading("Collection Comparison");
            itext.drawImage(bmpCollectionGraph.get(j));

            itext.addSubHeading("Upi Collection Comparison");
            itext.drawImage(bmpUpiCollectionGraph.get(j));

//            itext.addSubHeading("Ticket Resolution Chart");
//            itext.drawImage(bmpNewTicketGraph.get(j));
            if (j + 1 < mData.size()) {
                itext.nextPage();
            }
        }
        itext.closePdf();

    }


}
