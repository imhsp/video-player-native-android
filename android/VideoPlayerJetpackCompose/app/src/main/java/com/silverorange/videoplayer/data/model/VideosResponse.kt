package com.silverorange.videoplayer.data.model

class VideosResponse : ArrayList<VideosResponseItem>()

data class VideosResponseItem(
    val author: Author,
    val description: String,
    val fullURL: String,
    val hlsURL: String,
    val id: String,
    val publishedAt: String,
    val title: String
)

data class Author(
    val id: String,
    val name: String
)