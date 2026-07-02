package com.example.myroamnepal.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myroamnepal.R
import com.example.myroamnepal.model.PlaceModel
import com.example.myroamnepal.repo.PlaceRepoImpl
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.PlaceViewModel
import com.example.myroamnepal.viewModel.PlaceViewModelFactory

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
                PlaceDetailScreen(placeId = placeId, viewModel = placeViewModel)
            }
        }
    }
}

val DarkBlueText = Color(0xFF2D3E50)
val LightGrayBg = Color(0xFFF8F9FA)

@Composable
fun PlaceDetailScreen(placeId: String, viewModel: PlaceViewModel) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    var place by remember { mutableStateOf<PlaceModel?>(null) }
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(placeId) {
        if (placeId.isNotEmpty()) {
            // Fetch place details. We could add a getPlaceById to ViewModel as well.
            // For now, let's find it from the existing list if already loaded, 
            // or we could fetch it directly.
            PlaceRepoImpl(context).getPlaceById(placeId) { success, data, msg ->
                if (success) {
                    place = data
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Box(
                    modifier = Modifier
                        .background(BluePrimary)
                        .statusBarsPadding()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BluePrimary)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                        Spacer(modifier = Modifier.width(80.dp))

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
            BottomNavigation()
        }
    ) { innerPadding ->
        if (place == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(LightGrayBg)
            ) {

                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    AsyncImage(
                        model = place!!.imageUrl, 
                        contentDescription = place!!.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.three),
                        error = painterResource(id = R.drawable.three)
                    )
                    
                    // Heart (Favorite) Button
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
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

                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = place!!.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkBlueText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = place!!.description,
                        fontSize = 15.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp, color = Color.LightGray)


                    DetailSectionHeader(icon = Icons.Default.LocationOn, title = "Location")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = place!!.location,
                        fontSize = 15.sp,
                        color = DarkBlueText,
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Best Season Section
                    DetailSectionHeader(icon = Icons.Outlined.CalendarMonth, title = "Best Season to Visit")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (place!!.bestSeason.isNotEmpty()) place!!.bestSeason else "All year round",
                        fontSize = 15.sp,
                        color = DarkBlueText,
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Travel Tips Section
                    DetailSectionHeader(icon = Icons.Default.Info, title = "Travel Tips")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (place!!.tips.isNotEmpty()) place!!.tips else "No tips added yet.",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.clickable {
                            context.startActivity(Intent(context, ReviewActivity::class.java))
                        }) {
                            DetailSectionHeader(icon = Icons.Default.Star, title = "Reviews")
                            Spacer(modifier = Modifier.height(12.dp))
                            ReviewSummary()
                        }
                        Button(
                            onClick = {
                                context.startActivity(Intent(context, ReviewActivity::class.java))
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
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
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
        Text(text = title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = DarkBlueText)
    }
}

@Composable
fun ReviewSummary() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) {
            Icon(Icons.Default.Star, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "(0 Reviews)", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
