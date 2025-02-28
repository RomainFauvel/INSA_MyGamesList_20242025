package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R

object IGDB {

    lateinit var covers: List<Cover>
    lateinit var games: List<Game>
    lateinit var genres : List<Genre>
    lateinit var platforms_logos : List<PlatformLogos>
    lateinit var platforms : List<Platforms>

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
        for (game in games) {
            game.is_favorite = false
        }
    }
}

data class Cover(val id: Long, val url: String)

data class Game(val id: Long, val cover: Long, val first_release_date: Long, val genres: List<Long>,
                val name: String, val platforms : List<Long>, val summary: String, val toal_rating: Float, var is_favorite: Boolean)

data class Genre(val id:Long,val name:String)

data class PlatformLogos(val id:Long, val url:String)

data class Platforms(val id:Long,val name:String,val platform_logo:Long)