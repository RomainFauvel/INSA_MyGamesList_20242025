package com.insa.mygamelist.data


import android.provider.MediaStore.Audio.Genres
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("game")
    suspend fun getGames(): List<Game>

    @GET("genres")
    suspend fun getGenres(): List<Genres>

    @GET("covers")
    suspend fun getCovers(): List<Cover>

    @GET("platforms")
    suspend fun getPlatforms(): List<Platforms>

    @GET("platform_logos")
    suspend fun getPlaformLogos(): List<PlatformLogos>


}