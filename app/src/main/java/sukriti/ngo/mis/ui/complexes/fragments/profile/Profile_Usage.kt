package sukriti.ngo.mis.ui.complexes.fragments.profile

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.CabinProfileUsageBinding
import sukriti.ngo.mis.repository.utils.DateConverter.toDbDate
import sukriti.ngo.mis.repository.utils.DateConverter.toDbTimestamp

import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.AdapterProfileUsage
import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile
import sukriti.ngo.mis.ui.complexes.data.UsageProfile
import sukriti.ngo.mis.ui.complexes.interfaces.UsageProfileRequestHandler
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import java.io.File
import java.io.FileOutputStream


class Profile_Usage : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinProfileUsageBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Profile_Usage? = null

        fun getInstance(): Profile_Usage {
            return INSTANCE
                ?: Profile_Usage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinProfileUsageBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        userAlertClient.showWaitDialog("Loading Profiles..")
        viewModel.usageProfile.observe(viewLifecycleOwner, usageProfileObserver)
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)

        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(Nomenclature.DURATION_DEFAULT_SELECTION)
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })

    }

    var durationSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                var duration = Nomenclature.getDuration(index)
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getDisplayUsageProfileForDays(viewModel.getSelectedComplex(), viewModel.getSelectedCabin(),duration,requestHandler)

