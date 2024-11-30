package sukriti.ngo.mis.ui.reports.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.UsageReportBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.PDF_Downloder
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.PdfGenerator
import sukriti.ngo.mis.ui.reports.ReportsViewModel
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_DOWNLOAD
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_DOWNLOAD_PDF
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_EMAIL
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_EMAIL_PDF
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.BOTHCHECKED
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.BWTCHECKED
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_DOWNLOAD_REPORT
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.TOILETCHECKED
import sukriti.ngo.mis.ui.reports.adapters.ComplexUsageReportAdapter
import sukriti.ngo.mis.ui.reports.data.UsageReportData
import sukriti.ngo.mis.ui.reports.interfaces.ComplexUsageReportRequestHandler
import sukriti.ngo.mis.ui.tickets.data.TicketListData
import sukriti.ngo.mis.utils.ExcelClient.usageReportToCsv
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient


class GraphicalReport : Fragment() {

    private lateinit var mThis: Fragment
    private lateinit var viewModel: ReportsViewModel
    private lateinit var binding: UsageReportBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var mNavigationHandler: NavigationHandler
    private lateinit var reportData: ArrayList<UsageReportData>;
    private lateinit var complexUsageReportAdapter : ComplexUsageReportAdapter
    private var userDurationSelection = false

    private val pdf_link = "https://mis-report-pdf.s3.ap-south-1.amazonaws.com/your-pdf-file-name.pdf"
//    val pdf_link = "https://www.clickdimensions.com/links/TestPDFfile.pdf"
    val titles = arrayListOf(
        "Complex",
        "Cabin Type",
        "Cabin Count",
        "Usage",
        "Feedback",
        "Collection"
    )

    companion object {
        private var INSTANCE: GraphicalReport? = null

        fun getInstance(): GraphicalReport {
            if (INSTANCE == null) {
                Log.i("__loadData", "usageReport:GraphicalReport()")
                INSTANCE = GraphicalReport()
            }
            return INSTANCE as GraphicalReport
        }
    }

    fun setNavigationHandler(handler: NavigationHandler) {
        mNavigationHandler = handler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.setOnTouchListener(durationTouchListener)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(Nomenclature.getIndex(viewModel.getSelectedDuration(), items))
        binding.timeIntervalContainer.setOnClickListener{
            binding.timeInterval.performClick()
        }

        val reportActions = Nomenclature.getGraphReportActions()
        val actionsAdapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, reportActions)
        binding.actions.adapter = actionsAdapter
        binding.actions.onItemSelectedListener = actionSelectionListener
        binding.actions.setSelection(0)

        binding.cbtoilet.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            systemSelection()
        }

        binding.cbBwt.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            systemSelection()
        }

        Log.i("__loadData", "usageReport:selectionTree-observe()")
        viewModel.selectionTree.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            Log.i("__loadData", "usageReport:selectionTree-loadData()")
            loadData()
        })

