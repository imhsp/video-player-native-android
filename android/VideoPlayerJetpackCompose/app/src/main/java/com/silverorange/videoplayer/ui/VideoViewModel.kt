package com.silverorange.videoplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverorange.videoplayer.domain.GetVideosListUseCase
import com.silverorange.videoplayer.domain.mappers.ResultMap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val getVideosListUseCase: GetVideosListUseCase) : ViewModel() {

    private val _videoData = MutableLiveData<ResultMap>()
    val videoData: LiveData<ResultMap>
        get() = _videoData

    init {
        getVideoInfo()
    }

    fun getVideoInfo() {
        _videoData.value = ResultMap.Loading
        val videoListObservable = getVideosListUseCase.execute()
        val x = videoListObservable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResults, this::handleError)
    }

    private fun handleResults(result: ResultMap) {
        _videoData.value = result as ResultMap.Success
    }

    private fun handleError(throwable: Throwable) {
        _videoData.value = ResultMap.Failure(throwable)
    }
}