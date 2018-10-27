package com.example.aaron.locationbackgroundexample.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.aaron.locationbackgroundexample.R
import com.example.aaron.locationbackgroundexample.data.Globals

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this@MainActivity, Globals.PERMISSIONS, Globals.REQUEST_CAMERA_PERMISSION)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            Globals.REQUEST_CAMERA_PERMISSION -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    val t = Toast.makeText(applicationContext,getString(R.string.aceptaPermisos), Toast.LENGTH_LONG)
                    t.setGravity(Gravity.CENTER,0,0)
                    t.show()
                    finish()
                }
            }
        }
    }
}
