package sukriti.ngo.mis.utils

import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.SimpleHandler

class NavigationClient(fragmentManager: FragmentManager) {
    private var fragmentManager: FragmentManager = fragmentManager

    fun loadFragment(fragment: Fragment, label: String, addToStack: Boolean) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        if (addToStack)
            transaction.addToBackStack(label)
        transaction.commit()
    }

    fun loadFragment(fragment: Fragment, container: Int, label: String, addToStack: Boolean) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(container, fragment)
        if (addToStack)
            transaction.addToBackStack(label)

        transaction.commit()
    }

    fun loadFragmentAllowStateLoss(fragment: Fragment, container: Int, label: String, addToStack: Boolean) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(container, fragment)
        if (addToStack)
            transaction.addToBackStack(label)

        transaction.commitAllowingStateLoss()
    }

    fun loadFragment(
        fragment: Fragment,
        textView: TextView,
        title: String,
        label: String,
        addToStack: Boolean
    ) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        if (addToStack)
            transaction.addToBackStack(label)
        transaction.commit()
        textView.text = title
    }

    fun flipLoadFragment(fragment: Fragment, label: String) {
        val transaction = fragmentManager.beginTransaction()
//        transaction.setCustomAnimations(
//            R.animator.flip,
//            R.animator.flip_out,
//            R.animator.flip,
//            R.animator.flip_out
//        )
        transaction.setCustomAnimations(
            R.anim.flip_left_in,
            R.anim.flip_left_out,
            R.anim.flip_left_in,
            R.anim.flip_left_out
        )
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}