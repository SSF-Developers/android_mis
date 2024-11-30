package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure

import sukriti.ngo.mis.dataModel.dynamo_db.State

class SelectionStructure(){
    var state  :String =""
    var district :String=""
    var city : String =""
    var complex :String = ""

    constructor(state: String, district:String, city: String, complex:String ):this(){
        this.state=state
        this.district=district
        this.city=city
        this.complex=complex
    }
}