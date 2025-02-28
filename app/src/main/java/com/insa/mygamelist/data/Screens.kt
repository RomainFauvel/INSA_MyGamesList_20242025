package com.insa.mygamelist.data

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    game?.let {
                        Text(
                            text = it.name,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = "https:" + getUrl(it.cover),
                            contentDescription = "Cover du jeu",
                            modifier = Modifier.size(200.dp),
                            alignment = Alignment.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it.genres.mapNotNull { genreId ->
                                IGDB.genres.find { genre -> genre.id == genreId }?.name
                            }.joinToString(", "),
                            fontStyle = FontStyle.Italic
                        )
                    } ?: Text(
                        text = "Jeu introuvable",
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                }

                item {
                    val platformLogosIds = game?.platforms?.mapNotNull { p -> IGDB.platforms.find { it.id == p }?.platform_logo }
                    val platformLogos = platformLogosIds?.mapNotNull { id -> IGDB.platforms_logos.find { it.id == id } }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(platformLogos ?: emptyList()) { platformLogo ->
                            AsyncImage(
                                model = "https:" + platformLogo.url,
                                contentDescription = "Platform logo",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                }

                item {
                    game?.summary?.let {
                        Text(
                            text = it,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }

    MyGamesListTheme {
        Scaffold(
            topBar = { SearchAppBar(searchText) { searchText = it } },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            val filteredGames = IGDB.games.filter { game ->
                game.name.contains(searchText, ignoreCase = true) ||
                        game.genres.any { genreId ->
                            val genre = IGDB.genres.find { it.id == genreId }
                            genre?.name?.contains(searchText, ignoreCase = true) == true
                        }||
                        game.platforms.any { platformId ->
                            val platform = IGDB.platforms.find { it.id == platformId }
                            platform?.name?.contains(searchText, ignoreCase = true) == true
                        }
            }
            if(filteredGames.isEmpty()){
                Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    Text(
                        "No match :(",
                        textAlign = TextAlign.Center
                    )
                }

            }
            else{
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(filteredGames) { game ->
                        Log.d("TAG", "GameId" + (game.id).toString())
                        GameItem(game) { navController.navigate(Details(game.id)) }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(searchText: String, onSearchTextChange: (String) -> Unit) {
    var isSearching by remember { mutableStateOf(false) }

    TopAppBar(
        colors = topAppBarColors(
            containerColor = Color.Cyan,
            titleContentColor = Color.Black,
        ),
        title = {
            if (isSearching) {
                TextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    placeholder = { Text("Rechercher...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("Mon Application")
            }
        },
        actions = {
            IconButton(onClick = { isSearching = !isSearching }) {
                Icon(
                    imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Rechercher"
                )
            }
        }
    )
}
