package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces

import sukriti.ngo.mis.ui.management.data.device.Application

interface SubmitApplication {

    fun submitApp(app: Application)

    fun editApp(app: Application, index: Int)
}