package com.example.playsafe

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaySafeApp()
        }
    }
}

@Composable
fun PlaySafeApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("safe_steps") { SafeSteps(navController) } // integrated here
        composable("bubble_buddy") { FeatureDashboardScreen(R.drawable.bg_bubble_buddy, navController) }
        composable("toothy_time") { FeatureDashboardScreen(R.drawable.bg_toothy_time, navController) }
        composable("rescue_ring") { FeatureDashboardScreen(R.drawable.bg_rescue_ring, navController) }
        composable("menu") { FeatureDashboardScreen(R.drawable.bg_parent_dashboard, navController) }
        composable("avatar") { FeatureDashboardScreen(R.drawable.bg_avatar, navController) }
    }
}

@Composable
fun DashboardScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_dashboard),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(90.dp)
                        .clickable { navController.navigate("menu") },
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(90.dp)
                        .clickable { navController.navigate("avatar") },
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(200.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DashboardIcon(
                        imageRes = R.drawable.ic_safe_steps,
                        title = "Safe Steps",
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("safe_steps") }

                    DashboardIcon(
                        imageRes = R.drawable.ic_bubble_buddy,
                        title = "Bubble Buddy",
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("bubble_buddy") }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DashboardIcon(
                        imageRes = R.drawable.ic_toothy_time,
                        title = "Toothy Time",
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("toothy_time") }

                    DashboardIcon(
                        imageRes = R.drawable.ic_rescue_ring,
                        title = "Rescue Ring",
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("rescue_ring") }
                }
            }
        }
    }
}

@Composable
fun DashboardIcon(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(1.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun FeatureDashboardScreen(bgImage: Int, navController: NavHostController? = null) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgImage),
            contentDescription = "Feature background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Back",
                modifier = Modifier
                    .size(90.dp)
                    .clickable { navController?.popBackStack() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun SafeSteps(navController: NavHostController? = null) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(0) }

    // your exact initial dialogue (unchanged)
    var dialogue by remember {
        mutableStateOf("Welcome to Safe Steps! Let's learn how to cross the street safely. Follow the steps with me!")
    }

    var progress by remember { mutableFloatStateOf(0f) }
    var isBubbleVisible by remember { mutableStateOf(false) }

    // flags for LOOK step GIFs
    var showCars by remember { mutableStateOf(true) }
    var lookSequenceStarted by remember { mutableStateOf(false) }

    // audio helper
    fun playAudio(resId: Int) {
        val mp = MediaPlayer.create(context, resId)
        isBubbleVisible = true
        mp.setOnCompletionListener {
            it.release()
            isBubbleVisible = false
        }
        mp.start()
    }

    // play welcome audio once when composable first appears
    LaunchedEffect(Unit) {
        playAudio(R.raw.welcome) // ensure R.raw.welcome exists
    }

    // progress mapping when step changes
    LaunchedEffect(step) {
        progress = when (step) {
            0 -> 0f
            1 -> 0.33f
            2 -> 0.66f
            3 -> 1f
            else -> 0f
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hintPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_safe_steps),
            contentDescription = "Safe Steps",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "Back",
            modifier = Modifier
                .padding(16.dp)
                .size(60.dp)
                .clickable { navController?.popBackStack() },
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = progress, // fixed: pass float directly
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(12.dp),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Narrator bubble (visible while audio plays)
            if (isBubbleVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_narrator),
                        contentDescription = "Narrator",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = dialogue,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (step) {
                0 -> {
                    // STOP
                    Image(
                        painter = painterResource(id = R.drawable.ic_stop),
                        contentDescription = "STOP",
                        modifier = Modifier
                            .size(150.dp)
                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                            .clickable {
                                // exact string you provided
                                step = 1
                                dialogue =
                                    "First things first- we STOP! Never run into the street. Tap the red STOP button to show me you know how to stop."
                                playAudio(R.raw.stop)
                                // prepare LOOK state
                                showCars = true
                                lookSequenceStarted = false
                            }
                    )
                }

                1 -> {
                    // LOOK: show cars GIF first, then switch to clear GIF and proceed to LISTEN
                    dialogue =
                        "Great stopping! Now we LOOK both ways. Left, then right, then left again. Tap the LOOK button to check for cars."

                    // show appropriate GIF according to state
                    val modelRes = if (showCars) R.raw.look_cars else R.raw.no_cars
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(modelRes)
                            .decoderFactory(ImageDecoderDecoder.Factory())
                            .build(),
                        contentDescription = if (showCars) "Cars Coming" else "No Cars",
                        modifier = Modifier.size(180.dp),
                        contentScale = ContentScale.Fit
                    )

                    // only run the sequence once when we enter step 1
                    LaunchedEffect(step) {
                        if (!lookSequenceStarted) {
                            lookSequenceStarted = true
                            // first show "cars" message and audio
                            dialogue = "See those cars?That means we wait until they pass. Safety first!"
                            playAudio(R.raw.during_look) // ensure this exists
                            delay(3000) // keep cars visible

                            // switch to clear road
                            showCars = false

                            // then prompt LISTEN and move to step 2
                            dialogue =
                                "Good looking! Now close your eyes and LISTEN. Do you hear any cars? Tap LISTEN to practice using your ears."
                            playAudio(R.raw.listen)
                            // small delay to allow audio to start before moving
                            delay(200)
                            step = 2
                        }
                    }
                }

                2 -> {
                    // LISTEN
                    Image(
                        painter = painterResource(id = R.drawable.ic_listen),
                        contentDescription = "LISTEN",
                        modifier = Modifier
                            .size(150.dp)
                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                            .clickable {
                                step = 3
                                dialogue =
                                    "Perfect! No cars are coming. Now we can CROSS safely. Tap the green CROSS button to walk across."
                                playAudio(R.raw.cross)
                            }
                    )
                }

                3 -> {
                    // CROSS / SUCCESS
                    Image(
                        painter = painterResource(id = R.drawable.ic_cross),
                        contentDescription = "CROSS",
                        modifier = Modifier
                            .size(150.dp)
                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                            .clickable {
                                dialogue =
                                    "You did it!You followed all the safety steps: STOP, LOOK, LISTEN, CROSS! You're a street safety expert!"
                                playAudio(R.raw.success)

                                LaunchedEffect(Unit) {
                                    delay(5000)
                                    dialogue =
                                        "Remember our street safety rule:Always STOP, LOOK, LISTEN before you CROSS!"
                                    playAudio(R.raw.quick_rev)
                                }
                            }
                    )
                }

                else -> {
                    // If out of order: show toast + audio and reset to 0
                    Toast.makeText(
                        context,
                        "Whoops! We need to follow all the steps to stay safe.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialogue = "Whoops! We need to follow all the steps to stay safe."
                    playAudio(R.raw.whoops)
                    step = 0
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    MaterialTheme {
        DashboardScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SafeStepsPreview() {
    SafeSteps()
}
