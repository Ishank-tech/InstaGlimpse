package com.example.InstaGlimpse.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.InstaGlimpse.R
import com.example.InstaGlimpse.adapters.CategoryAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import papayacoders.instastory.Stories

@AndroidEntryPoint
class RootActivity : AppCompatActivity() {

    val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager

        val adapter = CategoryAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        val allDownloads = findViewById<ImageView>(R.id.all_downloads)
        allDownloads.setOnClickListener{
            val intent = Intent(this, DownloadsActivity::class.java)
            startActivity(intent)
        }

        val logout = findViewById<ImageView>(R.id.logout)
        logout.setOnClickListener{
            Stories.logout(this)
        }

        checkPermission()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Please accept all permissions", Toast.LENGTH_LONG).show()
            }
            checkPermission()
        }
    }

    private fun checkPermission() : Boolean{
        var result : Int
        val listPermissionNeeded : MutableList<String> = ArrayList()
        for(p in permissions){
            result = ContextCompat.checkSelfPermission(this, p)
            if(result != PackageManager.PERMISSION_GRANTED){
                listPermissionNeeded.add(p)
            }
        }
        if(listPermissionNeeded.isNotEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(), 0)
            return false
        }
        return true
    }

}