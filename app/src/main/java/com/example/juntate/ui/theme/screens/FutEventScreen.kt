package com.example.juntate.ui.theme.screens

// Import BottomNavigationBar if it's in another file
// import com.example.juntate.ui.theme.screens.BottomNavigationBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration // Needed for TimePicker layout
import androidx.compose.ui.res.painterResource // Keep this
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import java.text.SimpleDateFormat // Import needed
import java.util.* // Import needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutEventScreen(navController: NavHostController) {

    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") } // State for date
    var eventTime by remember { mutableStateOf("") } // State for time
    var eventLocality by remember { mutableStateOf("") }
    var eventNeighborhood by remember { mutableStateOf("") }
    var eventLevel by remember { mutableStateOf("") }
    var eventNotes by remember { mutableStateOf("") }
    var localityDropdownExpanded by remember { mutableStateOf(false) }
    var neighborhoodDropdownExpanded by remember { mutableStateOf(false) }
    var levelDropdownExpanded by remember { mutableStateOf(false) }

    // --- Date Picker States ---
    val datePickerState = rememberDatePickerState()
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // --- Time Picker States ---
    val timePickerState = rememberTimePickerState()
    var showTimePickerDialog by remember { mutableStateOf(false) }

    // --- Logic for Neighborhood Dropdown ---
    val availableNeighborhoods = when (eventLocality) {
        "Antonio Nariño" -> stringArrayResource(id = R.array.antonio_nariño)
        "Barrios Unidos" -> stringArrayResource(id = R.array.barrios_unidos)
        "Bogotá D.C" -> stringArrayResource(id = R.array.bogota)
        "Bosa" -> stringArrayResource(id = R.array.bosa)
        "Chapinero" -> stringArrayResource(id = R.array.chapinero)
        "Ciudad Bolívar" -> stringArrayResource(id = R.array.ciudad_bolivar)
        "Engativá" -> stringArrayResource(id = R.array.engativa)
        "Fontibón" -> stringArrayResource(id = R.array.fontibon)
        "Kennedy" -> stringArrayResource(id = R.array.kennedy)
        "La Candelaria" -> stringArrayResource(id = R.array.la_candelaria)
        "Los Mártires" -> stringArrayResource(id = R.array.los_martires)
        "Puente Aranda" -> stringArrayResource(id = R.array.puente_aranda)
        "Rafael Uribe Uribe" -> stringArrayResource(id = R.array.rafael_uribe)
        "San Cristóbal" -> stringArrayResource(id = R.array.san_cristobal)
        "Santa Fe" -> stringArrayResource(id = R.array.santa_fe)
        "Suba" -> stringArrayResource(id = R.array.suba)
        "Sumapaz" -> stringArrayResource(id = R.array.sumapaz)
        "Teusaquillo" -> stringArrayResource(id = R.array.teusaquillo)
        "Tunjuelito" -> stringArrayResource(id = R.array.tunjuelito)
        "Usaquén" -> stringArrayResource(id = R.array.usaquen)
        "Usme" -> stringArrayResource(id = R.array.usme)
        else -> emptyArray()
    }
    LaunchedEffect(eventLocality) {
        if (eventNeighborhood !in availableNeighborhoods || eventLocality.isEmpty()) {
            eventNeighborhood = ""
        }
    }

    // --- Date Picker Dialog Composable ---
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            eventDate = formatter.format(Date(millis)).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }
                        }
                    }
                ) { Text(stringResource(id = R.string.dialog_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { Text(stringResource(id = R.string.dialog_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- Time Picker Dialog Composable ---
    if (showTimePickerDialog) {
        TimePickerDialog(
            onDismissRequest = { showTimePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePickerDialog = false
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        calendar.isLenient = false
                        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
                        eventTime = formatter.format(calendar.time)
                    }
                ) { Text(stringResource(id = R.string.dialog_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) { Text(stringResource(id = R.string.dialog_cancel)) }
            },
            timePickerState = timePickerState
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.fut_event_screen_title),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_description),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            // Ensure BottomNavigationBar is defined or imported correctly
            BottomNavigationBar(navController = navController, currentScreen = "fut_event_screen")
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.0f to White,
                    0.75f to White,
                    1.0f to TextGray
                )
            )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            // --- Event Name ---
            FormSection(label = stringResource(id = R.string.fut_event_field_name_label)) {
                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    placeholder = { Text(stringResource(id = R.string.fut_event_field_name_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = formTextFieldColors(),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(24.dp))

            // --- Date Section ---
            FormSection(label = stringResource(id = R.string.fut_event_field_date_label)) {
                ClickableInfoRow(
                    textValue = eventDate,
                    placeholderText = stringResource(id = R.string.fut_event_field_date_placeholder),
                    icon = Icons.Default.CalendarToday,
                    iconDesc = stringResource(id = R.string.icon_desc_calendar),
                    onClick = { showDatePickerDialog = true }
                )
            }

            Spacer(Modifier.height(24.dp))

            // --- Time Section ---
            FormSection(label = stringResource(id = R.string.fut_event_field_time_label)) {
                ClickableInfoRow(
                    textValue = eventTime,
                    placeholderText = stringResource(id = R.string.fut_event_field_time_placeholder),
                    icon = Icons.Default.Schedule,
                    iconDesc = stringResource(id = R.string.icon_desc_time),
                    onClick = { showTimePickerDialog = true }
                )
            }

            Spacer(Modifier.height(24.dp))

            // --- Location (Localidad) ---
            FormSection(label = stringResource(id = R.string.fut_event_field_locality_label)) {
                val localities = stringArrayResource(id = R.array.bogota_localities)
                val isLocalityEmpty = eventLocality.isEmpty()

                ExposedDropdownMenuBox(
                    expanded = localityDropdownExpanded,
                    onExpandedChange = { localityDropdownExpanded = !localityDropdownExpanded },
                ) {
                    OutlinedTextField(
                        placeholder = { Text(stringResource(id = R.string.fut_event_field_locality_placeholder)) },
                        value = eventLocality,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { localityDropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = formTextFieldColors(isPlaceholder = isLocalityEmpty),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = stringResource(id = R.string.icon_desc_location),
                                tint = if (isLocalityEmpty) MediumGray else PrimaryGreen
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = localityDropdownExpanded) },
                        textStyle = LocalTextStyle.current.copy(
                            color = if (isLocalityEmpty) MediumGray else LocalContentColor.current,
                            fontWeight = if (isLocalityEmpty) FontWeight.Normal else FontWeight.Bold
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = localityDropdownExpanded,
                        onDismissRequest = { localityDropdownExpanded = false }
                    ) {
                        localities.forEach { locality ->
                            DropdownMenuItem(
                                text = { Text(locality, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    eventLocality = locality
                                    localityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Location (Barrio) ---
            FormSection(label = stringResource(id = R.string.fut_event_field_neighborhood_label)) {
                val isNeighborhoodEmpty = eventNeighborhood.isEmpty()
                val isLocalitySelected = eventLocality.isNotEmpty()

                ExposedDropdownMenuBox(
                    expanded = neighborhoodDropdownExpanded && isLocalitySelected,
                    onExpandedChange = { if (isLocalitySelected) neighborhoodDropdownExpanded = !neighborhoodDropdownExpanded },
                ) {
                    OutlinedTextField(
                        placeholder = { Text(stringResource(id = R.string.fut_event_field_neighborhood_placeholder)) },
                        value = eventNeighborhood,
                        onValueChange = {},
                        readOnly = true,
                        enabled = isLocalitySelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable(enabled = isLocalitySelected) { neighborhoodDropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = formTextFieldColors(isPlaceholder = isNeighborhoodEmpty || !isLocalitySelected),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                contentDescription = stringResource(id = R.string.icon_desc_neighborhood),
                                tint = if (isNeighborhoodEmpty || !isLocalitySelected) MediumGray else PrimaryGreen
                            )
                        },
                        trailingIcon = { if (isLocalitySelected) ExposedDropdownMenuDefaults.TrailingIcon(expanded = neighborhoodDropdownExpanded) },
                        textStyle = LocalTextStyle.current.copy(
                            color = if (isNeighborhoodEmpty || !isLocalitySelected) MediumGray else LocalContentColor.current,
                            fontWeight = if (isNeighborhoodEmpty || !isLocalitySelected) FontWeight.Normal else FontWeight.Bold
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = neighborhoodDropdownExpanded && isLocalitySelected,
                        onDismissRequest = { neighborhoodDropdownExpanded = false }
                    ) {
                        if (!isLocalitySelected || availableNeighborhoods.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.select_locality_first)) },
                                onClick = { },
                                enabled = false
                            )
                        } else {
                            availableNeighborhoods.forEach { neighborhood ->
                                DropdownMenuItem(
                                    text = { Text(neighborhood, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        eventNeighborhood = neighborhood
                                        neighborhoodDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Recommended Level (Dropdown) ---
            FormSection(label = stringResource(id = R.string.fut_event_field_level_label)) {
                val levels = stringArrayResource(id = R.array.experiencia)
                val isEmpty = eventLevel.isEmpty()

                ExposedDropdownMenuBox(
                    expanded = levelDropdownExpanded,
                    onExpandedChange = { levelDropdownExpanded = !levelDropdownExpanded },
                ) {
                    OutlinedTextField(
                        placeholder = { Text(stringResource(id = R.string.fut_event_field_level_placeholder)) },
                        value = eventLevel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { levelDropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = formTextFieldColors(isPlaceholder = isEmpty),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = stringResource(id = R.string.icon_desc_level),
                                tint = if (isEmpty) MediumGray else PrimaryGreen
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelDropdownExpanded) },
                        textStyle = LocalTextStyle.current.copy(
                            color = if (isEmpty) MediumGray else LocalContentColor.current,
                            fontWeight = if (isEmpty) FontWeight.Normal else FontWeight.Bold
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = levelDropdownExpanded,
                        onDismissRequest = { levelDropdownExpanded = false }
                    ) {
                        levels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    eventLevel = level
                                    levelDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Additional Notes ---
            FormSection(label = stringResource(id = R.string.fut_event_field_notes_label)) {
                OutlinedTextField(
                    value = eventNotes,
                    onValueChange = { eventNotes = it },
                    placeholder = { Text(stringResource(id = R.string.fut_event_field_notes_placeholder)) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = formTextFieldColors()
                )
            }

            Spacer(Modifier.height(32.dp))
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* TODO: Validate and Create Event Logic */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(
                    text = stringResource(id = R.string.fut_event_create_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// --- Helper Composables ---
@Composable
private fun FormSection(label: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = label,
            color = PrimaryGreen,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun ClickableInfoRow(
    textValue: String,
    placeholderText: String,
    icon: ImageVector,
    iconDesc: String,
    onClick: () -> Unit
) {
    val isEmpty = textValue.isEmpty()
    val displayText = if (isEmpty) placeholderText else textValue

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = formTextFieldColors(isPlaceholder = isEmpty),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = iconDesc,
                tint = if (isEmpty) MediumGray else PrimaryGreen
            )
        },
        textStyle = LocalTextStyle.current.copy(
            color = if (isEmpty) MediumGray else LocalContentColor.current,
            fontWeight = if (isEmpty) FontWeight.Normal else FontWeight.Bold
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun formTextFieldColors(isPlaceholder: Boolean = false): TextFieldColors {
    val textColor = if (isPlaceholder) MediumGray else LocalContentColor.current
    return OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = MutedGreen.copy(alpha = 0.3f),
        focusedContainerColor = MutedGreen.copy(alpha = 0.4f),
        unfocusedBorderColor = BorderGray.copy(alpha = 0.5f),
        focusedBorderColor = PrimaryGreen,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedPlaceholderColor = MediumGray,
        unfocusedPlaceholderColor = MediumGray,
        focusedLabelColor = PrimaryGreen,
        unfocusedLabelColor = PrimaryGreen,
        focusedLeadingIconColor = PrimaryGreen,
        unfocusedLeadingIconColor = if (isPlaceholder) MediumGray else PrimaryGreen,
        disabledTextColor = MediumGray,
        disabledPlaceholderColor = MediumGray.copy(alpha=0.7f),
        disabledLeadingIconColor = MediumGray,
        disabledBorderColor = BorderGray.copy(alpha = 0.3f),
        disabledContainerColor = MutedGreen.copy(alpha = 0.1f)
    )
}

// --- Time Picker Dialog Helper ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    timePickerState: TimePickerState
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = stringResource(R.string.fut_event_field_time_placeholder),
                    style = MaterialTheme.typography.labelMedium
                )
                TimePicker(state = timePickerState) // Material 3 TimePicker
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun FutEventScreenPreview() {
    JuntateTheme {
        FutEventScreen(navController = rememberNavController())
    }
}

// Ensure BottomNavigationBar is defined ONLY ONCE (e.g., in NavigationUI.kt) and imported above
// @Composable
// fun BottomNavigationBar(...) { ... }