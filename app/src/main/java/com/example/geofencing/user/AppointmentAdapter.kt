package com.example.geofencing.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.geofencing.LocationHelper
import com.example.geofencing.R
import com.example.geofencing.model.Appointment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AppointmentAdapter(
    private val context: Context,
    private val appointmentList: List<Appointment>,
    private val currentLocationlatlng: LatLng
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    private val locationHelper = LocationHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointmentList[position]
        val name = appointment.name
        val location = appointment.location
        val status = appointment.status
        holder.textViewName.text = "Name: $name"
        holder.textViewLocation.text = "Location: $location"
        holder.textViewStatus.text = "Status: $status"


        holder.itemView.setOnClickListener {
            showBottomSheetDialog(appointment, context, position)
        }


    }


    private fun showBottomSheetDialog(item: Appointment, context: Context, position: Int) {
        // Create a Bottom Sheet Dialog
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.bottomsheet_layout)

        // Find the views in the Bottom Sheet Dialog layout
        val nameTextView: TextView? = bottomSheetDialog.findViewById(R.id.textViewName)
        val appointmentLocationTextView: TextView? =
            bottomSheetDialog.findViewById(R.id.textViewAppointmentLocation)
        val userLocationTextView: TextView? =
            bottomSheetDialog.findViewById(R.id.textViewUserLocation)
        val statusTextView: TextView? = bottomSheetDialog.findViewById(R.id.textViewStatus)
        val changeStatusBtn: TextView? = bottomSheetDialog.findViewById(R.id.changeStatusBtn)

        // Set the item details in the dialog
        nameTextView?.text = "Name :${item.name}"
        appointmentLocationTextView?.text = "AppointmentLocation :${item.location}"
        var currentLocation = locationHelper.getPlaceNameFromCoordinates(
            context, currentLocationlatlng.latitude, currentLocationlatlng.longitude
        )
        userLocationTextView?.text = "CurrentLocation :${currentLocation}"

        val auth = Firebase.auth
        val database = Firebase.database.reference

        statusTextView?.text = "Status :${item.status}"
        bottomSheetDialog.show()

        val appointmentLocationLatLng =
            locationHelper.getLocationFromAddress(context, item.location)
        if (!locationHelper.isWithinGeofence(
                appointmentLocationLatLng!!, currentLocationlatlng, 200.0
            )
        ) {
            changeStatusBtn?.visibility = View.INVISIBLE
        } else {

            changeStatusBtn?.setOnClickListener {
                changeStatusBtn?.visibility = View.INVISIBLE

                val appointmentId = "a${position + 1}"
                database.child("appointments").child(auth.uid.toString()).child(appointmentId)
                    .child("status").setValue("completed").addOnCompleteListener {
                        if (it.isSuccessful) {

                            statusTextView?.text = "Status :completed "

                        }
                    }


            }


        }


        appointmentLocationTextView?.setOnClickListener {
            val latLng = locationHelper.getLocationFromAddress(context, item.location)
            val latitude = latLng?.latitude
            val longitude = latLng?.longitude
            val label = item.location
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


    inner class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewLocation: TextView = view.findViewById(R.id.textViewAppointmentLocation)
        val textViewStatus: TextView = view.findViewById(R.id.textViewUserLocation)
    }
}