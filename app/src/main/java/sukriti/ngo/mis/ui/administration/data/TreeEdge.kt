package sukriti.ngo.mis.ui.administration.data

import java.lang.reflect.Constructor

data class TreeEdge(
    var stateIndex: Int,
    var districtIndex: Int,
    var cityIndex: Int,
    var complexIndex: Int){
    constructor(stateIndex: Int) : this(stateIndex,-1,-1,-1) {
    }
    constructor(stateIndex: Int,districtIndex: Int) : this(stateIndex,districtIndex,-1,-1) {
    }
    constructor(stateIndex: Int,districtIndex: Int,cityIndex: Int) : this(stateIndex,districtIndex,cityIndex,-1) {
    }
}




