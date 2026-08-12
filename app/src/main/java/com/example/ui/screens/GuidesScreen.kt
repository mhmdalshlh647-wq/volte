package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun GuidesScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val guideView by viewModel.guideView.collectAsState()

    val guide = if (guideView == "iphone") {
        viewModel.repository.guideIphone
    } else {
        viewModel.repository.guideYemenMobileApp
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = "📘 أدلة تفعيل خدمة VoLTE",
                subtitle = "التعليمات والخطوات التفصيلية",
                onBack = { viewModel.navigateTo(NavTab.HOME) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Guide Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = if (guideView == "app") RedPrimary else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (guideView == "app") RedPrimary else MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .clickable { viewModel.setGuideView("app") }
                        .weight(1f)
                ) {
                    Text(
                        text = "📱 عبر تطبيق يمن موبايل",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (guideView == "app") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }

                Surface(
                    color = if (guideView == "iphone") RedPrimary else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (guideView == "iphone") RedPrimary else MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .clickable { viewModel.setGuideView("iphone") }
                        .weight(1f)
                ) {
                    Text(
                        text = "🍏 أجهزة آيفون iPhone",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (guideView == "iphone") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = guide.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = guide.intro,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )
        }

        itemsIndexed(guide.steps) { idx, step ->
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RedPrimary.copy(alpha = 0.15f))
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
            Spacer(modifier = Modifier.height(14.dp))
            SectionTitle(title = "🟢 حالات التفعيل التلقائي")

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "• تظهر الخدمة تلقائياً بمجرد إدخال الشريحة في الموديلات الحديثة (مثل Android 15).", fontSize = 13.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• تظهر الخدمة تلقائياً بعد إضافة نقاط الوصول الموضحة في جدول APN.", fontSize = 13.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• تظهر الخدمة تلقائياً بعد تحديث نظام التشغيل إلى Android 14 وإضافة APN في أجهزة سامسونج الأمريكية.", fontSize = 13.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            WarningBox(
                title = "تنبيه هام:",
                text = "يرجى تجنب طرق التفعيل غير المعتمدة مثل GCF Mode على سامسونج أو Shizuku/Pixel IMS على أجهزة بيكسل للوقاية من المشاكل البرمجية."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
