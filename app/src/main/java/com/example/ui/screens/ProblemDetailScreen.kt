package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DetailHeader
import com.example.ui.components.SectionTitle
import com.example.ui.components.WarningBox
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun ProblemDetailScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val selectedN by viewModel.selectedProblemN.collectAsState()
    val problem = viewModel.repository.problemsList.find { it.n == selectedN }

    if (problem == null) {
        Column(modifier = modifier.padding(20.dp)) {
            Text(text = "المشكلة غير موجودة")
        }
        return
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = problem.title,
                subtitle = "مشكلة رقم ${problem.n} • ${problem.manufacturer}",
                onBack = { viewModel.navigateTo(NavTab.PROBLEMS) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SectionTitle(title = "📄 وصف المشكلة")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = problem.desc,
                    fontSize = 13.5.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            SectionTitle(title = "✅ خطوات الحل الموصى بها")
        }

        itemsIndexed(problem.solution) { idx, step ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = (idx + 1).toString(),
                            fontWeight = FontWeight.ExtraBold,
                            color = RedPrimary,
                            fontSize = 12.5.sp
                        )
                    }

                    Text(
                        text = step,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            if (problem.warning.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                WarningBox(title = "تحذير:", text = problem.warning)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "الشركة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = problem.manufacturer, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "الموديل", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = problem.model, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
