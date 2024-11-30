package sukriti.ngo.mis.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardBinding
import sukriti.ngo.mis.databinding.LoadingBinding
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.super_admin.fragments.Dashboard
import sukriti.ngo.mis.utils.NavigationClient


class LoadingFragment() : Fragment() {
    private var _message: String = ""
    private lateinit var binding: LoadingBinding

    companion object {
        private var INSTANCE: LoadingFragment? = null

        fun getInstance(): LoadingFragment {
            return INSTANCE
                ?: LoadingFragment()
        }
    }

    fun setMessage(message : String){
        _message = message
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoadingBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }



    fun init() {
        binding.message.text = "Loading cabin stats..."
    }
}