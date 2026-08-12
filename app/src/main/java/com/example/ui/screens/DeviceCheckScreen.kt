package com.example.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DetailField
import com.example.ui.components.DetailHeader
import com.example.ui.components.DeviceCardItem
import com.example.ui.components.SectionTitle
import com.example.ui.components.WarningBox
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.VolteViewModel

@Composable
fun DeviceCheckScreen(
    viewModel: VolteViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val favorites by viewModel.favoriteDeviceIds.collectAsState()

    val currentManufacturer = Build.MANUFACTURER
    val currentModel = Build.MODEL
    val currentBrand = Build.BRAND
    val currentAndroidVer = Build.VERSION.RELEASE
    val currentSdk = Build.VERSION.SDK_INT

    val normMan = viewModel.repository.normalizeArabic(currentManufacturer)
    val normModel = viewModel.repository.normalizeArabic(currentModel)

    val matchedDevices = viewModel.repository.devicesList.filter { dev ->
        val devMan = viewModel.repository.normalizeArabic(dev.manufacturer)
        val devMod = viewModel.repository.normalizeArabic(dev.model)
        val devNums = viewModel.repository.normalizeArabic(dev.modelNumbers)
        devMan.contains(normMan) || devMod.contains(normModel) || devNums.contains(normModel)
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 14.dp)
    ) {
        item {
            DetailHeader(
                title = "🔍 أداة فحص الجهاز والمطابقة",
                subtitle = "قراءة مواصفات النظام الحالية",
                onBack = { viewModel.navigateTo(NavTab.HOME) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    DetailField(label = "الشركة المصنّعة الحالية", value = currentManufacturer)
                    DetailField(label = "طراز / موديل الهاتف", value = currentModel, isMono = true)
                    DetailField(label = "العلامة التجارية (Brand)", value = currentBrand)
                    DetailField(label = "إصدار نظام الأندرويد", value = "Android $currentAndroidVer (API $currentSdk)")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            WarningBox(
                title = "ملاحظة أمنية:",
                text = "يقوم التطبيق بقراءة معلومات نظام الأندرويد النظامية القياسية (Build Info) لمطابقة طراز هاتفك مع مرجع VoLTE التابع ليمن موبايل. لا يُنفّذ التطبيق أي أوامر Root أو تجاوز لحماية النظام."
            )

            Spacer(modifier = Modifier.height(12.dp))

            SectionTitle(title = "🔎 بحث مباشر بالطراز أو الموديل")

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("اكتب اسم هاتفك أو رقم الموديل هنا...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (matchedDevices.isNotEmpty()) {
                SectionTitle(title = "أجهزة مطابقة مصنعياً لنظام هاتفك", badgeCount = matchedDevices.size)
            }
        }

        items(matchedDevices, key = { it.id }) { dev ->
            val isFav = favorites.contains(dev.id)
            DeviceCardItem(
                device = dev,
                isFavorite = isFav,
                onFavoriteToggle = { viewModel.toggleFavorite(dev.id) },
                onClick = { viewModel.openDeviceDetail(dev.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
