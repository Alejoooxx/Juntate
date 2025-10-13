package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import com.example.juntate.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterSuccess: (String) -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val viewModel: AuthViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val registerSuccessMessage = stringResource(id = R.string.register_success_message)
    val emptyFieldsMessage = stringResource(id = R.string.register_error_empty_fields)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(White, LightGray)
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.esquina3),
            contentDescription = null,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(JuntateGreen),
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.TopStart)
                .offset(x = -(350.dp / 3.5f), y = -(350.dp / 3.5f))
                .alpha(0.35f)
        )

        Image(
            painter = painterResource(id = R.drawable.esquina4),
            contentDescription = null,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(JuntateGreen),
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (350.dp / 3.5f), y = (350.dp / 3.5f))
                .alpha(0.35f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = JuntateBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoverde),
                        contentDescription = stringResource(id = R.string.register_logo_description),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(White),
                        modifier = Modifier.size(110.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(id = R.string.register_title),
                        color = White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AuthInputField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = stringResource(id = R.string.register_placeholder_full_name),
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthInputField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = stringResource(id = R.string.register_placeholder_email),
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthInputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = stringResource(id = R.string.register_placeholder_password),
                        isPassword = true,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(id = R.string.register_social_prompt),
                        color = White,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SocialButton(
                            modifier = Modifier.weight(1f),
                            icon = R.drawable.google,
                            text = stringResource(id = R.string.social_google)
                        )
                        SocialButton(
                            modifier = Modifier.weight(1f),
                            icon = R.drawable.facebook,
                            text = stringResource(id = R.string.social_facebook)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SocialButton(
                            modifier = Modifier.weight(1f),
                            icon = R.drawable.instagram,
                            text = stringResource(id = R.string.social_instagram)
                        )
                        SocialButton(
                            modifier = Modifier.weight(1f),
                            icon = R.drawable.apple,
                            text = stringResource(id = R.string.social_apple)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                isLoading = true
                                viewModel.registerUser(
                                    name = name.trim(),
                                    email = email.trim(),
                                    password = password,
                                    onSuccess = {
                                        isLoading = false
                                        onRegisterSuccess(registerSuccessMessage)
                                    },
                                    onError = { errorMessage ->
                                        isLoading = false
                                        coroutineScope.launch { snackbarHostState.showSnackbar(errorMessage) }
                                    }
                                )
                            } else {
                                coroutineScope.launch { snackbarHostState.showSnackbar(emptyFieldsMessage) }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = JuntateGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
                        } else {
                            Text(
                                text = stringResource(id = R.string.register_create_account_button),
                                color = White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.clickable { onLoginClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.register_login_prompt), color = White, fontSize = 14.sp)
                        Text(
                            text = stringResource(id = R.string.register_login_button),
                            color = JuntateGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MediumGray) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = White,
            focusedContainerColor = White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = JuntateGreen
        ),
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 16.sp, color = Black)
    )
}

@Composable
fun SocialButton(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String
) {
    OutlinedButton(
        onClick = { },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = JuntateBackground
        ),
        border = BorderStroke(1.dp, BorderGray),
        contentPadding = PaddingValues(horizontal = 12.dp),
        modifier = modifier
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}