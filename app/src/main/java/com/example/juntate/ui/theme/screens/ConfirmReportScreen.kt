package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.juntate.R
import com.example.juntate.ui.theme.PrimaryGreen
import com.example.juntate.ui.theme.TextGray
import com.example.juntate.ui.theme.JuntateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmReportScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_description),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        },
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(PrimaryGreen, shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.confirm_report_icon_desc),
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.confirm_report_message),
                style = MaterialTheme.typography.titleLarge,
                color = TextGray,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmReportScreenPreview() {
    JuntateTheme {
        ConfirmReportScreen(navController = rememberNavController())
    }
}
