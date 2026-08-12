package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DetailHeader
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.searchHistory.collectAsState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = "🕘 سجل البحث",
                subtitle = "${historyList.size} عمليات بحث سابقة",
                onBack = { viewModel.navigateTo(NavTab.HOME) }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (historyList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🕘", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "لا يوجد سجل بحث حتى الآن",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FlowRow(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        historyList.forEach { h ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable {
                                    viewModel.onSearchQueryChanged(h.query)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = h.query,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "✕",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.clickable {
                                            viewModel.deleteHistoryQuery(h.query)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.clearHistory() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "🗑️ مسح سجل البحث بالكامل", color = RedPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
