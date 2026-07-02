package com.example.myroamnepal.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.myroamnepal.view.ui.theme.LightGray
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.PlaceViewModel
import com.example.myroamnepal.viewModel.PlaceViewModelFactory
import com.example.myroamnepal.viewModel.UserViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                val context = LocalContext.current
                val userViewModel: UserViewModel = viewModel()
                val placeViewModel: PlaceViewModel = viewModel(
                    factory = PlaceViewModelFactory(PlaceRepoImpl(context))
                )
                DashboardScreen(userViewModel = userViewModel, placeViewModel = placeViewModel)
            }
        }
    }
}

@Composable
fun DashboardScreen(userViewModel: UserViewModel, placeViewModel: PlaceViewModel) {
    val context = LocalContext.current
    val user by userViewModel.user.collectAsState()
    val places by placeViewModel.places.collectAsState()
    val loading by placeViewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
        placeViewModel.getAllPlaces()
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigation() },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            GreetingSection(userName = user?.fullName ?: "Guest")
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(onUploadClick = {
                context.startActivity(Intent(context, AddPlaceActivity::class.java))
            })
            Spacer(modifier = Modifier.height(24.dp))
            
            if (loading && places.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (places.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No places found. Be the first to upload!", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(places) { place ->
                        PlaceCard(place) {
                            val intent = Intent(context, PlaceDetailActivity::class.java)
                            intent.putExtra("PLACE_ID", place.id)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BluePrimary)
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RoamNepal",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { 
                context.startActivity(Intent(context, NotificationActivity::class.java))
            }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable {
                        context.startActivity(Intent(context, ProfileActivity::class.java))
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Column {
        Text(
            text = "Welcome, $userName!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BluePrimary
        )
        Text(
            text = "Explore the Beauty of Nepal",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CategorySection(onUploadClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryChip("Popular", isSelected = true)
        CategoryChip("Mountains")
        CategoryChip("Temples")
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onUploadClick,
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Upload Place", fontSize = 12.sp)
        }
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean = false) {
    Surface(
        color = if (isSelected) LightGray else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) BluePrimary else Color.Gray
        )
    }
}

@Composable
fun PlaceCard(place: PlaceModel, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                AsyncImage(
                    model = place.imageUrl,
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.three),
                    error = painterResource(id = R.drawable.three)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BluePrimary)
                    .padding(8.dp)
            ) {
                Text(
                    text = place.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun BottomNavigation() {
    val context = LocalContext.current
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = false,
            onClick = { 
                context.startActivity(Intent(context, FavoritesActivity::class.java))
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
