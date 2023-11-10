package com.example.geofencing

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geofencing.model.Appointment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

        bottomSheetDialog.show()


    }


    inner class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewLocation: TextView = view.findViewById(R.id.textViewAppointmentLocation)
        val textViewStatus: TextView = view.findViewById(R.id.textViewUserLocation)
    }
}