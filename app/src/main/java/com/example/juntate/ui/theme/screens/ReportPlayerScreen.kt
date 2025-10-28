package com.example.juntate.ui.theme.screens

import com.example.juntate.ui.theme.screens.BottomNavigationBar
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.juntate.R
import com.example.juntate.ui.theme.*

private data class ReportOptionState(
    val textResId: Int,
    var isChecked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPlayerScreen(navController: NavHostController) {

    // Agregar la forma de obtener el perfil del jugador (nombre, imagen) usando el ID

    val reportOptions = remember {
        mutableStateListOf(
            ReportOptionState(R.string.report_reason_behavior),
            ReportOptionState(R.string.report_reason_no_show),
            ReportOptionState(R.string.report_reason_offensive_language),
            ReportOptionState(R.string.report_reason_other)
        )
    }
    var otherReasonText by remember { mutableStateOf("") }
    val isOtherChecked = reportOptions.last().isChecked

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        bottomBar = { },
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = R.drawable.ic_profile_placeholder,
                contentDescription = "Foto del jugador",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                error = painterResource(id = R.drawable.ic_profile_placeholder)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Javier López",
                color = PrimaryGreen,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(id = R.string.report_player_prompt),
                color = Color.Black.copy(alpha = 0.8f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                reportOptions.forEachIndexed { index, option ->
                    ReportCheckboxRow(
                        text = stringResource(id = option.textResId),
                        checked = option.isChecked,
                        onCheckedChange = { isChecked ->
                            if (option.textResId == R.string.report_reason_other) {
                                reportOptions.indices.forEach { i ->
                                    reportOptions[i] = reportOptions[i].copy(isChecked = i == index)
                                }
                            } else if (isChecked) {
                                reportOptions[index] = option.copy(isChecked = true)
                                val otherIndex = reportOptions.indexOfFirst { it.textResId == R.string.report_reason_other }
                                if (otherIndex != -1) {
                                    reportOptions[otherIndex] = reportOptions[otherIndex].copy(isChecked = false)
                                    otherReasonText = ""
                                }
                            } else {
                                reportOptions[index] = option.copy(isChecked = false)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (isOtherChecked) {
                OutlinedTextField(
                    value = otherReasonText,
                    onValueChange = { otherReasonText = it },
                    placeholder = { Text(stringResource(id = R.string.report_specify_reason_placeholder), fontSize = 16.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 150.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black.copy(alpha = 0.6f),
                        focusedContainerColor = Color(0xFFF2F2F2),
                        unfocusedContainerColor = Color(0xFFF2F2F2),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        cursorColor = PrimaryGreen
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(if (isOtherChecked) 24.dp else 40.dp))
            }

            Button(
                onClick = {
                    // Agregar logica de registro en el Firebase
                    navController.navigate("confirm_report") {
                        popUpTo(navController.currentBackStackEntry!!.destination.route!!) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = reportOptions.any { it.isChecked } && (!isOtherChecked || otherReasonText.isNotBlank())
            ) {
                Text(
                    text = stringResource(id = R.string.report_send_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ReportCheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryGreen,
                uncheckedColor = Color.Gray
            ),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.Black.copy(alpha = 0.9f),
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ReportPlayerScreenPreview() {
    JuntateTheme {
        ReportPlayerScreen(navController = rememberNavController())
    }
}