package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure

class StateStructure{
    var name:String?=null
    var districts: ArrayList<DistrictStructure> =ArrayList()

    fun addDistrict(district: DistrictStructure){
        districts.add(district)
    }

}