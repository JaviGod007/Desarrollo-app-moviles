package com.example.appticketescuela.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets_table")
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val prioridad: String = "Baja",
    val rutaFoto: String? = null
)