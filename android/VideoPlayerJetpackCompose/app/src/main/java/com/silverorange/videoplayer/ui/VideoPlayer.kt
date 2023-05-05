package com.silverorange.videoplayer.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.silverorange.videoplayer.R
import com.silverorange.videoplayer.utils.Constants
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoLink: String,
    hasPrev: Boolean,
    hasNext: Boolean
) {

    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                MediaItem.fromUri(
                    videoLink
                )
            )
            prepare()
            playWhenReady = true
        }
    }

    var shouldShowControls by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var playbackState by remember { mutableStateOf(exoPlayer.playbackState) }

    Box(modifier = modifier) {
        LaunchedEffect(key1 = shouldShowControls) {
            if (shouldShowControls) {
                delay(Constants.PLAYER_CONTROLS_VISIBILITY)
                shouldShowControls = false
            }
        }

        DisposableEffect(key1 = Unit) {
            val listener = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    isPlaying = player.isPlaying
                    playbackState = player.playbackState
                }
            }

            exoPlayer.addListener(listener)
            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .clickable(
                    indication = null,
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() }) {
                    shouldShowControls = shouldShowControls.not()
                }
        ) {
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                    }
                }
            )
        }

        PlayerControls(
            modifier = Modifier.fillMaxSize(),
            isVisible = { shouldShowControls },
            isPlaying = { isPlaying },
            onPauseToggle = {
                when {
                    exoPlayer.isPlaying -> {
                        exoPlayer.pause()
                    }
                    exoPlayer.isPlaying.not() && playbackState == STATE_ENDED -> {
                        exoPlayer.seekTo(0, 0)
                        exoPlayer.playWhenReady = true
                    }
                    else -> {
                        exoPlayer.play()
                    }
                }
                isPlaying = isPlaying.not()
            },
            onPrevious = { exoPlayer.seekToPrevious() },
            onNext = { exoPlayer.seekToNext() })
    }
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    onPauseToggle: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val visible = remember(isVisible()) { isVisible() }
    val playing = remember(isPlaying()) { isPlaying() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        //black overlay across the video player
        Box(modifier = modifier.background(Color.Black.copy(alpha = 0.6f))) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                //replay button
                IconButton(modifier = Modifier.size(40.dp), onClick = onPrevious) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.ic_previous),
                        contentDescription = "Previous Video"
                    )
                }

                //pause/play toggle button
                IconButton(modifier = Modifier.size(40.dp), onClick = onPauseToggle) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(
                            id =
                            when {
                                playing -> {
                                    R.drawable.ic_pause
                                }
                                else -> {
                                    R.drawable.ic_play
                                }
                            }
                        ),
                        contentDescription = "Play/Pause"
                    )
                }

                //forward button
                IconButton(modifier = Modifier.size(40.dp), onClick = onNext) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = "Next Video"
                    )
                }
            }
        }
    }
}