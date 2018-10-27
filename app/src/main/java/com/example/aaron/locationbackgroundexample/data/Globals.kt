package com.example.aaron.locationbackgroundexample.data

import android.Manifest

object Globals {
    val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    const val REQUEST_LOCATION_PERMISSION: Int = 100
    const val APP_NAME: String = "Location test"
    const val ACTION_BROADCAST: String = "broadcastLocation"
}