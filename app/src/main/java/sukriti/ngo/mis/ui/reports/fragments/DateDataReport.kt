package sukriti.ngo.mis.ui.reports.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.UsageReportBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.DataReport_PDFGenerator
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.PdfGenerator
import sukriti.ngo.mis.ui.reports.ReportsViewModel
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_DOWNLOAD
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_EMAIL
import sukriti.ngo.mis.ui.reports.adapters.ComplexAdapterDateDataReport
import sukriti.ngo.mis.ui.reports.data.UsageReportData
import sukriti.ngo.mis.ui.reports.interfaces.ComplexUsageReportRequestHandler
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.ExcelClient.dateDataToCsv
import java.io.File
import java.util.*


class DateDataReport : Fragment() {

    private lateinit var mThis:Fragment
    private lateinit var viewModel: ReportsViewModel
    private lateinit var binding: UsageReportBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var mNavigationHandler: NavigationHandler
    private lateinit var reportData: ArrayList<UsageReportData>
    private var userDurationSelection = false

    val titles = arrayListOf(
        "Complex",
        "Date",
        "MWC Usage",
        "MWC Collection",
        "FWC Usage",
        "FWC Collection",
        "PWC Usage",
        "PWC Collection",
        "MUR Usage",
        "MUR Collection"
    )
    companion object {
        private var INSTANCE: DateDataReport? = null

        fun getInstance(): DateDataReport {
            if(INSTANCE == null) {
                INSTANCE = DateDataReport()
                Log.i("__loadData","dateDataReport:DateDataReport()")
            }
            return INSTANCE as DateDataReport
        }
    }

    fun setNavigationHandler(handler: NavigationHandler) {
        mNavigationHandler = handler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UsageReportBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mThis = this
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireParentFragment()).get(ReportsViewModel::class.java)
        binding.timeInterval.setOnTouchListener(durationTouchListener)
        val items = Nomenclature.getLimitedDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(3)
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })

        val reportActions = Nomenclature.getReportActions()
        val actionsAdapter =
            ArrayAdapter(requireContext(), R.layout.item_duration_selection, reportActions)
        binding.actions.adapter = actionsAdapter
        binding.actions.onItemSelectedListener = actionSelectionListener
        binding.actions.setSelection(0)

        Log.i("__loadData","dateDataReport:selectionTree-observe()")
        viewModel.selectionTree.observe(viewLifecycleOwner, Observer {
            val selectionTree = it ?: return@Observer
            Log.i("__loadData","dateDataReport:selectionTree-loadData()")
            loadData()
        })
    }

    var durationTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            userDurationSelection = true
            return false
        }
    }

    var durationSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                if (userDurationSelection) {
                    var selection = Nomenclature.getDurationLabels()[index]
                    Log.i("_selection", "selection: $selection")
                    if (sharedPrefsClient.getSelectionTreeStatus()) {
                        Log.i("__loadData","dateDataReport:onItemSelected-loadData()")
                        loadData()
                    } else {
                        setNoData("No unit selected in the Selection Tree. Please select units to view reports.")
                    }
                }

            }
        }

    private var actionSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_actionSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                when (index) {
                    0 -> {

                    }
                    1 -> {
//                        rahul karn
//                        viewModel.createFile(mThis,ACTION_DOWNLOAD,"UsageReport.csv")
                        takePermissions()
                    }
                    2 -> {
//                        viewModel.createFile(mThis, ACTION_EMAIL,"DataReport.pdf")
                        viewModel.chooseFileToMail(mThis, ACTION_EMAIL)
//                        mailPdf()
                    }
                }
            }
        }

    fun loadData() {
        userAlertClient.showWaitDialog("Loading data...")
        var selectedIndex =  binding.timeInterval.selectedItemPosition
        var duration = Nomenclature.getDuration(selectedIndex)
        var durationInDate = Nomenclature.getDataDuration(duration)
        viewModel.getUsageReportForComplexes(
            durationInDate,
            Utilities.getSelectedComplexes(viewModel.selectionTree.value),
            requestHandler
        )
    }

    var requestHandler: ComplexUsageReportRequestHandler = object : ComplexUsageReportRequestHandler {
        override fun getData(data: MutableList<UsageReportData>?) {
            userAlertClient.closeWaitDialog()
            if (data?.size == 0) {
                setNoData("No usage recorded for selected duration.")
            } else {
                binding.grid.visibility = View.VISIBLE
                binding.actionsContainer.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                val gridLayoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var complexUsageReportAdapter = ComplexAdapterDateDataReport(requireContext(),
                    data as ArrayList<UsageReportData>?
                )
                binding.grid.adapter = complexUsageReportAdapter
                reportData = data as ArrayList<UsageReportData>
            }
        }
    }

    fun setNoData(message: String) {
        binding.grid.visibility = View.GONE
        binding.actionsContainer.visibility = View.GONE
        binding.noDataContainer.visibility = View.VISIBLE
        binding.noDataLabel.text = message
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleResult(requireActivity(), dateDataToCsv(reportData,titles),requestCode,requestCode,data)
        binding.actions.setSelection(0)

        if(requestCode == ACTION_EMAIL){

        }else if(requestCode == ACTION_DOWNLOAD){
//            userAlertClient.showDialogMessage("File Saved","The file was successfully saved.",false)
            takePermissions()
        }else if(requestCode ==102) {
            takePermission()
        }
    }


//    rahul karn

    private fun mailPdf(){
        try {
//            val storage = "/storage/emulated/0/Download/DataReport.pdf"
            val storage = "DataReport.pdf"
            val file = File(Environment.getExternalStorageDirectory().absolutePath + "/Download/$storage")
            Log.i("___mail", "mailPdf: path : $file uri : ${Uri.parse(file.path)}")

            val uri =Uri.parse(file.path)
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            emailIntent.type = "application/pdf"
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            emailIntent.setPackage("com.google.android.gm");

            requireActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        }
        catch (t : Throwable){
            Toast.makeText(requireContext(), "Request failed try again: $t", Toast.LENGTH_LONG).show()
            Log.i("___mail", "mailPdf: $t")
        }
    }

    private fun writeToPdfFile(fileData: ArrayList<UsageReportData>) {
        userAlertClient.showWaitDialog("Downloading...")
        userAlertClient.showWaitDialog("Downloading...")
        val runnable = Runnable {
            val selectionStruct =viewModel.selectionState
            val dur = binding.timeInterval.selectedItem
            val dataReportPdfGenerator = DataReport_PDFGenerator(context, fileData)
            dataReportPdfGenerator.createPdf(selectionStruct,dur.toString())
            binding.actions.setSelection(0, true)
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("File Downloaded",
                "Data Report is been saved to download folder",
                false
            )
        }
        Handler(Looper.getMainLooper()).post(runnable)
    }

    private fun takePermissions() {
        if (isPermissionGranted()) {
            writeToPdfFile(reportData)
        } else {
            takePermission()
        }
    }

    private fun isPermissionGranted():Boolean{
        val read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return read== PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
    }

    private fun takePermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ),102)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 102){
            if (grantResults.isNotEmpty()){
                val writeExternalStorage = grantResults[0]== PackageManager.PERMISSION_GRANTED
                val readExternalStorage = grantResults[1]== PackageManager.PERMISSION_GRANTED
                if (readExternalStorage && writeExternalStorage){
                    writeToPdfFile(reportData)
                }
                else{
                    takePermission()
                }
            }
        }
    }
}
