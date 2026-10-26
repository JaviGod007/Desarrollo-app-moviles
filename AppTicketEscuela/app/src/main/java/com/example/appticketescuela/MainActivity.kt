package com.example.appticketescuela

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appticketescuela.ui.LoginScreen
import com.example.appticketescuela.ui.TicketScreen
import com.example.appticketescuela.ui.theme.AppTicketEscuelaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTicketEscuelaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //los viajes entre pantallas
                    val navController = rememberNavController()

                    //"login"
                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(
                                onLoginClick = {
                                    // Al hacer clic, viajamos a la ruta "tickets"
                                    navController.navigate("tickets")
                                }
                            )
                        }

                        composable("tickets") {
                            TicketScreen()
                        }

                    }
                }
            }
        }
    }
}