package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.new_wifi_ssid.wifiNameLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.NewWifiSsidBinding

class WifiDialog(
    private val submitWifi: SubmitWifi
): DialogFragment() {

    lateinit var binding: NewWifiSsidBinding
    public interface SubmitWifi {
        fun onSubmit(wifiName: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewWifiSsidBinding.inflate(inflater)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFragmentStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitWifiSsdi.setOnEditorActionListener {v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            }
            else {
                false
            }
        }

        binding.submitWifiSsdi.setOnClickListener {
            submit()
        }


        binding.close.setOnClickListener {
            dismiss()
        }
    }

    private fun submit() {
        if(binding.wifiNameTv.text.toString().isNotEmpty()) {
            wifiNameLayout.isErrorEnabled = false
            submitWifi.onSubmit(binding.wifiNameTv.text.toString())
            dismiss()
        }
        else {
            wifiNameLayout.error = "Field is required"
            wifiNameLayout.isErrorEnabled = true
        }
    }
}