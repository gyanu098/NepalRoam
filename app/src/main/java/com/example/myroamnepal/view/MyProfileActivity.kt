package com.example.myroamnepal.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.myroamnepal.viewModel.UserViewModel

class MyProfileActivity : ComponentActivity() {
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
                MyProfileScreen(userViewModel = userViewModel, placeViewModel = placeViewModel)
            }
        }
    }
}

@Composable
fun MyProfileScreen(userViewModel: UserViewModel, placeViewModel: PlaceViewModel) {
    val context = LocalContext.current
    val user by userViewModel.user.collectAsState()
    val userPlaces by placeViewModel.userPlaces.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 for Posts, 1 for Favourites

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    LaunchedEffect(user) {
        user?.let {
            placeViewModel.getPlacesByUser(it.uid)
        }
    }

    // Mock favorites for now until Favorite feature is implemented
    val favoritePlaces = emptyList<PlaceModel>()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
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
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favourites") },
                    label = { Text("Favourites") },
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, FavoritesActivity::class.java))
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // User Name and Profile Info Section
            ProfileHeaderSection(
                userName = user?.fullName ?: "Guest",
                postsCount = userPlaces.size,
                favCount = favoritePlaces.size
            )

            // Edit Profile Button (Instagram Style)
            Button(
                onClick = { context.startActivity(Intent(context, EditProfileActivity::class.java)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFEFEF), contentColor = Color.Black)
            ) {
                Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs for Posts and Favourites
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = BluePrimary,
                divider = { HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Posts") },
                    icon = { Icon(Icons.Default.GridView, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Favourites") },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) }
                )
            }

            // Grid view for Posts or Favourites
            val displayList = if (selectedTab == 0) userPlaces else favoritePlaces
            
            if (displayList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (selectedTab == 0) "No posts yet" else "No favorites yet",
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(displayList) { place ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = place.imageUrl,
                                contentDescription = place.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        val intent = Intent(context, PlaceDetailActivity::class.java)
                                        intent.putExtra("PLACE_ID", place.id)
                                        context.startActivity(intent)
                                    },
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.three),
                                error = painterResource(id = R.drawable.three)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(userName: String, postsCount: Int, favCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // User Profile Picture
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
            
            // Stats
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(postsCount.toString(), "Posts")
                ProfileStatItem(favCount.toString(), "Saved")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // User Name and Bio
        Text(
            text = userName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3E50)
        )
        Text(
            text = "Mountain Explorer | Traveler",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF2D3E50))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}
