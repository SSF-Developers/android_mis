package sukriti.ngo.mis.utils
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File
class MultiUploaderS3Client(bucketName:String) {

    var bucketName = bucketName

    fun uploadMultiple(fileToKeyUploads: MutableMap<String,File>, transferUtility: TransferUtility): Completable? {
        return transferUtility(transferUtility)
            .flatMapCompletable { transferUtility ->
                Observable.fromIterable(fileToKeyUploads.entries)
                    .flatMapCompletable { entry ->
                        uploadSingle(
                            transferUtility,
                            entry.value,
                            entry.key
                        )
                    }
            }
    }

    private fun transferUtility(transferUtility: TransferUtility): Single<TransferUtility?> {
        return Single.create { emitter ->
            emitter.onSuccess(
                transferUtility
            )
        }
    }

    private fun uploadSingle(
        transferUtility: TransferUtility?,
        aLocalFile: File?,
        toRemoteKey: String?
    ): Completable? {
        return Completable.create { emitter ->
            transferUtility?.upload(bucketName,toRemoteKey, aLocalFile)
                ?.setTransferListener(object : TransferListener {
                    override fun onStateChanged(
                        id: Int,
                        state: TransferState
                    ) {
                        if (TransferState.FAILED == state) {
                            emitter.onError(Exception("Transfer state was FAILED."))
                        } else if (TransferState.COMPLETED == state) {
                            emitter.onComplete()
                        }
                    }

                    override fun onProgressChanged(
                        id: Int,
                        bytesCurrent: Long,
                        bytesTotal: Long
                    ) {
                    }

                    override fun onError(id: Int, exception: Exception) {
                        emitter.onError(exception)
                    }
                })
        }
    }

}