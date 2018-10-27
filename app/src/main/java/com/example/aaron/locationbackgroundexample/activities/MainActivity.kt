package com.example.aaron.locationbackgroundexample.activities

import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.aaron.locationbackgroundexample.R
import com.example.aaron.locationbackgroundexample.data.Globals
import com.example.aaron.locationbackgroundexample.services.LocationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var filter: IntentFilter? = null
    var receiver: InfoReceiver? = null
    var locService : LocationService? = null
    private val mServiceConnection = object : ServiceConnection {


        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locService = binder.service
            locService?.requestLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locService = null
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            registerReceiver(receiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        receiver?.let { receiver ->
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                    IntentFilter(Globals.ACTION_BROADCAST))
        }


    }
    override fun onStop() {
        unbindService(mServiceConnection)
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this,LocationService::class.java),mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this@MainActivity, Globals.PERMISSIONS, Globals.REQUEST_LOCATION_PERMISSION)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            Globals.REQUEST_LOCATION_PERMISSION -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    val t = Toast.makeText(applicationContext,getString(R.string.aceptaPermisos), Toast.LENGTH_LONG)
                    t.setGravity(Gravity.CENTER,0,0)
                    t.show()
                    finish()
                } else {
                    filter = IntentFilter()
                    filter?.addAction(Globals.ACTION_BROADCAST)
                    receiver = InfoReceiver()
                    try {
                        registerReceiver(receiver, filter)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    inner class InfoReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Globals.ACTION_BROADCAST) run {
                val location = intent.getStringExtra(Globals.ACTION_BROADCAST)
                lblLocation.text = "${lblLocation.text } \n $location"
            }
        }

    }
}
