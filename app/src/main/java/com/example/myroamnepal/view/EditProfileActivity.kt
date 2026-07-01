package com.example.myroamnepal.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myroamnepal.R
import com.example.myroamnepal.view.ui.theme.BluePrimary
import com.example.myroamnepal.view.ui.theme.MyRoamNepalTheme

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyRoamNepalTheme {
                EditProfileScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("Gyanu") }
    var email by remember { mutableStateOf("gyanu@example.com") }
    var phone by remember { mutableStateOf("+977 9800000000") }
    var address by remember { mutableStateOf("Kathmandu, Nepal") }
    
    // State to hold the selected image URI
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Launcher for picking an image
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BluePrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profile Picture Section with Change Icon
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(3.dp, BluePrimary, CircleShape)
                        .background(Color.White)
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                // Change Picture Button (Camera Icon)
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { 
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    color = BluePrimary,
                    tonalElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Picture",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EditProfileTextField(
                        label = "Full Name",
                        value = name,
                        onValueChange = { name = it },
                        icon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EditProfileTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        icon = Icons.Default.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EditProfileTextField(
                        label = "Phone",
                        value = phone,
                        onValueChange = { phone = it },
                        icon = Icons.Default.Phone
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EditProfileTextField(
                        label = "Address",
                        value = address,
                        onValueChange = { address = it },
                        icon = Icons.Default.LocationOn
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save Changes Button
            Button(
                onClick = { /* Handle save changes logic here */ onBack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(text = "Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = BluePrimary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = BluePrimary
        ),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    MyRoamNepalTheme {
        EditProfileScreen(onBack = {})
    }
}
