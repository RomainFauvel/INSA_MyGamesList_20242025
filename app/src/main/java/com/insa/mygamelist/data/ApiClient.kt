package com.insa.mygamelist.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {
    private const val BASE_URL: String = "https://api.igdb.com/v4/"
    private const val CLIENT_ID="u5aqkx3lbkai7g2qr9ym9c6kbkbhts"
    private const val CLIENT_TOKEN="gle0dnxj6rzplvz84vd2dmqqtxpen6"

    private val gson : Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    private val httpClient : OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Client-ID", CLIENT_ID)
                    .addHeader("Authorization", "Bearer $CLIENT_TOKEN")
                    .addHeader("Content-Type","application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(interceptor)
            .build()
    }

    private val retrofit : Retrofit by lazy {
        var temp = Json { }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(temp.asConverterFactory(
                contentType = "application/json".toMediaType()
            ))
            .build()
    }

    val apiService : ApiService by lazy{
        retrofit.create(ApiService::class.java)
    }
}

