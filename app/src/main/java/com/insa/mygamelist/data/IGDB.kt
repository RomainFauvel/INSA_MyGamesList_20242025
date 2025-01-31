package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R

object IGDB {

    private lateinit var covers: List<Cover>
    private lateinit var games: List<Game>
    private lateinit var genres : List<Genre>
    private lateinit var platforms_logos : List<PlatformLogos>
    private lateinit var platforms : List<Platforms>

    private fun <T> loadData(context: Context, resId: Int, typeToken: TypeToken<List<T>>): List<T> {
        return Gson().fromJson(
            context.resources.openRawResource(resId).bufferedReader(),
            typeToken.type
        )
    }

    fun load(context: Context) {
        covers = loadData(context, R.raw.covers, object : TypeToken<List<Cover>>(){})
        games = loadData(context, R.raw.games, object : TypeToken<List<Game>>(){})
        genres = loadData(context, R.raw.genres, object : TypeToken<List<Genre>>(){})
        platforms_logos = loadData(context, R.raw.platform_logos, object : TypeToken<List<PlatformLogos>>(){})
        platforms = loadData(context, R.raw.platforms, object : TypeToken<List<Platforms>>(){})
        /*Log.d("Covers", "Covers : " + covers)
        Log.d("Covers", "Games : " + games)
        Log.d("Covers", "Genres : " + genres)
        Log.d("Covers", "PlatformsLogos : " + platforms_logos)
        Log.d("Covers", "Platforms" + platforms) */
    }
}

data class Cover(val id: Long, val url: String)

data class Game(val id: Long, val cover: Long, val first_release_date: Long, val genres: List<Long>,
    val name: String, val platforms : List<Long>, val summary: String, val toal_rating: Float)

data class Genre(val id:Long,val name:String)

data class PlatformLogos(val id:Long, val url:String)

data class Platforms(val id:Long,val name:String,val platform_logo:Long)