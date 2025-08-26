package ru.max.oleg.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// тестовые дата классы
data class Chat(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: String
)

data class Folder(
    val id: String,
    val name: String
)

// тестовые данные
val sampleChats = listOf(
    Chat("0", "Костет", "Спокойной ночи", "04:30"),
    Chat("1", "Вован", "ТЫ ГДЕ НАС КЛАН КИТАЙЦЕВ РЕЙДИТ", "07:15"),
    Chat("2", "7Г", "В рот ебал я вашу алгебру делать", "Послезавтра"),
    Chat("3", "Кубодрочеры", "Может кто подогнать пол стака алмазов?", "Tomorrow"),
    Chat("4", "Мама", "ТЫ ГДЕ УЖЕ ВТОРЫЕ СУТКИ ПРОПАДАЕШЬ?! А НУ ЖИВО ДОМОЙ!!!", "12:45"),
    Chat("5", "Брат", "Было ваше, стало наше)", "11:00"),
    Chat("6", "Нищиёб", "5 тыщ верни! Куда ты пропал?", "2 года назад"),
    Chat("7", "Училка", "Я тебе щас в жопу твой макс запихаю!", "1 сентября")
)

val sampleFolders = listOf(
    Folder("0", "Колегки"),
    Folder("1", "Одноклассники"),
    Folder("2", "7я"),
    Folder("3", "Темки")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatList(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val allFolders = remember { listOf(Folder("-1", "Все")) + sampleFolders }
    val pagerState = rememberPagerState(pageCount = { allFolders.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { SideBar() }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Олег") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Открыть боковое меню"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 72.dp, end = 32.dp),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Создать"
                    )
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                FolderTabs(
                    folders = allFolders,
                    selectedTabIndex = selectedTabIndex,
                    onTabClick = { index ->
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    val chatsToShow = when (allFolders[pageIndex].name) {
                        "Все" -> sampleChats
                        "Колегки" -> sampleChats.take(2)
                        "Одноклассники" -> sampleChats.drop(2).take(2)
                        "7я" -> sampleChats.takeLast(2)
                        else -> emptyList()
                    }
                    FolderChatList(
                        chats = chatsToShow,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun FolderTabs(
    folders: List<Folder>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.background,
                height = 4.dp
            )
        }
    ) {
        folders.forEachIndexed { index, folder ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabClick(index) },
                text = {
                    Text(
                        text = folder.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
fun FolderChatList(
    chats: List<Chat>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    if (chats.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("В этой папке пока нет чатов")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(items = chats, key = { it.id }) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = {
                        navController.navigate("chat/${chat.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = chat.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SideBar() {
    ModalDrawerSheet(
        modifier = Modifier.width(250.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Боковая\nменюшка",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}