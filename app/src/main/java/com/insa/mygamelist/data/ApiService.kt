package com.insa.mygamelist.data


import android.provider.MediaStore.Audio.Genres
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("games")
    suspend fun getGames(@Body body: RequestBody): List<Game>

    @POST("genres")
    suspend fun getGenres(@Body body: RequestBody): List<Genre>

    @POST("covers")
    suspend fun getCovers(@Body body: RequestBody ): List<Cover>

    @POST("platforms")
    suspend fun getPlatforms(@Body body: RequestBody): List<Platforms>

    @POST("platform_logos")
    suspend fun getPlatformLogos(@Body body: RequestBody): List<PlatformLogos>

}
