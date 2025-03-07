package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.Dispatcher
import java.io.BufferedReader
import java.io.InputStreamReader

class IGDBRepository(private val apiService: ApiService){

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val _covers = MutableStateFlow<List<Cover>>(emptyList())
    val covers: StateFlow<List<Cover>> = _covers

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _platforms = MutableStateFlow<List<Platforms>>(emptyList())
    val platforms: StateFlow<List<Platforms>> = _platforms

    private val _platformLogos = MutableStateFlow<List<PlatformLogos>>(emptyList())
    val platformLogos: StateFlow<List<PlatformLogos>> = _platformLogos

    suspend fun load() {
        withContext(Dispatchers.IO) {
            try {
                coroutineScope {
                    val gamesDeferred = async { apiService.getGames() }
                    val coversDeferred = async { apiService.getCovers() }
                    val genresDeferred = async { apiService.getGenres() }
                    val platformsDeferred = async { apiService.getPlatforms() }
                    val platformLogosDeferred = async { apiService.getPlaformLogos() }

                    _games.value = gamesDeferred.await()
                    _covers.value = coversDeferred.await()
                    _genres.value = genresDeferred.await()
                    _platforms.value = platformsDeferred.await()
                    _platformLogos.value = platformLogosDeferred.await()
                }
            } catch (e: Exception) {
                Log.e("IGDBRepository", "Erreur lors de la récupération des données", e)
            }
        }
    }
}

@Serializable
data class Cover(val id: Long, val url: String)

@Serializable
data class Game(val id: Long, val cover: Long, val first_release_date: Long, val genres: List<Long>,
                val name: String, val platforms : List<Long>, val summary: String, val toal_rating: Float, var is_favorite: Boolean)

@Serializable
data class Genre(val id:Long,val name:String)

@Serializable
data class PlatformLogos(val id:Long, val url:String)

@Serializable
data class Platforms(val id:Long,val name:String,val platform_logo:Long)


fun readFileFromInternalStorage(context: Context, fileName: String): List<Long> {
    val favoriteGameIds = mutableListOf<Long>()
    try {
        val fileInputStream = context.openFileInput(fileName)
        val reader = BufferedReader(InputStreamReader(fileInputStream))
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            line?.toLongOrNull()?.let { favoriteGameIds.add(it) }
        }

        reader.close()
        Log.d("TAG", "IDs des jeux favoris lus : $favoriteGameIds")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return favoriteGameIds
}