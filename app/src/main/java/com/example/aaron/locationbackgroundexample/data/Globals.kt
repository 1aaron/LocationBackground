package com.example.aaron.locationbackgroundexample.data

import android.Manifest

object Globals {
    val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    const val REQUEST_CAMERA_PERMISSION: Int = 100
}