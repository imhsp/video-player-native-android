package com.silverorange.videoplayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            Column(
                modifier = Modifier.systemBarsPadding(),
                verticalArrangement = Arrangement.Top
            ) {
                val data = state.value as ResultMap.Success
                VideoPlayer(
                    modifier = Modifier.weight(1f, fill = true),
                    videoLink = data.videos,
                )
            }
        }

        is ResultMap.Failure -> {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Error: Loading data from URL,\n\n1. Check if local server is running\n\n2. If you are using physical device please update url in below file\n\n/android/VideoPlayerJetpackCompose/app/src/main/java/com/silverorange/videoplayer/utils/Constants.kt")
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { videoViewModel.getVideoInfo() }) {
                    Text(text = "Retry")
                }
            }
        }
    }
}