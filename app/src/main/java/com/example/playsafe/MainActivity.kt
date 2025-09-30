package com.example.playsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
        composable("safe_steps") { FeatureDashboardScreen(R.drawable.bg_safe_steps, navController) }
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
        // Background PNG
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
            // Top bar (menu + avatar)
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

            // Dashboard grid of features
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

// Feature dashboard (only back button + background)
@Composable
fun FeatureDashboardScreen(bgImage: Int, navController: NavHostController? = null) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background PNG
        Image(
            painter = painterResource(id = bgImage),
            contentDescription = "Feature background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Back button only (top-left)
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

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    MaterialTheme {
        DashboardScreen(navController = rememberNavController())
    }
}

// ---- Feature Previews ----
@Preview(showBackground = true)
@Composable
fun SafeStepsPreview() {
    FeatureDashboardScreen(R.drawable.bg_safe_steps)
}

@Preview(showBackground = true)
@Composable
fun BubbleBuddyPreview() {
    FeatureDashboardScreen(R.drawable.bg_bubble_buddy)
}

@Preview(showBackground = true)
@Composable
fun ToothyTimePreview() {
    FeatureDashboardScreen(R.drawable.bg_toothy_time)
}

@Preview(showBackground = true)
@Composable
fun RescueRingPreview() {
    FeatureDashboardScreen(R.drawable.bg_rescue_ring)
}

@Preview(showBackground = true)
@Composable
fun AvatarPreview() {
    FeatureDashboardScreen(R.drawable.bg_avatar)
}