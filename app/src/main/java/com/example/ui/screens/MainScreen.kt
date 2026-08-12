package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.VolteTechnicianTheme
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showMoreSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        VolteTechnicianTheme(themeMode = themeMode) {
            Scaffold(
                topBar = {
                    TopBarSection(
                        searchQuery = searchQuery,
                        themeMode = themeMode,
                        onSearchChange = { viewModel.onSearchQueryChanged(it) },
                        onClearSearch = { viewModel.clearSearch() },
                        onToggleTheme = { viewModel.toggleThemeMode() }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentTab = currentTab,
                        onSelectTab = { tab ->
                            if (tab == NavTab.HOME && showMoreSheet) {
                                showMoreSheet = false
                            }
                            viewModel.navigateTo(tab)
                        },
                        onOpenMore = { showMoreSheet = true }
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentTab) {
                        NavTab.HOME -> HomeScreen(viewModel = viewModel)
                        NavTab.DEVICES -> DevicesScreen(viewModel = viewModel)
                        NavTab.CODES -> CodesScreen(viewModel = viewModel)
                        NavTab.PROBLEMS -> ProblemsScreen(viewModel = viewModel)
                        NavTab.APN -> ApnScreen(viewModel = viewModel)
                        NavTab.GUIDES -> GuidesScreen(viewModel = viewModel)
                        NavTab.FAVORITES -> FavoritesScreen(viewModel = viewModel)
                        NavTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                        NavTab.DEVICE_CHECK -> DeviceCheckScreen(viewModel = viewModel)
                        NavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        NavTab.COMPANIES -> CompaniesScreen(viewModel = viewModel)
                        NavTab.SEARCH -> SearchScreen(viewModel = viewModel)
                        NavTab.DEVICE_DETAIL -> DeviceDetailScreen(viewModel = viewModel)
                        NavTab.PROBLEM_DETAIL -> ProblemDetailScreen(viewModel = viewModel)
                        NavTab.COMPANY_DETAIL -> DevicesScreen(viewModel = viewModel)
                    }
                }

                if (showMoreSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showMoreSheet = false },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .navigationBarsPadding()
                        ) {
                            Text(
                                text = "القائمة الكاملة • المزيد",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            MoreSheetItem(icon = "🏢", label = "الشركات المصنّعة") {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showMoreSheet = false }
                                viewModel.navigateTo(NavTab.COMPANIES)
                            }
                            MoreSheetItem(icon = "🌐", label = "إعدادات نقاط الوصول APN") {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showMoreSheet = false }
                                viewModel.navigateTo(NavTab.APN)
                            }
                            MoreSheetItem(icon = "📘", label = "دليل التفعيل التفصيلي") {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showMoreSheet = false }
                                viewModel.navigateTo(NavTab.GUIDES)
                            }
                            MoreSheetItem(icon = "⭐", label = "الأجهزة المفضّلة") {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showMoreSheet = false }
                                viewModel.navigateTo(NavTab.FAVORITES)
                            }
                            MoreSheetItem(icon = "<ctrl42>", label = "سجل البحث") {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showMoreSheet = false }
                                viewModel.navigateTo(NavTab.HISTORY)
                            }
                            MoreSheetItem(icon = "🔍", label = "فحص جهازي ومطابقته") {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showMoreSheet = false }
                                viewModel.navigateTo(NavTab.DEVICE_CHECK)
                            }
                            MoreSheetItem(icon = "⚙️", label = "الإعدادات والتخصيص", isLast = true) {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showMoreSheet = false }
                                viewModel.navigateTo(NavTab.SETTINGS)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarSection(
    searchQuery: String,
    themeMode: AppThemeMode,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(RedPrimary, RedDark)
                )
            )
            .padding(top = statusBarPadding + 8.dp, start = 14.dp, end = 14.dp, bottom = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📶 فني VoLTE",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "يمن موبايل • مرجع فني أوفلاين شامل",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.16f))
                        .testTag("theme_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (themeMode == AppThemeMode.DARK) Icons.Default.Nightlight else Icons.Default.WbSunny,
                        contentDescription = "الوضع",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = RedPrimary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = "ابحث عن الجهاز، الموديل، الكود، المشكلة...",
                                fontSize = 13.5.sp,
                                color = Color.Gray
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("top_search_field")
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentTab: NavTab,
    onSelectTab: (NavTab) -> Unit,
    onOpenMore: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentTab == NavTab.HOME,
            onClick = { onSelectTab(NavTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
            label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = RedPrimary.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentTab == NavTab.DEVICES || currentTab == NavTab.DEVICE_DETAIL || currentTab == NavTab.COMPANY_DETAIL,
            onClick = { onSelectTab(NavTab.DEVICES) },
            icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = "الأجهزة") },
            label = { Text("الأجهزة", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = RedPrimary.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentTab == NavTab.CODES,
            onClick = { onSelectTab(NavTab.CODES) },
            icon = { Icon(Icons.Default.Code, contentDescription = "الأكواد") },
            label = { Text("الأكواد", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = RedPrimary.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentTab == NavTab.PROBLEMS || currentTab == NavTab.PROBLEM_DETAIL,
            onClick = { onSelectTab(NavTab.PROBLEMS) },
            icon = { Icon(Icons.Default.Build, contentDescription = "المشاكل") },
            label = { Text("المشاكل", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = RedPrimary.copy(alpha = 0.12f)
            )
        )

        NavigationBarItem(
            selected = currentTab in listOf(NavTab.COMPANIES, NavTab.APN, NavTab.GUIDES, NavTab.FAVORITES, NavTab.HISTORY, NavTab.DEVICE_CHECK, NavTab.SETTINGS),
            onClick = onOpenMore,
            icon = { Icon(Icons.Default.Menu, contentDescription = "المزيد") },
            label = { Text("المزيد", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = RedPrimary.copy(alpha = 0.12f)
            )
        )
    }
}

@Composable
fun MoreSheetItem(
    icon: String,
    label: String,
    isLast: Boolean = false,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = icon, fontSize = 20.sp)
            Text(
                text = label,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(text = "›", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (!isLast) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        }
    }
}
