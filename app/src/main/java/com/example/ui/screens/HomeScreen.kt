package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SectionTitle
import com.example.ui.components.WarningBox
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun HomeScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val devices = viewModel.repository.devicesList
    val secretCodes = viewModel.repository.secretCodes
    val autoCount = devices.count { it.category == "auto1" || it.category == "auto2" }
    val appCount = devices.count { it.category == "app" }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            SectionTitle(title = "📊 نظرة عامة")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    number = devices.size.toString(),
                    label = "إجمالي الأجهزة",
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(NavTab.DEVICES) }
                        .testTag("stat_devices")
                )
                StatCard(
                    number = secretCodes.size.toString(),
                    label = "الأكواد المسجّلة",
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(NavTab.CODES) }
                        .testTag("stat_codes")
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    number = autoCount.toString(),
                    label = "دعم تلقائي",
                    color = StatusGreen,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(NavTab.DEVICES) }
                        .testTag("stat_auto")
                )
                StatCard(
                    number = appCount.toString(),
                    label = "تطبيق يمن موبايل",
                    color = StatusBlue,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(NavTab.DEVICES) }
                        .testTag("stat_app")
                )
            }
        }

        item {
            SectionTitle(title = "⚡ اختصارات سريعة")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    QuickShortcutRow(icon = "🌐", title = "إعدادات APN — ims / ymdata / xcap") {
                        viewModel.navigateTo(NavTab.APN)
                    }
                    QuickShortcutRow(icon = "📘", title = "دليل تفعيل VoLTE (بيكسل، صيني، آيفون)") {
                        viewModel.navigateTo(NavTab.GUIDES)
                    }
                    QuickShortcutRow(icon = "🛠️", title = "9 مشاكل شائعة وحلولها التفصيلية") {
                        viewModel.navigateTo(NavTab.PROBLEMS)
                    }
                    QuickShortcutRow(icon = "🔍", title = "فحص جهازي ومطابقته بالقاعدة") {
                        viewModel.navigateTo(NavTab.DEVICE_CHECK)
                    }
                }
            }
        }

        item {
            SectionTitle(title = "⚠️ تحذيرات هامة من المصدر")
            WarningBox(
                title = "تحذير 1 — طريقة GCF Mode (سامسونج)",
                text = "لا يُنصح بها على الإطلاق، كونها تؤدي إلى عدم استقرار الخدمة والخروج عن التغطية فجأة (خمول الجهاز ورده كـ مشغول)، اضطرار المبرمجين للعودة إلى CDMA. طريقة غير معتمدة."
            )
            WarningBox(
                title = "تحذير 2 — طريقة Shizuku / Pixel IMS (جوجل بيكسل)",
                text = "لا يُنصح بها، كونها لا تتوافق مع شرائح يمن موبايل الحديثة وتتسبب في عدم تعرف الجهاز على الشريحة وضياع التغطية."
            )
        }

        item {
            SectionTitle(title = "✅ شروط هامة قبل التفعيل")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    KeyPointItem(title = "طراز وموديل الجهاز", desc = "التأكد من أن جهاز الموبايل يدعم خدمة VoLTE")
                    KeyPointItem(title = "تحديث نظام التشغيل", desc = "قد يدعم الجهاز الخدمة ولكن يتطلب تحديث النظام (مثل Android 13/14)")
                    KeyPointItem(title = "تغطية الشبكة", desc = "التأكد من توفر تغطية VoLTE في المنطقة")
                    KeyPointItem(title = "نوع الشريحة", desc = "يجب أن تكون الشريحة حديثة تدعم الخدمة (عليها علامة VoLTE)")
                }
            }
        }

        item {
            SectionTitle(title = "📶 VoLTE مقابل GSM / CDMA")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ComparisonRow(header = true, c1 = "الميزة", c2 = "GSM", c3 = "CDMA", c4 = "VoLTE")
                    ComparisonRow(header = false, c1 = "جودة الصوت", c2 = "ضعيفة", c3 = "متوسطة", c4 = "عالية (HD)")
                    ComparisonRow(header = false, c1 = "زمن الاتصال", c2 = "5~8 ث", c3 = "4~7 ث", c4 = "فوري (<1ث)")
                    ComparisonRow(header = false, c1 = "إنترنت أثناء المكالمة", c2 = "لا", c3 = "لا", c4 = "نعم (4G)")
                    ComparisonRow(header = false, c1 = "كفاءة التردد", c2 = "منخفضة", c3 = "متوسطة", c4 = "عالية جداً")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickShortcutRow(
    icon: String,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = title,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(text = "›", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun KeyPointItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = "✓ $title", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ComparisonRow(
    header: Boolean,
    c1: String, c2: String, c3: String, c4: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = c1,
            fontSize = 12.sp,
            fontWeight = if (header) FontWeight.Bold else FontWeight.SemiBold,
            color = if (header) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = c2,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = c3,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = c4,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (header) MaterialTheme.colorScheme.onSurfaceVariant else RedPrimary,
            modifier = Modifier.weight(1.1f)
        )
    }
}
