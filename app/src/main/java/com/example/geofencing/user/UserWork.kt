package com.example.geofencing.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geofencing.R
import com.example.geofencing.admin.UsersListScreen
import com.example.geofencing.databinding.ActivityUserWorkBinding
import com.example.geofencing.model.Appointment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class UserWork : AppCompatActivity() {
    private lateinit var userWorkBinding: ActivityUserWorkBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var appointmentRecyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userWorkBinding = ActivityUserWorkBinding.inflate(layoutInflater)
        setContentView(userWorkBinding.root)
        appointmentRecyclerView = userWorkBinding.appointmentRecycler

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.appointments)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = Firebase.auth

        database = Firebase.database.reference.child("appointments")


    }


    override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (isLocationPermissionGranted()) {
                fetchLocation()

            } else {
                requestLocationPermission()
            }
        }
    }

    private fun navigateToUserListScreen() {
        val intent = Intent(this, UsersListScreen::class.java)
        startActivity(intent)
        finishAffinity()
    }


    private fun isLocationPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
        )
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                currentLocation = LatLng(latitude, longitude)
                setUpRecyclerView(currentLocation)
            }
        }
    }

    private fun setUpRecyclerView(currentLocation: LatLng) {
        val appointments = ArrayList<Appointment>()

        val adapter = AppointmentAdapter(this, appointments, currentLocation)
        appointmentRecyclerView.adapter = adapter
        appointmentRecyclerView.layoutManager = LinearLayoutManager(this)


        database.child(auth.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the existing data in the list
                appointments.clear()
                // Iterate through the dataSnapshot to retrieve the updated data
                for (appointmentSnapshot in dataSnapshot.children) {
                    val appointment = appointmentSnapshot.getValue<Appointment>()
                    if (appointment != null) {
                        appointments.add(appointment)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("jay", "error received: $databaseError")

            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sign_out, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        auth.signOut()
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        when {
            currentUser == null -> navigateToLoginScreen()
            currentUser.email == getString(R.string.admin_email) -> navigateToUserListScreen()
        }
    }
}