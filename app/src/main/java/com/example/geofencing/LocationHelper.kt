package com.example.geofencing

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationHelper {
    fun getPlaceNameFromCoordinates(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var address = ""
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val returnedAddress = addresses[0]
                    val stringBuilder = StringBuilder()
                    for (i in 0..returnedAddress.maxAddressLineIndex) {
                        stringBuilder.append(returnedAddress.getAddressLine(i)).append(", ")
                    }
                    address = stringBuilder.toString()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    fun getLocationFromAddress(
        context: Context,
        locationName: String
    ): LatLng? {
        val geocoder = Geocoder(context)
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (addresses!!.isNotEmpty()) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                return LatLng(latitude, longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun isWithinGeofence(
        appointmentLocation: LatLng,
        currentLocationlatlng: LatLng,
        radius: Double
    ): Boolean {
        // You can use the same logic as in the previous example to determine if the user is within the geofence.
        // Make sure to replace the user's current location with the coordinates obtained using geocoding.
        val distance = calculateDistance(currentLocationlatlng, appointmentLocation)
        return distance <= radius
    }

    fun calculateDistance(userLocation: LatLng, appointmentLocation: LatLng): Double {
        val earthRadius = 6371 // Earth's radius in kilometers
        val dLat = Math.toRadians(appointmentLocation.latitude - userLocation.latitude)
        val dLng = Math.toRadians(appointmentLocation.longitude - userLocation.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(userLocation.latitude)) * cos(Math.toRadians(appointmentLocation.latitude)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c) * 1000
    }
}