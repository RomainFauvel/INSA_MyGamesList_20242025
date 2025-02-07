package com.insa.mygamelist.data

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.tooling.preview.Preview
import com.insa.mygamelist.ui.theme.MyGamesListTheme

@Composable
fun DetailsScreen(navController: NavHostController, itemId: Int?) {
    Column {
        Text("Détails de l'élément $itemId", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { navController.popBackStack() }) {
            Text("Retour")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(navController: NavHostController){
    MyGamesListTheme {
        Scaffold(topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = Color.Cyan,
                titleContentColor = Color.Black,
            ), title = { Text("My Games List") })
        }, modifier = Modifier.fillMaxSize()) { innerPadding ->
            LazyColumn (modifier =  Modifier.padding(innerPadding)) {
                items(IGDB.games) { game ->
                    GameItem(game, { navController.navigate(Details(game.id)) })
                }
            }
        }
    }
}