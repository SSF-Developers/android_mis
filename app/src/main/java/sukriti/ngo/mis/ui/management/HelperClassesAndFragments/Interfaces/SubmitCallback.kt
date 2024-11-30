package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces

import com.google.gson.JsonObject

interface SubmitCallback {
    fun onSubmit(json : JsonObject)
}