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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            game?.let {
                Text(text = it.name,
                    fontSize = 30.sp, fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline)
                AsyncImage(
                    model = "https:" + getUrl(it.cover),
                    contentDescription = "Cover du jeu",
                    modifier = Modifier.size(200.dp),
                    alignment = Alignment.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Genres: " + it.genres.mapNotNull { genreId ->
                        IGDB.genres.find { genre -> genre.id == genreId }?.name
                    }.joinToString(", "),
                    fontStyle = FontStyle.Italic

                )
            } ?: Text(
                text = "Jeu introuvable",
                color = Color.Red,
                fontSize = 18.sp
            )
            val platformLogosIds = game?.platforms?.mapNotNull { p -> IGDB.platforms.find { it.id == p }?.platform_logo }
            val platformLogos= platformLogosIds?.mapNotNull {id->IGDB.platforms_logos.find{it.id==id} }
            Row{
                platformLogos?.forEach({
                    AsyncImage(
                        model = "https:" + it.url,
                        contentDescription = "platform logo",
                        modifier = Modifier.size(80.dp)
                            .padding(horizontal=10.dp)
                    )

                })
            }
            game?.summary?.let {
                Text(
                    text= it,
                    fontSize = 20.sp
                )
            }


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