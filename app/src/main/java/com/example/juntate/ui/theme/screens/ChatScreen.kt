package com.example.juntate.ui.theme.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import com.example.juntate.viewmodel.AuthViewModel
import com.example.juntate.viewmodel.ChatViewModel
import com.example.juntate.viewmodel.Message
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    eventId: String,
    eventTitle: String,
    sportType: String
) {
    var messageText by remember { mutableStateOf("") }
    val chatViewModel: ChatViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val messages by chatViewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val currentUser = remember { Firebase.auth.currentUser }
    val userProfile by authViewModel.userProfile.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(currentUser) {
        if (currentUser != null && userProfile == null) {
            authViewModel.fetchCurrentUserProfile()
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null && userProfile != null) {
                val mimeType = context.contentResolver.getType(uri)
                val messageType = when {
                    mimeType?.startsWith("image/") == true -> "IMAGE"
                    mimeType?.startsWith("video/") == true -> "VIDEO"
                    else -> "FILE"
                }

                chatViewModel.sendMediaMessage(
                    sportType = sportType,
                    eventId = eventId,
                    uri = uri,
                    senderName = userProfile?.name ?: "Usuario",
                    messageType = messageType,
                    onSuccess = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index = 0)
                        }
                    },
                    onError = { errorMsg ->
                        coroutineScope.launch {
                            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    )

    LaunchedEffect(eventId, sportType) {
        chatViewModel.listenForMessages(sportType, eventId)
    }

    DisposableEffect(eventId, sportType) {
        onDispose {
            chatViewModel.clearChatListener()
        }
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(index = 0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(eventTitle, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                    if (messageText.isNotBlank() && userProfile != null) {
                        chatViewModel.sendMessage(
                            sportType = sportType,
                            eventId = eventId,
                            messageText = messageText.trim(),
                            senderName = userProfile?.name ?: "Usuario"
                        )
                        messageText = ""
                        coroutineScope.launch {
                            listState.animateScrollToItem(index = 0)
                        }
                    }
                },
                onAttachClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
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
                    ChatMessageBubble(
                        message = message,
                        isSentByCurrentUser = message.senderUid == currentUser?.uid
                    )
                }
            }
        }
    }
}


@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit
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
            IconButton(onClick = onAttachClick) {
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
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.chat_send_button_desc)
                )
            }
        }
    }
}


@Composable
fun ChatMessageBubble(message: Message, isSentByCurrentUser: Boolean) {
    val alignment = if (isSentByCurrentUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isSentByCurrentUser) PrimaryGreen else White
    val textColor = if (isSentByCurrentUser) White else Black
    val bubbleShape = if (isSentByCurrentUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    val timestampFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val displayTime = (message.timestamp as? Timestamp)?.toDate()?.let {
        timestampFormatter.format(it)
    } ?: "..."

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isSentByCurrentUser) 64.dp else 0.dp,
                end = if (isSentByCurrentUser) 0.dp else 64.dp
            ),
        horizontalAlignment = alignment
    ) {
        if (!isSentByCurrentUser) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
            )
        }

        Surface(
            color = bubbleColor,
            shape = bubbleShape,
            shadowElevation = 2.dp
        ) {
            when (message.messageType) {
                "IMAGE" -> {
                    AsyncImage(
                        model = message.mediaUrl,
                        contentDescription = "Imagen adjunta",
                        modifier = Modifier
                            .padding(4.dp)
                            .sizeIn(maxHeight = 250.dp, maxWidth = 250.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                        error = painterResource(id = R.drawable.ic_profile_placeholder)
                    )
                }
                "VIDEO" -> {
                    VideoPlayer(
                        uri = Uri.parse(message.mediaUrl ?: ""),
                        modifier = Modifier
                            .padding(4.dp)
                            .sizeIn(maxHeight = 250.dp, maxWidth = 250.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                else -> {
                    Text(
                        text = message.messageText ?: "",
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Text(
            text = displayTime,
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    DisposableEffect(uri) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier.background(Color.Black), contentAlignment = Alignment.Center) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
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
            eventTitle = stringResource(R.string.chat_screen_title_placeholder),
            sportType = "Futbol"
        )
    }
}