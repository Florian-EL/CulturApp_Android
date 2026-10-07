package com.example.culturapp_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "serie")
data class Serie(

    @PrimaryKey
    val id: Int?,

    val titre: String?,

    val note: Int?,

    val type: String?,

    val genre: String?,

    val vo: String?,

    val updated: String?,

    val etat: String?,

    val sortie: Int?,

    val nb_vu: Int?,

    val nb_ep_vu: Int?,

    val nb_ep_res: Int?,

    val nb_ep_tot: Int?,

    val priorite: Int?,

    val notice: String?,

    val titre_principal: String?,

    val titre_secondaire: String?,

    val studio: String?,

    )