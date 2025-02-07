package com.insa.mygamelist.data

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.insa.mygamelist.ui.theme.MyGamesListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavHostController, itemId: Long) {
    val game = IGDB.games.find { it.id == itemId }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color.Cyan,
                    titleContentColor = Color.Black,
                ),
                title = { Text(game?.name ?: "Jeu non trouvé") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            game?.let {
                AsyncImage(
                    model = "https:" + getUrl(it.cover),
                    contentDescription = "Cover du jeu",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Nom: ${it.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Genres: " + it.genres.mapNotNull { genreId ->
                        IGDB.genres.find { genre -> genre.id == genreId }?.name
                    }.joinToString(", ")
                )
            } ?: Text(
                text = "Jeu introuvable",
                color = Color.Red,
                fontSize = 18.sp
            )
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
                    Log.d("TAG", "GameId" + (game.id).toString())
                    GameItem(game, { navController.navigate(Details(game.id)) })
                }
            }
        }
    }
}