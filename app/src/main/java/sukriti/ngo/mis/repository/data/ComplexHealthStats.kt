package sukriti.ngo.mis.repository.data

import java.util.*

data class ComplexHealthStats(
    var complexName: String,
    var totalMwc: Int = 0,
    var faultyMwc: Int = 0,
    var totalFwc: Int = 0,
    var faultyFwc: Int = 0,
    var totalPwc: Int = 0,
    var faultyPwc: Int = 0,
    var totalMur: Int = 0,
    var faultyMur: Int = 0,
    var totalBwt: Int = 0,
    var faultyBwt: Int = 0
) {

    constructor(_complexName: String) : this(_complexName, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) {

    }

    fun getTotalFaultyCount(): Int {
        return faultyMwc + faultyFwc + faultyPwc + faultyMur + faultyBwt;
    }

    fun incrementTotalMwc() {
        totalMwc++;
    }

    fun incrementTotalFwc() {
        totalFwc++;
    }

    fun incrementTotalPwc() {
        totalPwc++;
    }

    fun incrementTotalMur() {
        totalMur++;
    }

    fun incrementTotalBwt() {
        totalBwt++;
    }

    fun incrementFaultyMwc() {
        faultyMwc++;
    }

    fun incrementFaultyFwc() {
        faultyFwc++;
    }

    fun incrementFaultyPwc() {
        faultyPwc++;
    }

    fun incrementFaultyMur() {
        faultyMur++;
    }

    fun incrementFaultyBwt() {
        faultyBwt++;
    }
}
