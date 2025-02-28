package com.insa.mygamelist

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import androidx.compose.foundation.lazy.items
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.insa.mygamelist.data.Details
import com.insa.mygamelist.data.DetailsScreen
import com.insa.mygamelist.data.GameList
import com.insa.mygamelist.data.GameListScreen
import com.insa.mygamelist.data.IGDB.games
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IGDB.load(this)

        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            NavHost(navController, startDestination = GameList) {
                composable<GameList> {
                    GameListScreen(navController)
                }
                composable<Details> { backStackEntry ->
                    val gameId: Long = backStackEntry.toRoute<Details>().gameId
                    DetailsScreen(navController,gameId)
                }
            }
        }
    }
    override fun onStop() {
        super.onStop()
        writeFavoriteGamesIdsToFile(this, "favorite_game_ids.txt")
    }

    fun writeFavoriteGamesIdsToFile(context: Context, fileName: String) {
        val favoriteGamesIds = games.filter { it.is_favorite }.map { it.id }
        Log.d("TAG", "Liste de favoris" + favoriteGamesIds.toString())
        val content = favoriteGamesIds.joinToString("\n")

        try {
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(fileOutputStream))

            writer.write(content)
            Log.d("TAG", "WRITE")
            Log.d("FileWrite", "Contenu écrit dans le fichier : $content")
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

