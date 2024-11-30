package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces

import sukriti.ngo.mis.ui.management.data.device.Device

interface PolicyToggle {
    fun onClick(
        device: Device,
        position: Int,
        state: Boolean
    )
}