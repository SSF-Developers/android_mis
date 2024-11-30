package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure

class CitiesStructure{
    var name:String ?=null
    val complexes: ArrayList<ComplexesStructure> =ArrayList()

    fun addComplex(complex: ComplexesStructure) {
        complexes.add(complex)
    }

}