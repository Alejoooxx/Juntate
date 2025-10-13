package com.example.juntate.ui.theme.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import com.example.juntate.viewmodel.AuthViewModel
import com.example.juntate.viewmodel.UserProfile
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val viewModel: AuthViewModel = viewModel()
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(false) }

    var editedName by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedBirthDate by remember { mutableStateOf("") }
    var editedLocation by remember { mutableStateOf("") }
    var editedProfilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var futbolLevel by remember { mutableStateOf<String?>(null) }
    var runningLevel by remember { mutableStateOf<String?>(null) }
    var gymLevel by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionDeniedMessage = stringResource(id = R.string.permission_denied)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> editedProfilePictureUri = uri }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, permissionDeniedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUserProfile()
    }

    LaunchedEffect(userProfile, isEditMode) {
        userProfile?.let {
            if (isEditMode) {
                editedName = it.name
                editedEmail = it.email
                editedBirthDate = it.birthDate
                editedLocation = it.location
                futbolLevel = it.futbolLevel
                runningLevel = it.runningLevel
                gymLevel = it.gymLevel
                editedProfilePictureUri = null
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                            selectedDate.timeInMillis = millis
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            editedBirthDate = formatter.format(selectedDate.time)
                        }
                    }
                ) { Text(stringResource(id = R.string.dialog_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(id = R.string.dialog_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            ProfileTopAppBar(
                isEditMode = isEditMode,
                onEditClick = { isEditMode = true },
                onSaveClick = {
                    val currentProfile = userProfile ?: return@ProfileTopAppBar

                    fun saveProfile(imageUrl: String) {
                        viewModel.updateUserProfile(
                            profileData = currentProfile.copy(
                                name = editedName,
                                email = editedEmail,
                                birthDate = editedBirthDate,
                                location = editedLocation,
                                profilePictureUrl = imageUrl,
                                futbolLevel = futbolLevel,
                                runningLevel = runningLevel,
                                gymLevel = gymLevel
                            ),
                            onSuccess = { successMessage ->
                                isEditMode = false
                                coroutineScope.launch { snackbarHostState.showSnackbar(successMessage) }
                            },
                            onError = { error ->
                                coroutineScope.launch { snackbarHostState.showSnackbar(error) }
                            }
                        )
                    }

                    if (editedProfilePictureUri != null) {
                        viewModel.uploadProfilePicture(
                            uri = editedProfilePictureUri!!,
                            onSuccess = { newImageUrl -> saveProfile(newImageUrl) },
                            onError = { error ->
                                coroutineScope.launch { snackbarHostState.showSnackbar(error) }
                            }
                        )
                    } else {
                        saveProfile(currentProfile.profilePictureUrl)
                    }
                },
                onCancelClick = { isEditMode = false },
                onLogoutClick = {
                    viewModel.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(White, LightGray)))
        ) {
            if (maxWidth < 600.dp) {
                PhoneProfileLayout(
                    isEditMode = isEditMode,
                    userProfile = userProfile,
                    editedName = editedName,
                    editedEmail = editedEmail,
                    editedBirthDate = editedBirthDate,
                    editedLocation = editedLocation,
                    futbolLevel = futbolLevel,
                    runningLevel = runningLevel,
                    gymLevel = gymLevel,
                    editedProfilePictureUri = editedProfilePictureUri,
                    onNameChange = { editedName = it },
                    onEmailChange = { editedEmail = it },
                    onLocationChange = { editedLocation = it },
                    onBirthDateClick = { showDatePicker = true },
                    onFutbolLevelSelected = { futbolLevel = it },
                    onRunningLevelSelected = { runningLevel = it },
                    onGymLevelSelected = { gymLevel = it },
                    onProfilePictureClick = { permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES) }
                )
            } else {
                TabletProfileLayout(
                    isEditMode, userProfile, editedName, { editedName = it },
                    editedEmail, { editedEmail = it },
                    editedBirthDate, { showDatePicker = true },
                    editedLocation, { editedLocation = it },
                    futbolLevel, { futbolLevel = it },
                    runningLevel, { runningLevel = it },
                    gymLevel, { gymLevel = it },
                    editedProfilePictureUri,
                    { permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES) }
                )
            }
        }
    }
}

