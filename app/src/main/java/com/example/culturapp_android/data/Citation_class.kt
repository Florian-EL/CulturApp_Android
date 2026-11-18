package com.example.culturapp_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citation")
data class Citation(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val citation: String?,

    val oeuvre: String?,

    val personnage: String?,

    val artiste: String?,

    )
