package com.example.InstaGlimpse.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.InstaGlimpse.Models.DownloadModel
import com.example.InstaGlimpse.R
import com.example.InstaGlimpse.adapters.DownloadAdapter
import com.example.InstaGlimpse.room.AppDatabase
import com.example.InstaGlimpse.room.DownloadDao
import com.example.InstaGlimpse.util.ClickListener
import com.example.InstaGlimpse.util.PathUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class DownloadsActivity : AppCompatActivity(), ClickListener {

    var adapter: DownloadAdapter? = null
    var downloadModels: MutableList<DownloadModel> = ArrayList()

    lateinit var downloadDao : DownloadDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloads)

        downloadDao = AppDatabase.invoke(this).channelDao()

        val data_list = findViewById<RecyclerView>(R.id.data_list_kt)
        adapter = DownloadAdapter( downloadModels, this@DownloadsActivity)
        data_list.layoutManager = LinearLayoutManager(this@DownloadsActivity)
        data_list.adapter = adapter
        getAllDownloadsFromDataBase()
        handleIntent()

    }

    private fun handleIntent() {
        val intent = intent
        if (intent != null) {
            val action = intent.action
            val type = intent.type
            if (Intent.ACTION_SEND == action && type != null) {
                if (type.equals("text/plain", ignoreCase = true)) {
                    handleTextData(intent)
                } else if (type.startsWith("image/")) {
                    handleImage(intent)
                } else if (type.equals("application/pdf", ignoreCase = true)) {
                    handlePdfFile(intent)
                }
            } else if (Intent.ACTION_SEND_MULTIPLE == action && type != null) {
                if (type.startsWith("image/")) {
                    handleMultipleImage(intent)
                }
            }
        }
    }

    private fun getAllDownloadsFromDataBase() {
        GlobalScope.launch(Dispatchers.IO){
            val downloadModelsLocal: List<DownloadModel> = getDownloads()
            if (downloadModelsLocal != null) {
                if (downloadModelsLocal.size > 0) {
                    downloadModels.addAll(downloadModelsLocal)
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun handlePdfFile(intent: Intent) {
        val pdffile = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (pdffile != null) {
            Log.d("Pdf File Path : ", "" + pdffile.path)
        }
    }

    private fun handleImage(intent: Intent) {
        val image = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (image != null) {
            Log.d("Image File Path : ", "" + image.path)
        }
    }

    private fun handleTextData(intent: Intent) {
        val textdata = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (textdata != null) {
            Log.d("Text Data : ", "" + textdata)
        }
    }

    private fun handleMultipleImage(intent: Intent) {
        val imageList = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageList != null) {
            for (uri in imageList) {
                Log.d("Path ", "" + uri.path)
            }
        }
    }

    override fun onCLickItem(file_path: String?) {
        Toast.makeText(this, "File Path :$file_path", Toast.LENGTH_SHORT).show()
        openFile(file_path.toString())
    }

    private fun getDownloads(): List<DownloadModel> {
        return downloadDao.getAllDownloads()
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@DownloadsActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@DownloadsActivity,
                "Please Give Permission to Upload File",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@DownloadsActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@DownloadsActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@DownloadsActivity,
                    "Permission Successfull",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this@DownloadsActivity, "Permission Failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun openFile(fileurl: String) {
        var fileurl: String? = fileurl
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission()
                return
            }
        }
        try {
            fileurl = PathUtil.getPath(this@DownloadsActivity, Uri.parse(fileurl))
            val file = File(fileurl)
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
            var type = mimeTypeMap.getMimeTypeFromExtension(ext)
            if (type == null) {
                type = "*/*"
            }
            val intent = Intent(Intent.ACTION_VIEW)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contne = FileProvider.getUriForFile(
                    this@DownloadsActivity,
                    "com.example.fawr_assignment",
                    file
                )
                intent.setDataAndType(contne, type)
            } else {
                intent.setDataAndType(Uri.fromFile(file), type)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to Open File", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }
}