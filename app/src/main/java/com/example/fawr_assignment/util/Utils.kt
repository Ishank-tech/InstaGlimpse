package com.example.fawr_assignment.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.fawr_assignment.Models.DownloadModel
import com.example.fawr_assignment.activities.DownloadsActivity
import com.example.fawr_assignment.room.AppDatabase
import com.example.fawr_assignment.room.DownloadDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object Utils {

    lateinit var downloadDao : DownloadDao
    val downloadModel = DownloadModel()

    fun startDownload(downloadPath : String, destinationPath : String, context : Context, filename : String){

        downloadDao = AppDatabase.invoke(context).channelDao()

        val request = DownloadManager.Request(Uri.parse(downloadPath))

        val extension = MimeTypeMap.getFileExtensionFromUrl(downloadPath)

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        request.setMimeType(mimeType)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setDescription("Downloading File...")
        request.setTitle(filename)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destinationPath + filename)
        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
        saveFileToDataBase(downloadPath, mimeType.toString(), filename)
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        MediaScannerConnection.scanFile(context, arrayOf(File(Environment.DIRECTORY_DOWNLOADS +"/"+destinationPath+filename).absolutePath),null){
                _, _ ->
        }

    }

    fun setChangeItemFilePath(path: String?, context: Context) {
        downloadModel.file_path = path
        downloadDao.upsertDownload(downloadModel)
        val intent = Intent(context, DownloadsActivity::class.java)
        context.startActivity(intent)
        Toast.makeText(
            context,
            "Downloading Complete",
            Toast.LENGTH_SHORT
        ).show()
    }

    var onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent) {
            val dm =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val query = DownloadManager.Query()
            query.setFilterById(id)
            val cursor =
                dm?.query(DownloadManager.Query().setFilterById(id))
            cursor?.moveToFirst()
            val downloaded_path =
                cursor?.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            setChangeItemFilePath(downloaded_path, context)
        }
    }

    fun isNullOrEmpty(s: String?): Boolean {
        return s == null || s.isEmpty() || s.equals("null", ignoreCase = true) || s.equals(
            "0",
            ignoreCase = true
        )
    }


    private fun saveFileToDataBase(
        url: String,
        mimeType: String,
        filename : String
    ) {
        GlobalScope.launch {
            val currentnum: Number = downloadDao.getAllDownloads().size
            val nextId = currentnum.toInt() + 1
            downloadModel.id = nextId
            downloadModel.title = filename
            downloadModel.file_path = ""
            downloadDao.upsertDownload(downloadModel)
        }
    }

}