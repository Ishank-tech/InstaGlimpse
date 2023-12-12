package com.example.InstaGlimpse.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.InstaGlimpse.Models.DownloadModel

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloadsTable")
    fun getAllDownloads(): List<DownloadModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertDownload(download: DownloadModel)

}