package com.example.ui.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ApnLineRow
import com.example.ui.components.DetailHeader
import com.example.ui.components.SectionTitle
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun ApnScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val apnList = viewModel.repository.apnList

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = "🌐 إعدادات نقاط الوصول (APN)",
                subtitle = "إعدادات شبكة يمن موبايل",
                onBack = { viewModel.navigateTo(NavTab.HOME) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SectionTitle(title = "نقاط الوصول المعرفية")
        }

        items(apnList) { apn ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ApnLineRow(
                        name = apn.name,
                        apn = apn.apn,
                        type = apn.type,
                        isExtra = apn.source == "extra"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val fullApnText = "ims / ims / ims  —  ymdata / ymdata / default  —  xcap / xcap / xcap"
                    copyToClipboard(context, "APN Configs", fullApnText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "نسخ جميع إعدادات APN", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
            }

            Text(
                text = "⚠️ إعداد xcap غير موجود في ملف VoLTE_Support_R25.6.pdf الأصلي — أُضيف بناءً على توضيح الفهرسة بنفس نمط ims.",
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
            )

            SectionTitle(title = "📋 خطوات الإدخال اليدوي")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "1. الذهاب إلى إعدادات الهاتف >> الشبكات >> أسماء نقاط الوصول (APN).", fontSize = 13.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "2. إضافة نقطة وصول جديدة بالبيانات الموضحة أعلاه لكل من ims و ymdata و xcap.", fontSize = 13.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "3. حفظ الإعدادات وإعادة تشغيل الجهاز لتحسس الخدمة.", fontSize = 13.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
