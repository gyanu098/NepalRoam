package com.example.myroamnepal.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme

class ReviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                ReviewScreen(onBack = { finish() })
            }
        }
    }
}

data class ReviewItem(val author: String, val comment: String, val rating: Int, val date: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(onBack: () -> Unit) {
    val reviews = listOf(
        ReviewItem("Sara M.", "Amazing place! Had a great time.", 5, "2 days ago"),
        ReviewItem("Rahul S.", "Beautiful view of the mountains and lake.", 5, "1 week ago"),
        ReviewItem("John D.", "A bit crowded on weekends, but worth it.", 4, "2 weeks ago"),
        ReviewItem("Anita K.", "Loved the boating experience!", 5, "1 month ago")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to Write Review */ },
                containerColor = BluePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Review")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ReviewHeaderSection()
            }
            items(reviews) { review ->
                ReviewListItem(review)
            }
        }
    }
}

@Composable
fun ReviewHeaderSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "4.8", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = BluePrimary)
            Row {
                repeat(5) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
                }
            }
            Text(text = "Based on 45 reviews", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun ReviewListItem(review: ReviewItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = review.author, fontWeight = FontWeight.Bold, color = Color(0xFF2D3E50), fontSize = 16.sp)
                Text(text = review.date, color = Color.Gray, fontSize = 12.sp)
            }
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                repeat(review.rating) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                }
            }
            Text(
                text = review.comment,
                color = Color.DarkGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    MyRoamNepalTheme {
        ReviewScreen(onBack = {})
    }
}
