package com.ssag.movieapp.ui.screens.player

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface
import com.ssag.movieapp.ui.theme.MovieAppTheme
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val streamUrl = intent.getStringExtra("STREAM_URL") ?: return
        val subtitleUrl = intent.getStringExtra("SUBTITLE_URL")

        setContent {
            MovieAppTheme {
                InternalPlayerScreen(
                    streamUrl = streamUrl,
                    subtitleUrl = subtitleUrl,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun InternalPlayerScreen(
    streamUrl: String,
    subtitleUrl: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItemBuilder = MediaItem.Builder()
                .setUri(streamUrl)
            
            if (!subtitleUrl.isNullOrEmpty()) {
                val subtitle = MediaItem.SubtitleConfiguration.Builder(android.net.Uri.parse(subtitleUrl))
                    .setMimeType(MimeTypes.TEXT_VTT)
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()
                mediaItemBuilder.setSubtitleConfigurations(listOf(subtitle))
            }
            
            setMediaItem(mediaItemBuilder.build())
            prepare()
            playWhenReady = true
        }
    }

    var isPlaying by remember { mutableStateOf(true) }
    var playbackState by remember { mutableStateOf(Player.STATE_IDLE) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var currentTracks by remember { mutableStateOf(Tracks.EMPTY) }
    var error by remember { mutableStateOf<String?>(null) }

    var showSubtitleDialog by remember { mutableStateOf(false) }
    var showAudioDialog by remember { mutableStateOf(false) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                playbackState = state
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                currentTracks = tracks
            }

            override fun onPlayerError(e: PlaybackException) {
                error = "Playback failed: ${e.message}"
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            delay(1000)
        }
    }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(5000)
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (playbackState == Player.STATE_BUFFERING) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = FlixPrimary
            )
        }

        if (error != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = error!!, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        error = null
                        exoPlayer.prepare()
                        exoPlayer.play()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlixPrimary)
                ) {
                    Text("Retry")
                }
                TextButton(onClick = onBack) {
                    Text("Exit", color = Color.White)
                }
            }
        }

        if (showControls && error == null) {
            PlayerControls(
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                onPlayPause = { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() },
                onSeek = { exoPlayer.seekTo(it) },
                onRewind = { exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0)) },
                onForward = { exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)) },
                onBack = onBack,
                onSubtitleClick = { showSubtitleDialog = true },
                onAudioClick = { showAudioDialog = true }
            )
        }

        if (showSubtitleDialog) {
            TrackSelectionDialog(
                title = "Subtitles",
                tracks = currentTracks,
                trackType = C.TRACK_TYPE_TEXT,
                onDismiss = { showSubtitleDialog = false },
                onTrackSelected = { group, trackIndex ->
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon()
                        .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, trackIndex))
                        .build()
                },
                onDisable = {
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon()
                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                        .build()
                },
                onEnable = {
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon()
                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                        .build()
                }
            )
        }

        if (showAudioDialog) {
            TrackSelectionDialog(
                title = "Audio Tracks",
                tracks = currentTracks,
                trackType = C.TRACK_TYPE_AUDIO,
                onDismiss = { showAudioDialog = false },
                onTrackSelected = { group, trackIndex ->
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon()
                        .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, trackIndex))
                        .build()
                }
            )
        }
    }
}

@Composable
fun TrackSelectionDialog(
    title: String,
    tracks: Tracks,
    trackType: Int,
    onDismiss: () -> Unit,
    onTrackSelected: (Tracks.Group, Int) -> Unit,
    onDisable: (() -> Unit)? = null,
    onEnable: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FlixSurface,
        title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDisable != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDisable(); onDismiss() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Off", color = Color.White)
                    }
                }
                
                tracks.groups.filter { it.type == trackType }.forEach { group ->
                    for (i in 0 until group.length) {
                        val format = group.getTrackFormat(i)
                        val label = format.language ?: "Track ${i + 1}"
                        val isSelected = group.isTrackSelected(i)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    onEnable?.invoke()
                                    onTrackSelected(group, i)
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = FlixPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, color = Color.White)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = FlixPrimary)
            }
        }
    )
}

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onBack: () -> Unit,
    onSubtitleClick: () -> Unit,
    onAudioClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        // Top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onAudioClick) {
                Icon(Icons.Default.Audiotrack, contentDescription = "Audio", tint = Color.White)
            }
            IconButton(onClick = onSubtitleClick) {
                Icon(Icons.Default.Subtitles, contentDescription = "Subtitles", tint = Color.White)
            }
        }

        // Center
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            IconButton(onClick = onRewind) {
                Icon(Icons.Default.Replay10, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
            IconButton(onClick = onForward) {
                Icon(Icons.Default.Forward10, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
        }

        // Bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentPosition), color = Color.White, fontSize = 12.sp)
                Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
            }
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..(duration.coerceAtLeast(1L).toFloat()),
                colors = SliderDefaults.colors(
                    thumbColor = FlixPrimary,
                    activeTrackColor = FlixPrimary
                )
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
