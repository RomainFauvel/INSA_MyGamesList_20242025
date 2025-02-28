package com.insa.mygamelist.data

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun GameItem(game: Game,onClick:()->Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .clickable {
                onClick.invoke()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https:" + getUrl(game.cover),
                contentDescription = "Cover du jeu",
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    game.name, fontSize = 20.sp, style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                )
                Text(
                    "Genres: " + game.genres.map { genreId -> IGDB.genres.find { it.id == genreId }?.name }
                        .joinToString(", "),
                    maxLines=1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = { game.is_favorite = !game.is_favorite },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (game.is_favorite) Color.Red else Color.Gray,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (game.is_favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (game.is_favorite) "Remove from favorites" else "Add to favorites",
                    tint = Color.White
                )
            }
        }
    }
}

fun getUrl(coverId : Long) : String {
    val cover = IGDB.covers.find { it.id == coverId}
    return cover?.url?:"//images.igdb.com/igdb/image/upload/t_cover_big/ul5wwtyyqzh06j98agmx.jpg"
}