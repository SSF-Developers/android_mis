package sukriti.ngo.mis.ui.management.data

import sukriti.ngo.mis.ui.management.data.device.ContactInfo

class Enterprise {
    var name = ""
    var enterpriseDisplayName = ""
    var selected = false

    var contactInfo = ContactInfo()
    var state = "active"
    var ttl : Long? = null
}