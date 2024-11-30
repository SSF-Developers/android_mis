package sukriti.ngo.mis.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.utils.NavigationClient


class LoadingScreen(context: Context) : Dialog(context,R.style.full_screen_dialog) {

    private var width: Int
    private var height: Int
    private var message: String = ""
    private lateinit var messageTv: TextView
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient

    constructor(context: Context,message: String) : this(context){
        Log.i("__constructor",""+message)
        this.message = message
        messageTv.text = message
    }

    init {
        val manager =
            context.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
        var params: WindowManager.LayoutParams
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            width = manager.defaultDisplay.width
            height = manager.defaultDisplay.height
        } else {
            val point = Point()
            manager.defaultDisplay.getSize(point)
            width = point.x
            height = point.y
        }
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        wlmp.width = width
        wlmp.height = height
        window!!.attributes = wlmp
        setTitle(message)
        setCancelable(false)
        setOnCancelListener(null)
        val parentParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val mView =
            View.inflate(context, R.layout.loading, null)
        messageTv = mView.findViewById<TextView>(R.id.message)
        addContentView(mView, parentParams)
    }

    override fun show() {
        super.show()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(this.window!!.attributes)
        lp.width = width
        lp.height = height
        this.window!!.attributes = lp
    }

}