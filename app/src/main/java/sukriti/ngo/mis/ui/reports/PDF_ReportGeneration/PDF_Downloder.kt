package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration

import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class PDF_Downloder(private val context: Context) : AsyncTask<String, Void, File>() {

    override fun doInBackground(vararg urls: String?): File? {
        val url = urls[0] ?: return null
        val fileName = "GraphicalReport.pdf"
        val directory =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

        directory?.mkdir()
        val file = File(directory, fileName)
//        if (!file.exists()) {
            try {
                Log.i("pdfDownloder", "downloadFile:  openStream")
                BufferedInputStream(URL(url).openStream()).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        Log.i("pdfDownloder", "downloadFile:  openStream :"+outputStream)
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.i("pdfDownloder", "downloadFile:  openStream completed")
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
//        }
        return file
    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)

        if (result != null) {
            Log.i("pdfDownloder", "downloadFile:  absolutePath :"+result.absolutePath)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123

        fun downloadFile(context: Context, url: String) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("pdfDownloder", "downloadFile: Started")
                PDF_Downloder(context).execute(url)
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }
}