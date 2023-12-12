package com.example.InstaGlimpse.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.InstaGlimpse.Models.DownloadModel

@Database(entities = [DownloadModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): DownloadDao
    companion object{
        @Volatile
        private  var instance: AppDatabase? = null
        private  val LOCK=Any()
        operator  fun invoke(context: Context)= instance?: synchronized(LOCK){
            instance?:createDatabase(context).also {it->
                instance=it
            }
        }
        private  fun  createDatabase(context: Context)= Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "downloads.db"
        ).allowMainThreadQueries().build()
    }
}