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
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
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
import com.example.myroamnepal.view.ui.theme.BluePrimary
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

val DarkBlueText = Color(0xFF2D3E50)
val LightGrayBg = Color(0xFFF8F9FA)

@Composable
fun PlaceDetailScreen() {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BluePrimary)
                        .statusBarsPadding()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terrain,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RoamNepal",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = BluePrimary) },
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
                .background(LightGrayBg)
        ) {

            Image(
                painter = painterResource(id = R.drawable.three), 
                contentDescription = "Phewa Lake",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Phewa Lake",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkBlueText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Beautiful lakeside in Pokhara for boating and mountain views.",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp, color = Color.LightGray)


                DetailSectionHeader(icon = Icons.Default.LocationOn, title = "Location")
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // Mock Map image
                            contentDescription = "Map View",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "View on Google Maps",
                                color = DarkBlueText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))


                DetailSectionHeader(icon = Icons.Default.Person, title = "Local Guides")
                Spacer(modifier = Modifier.height(12.dp))
                GuideCard()

                Spacer(modifier = Modifier.height(28.dp))


                DetailSectionHeader(icon = Icons.Default.Star, title = "Reviews")
                Spacer(modifier = Modifier.height(12.dp))
                ReviewSummary()
                Spacer(modifier = Modifier.height(16.dp))
                ReviewCard(
                    comment = "Amazing place! Had a great time.",
                    author = "Sara M.",
                    rating = 5
                )
                Spacer(modifier = Modifier.height(12.dp))
                ReviewCard(
                    comment = "Beautiful view of the mountains and lake.",
                    author = "Rahul S.",
                    rating = 5
                )
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DetailSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = DarkBlueText)
    }
}

@Composable
fun GuideCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), 
                contentDescription = "Guide",
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
                    .border(2.dp, BluePrimary.copy(alpha = 0.2f), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Karma Sherpa", fontWeight = FontWeight.Bold, color = DarkBlueText, fontSize = 16.sp)
                Text(text = "Expert Guide", fontStyle = FontStyle.Italic, color = BluePrimary, fontSize = 13.sp)
                Text(text = "Everest & Annapurna Treks", color = Color.Gray, fontSize = 12.sp)
            }
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Book Guide", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ReviewSummary() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) {
            Icon(Icons.Default.Star, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "(45 Reviews)", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ReviewCard(comment: String, author: String, rating: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = comment, 
                    color = DarkBlueText, 
                    fontSize = 14.sp, 
                    modifier = Modifier.weight(1f),
                    lineHeight = 18.sp
                )
                Row {
                    repeat(rating) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = author, color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
