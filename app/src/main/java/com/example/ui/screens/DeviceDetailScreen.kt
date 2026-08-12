package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ApnLineRow
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CodeRowItem
import com.example.ui.components.DetailField
import com.example.ui.components.DetailHeader
import com.example.ui.components.ProblemCardItem
import com.example.ui.components.SectionTitle
import com.example.ui.components.WarningBox
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun DeviceDetailScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedId by viewModel.selectedDeviceId.collectAsState()
    val favorites by viewModel.favoriteDeviceIds.collectAsState()

    val device = viewModel.repository.devicesList.find { it.id == selectedId }

    if (device == null) {
        Column(modifier = modifier.padding(20.dp)) {
            Text(text = "الجهاز غير موجود")
        }
        return
    }

    val isFav = favorites.contains(device.id)
    val normMan = viewModel.repository.normalizeArabic(device.manufacturer)

    val relatedCodes = viewModel.repository.secretCodes.filter {
        val cMan = viewModel.repository.normalizeArabic(it.manufacturer)
        cMan.contains(normMan) || normMan.contains(cMan)
    }

    val relatedRule = viewModel.repository.activationRules.find {
        val rComp = viewModel.repository.normalizeArabic(it.company)
        rComp.contains(normMan) || normMan.contains(rComp)
    }

    val relatedProblems = viewModel.repository.problemsList.filter {
        val pMan = viewModel.repository.normalizeArabic(it.manufacturer)
        pMan.contains(normMan) || normMan.contains(pMan)
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = "${device.manufacturer} ${device.model}",
                subtitle = "تفاصيل الجهاز • ${device.manufacturer}",
                onBack = { viewModel.navigateTo(com.example.ui.viewmodel.NavTab.DEVICES) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(category = device.category)

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isFav) "★ بالمفضلة" else "☆ إضافة للمفضلة",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { viewModel.toggleFavorite(device.id) }
                    )
                }
            }

            if (device.sourceTag == "extra") {
                Spacer(modifier = Modifier.height(8.dp))
                WarningBox(
                    title = "⚠️ خارج المصدر الأصلي:",
                    text = "هذا الجهاز غير مذكور في ملفي VoLTE_Support_R25.6.pdf أو ملف الأكواد الأصليين، وأُضيف بناءً على الفهرسة المكملة. لا توجد طريقة تفعيل رسمية موثّقة من يمن موبايل لهذا الطراز بالتحديد، وقد يحتاج إلى أداة برمجة فنية متخصصة."
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    DetailField(label = "الشركة المصنّعة", value = device.manufacturer)
                    DetailField(label = "أرقام الموديل", value = device.modelNumbers, isMono = true)
                    DetailField(label = "نوع المعالج (موثّق من المصدر)", value = device.chipset)
                    DetailField(
                        label = "نوع المعالج (تقديري 🔎)",
                        value = "${device.chipsetGuess}\n(تقدير عام حسب طراز الجهاز — غير موثّق من ملفي PDF)"
                    )

                    if (device.category == "tools") {
                        DetailField(
                            label = "طريقة التفعيل",
                            value = "لا توجد طريقة رسمية موثّقة في المصدر — يحتاج احتمالاً لبرمجة عبر أدوات صيانة فنية متخصصة."
                        )
                    } else if (relatedRule != null) {
                        DetailField(label = "طريقة التفعيل (حسب الجدول الرسمي)", value = relatedRule.method)
                        if (relatedRule.note.isNotBlank() && relatedRule.note != "غير متوفر في المصدر") {
                            DetailField(label = "ملاحظة التفعيل", value = relatedRule.note)
                        }
                    } else {
                        DetailField(
                            label = "طريقة التفعيل",
                            value = if (device.category == "app") "التفعيل اليدوي عبر تطبيق يمن موبايل." else "دعم تلقائي بمجرد إدخال الشريحة أو إضافة نقاط الوصول."
                        )
                    }
                }
            }
        }

        item {
            SectionTitle(title = "🌐 نقاط الوصول (APN)")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    viewModel.repository.apnList.forEach { apn ->
                        ApnLineRow(name = apn.name, apn = apn.apn, type = apn.type, isExtra = apn.source == "extra")
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            val apnText = "ims / ims / ims  —  ymdata / ymdata / default  —  xcap / xcap / xcap"
                            copyToClipboard(context, "APN Settings", apnText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "نسخ إعدادات APN الثلاثة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (relatedCodes.isNotEmpty()) {
            item {
                SectionTitle(title = "#️⃣ الأكواد المرتبطة", badgeCount = relatedCodes.size)
            }
            items(relatedCodes) { code ->
                CodeRowItem(codeItem = code)
            }
        }

        if (relatedProblems.isNotEmpty()) {
            item {
                SectionTitle(title = "🛠️ مشاكل محتملة لهذا الجهاز")
            }
            items(relatedProblems) { prob ->
                ProblemCardItem(problem = prob, onClick = { viewModel.openProblemDetail(prob.n) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
