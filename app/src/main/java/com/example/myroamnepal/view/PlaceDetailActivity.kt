package com.example.myroamnepal.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myroamnepal.R
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme

class PlaceDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                PlaceDetailScreen()
            }
        }
    }
}

val OrangeRoam = Color(0xFFF58220)
val DarkBlueText = Color(0xFF2D3E50)

@Composable
fun PlaceDetailScreen() {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OrangeRoam)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual logo if available
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RoamNepal",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = OrangeRoam) },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF8F9FA))
        ) {
            // Main Image
            Image(
                painter = painterResource(id = R.drawable.three), // Phewa Lake
                contentDescription = "Phewa Lake",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Title and Subtitle
                Text(
                    text = "Phewa Lake",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlueText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Beautiful lakeside in Pokhara for boating and mountain views.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                // Location Section
                SectionTitle(icon = Icons.Default.LocationOn, title = "Location")
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // Mock Map image
                            contentDescription = "Map",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Text("View on Google Maps", color = DarkBlueText, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Local Guides Section
                SectionTitle(icon = painterResource(id = R.drawable.ic_launcher_foreground), title = "Local Guides")
                Spacer(modifier = Modifier.height(12.dp))
                GuideCard()

                Spacer(modifier = Modifier.height(24.dp))

                // Reviews Section
                SectionTitle(icon = Icons.Default.Star, title = "Reviews")
                Spacer(modifier = Modifier.height(8.dp))
                RatingSummary()
                Spacer(modifier = Modifier.height(12.dp))
                ReviewItem(
                    comment = "Amazing place! Had a great time.",
                    author = "Sara M.",
                    rating = 5
                )
                Spacer(modifier = Modifier.height(8.dp))
                ReviewItem(
                    comment = "Beautiful view of the mountains and lake.",
                    author = "Rahul S.",
                    rating = 5
                )
            }
        }
    }
}

@Composable
fun SectionTitle(icon: Any, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (icon) {
            is androidx.compose.ui.graphics.vector.ImageVector -> Icon(icon, contentDescription = null, tint = OrangeRoam, modifier = Modifier.size(20.dp))
            is androidx.compose.ui.graphics.painter.Painter -> Icon(icon, contentDescription = null, tint = OrangeRoam, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBlueText)
    }
}

@Composable
fun GuideCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Guide photo
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Karma Sherpa", fontWeight = FontWeight.Bold, color = DarkBlueText)
                Text(text = "Expert Guide", fontStyle = FontStyle.Italic, color = OrangeRoam, fontSize = 13.sp)
                Text(text = "Everest & Annapurna Treks", color = Color.Gray, fontSize = 12.sp)
            }
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = OrangeRoam),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Book Guide", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RatingSummary() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) {
            Icon(Icons.Default.Star, contentDescription = null, tint = OrangeRoam, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "(45 Reviews)", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun ReviewItem(comment: String, author: String, rating: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = comment, color = DarkBlueText, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Row {
                    repeat(rating) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = OrangeRoam, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = author, color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceDetailPreview() {
    MyRoamNepalTheme {
        PlaceDetailScreen()
    }
}
