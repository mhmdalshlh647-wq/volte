package com.example.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.ProblemCardItem
import com.example.ui.components.SectionTitle
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun ProblemsScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val problems = viewModel.repository.problemsList

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            SectionTitle(title = "🛠️ مشاكل VoLTE الشائعة وحلولها", badgeCount = problems.size)
        }

        items(problems, key = { it.n }) { prob ->
            ProblemCardItem(
                problem = prob,
                onClick = { viewModel.openProblemDetail(prob.n) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