@Composable
fun PhoneProfileLayout(
    isEditMode: Boolean,
    userProfile: UserProfile?,
    editedName: String, onNameChange: (String) -> Unit,
    editedEmail: String, onEmailChange: (String) -> Unit,
    editedBirthDate: String, onBirthDateClick: () -> Unit,
    editedLocation: String, onLocationChange: (String) -> Unit,
    futbolLevel: String?, onFutbolLevelSelected: (String) -> Unit,
    runningLevel: String?, onRunningLevelSelected: (String) -> Unit,
    gymLevel: String?, onGymLevelSelected: (String) -> Unit,
    editedProfilePictureUri: Uri?,
    onProfilePictureClick: () -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ProfileHeader(
                isEditMode = isEditMode,
                profilePictureUri = editedProfilePictureUri,
                profilePictureUrl = userProfile?.profilePictureUrl,
                onProfilePictureClick = onProfilePictureClick
            )
        }
        item {
            UserInfoSection(
                isEditMode = isEditMode,
                userProfile = userProfile,
                editedName = editedName, onNameChange = onNameChange,
                editedEmail = editedEmail, onEmailChange = onEmailChange,
                editedBirthDate = editedBirthDate, onBirthDateClick = onBirthDateClick,
                editedLocation = editedLocation, onLocationChange = onLocationChange
            )
        }
        item {
            Divider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp), color = MediumGray)
        }
        item {
            SportsSection(
                isEditMode = isEditMode,
                futbolLevel = if (isEditMode) futbolLevel else userProfile?.futbolLevel,
                onFutbolLevelSelected = onFutbolLevelSelected,
                runningLevel = if (isEditMode) runningLevel else userProfile?.runningLevel,
                onRunningLevelSelected = onRunningLevelSelected,
                gymLevel = if (isEditMode) gymLevel else userProfile?.gymLevel,
                onGymLevelSelected = onGymLevelSelected
            )
        }
    }
}

@Composable
fun TabletProfileLayout(
    isEditMode: Boolean,
    userProfile: UserProfile?,
    editedName: String, onNameChange: (String) -> Unit,
    editedEmail: String, onEmailChange: (String) -> Unit,
    editedBirthDate: String, onBirthDateClick: () -> Unit,
    editedLocation: String, onLocationChange: (String) -> Unit,
    futbolLevel: String?, onFutbolLevelSelected: (String) -> Unit,
    runningLevel: String?, onRunningLevelSelected: (String) -> Unit,
    gymLevel: String?, onGymLevelSelected: (String) -> Unit,
    editedProfilePictureUri: Uri?,
    onProfilePictureClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(
                isEditMode = isEditMode,
                profilePictureUri = editedProfilePictureUri,
                profilePictureUrl = userProfile?.profilePictureUrl,
                onProfilePictureClick = onProfilePictureClick
            )
            UserInfoSection(
                isEditMode = isEditMode,
                userProfile = userProfile,
                editedName = editedName, onNameChange = onNameChange,
                editedEmail = editedEmail, onEmailChange = onEmailChange,
                editedBirthDate = editedBirthDate, onBirthDateClick = onBirthDateClick,
                editedLocation = editedLocation, onLocationChange = onLocationChange
            )
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            SportsSection(
                isEditMode = isEditMode,
                futbolLevel = if (isEditMode) futbolLevel else userProfile?.futbolLevel,
                onFutbolLevelSelected = onFutbolLevelSelected,
                runningLevel = if (isEditMode) runningLevel else userProfile?.runningLevel,
                onRunningLevelSelected = onRunningLevelSelected,
                gymLevel = if (isEditMode) gymLevel else userProfile?.gymLevel,
                onGymLevelSelected = onGymLevelSelected
            )
        }
    }
}

