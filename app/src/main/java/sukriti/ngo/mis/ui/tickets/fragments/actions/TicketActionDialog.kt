package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardUsageDetailsBinding
import sukriti.ngo.mis.databinding.TicketActionDialogBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_COMPARISON
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_TIMELINE
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_COMPARISON
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_TIMELINE
import sukriti.ngo.mis.ui.tickets.TicketDetailViewModel
import sukriti.ngo.mis.ui.tickets.data.TicketAction
import sukriti.ngo.mis.ui.tickets.interfaces.TicketActionDialogHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class TicketActionDialog : DialogFragment() {
    private lateinit var binding: TicketActionDialogBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: TicketDetailViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var mTicketActionDialogHandler: TicketActionDialogHandler
    private lateinit var ticketAction: TicketAction
    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: TicketActionDialog? = null

        fun getInstance(): TicketActionDialog {
            return INSTANCE
                ?: TicketActionDialog()
        }
    }

    fun setData(ticketAction: TicketAction, mTicketActionDialogHandler: TicketActionDialogHandler) {
        this.ticketAction = ticketAction
        this.mTicketActionDialogHandler = mTicketActionDialogHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TicketActionDialogBinding.inflate(inflater, container, false)
        binding.header.text = "Collection Stats"
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(TicketDetailViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        binding.header.text = ticketAction.actionName
        binding.actionDescription.text = Nomenclature.getActionDescription(ticketAction.actionId)
        if (ticketAction.actionId == Nomenclature.TICKET_ACTION_ACCEPT_ASSIGNMENT
            || ticketAction.actionId == Nomenclature.TICKET_ACTION_MARK_RESOLVED
            || ticketAction.actionId == Nomenclature.TICKET_ACTION_MARK_CLOSED
            || ticketAction.actionId == Nomenclature.TICKET_ACTION_RE_OPEN) {
            binding.confirmAction.visibility = View.VISIBLE
            binding.confirmAction.hint = "Validate Action"
        } else {
            binding.confirmAction.visibility = View.GONE
        }

        binding.close.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.submit.setOnClickListener(View.OnClickListener {
            Utilities.hideKeypad(requireContext(), binding.comment.editText)
            if (validate()) {
                var comment = binding.comment.editText?.text.toString()
                mTicketActionDialogHandler.onSubmit(ticketAction, comment)
                dismiss()
            }
        })
    }

    fun validate(): Boolean {
        var validated = true
        if (binding.comment.editText?.text.toString().isEmpty()) {
            validated = false
            setError(
                binding.comment,
                "A comment describing your intent is mandatory."
            )
        } else {
            clearError(binding.comment)
        }

        if (ticketAction.actionId == Nomenclature.TICKET_ACTION_ACCEPT_ASSIGNMENT) {
            if (binding.confirmAction.editText?.text.toString() != "accept lead") {
                validated = false
                setError(
                    binding.confirmAction,
                    "Type 'accept lead' to accept ticket lead role."
                )
            } else {
                clearError(binding.confirmAction)
            }
        }

        if (ticketAction.actionId == Nomenclature.TICKET_ACTION_MARK_RESOLVED) {
            if (binding.confirmAction.editText?.text.toString() != "mark resolved") {
                validated = false
                setError(
                    binding.confirmAction,
                    "Type 'mark resolved' to update ticket status to be resolved."
                )
            } else {
                clearError(binding.confirmAction)
            }
        }

        if (ticketAction.actionId == Nomenclature.TICKET_ACTION_MARK_CLOSED) {
            if (binding.confirmAction.editText?.text.toString() != "close ticket") {
                validated = false
                setError(
                    binding.confirmAction,
                    "Type 'close ticket' to mark ticket closed."
                )
            } else {
                clearError(binding.confirmAction)
            }
        }

        if (ticketAction.actionId == Nomenclature.TICKET_ACTION_RE_OPEN) {
            if (binding.confirmAction.editText?.text.toString() != "reopen ticket") {
                validated = false
                setError(
                    binding.confirmAction,
                    "Type 'reopen ticket' to confirm action."
                )
            } else {
                clearError(binding.confirmAction)
            }
        }
        return validated
    }

    fun setError(textInput: TextInputLayout, error: String) {
        textInput.error = error
    }

    fun clearError(textInput: TextInputLayout) {
        textInput.isErrorEnabled = false
    }
}
