package com.silverorange.videoplayer.domain

import com.silverorange.videoplayer.data.source.RemoteService
import com.silverorange.videoplayer.domain.mappers.ResultMap
import io.reactivex.Observable
import javax.inject.Inject

class GetVideosListUseCase @Inject constructor(private val repoService: RemoteService) {
    fun execute(): Observable<ResultMap> {
        return repoService.getVideoList()
            .map { ResultMap.Success(it) as ResultMap }
            .onErrorReturn { ResultMap.Failure(it) }
    }
}