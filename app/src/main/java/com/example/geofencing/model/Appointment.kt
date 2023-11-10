package com.example.geofencing.model

data class Appointment(val name : String="" ,val location : String ="" , val status :String=""){
    companion object{
        val statusOptions = arrayOf("Pending", "Completed")
    }
}