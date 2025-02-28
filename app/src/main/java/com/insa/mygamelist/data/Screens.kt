package com.insa.mygamelist.data
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.insa.mygamelist.ui.theme.MyGamesListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavHostController, itemId: Long) {
    val game = IGDB.games.find { it.id == itemId }

    var isFavorite by remember { mutableStateOf(game?.is_favorite ?: false) }

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            isFavorite = !isFavorite
                            game.is_favorite = isFavorite
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                                tint = Color.Red
                            )
                        }
                    }
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
    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedTags by rememberSaveable { mutableStateOf(listOf<String>()) }

    val predefinedTags = IGDB.genres.map { it.name }.sorted()

    MyGamesListTheme {
        Scaffold(
            topBar = {
                Column {
                    SearchAppBar(searchText) { searchText = it }
                    TagFilterBar(predefinedTags, selectedTags) { tag ->
                        selectedTags = if (selectedTags.contains(tag)) {
                            selectedTags - tag
                        } else {
                            selectedTags + tag
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            val filteredGames = IGDB.games.filter { game ->
                (game.name.contains(searchText, ignoreCase = true) ||
                        game.genres.any { genreId ->
                            val genre = IGDB.genres.find { it.id == genreId }
                            genre?.name?.contains(searchText, ignoreCase = true) == true
                        } ||
                        game.platforms.any { platformId ->
                            val platform = IGDB.platforms.find { it.id == platformId }
                            platform?.name?.contains(searchText, ignoreCase = true) == true
                        }) &&
                        (selectedTags.isEmpty() || selectedTags.all { tag ->
                            game.genres.any { genreId ->
                                val genre = IGDB.genres.find { it.id == genreId }
                                genre?.name == tag
                            }
                        })
            }
            if (filteredGames.isEmpty()) {
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    Text(
                        "No match :(",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
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
fun TagFilterBar(tags: List<String>, selectedTags: List<String>, onTagSelected: (String) -> Unit) {
    var filterByTag by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { filterByTag = !filterByTag },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (filterByTag) Color.LightGray else Color.White,
            contentColor = if (filterByTag) Color.White else Color.LightGray
        )
    ) {
        Icon(
            imageVector =Icons.AutoMirrored.Filled.List,
            contentDescription = "Show Filter"
        )
    }
    if (filterByTag) {
        LazyRow(modifier = Modifier.padding(8.dp)) {
            items(tags) { tag ->
                val isSelected = selectedTags.contains(tag)
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagSelected(tag) },
                    label = { Text(tag) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(searchText: String, onSearchTextChange: (String) -> Unit) {
    var isSearching by rememberSaveable { mutableStateOf(false) }

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
