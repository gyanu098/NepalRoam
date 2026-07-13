package com.example.myroamnepal.view

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myroamnepal.model.ReviewModel
import com.example.myroamnepal.repo.ReviewRepoImpl
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.ReviewViewModel
import com.example.myroamnepal.viewModel.ReviewViewModelFactory
import com.example.myroamnepal.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

class ReviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val placeId = intent.getStringExtra("PLACE_ID") ?: ""
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                val reviewViewModel: ReviewViewModel = viewModel(
                    factory = ReviewViewModelFactory(ReviewRepoImpl())
                )
                val userViewModel: UserViewModel = viewModel()
                
                ReviewScreen(
                    placeId = placeId,
                    reviewViewModel = reviewViewModel,
                    userViewModel = userViewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    placeId: String,
    reviewViewModel: ReviewViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val reviews by reviewViewModel.reviews.collectAsState()
    val loading by reviewViewModel.loading.collectAsState()
    val message by reviewViewModel.message.collectAsState()
    val user by userViewModel.user.collectAsState()

    LaunchedEffect(placeId) {
        reviewViewModel.getReviewsByPlace(placeId)
        userViewModel.loadCurrentUser()
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            reviewViewModel.clearMessage()
            if (it == "Review added successfully") {
                rating = 0
                reviewText = ""
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() + 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Surface(
                    color = BluePrimary,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reviews & Ratings",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    WriteReviewCard(
                        rating = rating,
                        onRatingChange = { rating = it },
                        reviewText = reviewText,
                        onReviewTextChange = { reviewText = it },
                        loading = loading,
                        onSubmit = {
                            if (user != null) {
                                val newReview = ReviewModel(
                                    placeId = placeId,
                                    userId = user!!.uid,
                                    userName = user!!.fullName,
                                    rating = rating.toDouble(),
                                    comment = reviewText
                                )
                                reviewViewModel.addReview(newReview)
                            } else {
                                Toast.makeText(context, "Please login to add a review", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }


            item {
                Text(
                    text = "Community Reviews",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }


            if (reviews.isEmpty() && !loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No reviews yet. Be the first to review!", color = Color.Gray)
                    }
                }
            } else {
                items(reviews) { review ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ReviewListItem(review)
                    }
                }
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
    loading: Boolean,
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
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp),
                enabled = !loading && rating > 0 && reviewText.isNotBlank()
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
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
}

@Composable
fun ReviewListItem(review: ReviewModel) {
    val date = remember(review.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(review.timestamp))
    }
    
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
                    text = review.userName,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50),
                    fontSize = 16.sp
                )
                Text(text = date, color = Color.Gray, fontSize = 12.sp)
            }
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
