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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myroamnepal.R
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme

class MyProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                MyProfileScreen()
            }
        }
    }
}

@Composable
fun MyProfileScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 for Posts, 1 for Favourites

    val userPosts = listOf(
        Destination("Cave", R.drawable.placeone),
        Destination("Hidden lake", R.drawable.placetwo),
        Destination("Waterfall near kathmandu", R.drawable.three),
        Destination("Beautiful temple", R.drawable.four),
        Destination("Hidden lake", R.drawable.placetwo),
        Destination("Cave", R.drawable.placeone)
    )

    val favoritePlaces = listOf(
        Destination("Hidden lake", R.drawable.placetwo),
        Destination("Waterfall near kathmandu", R.drawable.three)
    )

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
            ProfileHeaderSection(postsCount = userPosts.size, favCount = favoritePlaces.size)

            // Side-by-side Options (Posts and Favourites)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = BluePrimary,
                divider = { HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Posts", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.GridView, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Favourites", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) }
                )
            }

            // Grid view for Posts or Favourites (3 posts in 1 row style)
            val displayList = if (selectedTab == 0) userPosts else favoritePlaces
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(displayList) { destination ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = painterResource(id = destination.imageRes),
                            contentDescription = destination.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    context.startActivity(Intent(context, PlaceDetailActivity::class.java))
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(postsCount: Int, favCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User Profile Picture
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, BluePrimary, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        // User Name
        Text(
            text = "Gyanu",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3E50)
        )
        Text(
            text = "Mountain Explorer | Traveler",
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Simple Stats - Posts and Favourites count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStatItem(postsCount.toString(), "Posts")
            ProfileStatItem(favCount.toString(), "Favourites")
        }
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF2D3E50))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun MyProfileScreenPreview() {
    MyRoamNepalTheme {
        MyProfileScreen()
    }
}
