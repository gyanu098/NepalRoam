package com.example.myroamnepal.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    // States for the new review input
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    
    // Using mutableStateListOf to make the list reactive for local additions
    val reviews = remember {
        mutableStateListOf(
            ReviewItem("Sara M.", "Amazing place! Had a great time.", 5, "2 days ago"),
            ReviewItem("Rahul S.", "Beautiful view of the mountains and lake.", 5, "1 week ago"),
            ReviewItem("John D.", "A bit crowded on weekends, but worth it.", 4, "2 weeks ago"),
            ReviewItem("Anita K.", "Loved the boating experience!", 5, "1 month ago")
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reviews & Ratings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BluePrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Write Review Section at the top
            item {
                WriteReviewCard(
                    rating = rating,
                    onRatingChange = { rating = it },
                    reviewText = reviewText,
                    onReviewTextChange = { reviewText = it },
                    onSubmit = {
                        if (rating > 0 && reviewText.isNotBlank()) {
                            reviews.add(0, ReviewItem("Gyanu", reviewText, rating, "Just now"))
                            // Reset input fields
                            rating = 0
                            reviewText = ""
                        }
                    }
                )
            }

            item {
                Text(
                    text = "Community Reviews",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // List of existing reviews
            items(reviews) { review ->
                ReviewListItem(review)
            }
        }
    }
}

@Composable
fun WriteReviewCard(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    reviewText: String,
    onReviewTextChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "How was your experience?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Star Rating Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    Icon(
                        imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (starIndex <= rating) BluePrimary else Color.LightGray,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onRatingChange(starIndex) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Review Text Input
            OutlinedTextField(
                value = reviewText,
                onValueChange = onReviewTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Share your thoughts about this place...") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp),
                enabled = rating > 0 && reviewText.isNotBlank()
            ) {
                Text(
                    text = "Submit Review",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
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
                Text(
                    text = review.author,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50),
                    fontSize = 16.sp
                )
                Text(text = review.date, color = Color.Gray, fontSize = 12.sp)
            }
            // Display Rating Stars
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (index < review.rating) BluePrimary else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
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
