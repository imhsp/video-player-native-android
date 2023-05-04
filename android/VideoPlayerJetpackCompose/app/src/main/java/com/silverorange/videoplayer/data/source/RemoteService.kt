package com.silverorange.videoplayer.data.source

import com.silverorange.videoplayer.data.model.VideosResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface RemoteService {
    @GET("/videos")
    fun getVideoList(): Observable<VideosResponse>

}