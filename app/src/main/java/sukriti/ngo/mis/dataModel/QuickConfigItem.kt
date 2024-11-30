package sukriti.ngo.mis.dataModel

data class QuickConfigItem(
    var title: String = "",
    var description: String = "",
    var type: Type) {

    enum class Type {
        USAGE_CHARGE_CONFIG, FLUSH_CONFIG, FLOOR_CLEAN_CONFIG, LIGHT_FAN_CONFIG, DATA_REQUEST_CONFIG
    }
}