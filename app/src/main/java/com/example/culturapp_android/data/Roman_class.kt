package com.example.culturapp_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roman")
data class Roman(

    @PrimaryKey
    val id: Int?,

    val titre: String?,

    val auteur: String?,

    val note: Int?,

    val type: String?,

    val genre: String?,

    val updated: String?,

    val etat: String?,

    val nb_vu: Int?,

    val nb_ep_vu: Int?,

    val nb_ep_res: Int?,

    val nb_ep_tot: Int?,

    val possede: String?,

    val notice: String?,

    val titre_principal: String?,

    val titre_secondaire: String?,

    )