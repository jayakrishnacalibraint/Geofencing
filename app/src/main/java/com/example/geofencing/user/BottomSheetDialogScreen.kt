package com.example.geofencing.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.geofencing.LocationHelper
import com.example.geofencing.R
import com.example.geofencing.databinding.BottomsheetLayoutBinding
import com.example.geofencing.model.Appointment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BottomSheetDialogScreen {

    private val locationHelper = LocationHelper()

    fun showBottomSheetDialog(
        appointment: Appointment,
        context: Context,
        position: Int,
        currentLocationlatlng: LatLng
    ) {
        // Create a Bottom Sheet Dialog
        val bottomSheetDialog = BottomSheetDialog(context)
        val currentLocation = locationHelper.getPlaceNameFromCoordinates(
            context,
            currentLocationlatlng.latitude,
            currentLocationlatlng.longitude
        )
        val auth = Firebase.auth
        val database = Firebase.database.reference
        val bottomsheetLayoutBinding =
            BottomsheetLayoutBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(bottomsheetLayoutBinding.root)


        // Find the views in the Bottom Sheet Dialog layout
        val nameTextView = bottomsheetLayoutBinding.textViewName
        val appointmentLocationTextView = bottomsheetLayoutBinding.textViewAppointmentLocation
        val userLocationTextView = bottomsheetLayoutBinding.textViewUserLocation
        val statusTextView = bottomsheetLayoutBinding.textViewStatus
        val changeStatusBtn = bottomsheetLayoutBinding.changeStatusBtn

        // Set the item details in the dialog
        nameTextView.text = context.getString(R.string.name_label, appointment.name)
        appointmentLocationTextView.text =
            context.getString(R.string.appointment_location_label, appointment.location)
        userLocationTextView.text =
            context.getString(R.string.current_location_label, currentLocation)
        statusTextView.text = context.getString(R.string.status_label, appointment.status)
        bottomSheetDialog.show()

        val appointmentLocationLatLng =
            locationHelper.getLocationFromAddress(context, appointment.location)
        if (!locationHelper.isWithinGeofence(
                appointmentLocationLatLng!!,
                currentLocationlatlng,
                200.0
            )||appointment.status=="completed"
        ) {
            changeStatusBtn.visibility = View.INVISIBLE
        } else {

            changeStatusBtn.setOnClickListener {

                val appointmentId = "a${position + 1}"
                database.child("appointments").child(auth.uid.toString()).child(appointmentId)
                    .child("status").setValue("completed").addOnCompleteListener {
                        if (it.isSuccessful) {
                            statusTextView.text =
                                context.getString(R.string.status_label, "completed")

                            changeStatusBtn.visibility = View.INVISIBLE

                        }
                    }
            }
        }


        appointmentLocationTextView.setOnClickListener {
            val latLng = locationHelper.getLocationFromAddress(context, appointment.location)
            val latitude = latLng?.latitude
            val longitude = latLng?.longitude
            val label = appointment.location
            val uri = "geo:$latitude,$longitude?q=$latitude,$longitude($label)"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Handle the case where Maps app is not installed
                // You can redirect the user to the Google Play Store to download the app, for example.
                Toast.makeText(context, "Download maps from Playstore", Toast.LENGTH_SHORT).show()
            }
        }


    }

}