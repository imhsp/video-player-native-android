package com.silverorange.videoplayer.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.silverorange.videoplayer.domain.mappers.ResultMap
import io.noties.markwon.Markwon

@Composable
fun VideoScreen(videoViewModel: VideoViewModel = hiltViewModel()) {

    val activity = LocalContext.current
    val state = videoViewModel.videoData.observeAsState(ResultMap.Loading)
    val currentIndex = remember(Int) { 0 }
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