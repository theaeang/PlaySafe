package com.example.rescuedial

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun RescueDialScreen(navController: NavHostController? = null) {
    val context = LocalContext.current
    val activity = (context as? Activity)

    // --- TextToSpeech setup ---
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                    tts.language = Locale.US
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }

    // --- States ---
    var step by remember { mutableStateOf(0) } // 0 = Start, 1 = DialPad, 2 = Calling
    var input by remember { mutableStateOf("") }
    var showVideo by remember { mutableStateOf(false) }
    var mascotShakeTrigger by remember { mutableStateOf(false) }
    var gameStarted by remember { mutableStateOf(false) }

    // --- Tone generator ---
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 80) }

    // --- Mascot animations ---
    val mascotShake = remember { Animatable(0f) }
    val mascotScale = rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse)
    )

    val stepDialogues = mapOf(
        0 to "Tap the numbers to dial 911!",
        1 to "Tap the numbers to dial 911!",
        2 to "Calling now..."
    )

    // --- Background audio ---
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(gameStarted) {
        mediaPlayer?.release()
        if (!gameStarted) {
            mediaPlayer = MediaPlayer.create(context, R.raw.rescue_start).apply {
                isLooping = true
                start()
            }
        } else {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }

    // --- Main Layout ---
    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // --- Back button (fixed) ---
        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "Back",
            modifier = Modifier
                .padding(16.dp)
                .size(60.dp)
                .clickable {
                    if (navController != null && !navController.popBackStack()) {
                        activity?.finish()
                    } else if (navController == null) {
                        activity?.finish()
                    }
                }
        )

        // --- Start Screen ---
        if (step == 0 && !gameStarted) {
            val infiniteTransition = rememberInfiniteTransition()
            val startScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    tween(1000, easing = LinearEasing),
                    RepeatMode.Reverse
                )
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_start),
                    contentDescription = "Start",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(250.dp)
                        .graphicsLayer(scaleX = startScale, scaleY = startScale)
                        .clickable {
                            gameStarted = true
                            step = 1
                            input = ""
                            tts.speak(
                                "Let's practice calling emergency numbers!",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                UUID.randomUUID().toString()
                            )
                        }
                )
            }
        }

        // --- Dial Pad Screen ---
        if (step == 1) {
            LaunchedEffect(mascotShakeTrigger) {
                if (mascotShakeTrigger) {
                    mascotShake.snapTo(0f)
                    mascotShake.animateTo(15f, tween(100))
                    mascotShake.animateTo(-15f, tween(100))
                    mascotShake.animateTo(0f, tween(100))
                    mascotShakeTrigger = false
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mascot
                Image(
                    painter = painterResource(id = R.drawable.buddy),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer(
                            scaleX = mascotScale.value,
                            scaleY = mascotScale.value,
                            rotationZ = mascotShake.value
                        )
                )

                // Dialogue bubble
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = stepDialogues[step] ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input display
                Text(
                    text = input.ifEmpty { "Tap numbers..." },
                    fontSize = 36.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dial pad (flexible space)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val rows = listOf(
                        listOf('1', '2', '3'),
                        listOf('4', '5', '6'),
                        listOf('7', '8', '9'),
                        listOf('*', '0', '#')
                    )

                    rows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            row.forEach { digit ->
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3A3A3A))
                                        .clickable {
                                            if (input.length < 3) {
                                                input += digit
                                                toneGen.startTone(
                                                    ToneGenerator.TONE_PROP_BEEP,
                                                    120
                                                )
                                                tts.speak(
                                                    digit.toString(),
                                                    TextToSpeech.QUEUE_FLUSH,
                                                    null,
                                                    UUID.randomUUID().toString()
                                                )
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit.toString(),
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Call button
                Image(
                    painter = painterResource(id = R.drawable.dial_button),
                    contentDescription = "Call",
                    modifier = Modifier
                        .size(150.dp)
                        .clickable {
                            if (input == "911") {
                                step = 2
                                showVideo = true
                            } else if (input.length == 3) {
                                mascotShakeTrigger = true
                                input = ""
                                tts.speak(
                                    "Try again!",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    UUID.randomUUID().toString()
                                )
                            }
                        }
                )
            }
        }

        // --- Fullscreen Video ---
        if (step == 2 && showVideo) {
            AndroidView(
                factory = {
                    VideoView(context).apply {
                        val uri =
                            Uri.parse("android.resource://${context.packageName}/${R.raw.call_sequence}")
                        setVideoURI(uri)
                        setOnCompletionListener {
                            step = 0
                            input = ""
                            gameStarted = false
                            showVideo = false
                            Toast.makeText(
                                context,
                                "Well done! You called help!",
                                Toast.LENGTH_SHORT
                            ).show()
                            tts.speak(
                                "Well done! You called help!",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                UUID.randomUUID().toString()
                            )
                        }
                        start()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
