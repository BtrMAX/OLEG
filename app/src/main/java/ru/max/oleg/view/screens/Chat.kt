package ru.max.oleg.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class User(val id: String, val name: String)

enum class MessageStatus {
    SENDING,
    SENT,
    READ
}

data class Message(
    val text: String,
    val timestamp: String,
    val author: User,
    val status: MessageStatus? = null
)

val currentUser = User(id = "1", name = "Oleg")
val chatPartner = User(id = "2", name = "Max")

val testMessages = listOf(
    Message("Привет", "10:01", chatPartner),
    Message("Как дела?", "10:02", chatPartner),
    Message("Ку!", "10:02", currentUser, MessageStatus.READ),
    Message("Сплю", "10:02", currentUser, MessageStatus.READ),
    Message("Утром?", "10:02", chatPartner),
    Message("А отвечаешь ты мне как??", "10:02", chatPartner),
    Message("Боже, хватит придираться", "10:03", currentUser, MessageStatus.READ),
    Message("Хватит спать, пошли пиво пить", "10:04", chatPartner),
    Message("Иди нахуй, я ОЛЕГа всю ночь писал", "10:04", currentUser, MessageStatus.SENT),
    Message("И заблокал, красавчик", "10:04", currentUser, MessageStatus.SENDING)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(navController: NavController) {
    var inputState by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()

    LaunchedEffect(testMessages.size) {
        listState.animateScrollToItem(testMessages.size)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(user = chatPartner) {
                navController.popBackStack()
            }
        },
        bottomBar = {
            MessageInput(
                value = inputState,
                onValueChange = { inputState = it },
                onSendClick = {}
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(testMessages) { message ->
                if (message.author.id == currentUser.id) {
                    MyMessage(message = message)
                } else {
                    PartnerMessage(message = message)
                }
            }
        }
    }
}

@Composable
fun MyMessage(message: Message) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Column(modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)) {
                Text(
                    text = message.text,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    message.status?.let {
                        MessageStatusIndicator(status = it)
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerMessage(message: Message) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Column(modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)) {
                Text(
                    text = message.text,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = message.timestamp,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun MessageStatusIndicator(status: MessageStatus) {
    val icon = when (status) {
        MessageStatus.SENDING -> Icons.Outlined.Email
        MessageStatus.SENT -> Icons.Default.Done
        MessageStatus.READ -> Icons.Default.CheckCircle
    }

    Icon(
        imageVector = icon,
        contentDescription = "Статус сообщения",
        modifier = Modifier.size(16.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(user: User, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Аватар"
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.name,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "был(а) недавно",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun MessageInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Сообщение") },
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onSendClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Отправить",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}