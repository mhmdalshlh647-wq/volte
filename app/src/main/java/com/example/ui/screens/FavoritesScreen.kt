package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DetailHeader
import com.example.ui.components.DeviceCardItem
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun FavoritesScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favoriteDeviceIds.collectAsState()
    val allDevices = viewModel.repository.devicesList
    val favoriteDevices = allDevices.filter { favorites.contains(it.id) }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = "⭐ الأجهزة المفضّلة",
                subtitle = "${favoriteDevices.size} جهاز في المفضلة",
                onBack = { viewModel.navigateTo(NavTab.HOME) }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (favoriteDevices.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "⭐", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "لم تُضف أي أجهزة للمفضلة بعد",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اضغط على نجمة المفضلة بجانب أي جهاز للوصول السريع إليه هنا",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(favoriteDevices, key = { it.id }) { dev ->
                DeviceCardItem(
                    device = dev,
                    isFavorite = true,
                    onFavoriteToggle = { viewModel.toggleFavorite(dev.id) },
                    onClick = { viewModel.openDeviceDetail(dev.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
