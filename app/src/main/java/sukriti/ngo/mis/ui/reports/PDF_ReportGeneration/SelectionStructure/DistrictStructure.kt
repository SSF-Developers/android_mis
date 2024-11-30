package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure

class DistrictStructure{
    var name:String?=null
    val cities: ArrayList<CitiesStructure> =ArrayList()

    fun addCiti(citi: CitiesStructure){
        cities.add(citi)
    }
}