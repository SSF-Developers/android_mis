package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces

import sukriti.ngo.mis.ui.management.data.device.Application


interface EditAppClickCallback {

    fun edit(app: Application, index: Int)
}