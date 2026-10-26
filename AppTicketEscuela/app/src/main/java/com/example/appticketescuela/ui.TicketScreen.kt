package com.example.appticketescuela.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appticketescuela.data.AppDatabase
import com.example.appticketescuela.data.Ticket
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun TicketScreen() {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Baja") }
    var rutaFoto by remember { mutableStateOf<String?>(null) } // Variable para la foto

    var listaTickets by remember { mutableStateOf(listOf<Ticket>()) }

    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dao = remember { AppDatabase.obtenerBaseDatos(contexto).ticketDao() }

    fun recargarTickets() {
        coroutineScope.launch {
            listaTickets = dao.obtenerTodosLosTickets()
        }
    }

    LaunchedEffect(Unit) { recargarTickets() }

    // 1. Configuracion de la cámara
    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Si hay foto, la guardamos como archivo en el teléfono
            val archivo = File(contexto.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
            archivo.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            rutaFoto = archivo.absolutePath // Guardamos la ruta del archivo
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gestión de Tickets", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Asunto (Ej: tele sala 8)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().height(90.dp), maxLines = 4)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Nivel de Urgencia:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.Start))
        // boton de urgencia
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { prioridad = "Baja" }, colors = ButtonDefaults.buttonColors(containerColor = if (prioridad == "Baja") Color(0xFF4CAF50) else Color.LightGray)) { Text("Baja") }
            Button(onClick = { prioridad = "Media" }, colors = ButtonDefaults.buttonColors(containerColor = if (prioridad == "Media") Color(0xFFFFC107) else Color.LightGray)) { Text("Media", color = if (prioridad == "Media") Color.Black else Color.White) }
            Button(onClick = { prioridad = "Urgente" }, colors = ButtonDefaults.buttonColors(containerColor = if (prioridad == "Urgente") Color(0xFFF44336) else Color.LightGray)) { Text("Urgente") }
        }

        // 2. boton para sacar lafoto
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { launcherCamara.launch(null) }) {
                Text("Tomar Foto")
            }

            // Si sacamos la foto mostramos un cuadrito pequeño
            if (rutaFoto != null) {
                Spacer(modifier = Modifier.width(16.dp))
                AsyncImage(
                    model = File(rutaFoto!!),
                    contentDescription = "Vista previa",
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (titulo.isBlank() || descripcion.isBlank()) {
                    Toast.makeText(contexto, "Completa asunto y descripción", Toast.LENGTH_SHORT).show()
                } else {
                    coroutineScope.launch {
                        dao.insertarTicket(Ticket(titulo = titulo, descripcion = descripcion, prioridad = prioridad, rutaFoto = rutaFoto))
                        titulo = ""
                        descripcion = ""
                        prioridad = "Baja"
                        rutaFoto = null // Limpiamos la foto
                        Toast.makeText(contexto, "¡Ticket guardado!", Toast.LENGTH_SHORT).show()
                        recargarTickets()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Guardar Ticket") }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tickets Registrados", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaTickets) { ticket ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Asunto: ${ticket.titulo}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Detalle: ${ticket.descripcion}", style = MaterialTheme.typography.bodyMedium)
                        }

                        //si se agrega foto al ticket sale en el registro
                        if (ticket.rutaFoto != null) {
                            AsyncImage(
                                model = File(ticket.rutaFoto),
                                contentDescription = "Foto adjunta",
                                modifier = Modifier.size(60.dp).padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        val colorFondo = when(ticket.prioridad) {
                            "Baja" -> Color(0xFF4CAF50)
                            "Media" -> Color(0xFFFFC107)
                            "Urgente" -> Color(0xFFF44336)
                            else -> Color.Gray
                        }

                        Surface(
                            color = colorFondo,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = ticket.prioridad,
                                color = if(ticket.prioridad == "Media") Color.Black else Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}