package com.insa.mygamelist.front

import kotlinx.serialization.Serializable

// Define a home route that doesn't take any arguments
@Serializable
object GameList

@Serializable
data class Details(val gameId:Long)
