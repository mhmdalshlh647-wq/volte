package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CodeRowItem
import com.example.ui.components.SectionTitle
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun CodesScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val activeFilter by viewModel.codeFilter.collectAsState()
    val allCodes = viewModel.repository.secretCodes

    val groups = listOf("all", "Samsung", "Redmi", "MTK", "Unisoc", "Vivo", "OnePlus", "Coolpad", "أجهزة صينية")

    val filteredList = if (activeFilter == "all") {
        allCodes
    } else {
        allCodes.filter { code ->
            val normFilter = viewModel.repository.normalizeArabic(activeFilter)
            val normMan = viewModel.repository.normalizeArabic("${code.manufacturer} ${code.chipset}")
            normMan.contains(normFilter)
        }
    }

    val groupedByType = filteredList.groupBy { it.codeType }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            SectionTitle(title = "#️⃣ أكواد الهواتف والسرّية", badgeCount = allCodes.size)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(groups) { group ->
                    val label = if (group == "all") "الكل" else group
                    FilterChipItem(
                        label = label,
                        selected = activeFilter == group,
                        onClick = { viewModel.setCodeFilter(group) }
                    )
                }
            }
        }

        if (groupedByType.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 38.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "لا توجد أكواد مطابقة للمصنع المحدد",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            groupedByType.forEach { (type, codes) ->
                item {
                    SectionTitle(title = type, badgeCount = codes.size)
                }
                items(codes) { codeItem ->
                    CodeRowItem(codeItem = codeItem)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
