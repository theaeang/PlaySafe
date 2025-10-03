package com.example.avatarselector

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvatarSelectorScreen { selected ->
                Toast.makeText(this, "Selected avatar: $selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun AvatarSelectorScreen(onAvatarSelected: (Int) -> Unit) {
    val avatars = listOf(
        R.drawable.avatar1,
        R.drawable.avatar2,
        R.drawable.avatar3
    )

    var selectedAvatar by remember { mutableStateOf(avatars[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F7FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Select a character",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Big avatar preview
        Image(
            painter = painterResource(id = selectedAvatar),
            contentDescription = "Selected Avatar",
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(100.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar options row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            avatars.forEach { avatar ->
                val isSelected = avatar == selectedAvatar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) BorderStroke(3.dp, Color.Red)
                    else BorderStroke(2.dp, Color.Gray),
                    modifier = Modifier
                        .size(90.dp)
                        .clickable { selectedAvatar = avatar }
                ) {
                    Image(
                        painter = painterResource(id = avatar),
                        contentDescription = "Avatar Option",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Select button
        Button(
            onClick = { onAvatarSelected(selectedAvatar) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE0E0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        ) {
            Text("Select", fontSize = 20.sp, color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}
