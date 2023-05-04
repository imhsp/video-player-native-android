package com.silverorange.videoplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.silverorange.videoplayer.domain.mappers.ResultMap

@Composable
fun VideoScreen(videoViewModel: VideoViewModel = hiltViewModel()) {

    val state = videoViewModel.videoData.observeAsState(ResultMap.Loading)
    when (state.value) {
        ResultMap.Loading -> {
            CircularProgressIndicator()
        }

        is ResultMap.Success -> {
            Column {
                val data = state.value as ResultMap.Success
                Text(text = "Data: ${data.videos}")
            }
        }

        is ResultMap.Failure -> {
            Column {
                val errorData = state.value as ResultMap.Failure
                Text(text = "Error: ${errorData.throwable.message}")
                Button(onClick = { videoViewModel.getVideoInfo() }) {
                    Text(text = "Retry")
                }
            }
        }
    }
}