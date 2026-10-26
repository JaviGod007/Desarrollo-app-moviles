package com.example.appticketescuela.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards //etiqueta q evita el choque de compatibilidad con java
interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTicket(ticket: Ticket): Long

    @Query("SELECT * FROM tickets_table ORDER BY id DESC")
    suspend fun obtenerTodosLosTickets(): List<Ticket>
}