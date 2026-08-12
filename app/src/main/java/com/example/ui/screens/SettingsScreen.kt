package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.ui.components.DetailField
import com.example.ui.components.DetailHeader
import com.example.ui.components.SectionTitle
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun SettingsScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val fontSizeMode by viewModel.fontSizeMode.collectAsState()
    val historyList by viewModel.searchHistory.collectAsState()
    val favorites by viewModel.favoriteDeviceIds.collectAsState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = "⚙️ الإعدادات والتخصيص",
                subtitle = "الخصوصية والمظهر والمعلومات",
                onBack = { viewModel.navigateTo(NavTab.HOME) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SectionTitle(title = "🎨 المظهر والخطوط")

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Theme Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "المظهر (Theme)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "الوضع الداكن / الفاتح", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            SegmentButton(
                                label = "داكن",
                                selected = themeMode == AppThemeMode.DARK,
                                onClick = { viewModel.setThemeMode(AppThemeMode.DARK) }
                            )
                            SegmentButton(
                                label = "فاتح",
                                selected = themeMode == AppThemeMode.LIGHT,
                                onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Font Size Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "حجم الخط", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "تعديل حجم النصوص للقراءة", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            SegmentButton(
                                label = "صغير",
                                selected = fontSizeMode == "sm",
                                onClick = { viewModel.setFontSizeMode("sm") }
                            )
                            SegmentButton(
                                label = "متوسط",
                                selected = fontSizeMode == "md",
                                onClick = { viewModel.setFontSizeMode("md") }
                            )
                            SegmentButton(
                                label = "كبير",
                                selected = fontSizeMode == "lg",
                                onClick = { viewModel.setFontSizeMode("lg") }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SectionTitle(title = "🧹 إدارة البيانات المحفوظة")

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "سجل البحث", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${historyList.size} عمليات بحث مسجلة", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { viewModel.clearHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "مسح", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "المفضّلة", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${favorites.size} أجهزة بالمفضلة", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { viewModel.clearFavorites() },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "إعادة ضبط", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SectionTitle(title = "ℹ️ معلومات التطبيق والحقوق")

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    DetailField(label = "اسم التطبيق", value = "فني VoLTE – يمن موبايل")
                    DetailField(label = "مطور التطبيق", value = "محمد مطهر الشلح")
                    DetailField(label = "الوظيفة والنطاق", value = "مرجع فني أوفلاين شامل لخدمة VoLTE، إعدادات APN، أكواد Secret Codes، والمشاكل والحلول الموثقة.")
                    DetailField(label = "الخصوصية المطلقة", value = "يعمل التطبيق بدون الحاجة للاتصال بالإنترنت، لا يجمع أو يرسل أية بيانات شخصية، وجميع تفضيلاتك تُحفظ محلياً على جهازك فقط.")
                    DetailField(label = "مصادر البيانات الأصلية", value = "1) VoLTE_Support_R25.6.pdf\n2) معظم_الاكواد_البرمجية_على_الهواتف.pdf")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SegmentButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) RedPrimary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
