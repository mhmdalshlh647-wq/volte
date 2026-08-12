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
import com.example.ui.components.CodeRowItem
import com.example.ui.components.DeviceCardItem
import com.example.ui.components.ProblemCardItem
import com.example.ui.components.SectionTitle
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun SearchScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val favorites by viewModel.favoriteDeviceIds.collectAsState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            SectionTitle(title = "نتائج البحث عن: \"$query\"", badgeCount = searchResults.totalCount)
        }

        if (searchResults.totalCount == 0) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🔍", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "لا توجد نتائج مطابقة لـ \"$query\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "جرّب كتابة اسم الشركة، موديل الجهاز، أو كود USSD",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            if (searchResults.devices.isNotEmpty()) {
                item {
                    SectionTitle(title = "📱 الأجهزة المطابقة", badgeCount = searchResults.devices.size)
                }
                items(searchResults.devices, key = { "dev_${it.id}" }) { dev ->
                    val isFav = favorites.contains(dev.id)
                    DeviceCardItem(
                        device = dev,
                        isFavorite = isFav,
                        onFavoriteToggle = { viewModel.toggleFavorite(dev.id) },
                        onClick = { viewModel.openDeviceDetail(dev.id) }
                    )
                }
            }

            if (searchResults.codes.isNotEmpty()) {
                item {
                    SectionTitle(title = "#️⃣ الأكواد المطابقة", badgeCount = searchResults.codes.size)
                }
                items(searchResults.codes, key = { "code_${it.code}_${it.manufacturer}" }) { code ->
                    CodeRowItem(codeItem = code)
                }
            }

            if (searchResults.problems.isNotEmpty()) {
                item {
                    SectionTitle(title = "🛠️ المشاكل والحلول المطابقة", badgeCount = searchResults.problems.size)
                }
                items(searchResults.problems, key = { "prob_${it.n}" }) { prob ->
                    ProblemCardItem(
                        problem = prob,
                        onClick = { viewModel.openProblemDetail(prob.n) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