//     loadData()
        viewModel.usageReportData.observe(viewLifecycleOwner,usageReportObserver)
    }

    private var usageReportObserver :Observer<MutableList<UsageReportData>>  = Observer {
        val data = it as MutableList<UsageReportData>
        Log.i("reportLambda", "usageReportObserver: "+Gson().toJson(data))
        Log.i("reportLambda", "usageReportObserver Feedback: "+Gson().toJson(data[0].feedbackDistribution))
        requestHandler.getData(data)
    }

    private var resultFromServer: RepositoryCallback<String> = RepositoryCallback { result ->
            userAlertClient.closeWaitDialog()
            if(result is _Result.Success<String>){
                Log.i("reportLambda", "Success: ")
                userAlertClient.showWaitDialog("Rendering Data...")

            }else{
                val err = result as _Result.Error<TicketListData>
                Log.i("reportLambda", "error: "+err.message)
                userAlertClient.showDialogMessage("Error Alert",err.message,false)
            }
        }

    private var durationTouchListener: View.OnTouchListener = View.OnTouchListener { v, event ->
            userDurationSelection = true
            false
        }

    var durationSelectionListener: OnItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                if (userDurationSelection) {
                    val selection = Nomenclature.getDurationLabels()[index]
                    Log.i("_selection", "selection: $selection")
                    viewModel.setSelectedDuration(selection)
                    viewModel.duration = Nomenclature.getDuration(index)
                    if (sharedPrefsClient.getSelectionTreeStatus()) {
                        Log.i("__loadData", "usageReport:onItemSelected-loadData()")
                        loadData()
                    }
                    else {
                        setNoData("No unit selected in the Selection Tree. Please select units to view reports.")
                    }

                    userDurationSelection = false
                }
            }
        }

    private var actionSelectionListener: OnItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_actionSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                when (index) {
                    0 -> {
                    }
                    1 -> {
//                          viewModel.createFile(mThis, ACTION_DOWNLOAD_PDF, "GraphicalReport.pdf")
                        takePermissions()
                    }
                    2 -> {
//                        viewModel.createFile(mThis, ACTION_DOWNLOAD, "GraphicalReportData.csv")
                        mNavigationHandler.navigateTo(NAV_DOWNLOAD_REPORT)
                    }
                    3 -> {
//                        viewModel.createFile(mThis, ACTION_EMAIL_PDF, "GraphicalReport.pdf")
                        viewModel.chooseFileToMail(mThis, ACTION_EMAIL_PDF)
                    }
                    4 -> {
                        viewModel.createFile(mThis, ACTION_EMAIL, "GraphicalReportData.csv")
                    }
                }
            }
        }

    fun loadData() {
        userAlertClient.showWaitDialog("Loading data...")
//        var selectedIndex =
//            Nomenclature.getIndex(viewModel.getSelectedDuration(), Nomenclature.getDurationLabels())
//        var duration = Nomenclature.getDuration(selectedIndex)
//        var durationInDate = Nomenclature.getDataDuration(duration)
//
//        viewModel.getUsageReportForComplexes(
//            durationInDate,
//            Utilities.getSelectedComplexes(viewModel.selectionTree.value),
//            requestHandler
//        )
//        viewModel.getReportData(resultFromServer)
        viewModel.getReportData(resultFromServer)
    }

    var requestHandler: ComplexUsageReportRequestHandler = ComplexUsageReportRequestHandler { data ->
            userAlertClient.closeWaitDialog()
        Log.d("Report Data", Gson().toJson(data))
            if (data?.size == 0) {
                setNoData("No usage recorded for selected duration.")
            } else {
                binding.grid.visibility = View.VISIBLE
                binding.actionsContainer.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                val gridLayoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                complexUsageReportAdapter = ComplexUsageReportAdapter(
                    requireContext(),
                    data as ArrayList<UsageReportData>?
                )
                binding.grid.adapter = complexUsageReportAdapter
                if(data !=null)
                    reportData = data
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

        binding.actions.setSelection(0)

        when (requestCode) {
//            rahul karn
            102 -> takePermission()
//            ************

            ACTION_DOWNLOAD_PDF -> {
                var uri: Uri? = data?.data
                if (uri != null) {
//                    writeToPdfFile(uri, "testData")

//                  rahul karn
//                  writeToPdfFile(uri, reportData) cmnt by rahul karn
                    takePermissions()
//                    ************
                }
            }
            else -> {
                viewModel.handleResult(
                    requireActivity(),
                    usageReportToCsv(reportData, titles),
                    requestCode,
                    requestCode,
                    data
                )

                if (requestCode == ACTION_EMAIL) {
                } else if (requestCode == ACTION_DOWNLOAD) {
                    userAlertClient.showDialogMessage(
                        "File Saved",
                        "The file was successfully saved.",
                        false
                    )
                }
            }

        }

    }

    private fun writeToPdfFile(fileData: ArrayList<UsageReportData>) {
        userAlertClient.showWaitDialog("Downloading...")
        val runnable = Runnable {
            val selectionStruct =viewModel.selectionState
            val dur = binding.timeInterval.selectedItem
            val pdfGenerator = PdfGenerator(context,  fileData)
            pdfGenerator.createView()
            pdfGenerator.createPdf(selectionStruct,dur.toString())
                userAlertClient.closeWaitDialog()
                binding.actions.setSelection(0, true)
                userAlertClient.showDialogMessage("File Downloaded",
                    "Graphical Report is been saved to download folder",
                    false
                )
        }
        Handler(Looper.getMainLooper()).post(runnable)
    }

    fun getScreenshotFromRecyclerView(view: RecyclerView): Bitmap? {
        val adapter = view.getAdapter()
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size: Int = adapter.getItemCount()
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache: LruCache<String, Bitmap> = LruCache(cacheSize)
            for (i in 0 until size) {
                val holder: RecyclerView.ViewHolder =
                    adapter.createViewHolder(view, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        view.getWidth(),
                        View.MeasureSpec.EXACTLY
                    ),
                    View.MeasureSpec.makeMeasureSpec(
                        0,
                        View.MeasureSpec.UNSPECIFIED
                    )
                )
                holder.itemView.layout(
                    0,
                    0,
                    holder.itemView.getMeasuredWidth(),
                    holder.itemView.getMeasuredHeight()
                )
                holder.itemView.setDrawingCacheEnabled(true)
                holder.itemView.buildDrawingCache()
                try {
                    val drawingCache: Bitmap = holder.itemView.getDrawingCache()
                    if (drawingCache != null) {
                        bitmaCache.put(i.toString(), drawingCache)
                    }
                    height += holder.itemView.getMeasuredHeight()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    Log.e("_createPdf-1", "position " + i)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    Log.e("_createPdf-2", "position " + i)
                }

            }
            bigBitmap =
                Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)
            bigCanvas.drawColor(Color.WHITE)
            for (i in 0 until size) {
                val bitmap: Bitmap = bitmaCache.get(i.toString())
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.getHeight()
                bitmap.recycle()
            }
        }
        return bigBitmap
    }

//    rahul karn
    private fun takePermissions(){
    if (isPermissionGranted()) {
//                    pdf downloder
//            writeToPdfFile(reportData)
        PDF_Downloder.downloadFile(requireActivity(), pdf_link)

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
        takePermissions()
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
//                    pdf downloder
//                   writeToPdfFile(reportData)
                    PDF_Downloder.downloadFile(requireActivity(), pdf_link)
                }
                else{
                    takePermission()
                }
            }
        }
    }




    private fun systemSelection(){
        Log.i("gg", "systemSelection: ")
        when {
            binding.cbtoilet.isChecked && binding.cbBwt.isChecked -> {
                if(::complexUsageReportAdapter.isInitialized) {
                    complexUsageReportAdapter.updateSys(BOTHCHECKED)
                }
            }
            binding.cbtoilet.isChecked && !binding.cbBwt.isChecked -> {
                if (::complexUsageReportAdapter.isInitialized) {
                    complexUsageReportAdapter.updateSys(TOILETCHECKED)
                }
            }
            !binding.cbtoilet.isChecked && binding.cbBwt.isChecked -> {
                if(::complexUsageReportAdapter.isInitialized) {
                    complexUsageReportAdapter.updateSys(BWTCHECKED)
                }
            }
        }
    }

}
