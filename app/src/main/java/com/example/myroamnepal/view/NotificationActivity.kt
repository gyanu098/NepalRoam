package com.example.myroamnepal.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                NotificationScreen(onBack = { finish() })
            }
        }
    }
}

data class NotificationItem(val title: String, val message: String, val time: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBack: () -> Unit) {
    val notifications = listOf(
        NotificationItem("Welcome!", "Thank you for joining RoamNepal. Start exploring now!", "2 hours ago"),
        NotificationItem("New Place Added", "Check out the new 'Waterfall near Kathmandu' added recently.", "5 hours ago"),
        NotificationItem("Profile Updated", "Your profile information has been successfully updated.", "1 day ago"),
        NotificationItem("Review Liked", "Someone liked your review for Phewa Lake.", "2 days ago")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BluePrimary)
            )
        }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No notifications yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BluePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = BluePrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50),
                    fontSize = 16.sp
                )
                Text(
                    text = notification.message,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    MyRoamNepalTheme {
        NotificationScreen(onBack = {})
    }
}
