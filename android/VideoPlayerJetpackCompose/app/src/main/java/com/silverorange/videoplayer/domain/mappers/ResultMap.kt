package com.silverorange.videoplayer.domain.mappers

import com.silverorange.videoplayer.data.model.VideosResponse

open class ResultMap {
    object Loading : ResultMap()
    data class Success(val videos: VideosResponse) : ResultMap()
    data class Failure(val throwable: Throwable) : ResultMap()
}