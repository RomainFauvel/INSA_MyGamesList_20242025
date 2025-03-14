package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.InputStreamReader

object IGDBRepository{

    val apiService=ApiClient.apiService
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

    suspend fun load(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                coroutineScope {
                    val gamesDeferred = async { apiService.getGames(body="fields id, cover, first_release_date, genres, name, platforms, summary, total_rating; where id != null & cover != null & first_release_date != null & genres != null & name != null & platforms != null & summary != null & total_rating != null;".toRequestBody("text/plain".toMediaTypeOrNull())) }
                    val coversDeferred = async { apiService.getCovers(body="fields id, url; where id != null & url != null;".toRequestBody("text/plain".toMediaTypeOrNull())) }
                    val genresDeferred = async { apiService.getGenres(body="fields id, name; where id != null & name != null;".toRequestBody("text/plain".toMediaTypeOrNull())) }
                    val platformsDeferred = async { apiService.getPlatforms(body="fields id, name, platform_logo; where id != null & name != null & platform_logo != null;".toRequestBody("text/plain".toMediaTypeOrNull())) }
                    val platformLogosDeferred = async { apiService.getPlatformLogos(body = "fields id, url; where id != null & url != null;".toRequestBody("text/plain".toMediaTypeOrNull())) }

                    _games.value = gamesDeferred.await()
                    _covers.value = coversDeferred.await()
                    _genres.value = genresDeferred.await()
                    _platforms.value = platformsDeferred.await()
                    _platformLogos.value = platformLogosDeferred.await()

                    val favoriteGameIds = readFileFromInternalStorage(context, "favorite_game_ids.txt")
                    for (game in _games.value) {
                        game.is_favorite = favoriteGameIds.contains(game.id)
                    }
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
                val name: String, val platforms : List<Long>, val summary: String, val total_rating: Float, var is_favorite: Boolean = false)

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