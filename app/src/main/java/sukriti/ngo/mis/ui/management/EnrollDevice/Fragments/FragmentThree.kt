package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.Fragment3Binding
import sukriti.ngo.mis.ui.management.EnrollDevice.Lambda.DDBSaveLambda.DynamoDbDataWriterResponseCallback
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.ViewPagerControl
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class FragmentThree(
    val viewModel: EnrollDeviceViewModel,
    val managementViewModel: ManagementViewModel,
    val lambdaClient: LambdaClient
) : Fragment() {

    private val Debugging = "debugging"
    private lateinit var userAlertClient: UserAlertClient
    var interactionListener: ViewPagerControl? = null
    private lateinit var binding: Fragment3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Debugging, "onCreate: FragmentThree")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.i(Debugging, "onCreateView: FragmentThree")
        binding = Fragment3Binding.inflate(layoutInflater)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.walkthrough_pager)

/*        view.fragThreeNext.setOnClickListener {
            viewPager?.currentItem = 3
        }

        view.fragThreePrev.setOnClickListener {
            viewPager?.currentItem = 1
        }*/

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(Debugging, "onViewCreated: FragmentThree")
        userAlertClient = UserAlertClient(activity)
        binding.next.setOnClickListener {


            if(viewModel.stepThreeCompleted) {
                Utilities.hideKeypad(context, binding.serialNumber)
                interactionListener?.goToNextPage()

          } else {
                if(verifyDetails())
                    saveDataInDynamoDb()
            }

        }


        binding.back.setOnClickListener {
            interactionListener?.goToPrevPage()
        }

    }

    private fun verifyDetails(): Boolean {
        var detailsVerified = true
        if(binding.serialNumber.text.toString().isEmpty()) {
            binding.serialNumberLayout.isErrorEnabled = true
            binding.serialNumberLayout.error = "Fill this field first"
            detailsVerified = false
        }
        else {
            binding.serialNumberLayout.isErrorEnabled = false
            detailsVerified = true
        }
        return detailsVerified
    }
    private fun saveDataInDynamoDb() {
        val payload = JsonObject()
        payload.addProperty("serial_number", binding.serialNumber.text.toString())
        payload.addProperty("cabin_name", viewModel.cabinDetails.Name)
        viewModel.deviceSerialNumber = binding.serialNumber.text.toString()
        payload.addProperty("command", "save-data")

        val cabin = JsonObject()
        cabin.addProperty("Name", viewModel.cabinDetails.Name)
        cabin.addProperty("Arn", viewModel.cabinDetails.Arn)
        cabin.addProperty("Id", viewModel.cabinDetails.Id)

        cabin.addProperty("DefaultClientId", viewModel.cabinDetails.DefaultClientId)
        cabin.addProperty("ThingType", viewModel.cabinDetails.ThingType)
        cabin.addProperty("BillingGroup", viewModel.cabinDetails.BillingGroup)
        cabin.addProperty("Version", viewModel.cabinDetails.Version)
        cabin.addProperty("ThingGroup", viewModel.cabinDetails.ThingGroup)

        val attributesArray = JsonArray()
        for(attribute in viewModel.cabinDetails.attributesMap) {
            val obj = JsonObject()
            obj.addProperty("Name", attribute.key)
            obj.addProperty("Value", attribute.value)
            attributesArray.add(obj)
        }
        cabin.add("Attributes", attributesArray)
        payload.add("cabin_details", cabin)

        payload.add("complex_details", getComplexJson(viewModel.complexDetails))

        val extraDetails = JsonObject()
        extraDetails.addProperty("serial_number", binding.serialNumber.text.toString())
        payload.add("extra_details", extraDetails)
        payload.addProperty("enterpriseId", managementViewModel.getSelectedEnterprise().name);

//        payload.add("policy", getPolicyJson(viewModel.policies[1]))

        Log.i("ddbPayload", Gson().toJson(payload))
        userAlertClient.showWaitDialog("Processing...")
        try {
            lambdaClient.ExecuteDynamoDbDataWriter(
                payload,
                object : DynamoDbDataWriterResponseCallback {
                    override fun onSuccess(response: JsonObject) {
                        userAlertClient.closeWaitDialog()
                        val statusCode = response.get("statusCode").asInt
                        if (statusCode == 200) {
                            val message = response.get("body").asString
                            userAlertClient.showDialogMessage(
                                "Success",
                                message
                            ) {
                                viewModel.ddbEntryCreated = true
                                viewModel.stepThreeCompleted = true
                                interactionListener?.goToNextPage()
                            }

                        } else {
                            val message = response.get("body").asString
                            userAlertClient.showDialogMessage(
                                "Error, Please retry", message
                            ) {
                                // Do nothing and don't let the user move forward.
                            }
                        }
                    }

                    override fun onError(message: String) {
                        userAlertClient.closeWaitDialog()
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                })
        } catch (exception: Exception) {
            userAlertClient.closeWaitDialog()
            Log.i("Exception", exception.message)
        }
    }

    private fun getComplexJson(details: ComplexDetails): JsonObject {
        val complex = JsonObject()
        Log.i("getComplexJson", Gson().toJson(details))
        complex.addProperty("Name", viewModel.complexDetails.complexName)
        Log.i("getComplexJson", details.complexName)
        complex.addProperty("Description", "");
        complex.addProperty("Parent", details.cityCode)
        Log.i("getComplexJson", details.cityCode)

        val attributes = JsonArray()

        val stateName = JsonObject()
        stateName.addProperty("Name", "STATE_NAME")
        stateName.addProperty("Value", details.stateName)
        Log.i("getComplexJson", "stateName " + details.stateName)
        attributes.add(stateName)


        val districtName = JsonObject()
        districtName.addProperty("Name", "DISTRICT_NAME")
        districtName.addProperty("Value", details.districtName)
        Log.i("getComplexJson", "districtName " + details.districtName)
        attributes.add(districtName)


        val cityName = JsonObject()
        cityName.addProperty("Name", "CITY_NAME")
        cityName.addProperty("Value", details.cityName)
        Log.i("getComplexJson", "cityName " + details.cityName)
        attributes.add(cityName)


        val stateCode = JsonObject()
        stateCode.addProperty("Name", "STATE_CODE")
        stateCode.addProperty("Value", details.stateCode)
        Log.i("getComplexJson", "stateCode " + details.stateCode)
        attributes.add(stateCode)


        val districtCode = JsonObject()
        districtCode.addProperty("Name", "DISTRICT_CODE")
        districtCode.addProperty("Value", details.districtCode)
        Log.i("getComplexJson", "districtCode " + details.districtCode)
        attributes.add(districtCode)


        val cityCode = JsonObject()
        cityCode.addProperty("Name", "CITY_CODE")
        cityCode.addProperty("Value", details.cityCode)
        Log.i("getComplexJson", "cityCode " + details.cityCode)
        attributes.add(cityCode)


        val latitude = JsonObject()
        latitude.addProperty("Name", "LATT")
        latitude.addProperty("Value", details.latitude)
        Log.i("getComplexJson", "latitude " + details.latitude)
        attributes.add(latitude)


        val longitude = JsonObject()
        longitude.addProperty("Name", "LONG")
        longitude.addProperty("Value", details.longitude)
        Log.i("getComplexJson", "longitude " + details.longitude)
        attributes.add(longitude)


        val client = JsonObject()
        client.addProperty("Name", "CLNT")
        client.addProperty("Value", details.client)
        Log.i("getComplexJson", "client " + details.client)
        attributes.add(client)


        val billingGroup = JsonObject()
        billingGroup.addProperty("Name", "BILL")
        billingGroup.addProperty("Value", details.billingGroup)
        Log.i("getComplexJson", "billingGroup " + details.billingGroup)
        attributes.add(billingGroup)


        val date = JsonObject()
        date.addProperty("Name", "DATE")
        date.addProperty("Value", details.date)
        Log.i("getComplexJson", "date " + details.date)
        attributes.add(date)


        val deviceType = JsonObject()
        deviceType.addProperty("Name", "DEVT")
        deviceType.addProperty("Value", details.deviceType)
        Log.i("getComplexJson", "deviceType " + details.deviceType)
        attributes.add(deviceType)


        val smartness = JsonObject()
        smartness.addProperty("Name", "SLVL")
        smartness.addProperty("Value", details.smartness)
        Log.i("getComplexJson", "smartness " + details.smartness)
        attributes.add(smartness)


        val wCCountMale = JsonObject()
        wCCountMale.addProperty("Name", "QMWC")
        wCCountMale.addProperty("Value", details.wCCountMale)
        Log.i("getComplexJson", "wCCountMale " + details.wCCountMale)
        attributes.add(wCCountMale)


        val wCCountFemale = JsonObject()
        wCCountFemale.addProperty("Name", "QFWC")
        wCCountFemale.addProperty("Value", details.wCCountFemale)
        Log.i("getComplexJson", "wCCountFemale " + details.wCCountFemale)
        attributes.add(wCCountFemale)


        val wCCountPD = JsonObject()
        wCCountPD.addProperty("Name", "QPWC")
        wCCountPD.addProperty("Value", details.wCCountPD)
        Log.i("getComplexJson", "wCCountPD " + details.wCCountPD)
        attributes.add(wCCountPD)


        val urinals = JsonObject()
        urinals.addProperty("Name", "QURI")
        urinals.addProperty("Value", details.urinals)
        Log.i("getComplexJson", "urinals " + details.urinals)
        attributes.add(urinals)


        val urinalCabins = JsonObject()
        urinalCabins.addProperty("Name", "QURC")
        urinalCabins.addProperty("Value", details.urinalCabins)
        Log.i("getComplexJson", "urinalCabins " + details.urinalCabins)
        attributes.add(urinalCabins)


        val bwt = JsonObject()
        bwt.addProperty("Name", "QBWT")
        bwt.addProperty("Value", details.bwt)
        Log.i("getComplexJson", "bwt " + details.bwt)
        attributes.add(bwt)


        val commissioningStatus = JsonObject()
        commissioningStatus.addProperty("Name", "COCO")
        commissioningStatus.addProperty("Value", details.commissioningStatus)
        Log.i("getComplexJson", "commissioningStatus " + details.commissioningStatus)
        attributes.add(commissioningStatus)


        val napkinVmCount = JsonObject()
        napkinVmCount.addProperty("Name", "QSNV")
        napkinVmCount.addProperty("Value", details.napkinVmCount)
        Log.i("getComplexJson", "napkinVmCount " + details.napkinVmCount)
        attributes.add(napkinVmCount)


        val napkinIncineratorCount = JsonObject()
        napkinIncineratorCount.addProperty("Name", "QSNI")
        napkinIncineratorCount.addProperty("Value", details.napkinIncineratorCount)
        Log.i("getComplexJson", "napkinIncineratorCount " + details.napkinIncineratorCount)
        attributes.add(napkinIncineratorCount)


        val napkinIncineratorManufacturer = JsonObject()
        napkinIncineratorManufacturer.addProperty("Name", "MSNI")
        napkinIncineratorManufacturer.addProperty("Value", details.napkinIncineratorManufacturer)
        Log.i("getComplexJson", "napkinIncineratorManufacturer " + details.napkinIncineratorManufacturer)
        attributes.add(napkinIncineratorManufacturer)


        val napkinVmManufacturer = JsonObject()
        napkinVmManufacturer.addProperty("Name", "MSNV")
        napkinVmManufacturer.addProperty("Value", details.napkinVmManufacturer)
        Log.i("getComplexJson", "napkinVmManufacturer " + details.napkinVmManufacturer)
        attributes.add(napkinVmManufacturer)


        val kioskArea = JsonObject()
        kioskArea.addProperty("Name", "AR_K")
        kioskArea.addProperty("Value", details.kioskArea)
        Log.i("getComplexJson", "kioskArea " + details.kioskArea)
        attributes.add(kioskArea)


        val waterAtmCapacity = JsonObject()
        waterAtmCapacity.addProperty("Name", "CWTM")
        waterAtmCapacity.addProperty("Value", details.waterAtmCapacity)
        Log.i("getComplexJson", "waterAtmCapacity " + details.waterAtmCapacity)
        attributes.add(waterAtmCapacity)

        val supervisorRoomSize = JsonObject()
        supervisorRoomSize.addProperty("Name", "ARSR")
        supervisorRoomSize.addProperty("Value", details.supervisorRoomSize)
        Log.i("getComplexJson", "supervisorRoomSize " + details.supervisorRoomSize)
        attributes.add(supervisorRoomSize)

        val manufacturer = JsonObject()
        manufacturer.addProperty("Name", "MANU")
        manufacturer.addProperty("Value", details.manufacturer)
        Log.i("getComplexJson", "manufacturer " + details.manufacturer)
        attributes.add(manufacturer)


        val civilPartner = JsonObject()
        civilPartner.addProperty("Name", "CIVL")
        civilPartner.addProperty("Value", details.civilPartner)
        Log.i("getComplexJson", "civilPartner " + details.civilPartner)
        attributes.add(civilPartner)


        val techProvider = JsonObject()
        techProvider.addProperty("Name", "TECH")
        techProvider.addProperty("Value", details.techProvider)
        Log.i("getComplexJson", "techProvider " + details.techProvider)
        attributes.add(techProvider)


        val oAndMPartner = JsonObject()
        oAndMPartner.addProperty("Name", "ONMP")
        oAndMPartner.addProperty("Value", details.oMPartner)
        Log.i("getComplexJson", "oMPartner " + details.oMPartner)
        attributes.add(oAndMPartner)


        val UUID = JsonObject()
        UUID.addProperty("Name", "UUID")
        UUID.addProperty("Value", details.uuid)
        Log.i("getComplexJson", "uuid " + details.uuid)
        attributes.add(UUID)


        val routerMobile = JsonObject()
        routerMobile.addProperty("Name", "ROUTER_MOBILE")
        routerMobile.addProperty("Value", details.routerMobile)
        Log.i("getComplexJson", "routerMobile " + details.routerMobile)
        attributes.add(routerMobile)


        val routerImei = JsonObject()
        routerImei.addProperty("Name", "ROUTER_IMEI")
        routerImei.addProperty("Value", details.routerImei)
        Log.i("getComplexJson", "routerImei " + details.routerImei)
        attributes.add(routerImei)


        val modifiedBy = JsonObject()
        modifiedBy.addProperty("Name", "MODIFIED_BY")
        modifiedBy.addProperty("Value", details.modifiedBy)
        Log.i("getComplexJson", "modifiedBy " + details.modifiedBy)
        attributes.add(modifiedBy)

        val thingGroupType = JsonObject()
        thingGroupType.addProperty("Name", "THINGGROUPTYPE")
        thingGroupType.addProperty("Value", details.thingGroupType)
        Log.i("getComplexJson", "thingGroupType " + details.thingGroupType)
        attributes.add(thingGroupType)


        val address = JsonObject()
        address.addProperty("Name", "ADDR")
        address.addProperty("Value", details.address)
        Log.i("getComplexJson", "Address " + details.address)
        attributes.add(address)

        complex.add("Attributes", attributes)
        return complex
    }

    override fun onStart() {
        super.onStart()
        Log.i(Debugging, "onStart: FragmentThree")
    }

    override fun onResume() {
        super.onResume()
        Log.i(Debugging, "onResume: FragmentThree")
    }


    override fun onPause() {
        super.onPause()
        Log.i(Debugging, "onPause: FragmentThree ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(Debugging, "onStop: FragmentThree ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(Debugging, "onDestroyView: FragmentThree ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(Debugging, "onDestroy: FragmentThree ")
    }


}