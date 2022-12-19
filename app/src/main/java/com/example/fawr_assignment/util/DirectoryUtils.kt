package com.example.fawr_assignment.util

import android.os.Environment
import java.io.File

object DirectoryUtils {
    val DIRECTORY = "/Insta saver/"
    val DIRECTORY_FOLDER = File("${Environment.getExternalStorageDirectory()}/Download/Insta saver/")

    fun createFile(){
        if(!DIRECTORY_FOLDER.exists()){
            DIRECTORY_FOLDER.mkdirs()
        }
    }
}