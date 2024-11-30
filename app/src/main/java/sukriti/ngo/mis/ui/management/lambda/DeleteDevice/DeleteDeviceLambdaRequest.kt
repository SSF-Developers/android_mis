package sukriti.ngo.mis.ui.management.lambda.DeleteDevice

class DeleteDeviceLambdaRequest(
    val command: String,
    val enterpriseId: String,
    val deviceId: String?,
    val serial_number: String?
) {

}