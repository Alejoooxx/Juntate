package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import kotlinx.coroutines.launch

data class Message(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isSentByCurrentUser: Boolean
)

val dummyMessages = listOf(
    Message("4", "Carlos", "Está bien, hasta mañana.", "15:53", true),
    Message("3", "Javier", "Recuerden llegar un poco antes", "15:37", false),
    Message("2", "Carlos", "A las 4:00 p.m., no falten!", "15:32", true),
    Message("1", "Javier", "Hola, ¿a qué hora nos vemos?", "15:25", false)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    eventId: String,
    eventTitle: String
) {
    var messageText by remember { mutableStateOf("") }
    val messages = dummyMessages
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(eventTitle, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.chat_back_button_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                value = messageText,
                onValueChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        println("Mensaje enviado (simulado): $messageText")
                        messageText = ""
                        coroutineScope.launch {
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(index = messages.size - 1)
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(White, LightGray)
                    )
                )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true,
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(message = message)
                }
            }
        }
    }
}


@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        color = White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /*Lógica de adjuntar archivo */ }) {
                Icon(
                    Icons.Default.AttachFile,
                    contentDescription = stringResource(R.string.chat_attach_file_desc),
                    tint = MediumGray
                )
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(stringResource(R.string.chat_message_placeholder)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = LightGray,
                    unfocusedContainerColor = LightGray
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSendClick,
                enabled = value.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = PrimaryGreen,
                    contentColor = White,
                    disabledContainerColor = MediumGray
                ),
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = stringResource(R.string.chat_send_button_desc)
                )
            }
        }
    }
}


@Composable
fun ChatMessageBubble(message: Message) {
    val alignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isSentByCurrentUser) PrimaryGreen else White
    val textColor = if (message.isSentByCurrentUser) White else Black
    val bubbleShape = if (message.isSentByCurrentUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isSentByCurrentUser) 64.dp else 0.dp,
                end = if (message.isSentByCurrentUser) 0.dp else 64.dp
            ),
        horizontalAlignment = alignment
    ) {
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
        )

        Surface(
            color = bubbleColor,
            shape = bubbleShape,
            shadowElevation = 2.dp
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        Text(
            text = message.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    JuntateTheme {
        ChatScreen(
            navController = rememberNavController(),
            eventId = "previewEventId",
            eventTitle = stringResource(R.string.chat_screen_title_placeholder)
        )
    }
}
