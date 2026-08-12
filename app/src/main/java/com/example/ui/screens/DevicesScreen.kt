package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DeviceCardItem
import com.example.ui.components.SectionTitle
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun DevicesScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val categoryFilter by viewModel.deviceCategoryFilter.collectAsState()
    val companyFilter by viewModel.deviceCompanyFilter.collectAsState()
    val favorites by viewModel.favoriteDeviceIds.collectAsState()

    val allDevices = viewModel.repository.devicesList
    val allCompanies = listOf("all") + allDevices.map { it.manufacturer }.distinct().sorted()

    val filteredList = allDevices.filter { dev ->
        val catMatches = categoryFilter == "all" || dev.category == categoryFilter
        val compMatches = companyFilter == "all" || dev.manufacturer == companyFilter
        catMatches && compMatches
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            SectionTitle(title = "📱 قاعدة بيانات الأجهزة", badgeCount = filteredList.size)

            // Filter Chips 1: Category
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item { FilterChipItem("الكل", categoryFilter == "all") { viewModel.setDeviceCategoryFilter("all") } }
                item { FilterChipItem("✅ دعم تلقائي كامل", categoryFilter == "auto1") { viewModel.setDeviceCategoryFilter("auto1") } }
                item { FilterChipItem("🟡 دعم حسب الشريحة", categoryFilter == "auto2") { viewModel.setDeviceCategoryFilter("auto2") } }
                item { FilterChipItem("🔧 عبر التطبيق", categoryFilter == "app") { viewModel.setDeviceCategoryFilter("app") } }
                item { FilterChipItem("🧰 برمجة عبر أدوات", categoryFilter == "tools") { viewModel.setDeviceCategoryFilter("tools") } }
            }

            // Filter Chips 2: Companies
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(allCompanies) { comp ->
                    val label = if (comp == "all") "كل الشركات" else comp
                    FilterChipItem(label, companyFilter == comp) { viewModel.setDeviceCompanyFilter(comp) }
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 38.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "لا توجد أجهزة مطابقة للفلاتر المختارة",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredList, key = { it.id }) { dev ->
                val isFav = favorites.contains(dev.id)
                DeviceCardItem(
                    device = dev,
                    isFavorite = isFav,
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

@Composable
fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) RedPrimary else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) RedPrimary else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("filter_chip_$label")
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
        )
    }
}
