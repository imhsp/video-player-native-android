package com.silverorange.videoplayer.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.silverorange.videoplayer.R
import com.silverorange.videoplayer.data.model.VideosResponse
import com.silverorange.videoplayer.utils.Constants
import io.noties.markwon.Markwon
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoLink: VideosResponse,
) {

    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItems(
                videoLink.map {
                    val mediaMetadata =
                        MediaMetadata.Builder().setTitle(it.title).setArtist(it.author.name)
                            .setDescription(it.description).build()

                    MediaItem.Builder().setMediaMetadata(mediaMetadata).setUri(it.fullURL).build()
                }
            )
            prepare()
        }
    }

    var shouldShowControls by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var playbackState by remember { mutableStateOf(exoPlayer.playbackState) }
    var hasNext by remember { mutableStateOf(exoPlayer.hasNextMediaItem()) }
    var hasPrevious by remember { mutableStateOf(exoPlayer.hasPreviousMediaItem()) }
    var desc by remember { mutableStateOf(videoLink.first().description) }

    Column(modifier = modifier) {
        Box {
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
                        desc = videoLink[player.currentMediaItemIndex].description
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        desc = mediaItem?.mediaMetadata?.description.toString()
                        hasNext = exoPlayer.hasNextMediaItem()
                        hasPrevious = exoPlayer.hasPreviousMediaItem()
                    }
                }

                exoPlayer.addListener(listener)
                onDispose {
                    exoPlayer.removeListener(listener)
                    exoPlayer.release()
                }
            }

            // Create element height in dp state
            val localDensity = LocalDensity.current
            var videoHeight by remember { mutableStateOf(0.dp) }
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable(
                        indication = null,
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() }) {
                        shouldShowControls = shouldShowControls.not()
                    }
                    .onGloballyPositioned {
                        videoHeight = with(localDensity) { it.size.height.toDp() }
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
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                        }
                    }
                )

                PlayerControls(
                    modifier = Modifier.height(videoHeight),
                    isVisible = { shouldShowControls },
                    isPlaying = { isPlaying },
                    isHasNext = { hasNext },
                    isHasPrevious = { hasPrevious },
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
                    onPrevious = { exoPlayer.seekToPreviousMediaItem() },
                    onNext = { exoPlayer.seekToNextMediaItem() })
            }
        }

        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f, fill = true)
                .verticalScroll(rememberScrollState())
        ) {
            AndroidView(factory = {
                TextView(context).apply {
                    layoutParams =
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                }
            },
            update = {
                val markWon: Markwon = Markwon.create(context)
                it.text = markWon.toMarkdown(desc)
            })
        }
    }
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    isHasNext: () -> Boolean,
    isHasPrevious: () -> Boolean,
    onPauseToggle: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val visible = remember(isVisible()) { isVisible() }
    val playing = remember(isPlaying()) { isPlaying() }
    val hasPrevious = remember(isHasPrevious()) { isHasPrevious() }
    val hasNext = remember(isHasNext()) { isHasNext() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = modifier) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.size(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White.copy(alpha = 0.6f)),
                    shape = CircleShape,
                    enabled = hasPrevious,
                    onClick = onPrevious
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (hasPrevious) 1f else 0.2f),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.ic_previous),
                        contentDescription = "Previous Video"
                    )
                }

                Button(
                    modifier = Modifier.size(80.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White.copy(alpha = 0.6f)),
                    shape = CircleShape,
                    onClick = onPauseToggle
                ) {
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

                Button(
                    modifier = Modifier.size(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White.copy(alpha = 0.6f)),
                    shape = CircleShape,
                    enabled = hasNext,
                    onClick = onNext
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (hasNext) 1f else 0.2f),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = "Next Video"
                    )
                }
            }
        }
    }
}