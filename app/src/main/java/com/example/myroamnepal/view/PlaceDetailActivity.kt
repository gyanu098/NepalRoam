package com.example.myroamnepal.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myroamnepal.R
import com.example.myroamnepal.model.PlaceModel
import com.example.myroamnepal.repo.PlaceRepoImpl
import com.example.myroamnepal.repo.ReviewRepoImpl
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.PlaceViewModel
import com.example.myroamnepal.viewModel.PlaceViewModelFactory
import com.example.myroamnepal.viewModel.ReviewViewModel
import com.example.myroamnepal.viewModel.ReviewViewModelFactory
import com.example.myroamnepal.viewModel.UserViewModel

class PlaceDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val placeId = intent.getStringExtra("PLACE_ID") ?: ""
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                val context = LocalContext.current
                val placeViewModel: PlaceViewModel = viewModel(
                    factory = PlaceViewModelFactory(PlaceRepoImpl(context))
                )
                val reviewViewModel: ReviewViewModel = viewModel(
                    factory = ReviewViewModelFactory(ReviewRepoImpl())
                )
                val userViewModel: UserViewModel = viewModel()

                PlaceDetailScreen(
                    placeId = placeId,
                    viewModel = placeViewModel,
                    reviewViewModel = reviewViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

@Composable
fun PlaceDetailScreen(
    placeId: String,
    viewModel: PlaceViewModel,
    reviewViewModel: ReviewViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var place by remember { mutableStateOf<PlaceModel?>(null) }
    
    val user by userViewModel.user.collectAsState()
    val isFavorite = user?.favorites?.contains(placeId) == true

    val averageRating by reviewViewModel.averageRating.collectAsState()
    val reviews by reviewViewModel.reviews.collectAsState()

    LaunchedEffect(placeId) {
        if (placeId.isNotEmpty()) {
            PlaceRepoImpl(context).getPlaceById(placeId) { success, data, _ ->
                if (success) {
                    place = data
                }
            }
            reviewViewModel.getReviewsByPlace(placeId)
            reviewViewModel.getAverageRating(placeId)
            userViewModel.loadCurrentUser()
        }
    }

    Scaffold(
        bottomBar = { DetailBottomNavigation() },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (place == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
            ) {
                // Movable Header
                item {
                    Surface(shadowElevation = 4.dp, color = BluePrimary) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                IconButton(onClick = { 
                                    if (context is ComponentActivity) {
                                        context.finish()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Place Details",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1.3f))
                            }
                        }
                    }
                }

                // Place Image
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                        AsyncImage(
                            model = place!!.imageUrl, 
                            contentDescription = place!!.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.three),
                            error = painterResource(id = R.drawable.three)
                        )
                        
                        IconButton(
                            onClick = { 
                                userViewModel.toggleFavorite(placeId)
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray
                            )
                        }
                    }
                }

                // Details Content
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = place!!.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = place!!.description,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                        DetailSectionHeader(icon = Icons.Default.LocationOn, title = "Location")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = place!!.location,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        DetailSectionHeader(icon = Icons.Outlined.CalendarMonth, title = "Best Season to Visit")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (place!!.bestSeason.isNotEmpty()) place!!.bestSeason else "All year round",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        DetailSectionHeader(icon = Icons.Default.Info, title = "Travel Tips")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (place!!.tips.isNotEmpty()) place!!.tips else "No tips added yet.",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.clickable {
                                val intent = Intent(context, ReviewActivity::class.java)
                                intent.putExtra("PLACE_ID", placeId)
                                context.startActivity(intent)
                            }) {
                                DetailSectionHeader(icon = Icons.Default.Star, title = "Reviews")
                                Spacer(modifier = Modifier.height(12.dp))
                                ReviewSummary(averageRating, reviews.size)
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(context, ReviewActivity::class.java)
                                    intent.putExtra("PLACE_ID", placeId)
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Add Review", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Uploaded by: ${place!!.uploadedByName}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ReviewSummary(averageRating: Float, reviewCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < averageRating.toInt()) BluePrimary else Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "($reviewCount Reviews)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DetailBottomNavigation() {
    val context = LocalContext.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(intent)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = {
                context.startActivity(Intent(context, MyProfileActivity::class.java))
            }
        )
    }
}
