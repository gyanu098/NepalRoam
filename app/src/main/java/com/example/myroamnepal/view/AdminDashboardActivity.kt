package com.example.myroamnepal.view

import android.content.Intent
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myroamnepal.model.PlaceModel
import com.example.myroamnepal.model.UserModel
import com.example.myroamnepal.repo.PlaceRepoImpl
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme
import com.example.myroamnepal.viewModel.PlaceViewModel
import com.example.myroamnepal.viewModel.PlaceViewModelFactory
import com.example.myroamnepal.viewModel.UserViewModel

class AdminDashboardActivity : ComponentActivity() {
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
                AdminDashboardScreen(
                    userViewModel = userViewModel,
                    placeViewModel = placeViewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    userViewModel: UserViewModel,
    placeViewModel: PlaceViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 for Users, 1 for Posts
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.getAllUsers()
        placeViewModel.getAllPlaces()
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp, color = BluePrimary) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Panel",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = BluePrimary,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color.White
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Users") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Posts") }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (selectedTab == 0) {
                UserManagementSection(userViewModel)
            } else {
                PostManagementSection(placeViewModel)
            }
        }
    }
}

@Composable
fun UserManagementSection(viewModel: UserViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current

    if (loading && users.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                UserAdminCard(
                    user = user,
                    onDelete = {
                        viewModel.deleteUser(user.uid) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onUpdate = { updatedUser ->
                        viewModel.updateUser(updatedUser.uid, updatedUser.fullName, updatedUser.phone, updatedUser.profileImageUrl)
                    }
                )
            }
        }
    }
}

@Composable
fun UserAdminCard(user: UserModel, onDelete: () -> Unit, onUpdate: (UserModel) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete ${user.fullName}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        var name by remember { mutableStateOf(user.fullName) }
        var phone by remember { mutableStateOf(user.phone) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit User") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdate(user.copy(fullName = name, phone = phone))
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = user.email, color = Color.Gray, fontSize = 14.sp)
                Text(text = "Role: ${user.role}", color = BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BluePrimary)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun PostManagementSection(viewModel: PlaceViewModel) {
    val places by viewModel.places.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current

    if (loading && places.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(places) { place ->
                PostAdminCard(
                    place = place,
                    onDelete = {
                        viewModel.deletePlace(place.id)
                    },
                    onUpdate = { updatedPlace ->
                        viewModel.updatePlace(updatedPlace)
                    }
                )
            }
        }
    }
}

@Composable
fun PostAdminCard(place: PlaceModel, onDelete: () -> Unit, onUpdate: (PlaceModel) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete '${place.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        var name by remember { mutableStateOf(place.name) }
        var location by remember { mutableStateOf(place.location) }
        var description by remember { mutableStateOf(place.description) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Post") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Place Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, minLines = 3)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdate(place.copy(name = name, location = location, description = description))
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = place.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = place.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "By: ${place.uploadedByName}", color = Color.Gray, fontSize = 12.sp)
                Text(text = place.location, color = BluePrimary, fontSize = 12.sp)
            }
            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BluePrimary)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}