//            viewModel.usageProfile.observe(viewLifecycleOwner,usageProfileObserver)
            }

        }

    var usageProfileObserver: Observer<ArrayList<UsageProfile>> = Observer {
        val usageProfiles = it as ArrayList<UsageProfile>
        Log.i("cabinDetails", "usageProfile: " + Gson().toJson(usageProfiles))
        viewModel.mutableDisplayUsageprofile(usageProfiles,requestHandler)
//        userAlertClient.closeWaitDialog()
//        requestHandler.getData(usagesProfiles)

    }

    var displayUsage: MutableList<DisplayUsageProfile>? = null

    var requestHandler: UsageProfileRequestHandler = object : UsageProfileRequestHandler {
        override fun getData(data: MutableList<DisplayUsageProfile>?) {
            if (data?.size == 0) {
                binding.gridContainer.visibility = View.GONE
                binding.noDataContainer.visibility = View.VISIBLE
                binding.noDataLabel.text = "No usage recorded for selected duration."
            } else {
                displayUsage = data
                binding.gridContainer.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                Log.i(_tag, "requestHandler: " + data?.size)
                setTitles()
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = AdapterProfileUsage(context, data)
                binding.grid.adapter = mwcCabinListAdapter
//                toExcel()
            }
            userAlertClient.closeWaitDialog()
        }
    }

    fun setTitles() {
        val ids = intArrayOf(
            R.id.title_01,
            R.id.title_02,
            R.id.title_03,
            R.id.title_04,
            R.id.title_05,
            R.id.title_06,
            R.id.title_07,
            R.id.title_08,
            R.id.title_09,
            R.id.title_10,
            R.id.title_11,
            R.id.title_12,
            R.id.title_13,
            R.id.title_14,
            R.id.title_15,
            R.id.title_16,
            R.id.title_17,
            R.id.title_18,
            R.id.title_19,
            R.id.title_20,
            R.id.title_21
        )
        val titles = arrayListOf<String>(
            "Date",
            "Time",
            "Cabin Type",
            "Duration",
            "Usage Charge",
            "Feedback",
            "Entry Type",
            "Air Dryer",
            "Fan Time",
            "Floor Clean",
            "Full Flush",
            "Manual Flush",
            "Light Time",
            "Mini Flush",
            "Pre Flush",
            "RFID",
            "Client",
            "Complex",
            "State",
            "District",
            "City"
        )
        val fields = arrayOfNulls<TextView>(ids.size)

        for (i in ids.indices) {
            fields[i] = binding.root.findViewById(ids[i])
            fields[i]?.text = titles[i]

            when {
                titles[i] == "Usage Charge" -> {
                    if (sharedPrefsClient.getUiResult().data.usage_charge_profile === "true") {
                        fields[i]?.visibility = View.VISIBLE
                    } else {
                        fields[i]?.visibility = View.GONE
                    }
                }
                titles[i] == "Air Dryer" -> {
                    if (sharedPrefsClient.getUiResult().data.air_dryer_profile === "true") {
                        fields[i]?.visibility = View.VISIBLE
                    } else {
                        fields[i]?.visibility = View.GONE
                    }
                }
                titles[i] == "RFID" -> {

                    if (sharedPrefsClient.getUiResult().data.rfid_profile === "true") {
                        fields[i]?.visibility = View.VISIBLE
                    } else {
                        fields[i]?.visibility = View.GONE
                    }
                }
            }
        }
    }

    private val file = File(Environment.getExternalStorageDirectory(), "Download/UsageProfile.xls")
    fun toExcel() {
        val hssfWorkbook = HSSFWorkbook()
        val hssfSheet = hssfWorkbook.createSheet("Custom Sheet")

        val titles = arrayListOf<String>(
            "Date",
            "Time",
            "Cabin Type",
            "Duration",
            "Usage Charge",
            "Feedback",
            "Entry Type",
            "Air Dryer",
            "Fan Time",
            "Floor Clean",
            "Full Flush",
            "Manual Flush",
            "Light Time",
            "Mini Flush",
            "Pre Flush",
            "RFID",
            "Client",
            "Complex",
            "State",
            "District",
            "City"
        )
        var hssfRow = hssfSheet.createRow(0)

        for (j in 0..20) {
            val hssfCell = hssfRow?.createCell(j)
            hssfCell!!.setCellValue(titles[j])
        }

        for (i in 0 until displayUsage!!.size) {
            hssfRow = hssfSheet.createRow(i + 1)
            Log.i(
                "_checkTimeStamp",
                "toExcel: deviceTimeStamp : " + i + " :" + toDbDate(toDbTimestamp(displayUsage!![i].usageProfile.DEVICE_TIMESTAMP))
            )
            Log.i(
                "_checkTimeStamp",
                "toExcel: serverTimeStamp : " + i + " :" + toDbDate(toDbTimestamp(displayUsage!![i].usageProfile.SERVER_TIMESTAMP))
            )
            Log.i(
                "_checkTimeStamp",
                "toExcel: timestamp : " + i + " :" + toDbDate(toDbTimestamp(displayUsage!![i].usageProfile.TimeStamp))
            )
            val cell = ArrayList<String>()
            cell.add(displayUsage!![i].date)
            cell.add(displayUsage!![i].time)
            cell.add(Nomenclature.getCabinType(displayUsage!![i].usageProfile.SHORT_THING_NAME))
            cell.add(displayUsage!![i].usageProfile.Duration)
            cell.add(displayUsage!![i].usageProfile.Amountcollected)
            cell.add(displayUsage!![i].usageProfile.feedback)
            cell.add(displayUsage!![i].usageProfile.Entrytype)
            cell.add(displayUsage!![i].usageProfile.Airdryer)
            cell.add(displayUsage!![i].usageProfile.Fantime)
            cell.add(displayUsage!![i].usageProfile.Floorclean)
            cell.add(displayUsage!![i].usageProfile.Fullflush)
            cell.add(displayUsage!![i].usageProfile.Manualflush)
            cell.add(displayUsage!![i].usageProfile.Lighttime)
            cell.add(displayUsage!![i].usageProfile.Miniflush)
            cell.add(displayUsage!![i].usageProfile.Preflush)
            cell.add(displayUsage!![i].usageProfile.RFID)
            cell.add(displayUsage!![i].usageProfile.CLIENT)
            cell.add(displayUsage!![i].usageProfile.COMPLEX)
            cell.add(displayUsage!![i].usageProfile.STATE)
            cell.add(displayUsage!![i].usageProfile.DISTRICT)
            cell.add(displayUsage!![i].usageProfile.CITY)

            for (j in 0..20) {
                val hssfCell = hssfRow?.createCell(j)
                hssfCell!!.setCellValue(cell[j])
            }

        }
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileOutputStream = FileOutputStream(file)
            hssfWorkbook.write(fileOutputStream)
            if (fileOutputStream != null) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
