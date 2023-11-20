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
import com.example.geofencing.databinding.BottomsheetLayoutBinding
import com.example.geofencing.databinding.SingleAppointmentBinding
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {

        val singleAppointmentBinding =
            SingleAppointmentBinding.inflate(LayoutInflater.from(context), parent, false)
        return AppointmentViewHolder(singleAppointmentBinding)
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointmentList[position]
        val name = appointment.name
        val appointmentLocation = appointment.location
        val status = appointment.status

        holder.singleAppointmentBinding.textViewName.text = context.getString(R.string.name_label, name)
        holder.singleAppointmentBinding.textViewAppointmentLocation.text = context.getString(R.string.appointment_location_label, appointmentLocation)
        holder.singleAppointmentBinding.textViewAppointmentStatus.text = context.getString(R.string.status_label, status)


        holder.itemView.setOnClickListener {
            BottomSheetDialogScreen().showBottomSheetDialog(appointment, context, position,currentLocationlatlng)
        }

    }


    class AppointmentViewHolder(val singleAppointmentBinding: SingleAppointmentBinding) :RecyclerView.ViewHolder(singleAppointmentBinding.root){}



}

