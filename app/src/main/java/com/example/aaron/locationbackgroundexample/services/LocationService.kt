package com.example.aaron.locationbackgroundexample.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.aaron.locationbackgroundexample.activities.MainActivity
import com.example.aaron.locationbackgroundexample.data.Globals
import com.google.android.gms.location.*
import com.example.aaron.locationbackgroundexample.R
import com.example.aaron.locationbackgroundexample.data.Globals.ACTION_BROADCAST

class LocationService : Service() {
    var mLocationRequest = LocationRequest()

    private val mBinder = LocalBinder()
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 15000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val NOTIFICATION_ID = 12345678
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null
    private var notificationManager: NotificationManager? = null
    private val CHANNEL_ID = "channel_01"

    override fun onCreate() {
        Log.e(Globals.APP_NAME,"on create service")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult!!.lastLocation)
            }
        }
        createLocationRequest()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.setSound(null, null)

            // Set the Notification Channel for the Notification Manager.
            notificationManager?.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun requestLocationUpdates() {
        Log.i(Globals.APP_NAME, "Requesting location updates")
        startService(Intent(applicationContext, LocationService::class.java))
        try {
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest,
                    locationCallBack, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Log.e(Globals.APP_NAME, "Lost location permission. Could not request updates. $unlikely")
        }

    }

    fun removeLocationUpdates() {
        Log.i(Globals.APP_NAME, "Removing location updates")
        try {
            mFusedLocationClient?.removeLocationUpdates(locationCallBack)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e(Globals.APP_NAME, "Lost location permission. Could not remove updates. $unlikely")
        }

    }

    inner class LocalBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e(Globals.APP_NAME,"onBind")
        stopForeground(true)
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.e(Globals.APP_NAME,"onReBind")
        stopForeground(true)
        super.onRebind(intent)
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(Globals.APP_NAME,"un bind")
        startForeground(NOTIFICATION_ID, getNotification())
        return true
    }

    private fun getNotification(): Notification {

        // The PendingIntent to launch activity.
        val activityPendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, MainActivity::class.java), 0)

        val builder = NotificationCompat.Builder(this)
                .setContentText("Continuing work")
                .setContentIntent(activityPendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(null)
                .setWhen(System.currentTimeMillis())

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }

        return builder.build()
    }

    private fun onNewLocation(location: Location?) {
        Log.e(Globals.APP_NAME, "New location: " + location!!)
        val intent = Intent(Globals.ACTION_BROADCAST)
        intent.putExtra(ACTION_BROADCAST, "lat: ${location.latitude} lon:${location.longitude}")
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
}