@Composable
fun ProfileHeader(
    isEditMode: Boolean,
    profilePictureUri: Uri?,
    profilePictureUrl: String?,
    onProfilePictureClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(24.dp))
    Box {
        AsyncImage(
            model = profilePictureUri ?: profilePictureUrl,
            contentDescription = stringResource(id = R.string.profile_picture_content_description),
            placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
            error = painterResource(id = R.drawable.ic_profile_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, PrimaryGreen, CircleShape)
                .clickable(enabled = isEditMode, onClick = onProfilePictureClick)
        )
        if (isEditMode) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.profile_edit_photo_icon_description),
                tint = White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(PrimaryGreen, CircleShape)
                    .padding(8.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoSection(
    isEditMode: Boolean,
    userProfile: UserProfile?,
    editedName: String, onNameChange: (String) -> Unit,
    editedEmail: String, onEmailChange: (String) -> Unit,
    editedBirthDate: String, onBirthDateClick: () -> Unit,
    editedLocation: String, onLocationChange: (String) -> Unit
) {
    if (isEditMode) {
        val bogotaLocalities = stringArrayResource(id = R.array.bogota_localities)
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            OutlinedTextField(value = editedName, onValueChange = onNameChange, label = { Text(stringResource(id = R.string.profile_label_full_name)) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = editedEmail,
                onValueChange = {},
                label = { Text(stringResource(id = R.string.profile_label_email)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(id = R.string.profile_locked_field_description)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    disabledContainerColor = Color.LightGray.copy(alpha = 0.2f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                OutlinedTextField(
                    value = editedBirthDate,
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.profile_label_birth_date)) },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = LocalContentColor.current,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(onClick = onBirthDateClick)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = editedLocation,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.profile_label_location)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    bogotaLocalities.forEach { locality ->
                        DropdownMenuItem(
                            text = { Text(locality) },
                            onClick = {
                                onLocationChange(locality)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    } else {
        val loadingText = stringResource(id = R.string.loading)
        Column {
            ProfileInfoCard(label = stringResource(id = R.string.profile_label_full_name), value = userProfile?.name ?: loadingText)
            ProfileInfoCard(label = stringResource(id = R.string.profile_label_email), value = userProfile?.email ?: loadingText)
            ProfileInfoCard(label = stringResource(id = R.string.profile_label_birth_date), value = userProfile?.birthDate ?: "")
            ProfileInfoCard(label = stringResource(id = R.string.profile_label_location), value = userProfile?.location ?: "")
        }
    }
}

@Composable
fun SportsSection(
    isEditMode: Boolean,
    futbolLevel: String?, onFutbolLevelSelected: (String) -> Unit,
    runningLevel: String?, onRunningLevelSelected: (String) -> Unit,
    gymLevel: String?, onGymLevelSelected: (String) -> Unit
) {
    if (isEditMode) {
        Column {
            SportPreferenceSection(sportName = stringResource(id = R.string.sport_soccer), currentLevel = futbolLevel, onLevelSelected = onFutbolLevelSelected)
            SportPreferenceSection(sportName = stringResource(id = R.string.sport_running), currentLevel = runningLevel, onLevelSelected = onRunningLevelSelected)
            SportPreferenceSection(sportName = stringResource(id = R.string.sport_gym), currentLevel = gymLevel, onLevelSelected = onGymLevelSelected)
        }
    } else {
        Column {
            SportDisplayCard(sportName = stringResource(id = R.string.sport_soccer), selectedLevel = futbolLevel)
            SportDisplayCard(sportName = stringResource(id = R.string.sport_running), selectedLevel = runningLevel)
            SportDisplayCard(sportName = stringResource(id = R.string.sport_gym), selectedLevel = gymLevel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(
    isEditMode: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        title = {
            TextButton(onClick = onLogoutClick) {
                Text(stringResource(id = R.string.profile_logout_button), color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            if (isEditMode) {
                TextButton(onClick = onSaveClick) { Text(stringResource(id = R.string.profile_save_button), fontWeight = FontWeight.Bold) }
                TextButton(onClick = onCancelClick) { Text(stringResource(id = R.string.dialog_cancel), fontWeight = FontWeight.Bold) }
            } else {
                TextButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.profile_edit_icon_description), tint = PrimaryGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(id = R.string.profile_edit_button), color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun ProfileInfoCard(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .background(MutedGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SportDisplayCard(sportName: String, selectedLevel: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = sportName,
            color = PrimaryGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                .background(MutedGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selectedLevel ?: stringResource(id = R.string.sport_level_placeholder),
                color = if (selectedLevel != null) PrimaryGreen else MediumGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportPreferenceSection(
    sportName: String,
    currentLevel: String?,
    onLevelSelected: (String) -> Unit
) {
    val levels = listOf(
        stringResource(id = R.string.sport_level_beginner),
        stringResource(id = R.string.sport_level_intermediate),
        stringResource(id = R.string.sport_level_advanced)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = sportName,
            color = PrimaryGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            levels.forEach { level ->
                val isSelected = (currentLevel == level)
                val border = if (isSelected) null else BorderStroke(1.dp, PrimaryGreen)

                FilterChip(
                    selected = isSelected,
                    onClick = { onLevelSelected(level) },
                    label = { Text(level) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MutedGreen,
                        labelColor = PrimaryGreen,
                        selectedContainerColor = PrimaryGreen,
                        selectedLabelColor = White
                    ),
                    border = border
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", name = "Phone Preview")
@Composable
fun ProfileScreenPhonePreview() {
    JuntateTheme {
        ProfileScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240", name = "Tablet Preview")
@Composable
fun ProfileScreenTabletPreview() {
    JuntateTheme {
        ProfileScreen(navController = rememberNavController())
    }
}