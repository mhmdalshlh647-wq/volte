package com.example.data.repository

import com.example.data.db.FavoriteDeviceEntity
import com.example.data.db.SearchHistoryEntity
import com.example.data.db.VolteDao
import com.example.data.model.ActivationRule
import com.example.data.model.ApnItem
import com.example.data.model.DeviceItem
import com.example.data.model.GuideContent
import com.example.data.model.ProblemItem
import com.example.data.model.SecretCodeItem
import kotlinx.coroutines.flow.Flow

class VolteRepository(private val dao: VolteDao) {

    val favoriteDeviceIds: Flow<List<String>> = dao.getFavoriteDeviceIds()
    val searchHistory: Flow<List<SearchHistoryEntity>> = dao.getSearchHistory()

    suspend fun toggleFavorite(deviceId: String, isCurrentlyFav: Boolean) {
        if (isCurrentlyFav) {
            dao.removeFavorite(deviceId)
        } else {
            dao.addFavorite(FavoriteDeviceEntity(deviceId = deviceId))
        }
    }

    suspend fun addSearchHistory(query: String) {
        val trimmed = query.trim()
        if (trimmed.length > 1) {
            dao.addSearchHistory(SearchHistoryEntity(query = trimmed, timestamp = System.currentTimeMillis()))
        }
    }

    suspend fun deleteSearchQuery(query: String) {
        dao.deleteSearchQuery(query)
    }

    suspend fun clearHistory() {
        dao.clearSearchHistory()
    }

    suspend fun clearFavorites() {
        dao.clearFavorites()
    }

    // Helper to normalize Arabic strings for smart searching
    fun normalizeArabic(text: String): String {
        return text.lowercase()
            .replace(Regex("[أإآا]"), "ا")
            .replace("ى", "ي")
            .replace("ة", "ه")
            .replace(Regex("[\\u064B-\\u065F]"), "") // Remove Arabic diacritics / Tashkeel
            .replace(Regex("[#\\*\\(\\)\\-\\.\\/]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    // --- DATASETS ---

    val devicesList: List<DeviceItem> by lazy {
        val list = mutableListOf<DeviceItem>()
        var idCounter = 1

        fun addDevs(company: String, items: List<Pair<String, String>>, category: String, sourceTag: String = "pdf") {
            for ((model, nums) in items) {
                list.add(
                    DeviceItem(
                        id = "d${idCounter++}",
                        manufacturer = company,
                        model = model,
                        modelNumbers = if (nums.isBlank()) "غير متوفر في المصدر" else nums,
                        chipset = "غير متوفر في المصدر",
                        chipsetGuess = guessChipset(company, model),
                        category = category,
                        sourceTag = sourceTag
                    )
                )
            }
        }

        // --- Category Auto 1 (A-1): Full Automatic Support ---
        addDevs("Samsung", listOf(
            "Galaxy S25 Ultra" to "SM-S928B, SM-S928U", "Galaxy S25+" to "SM-S926B, SM-S926U", "Galaxy S25" to "SM-S921B, SM-S921U",
            "Galaxy S24 Ultra" to "SM-S928B, SM-S928U", "Galaxy S24+" to "SM-S926B, SM-S926U", "Galaxy S24" to "SM-S921B, SM-S921U",
            "Galaxy S23 Ultra" to "SM-S918B, SM-S918U", "Galaxy S23+" to "SM-S916B, SM-S916U", "Galaxy S23" to "SM-S911B, SM-S911U",
            "Galaxy S22 Ultra" to "SM-S908B, SM-S908U", "Galaxy S22+" to "SM-S906B, SM-S906U", "Galaxy S22" to "SM-S901B, SM-S901U",
            "Galaxy S21 Ultra" to "SM-S998B, SM-S998U", "Galaxy S21+" to "SM-S996B, SM-S996U", "Galaxy S21" to "SM-S991B, SM-S991U",
            "Galaxy Note20 Ultra" to "SM-N986B", "Galaxy Note20" to "SM-N981B", "Galaxy S20+" to "SM-G986B",
            "Galaxy S20" to "SM-G980F/DS, SM-G981B", "Galaxy S20 FE" to "SM-G781", "Galaxy Note10+" to "SM-N975F/DS",
            "Galaxy Note10+ 5G" to "SM-N976F", "Galaxy Note10" to "SM-N970F/DS", "Galaxy S10+" to "SM-G975F/DS",
            "Galaxy S10" to "SM-G973F/DS", "Galaxy S10E" to "SM-G970",
            "Galaxy Z Fold 5" to "SM-F946B, SM-F946U", "Galaxy Z Flip 5" to "SM-F731B, SM-F731U",
            "Galaxy Z Fold 4" to "SM-F936B, SM-F936U", "Galaxy Z Flip 4" to "SM-F721B, SM-F721U",
            "Galaxy Z Fold 3" to "SM-F926B, SM-F926U", "Galaxy Z Flip 3" to "SM-F711B, SM-F711U",
            "Galaxy F14 5G" to "SM-E146B", "Galaxy F23 5G" to "SM-E236B", "Galaxy F41" to "SM-F415F",
            "Galaxy M14 5G" to "SM-M146B", "Galaxy M23 5G" to "SM-M236B", "Galaxy M32" to "SM-M325F",
            "Galaxy M33 5G" to "SM-M336B", "Galaxy M53 5G" to "SM-M536B", "Galaxy M04" to "SM-M045F",
            "Galaxy Tab S9 Ultra" to "SM-X910, SM-X916B", "Galaxy Tab S9 FE+" to "SM-X610, SM-X616B",
            "Galaxy Tab S9+" to "SM-X716B", "Galaxy Tab S9 FE" to "SM-X510, SM-X516B", "Galaxy Tab S9" to "SM-X710",
            "Galaxy Tab S8 Ultra" to "SM-X900, SM-X906B", "Galaxy Tab S8+" to "SM-X800, SM-X806B", "Galaxy Tab S8" to "SM-X700, SM-X706B",
            "Galaxy Tab A8" to "SM-X200, SM-X205", "Galaxy Tab S7 FE" to "SM-T733, SM-T736B", "Galaxy Tab A7 Lite" to "SM-T220, SM-T225",
            "Galaxy A15" to "SM-A155F, SM-A156B", "Galaxy A16" to "SM-A165F, SM-A166B", "Galaxy A25 5G" to "SM-A256B",
            "Galaxy A26 5G" to "SM-A266B", "Galaxy A36 5G" to "SM-A366B", "Galaxy A56 5G" to "SM-A566B",
            "Galaxy A04s" to "SM-A047F", "Galaxy A05s" to "SM-A057F", "Galaxy A13" to "SM-A135F",
            "Galaxy A06" to "SM-A065F, SM-A066B", "Galaxy A14 5G" to "SM-A146B, SM-A146U", "Galaxy A22 5G" to "SM-A226B",
            "Galaxy A23" to "SM-A235F, SM-A236B", "Galaxy A32 5G" to "SM-A326B", "Galaxy A33 5G" to "SM-A336B",
            "Galaxy A34 5G" to "SM-A346B", "Galaxy A52" to "SM-A525F, SM-A526B, SM-A528B", "Galaxy A53 5G" to "SM-A536B",
            "Galaxy A54 5G" to "SM-A546B", "Galaxy A71 5G" to "SM-A716B", "Galaxy A72" to "SM-A725F", "Galaxy A73 5G" to "SM-A736B"
        ), "auto1")

        addDevs("iPhone", listOf(
            "iPhone 16 Pro Max" to "", "iPhone 16 Pro" to "", "iPhone 16" to "",
            "iPhone 15 Pro Max" to "", "iPhone 15 Pro" to "", "iPhone 15" to "",
            "iPhone 14 Pro Max" to "", "iPhone 14 Pro" to "", "iPhone 14" to "",
            "iPhone 13 Pro Max" to "", "iPhone 13 Pro" to "", "iPhone 13" to "",
            "iPhone 12 Pro Max" to "", "iPhone 12 Pro" to "", "iPhone 12" to "",
            "iPhone 11 Pro Max" to "", "iPhone 11 Pro" to "", "iPhone 11" to ""
        ), "auto1")

        addDevs("OPPO", listOf(
            "Oppo A5x" to "", "Oppo A96" to "", "Oppo A76" to "", "Oppo A16" to "", "Oppo A16K" to "",
            "Reno 7" to "", "Reno 7 pro" to "", "Reno 6z" to "", "Reno 8z" to "", "Reno 5F" to ""
        ), "auto1")

        addDevs("Realme", listOf(
            "Realme 14T" to "RMX5078", "Realme GT7" to "RMX5061", "Realme GT 7T" to "RMX5085"
        ), "auto1")

        addDevs("Nokia", listOf(
            "Nokia 5710 XpressAudio" to "", "Nokia 6300 4G" to "", "Nokia 8210 4G" to ""
        ), "auto1")

        // --- Category Auto 2 (A-2): Auto Support (SIM Dependent) ---
        addDevs("VIVO", listOf(
            "Vivo X200" to "", "Vivo X200 Pro" to "", "Vivo X100" to "", "Vivo X100 Pro" to "",
            "Vivo V50 5G" to "", "Vivo V50 Lite" to "", "Vivo V40 5G" to "", "Vivo V40 Lite 5G" to "",
            "Vivo V40 Lite" to "", "Vivo V30 Lite" to "", "Vivo V29 Lite" to "", "Vivo Y36" to "",
            "Vivo Y35" to "", "Vivo Y29" to "", "Vivo Y28" to "", "Vivo Y27" to "", "Vivo Y21T" to "",
            "Vivo Y19s" to "", "Vivo Y18" to "", "Vivo Y16" to "", "Vivo Y17s" to "", "Vivo Y11" to "", "Vivo Y03" to ""
        ), "auto2")

        addDevs("Xiaomi", listOf(
            "Xiaomi 13 Ultra" to "", "Xiaomi 13 Pro" to "", "Xiaomi 13" to "", "Xiaomi 12 Pro" to "",
            "Xiaomi 12" to "", "Xiaomi 12T Pro" to "", "Xiaomi 12T" to "", "Xiaomi 15 Ultra" to "",
            "Xiaomi 15 Pro" to "", "Xiaomi 15" to "", "Xiaomi 14 Ultra" to "", "Xiaomi 14 Pro" to "", "Xiaomi 14" to ""
        ), "auto2")

        addDevs("POCO", listOf(
            "POCO F6 Pro" to "", "POCO F6" to "", "POCO X6 Pro" to "", "POCO X6" to "", "POCO M6 Pro" to "",
            "POCO M6" to "", "POCO C75" to "", "POCO C65" to "", "POCO X7 Pro" to "", "POCO X7" to "",
            "POCO F7 Ultra" to "", "POCO F7 Pro" to "", "POCO F7" to "", "POCO Pad" to "", "POCO F5 Pro" to "",
            "POCO F5" to "", "POCO F4 GT" to "", "POCO X5 Pro 5G" to "", "POCO X4 GT" to ""
        ), "auto2")

        addDevs("TECNO", listOf(
            "Phantom V2 Fold" to "LT10", "Phantom V2 Flip" to "LT11", "Camon 30" to "CK7", "Camon 30 5G" to "CK8",
            "Camon 30 Pro" to "CK9", "Camon 30 Premier 5G" to "CK10", "Pova 6" to "LI8", "Pova 6 Pro" to "LI10",
            "Pova 6 Neo" to "LI9", "Spark 20" to "KH7", "Spark 20 Pro" to "KH9", "Spark 20 Pro+" to "KH10",
            "Camon 40" to "CM7", "Camon 40 Pro" to "CM9", "Camon 40 Premier" to "CM10"
        ), "auto2")

        addDevs("INFINIX", listOf(
            "GT 20" to "X6870", "GT 20 Pro" to "X6871", "GT 20e" to "X6872", "Note 40" to "X6850",
            "Note 40 Pro" to "X6851", "Note 40 Pro+" to "X6852", "Hot 40" to "X6520", "Hot 40i" to "X6521",
            "Hot 40 Pro" to "X6522", "Zero Ultra 2" to "X6950", "Zero Ultra 2 Lite" to "X6951",
            "Note 50" to "X7050", "Note 50 Pro" to "X7051"
        ), "auto2")

        addDevs("itel", listOf(
            "P55" to "P6611", "P55+ 5G" to "P6651", "P55 Pro" to "P6652", "S24" to "S6611",
            "S24+" to "S6612", "S24 Pro" to "S6652", "A70" to "A6611", "A70 Pro" to "A6651",
            "P60" to "P6711", "P60 5G" to "P6751"
        ), "auto2")

        addDevs("Redmi", listOf(
            "Redmi Note 14 Pro+ 5G" to "", "Redmi Note 14 Pro 5G" to "", "Redmi Note 14 5G" to "",
            "Redmi Note 14 Pro" to "", "Redmi Note 14" to "", "Redmi A3 Pro" to "", "Redmi 14C" to "",
            "Redmi Note 13 Pro+ 5G" to "", "Redmi Note 13 Pro" to "", "Redmi Note 13" to "",
            "Redmi Note 12 Pro+ 5G" to "", "Redmi Note 12 Pro" to "", "Redmi Note 12" to "",
            "Redmi K60 Ultra" to "", "Redmi K60 Pro" to "", "Redmi K60" to "", "Redmi 12 5G" to "",
            "Redmi 12" to "", "Redmi 12C" to "", "Redmi K40 Pro" to "", "Redmi K40 Pro Plus" to "",
            "Redmi K40 Gaming" to "", "Redmi K50" to "", "Redmi K50 Pro" to "", "Redmi K50 Pro Plus" to "",
            "Redmi K50 Ultra" to "", "Redmi K50 Gaming" to "", "Redmi K70" to "", "Redmi K70 Pro" to "",
            "Redmi K70 Ultra" to "", "Redmi K70 Gaming" to "", "Redmi K70E" to "", "Redmi K80" to "",
            "Redmi K80 Pro" to "", "Redmi Turbo 3" to "", "Redmi Turbo 3 Pro" to "", "Redmi Turbo 4" to "",
            "Redmi Turbo 4 Pro" to "", "Redmi 10C" to "", "Redmi Note 11" to "", "Redmi Note 11 Pro" to "",
            "Redmi Note 11 Pro 5G" to "", "Redmi Note 11 Pro Plus" to "", "Redmi K20" to "", "Redmi K20 Pro" to "",
            "Redmi K30" to "", "Redmi K30 Ultra" to "", "Redmi K30 Pro" to "", "Redmi K30S" to "", "Redmi K40" to ""
        ), "auto2")

        addDevs("LT", listOf(
            "LT-M10" to "", "LT-M10S" to "", "LT-M10 PLUS" to "", "LT-M15" to "", "LT-M15 PLUS" to "",
            "LT-M20" to "", "LT-M20S" to "", "LT-M30" to "", "LT-M30S" to "", "LT-M30 PLUS" to "",
            "LT-M40" to "", "LT-M50" to "", "LT-M50S" to "", "LT-M60" to "", "LT-M70" to "",
            "LT-M20 PLUS" to "", "LT-Flip" to ""
        ), "auto2")

        addDevs("Cool Pad", listOf(
            "Cool X100 8+8/512" to "", "Cool 30play 8+8/256" to "", "Cool 30ECO 6+10/256" to "",
            "Cool 40i 4+4/128" to "", "Cool 40i 4+4/64" to ""
        ), "auto2")

        addDevs("UMIDIGI", listOf(
            "Umidigi G9C" to "", "Umidigi Not100A" to "", "Umidigi G100A" to "", "Umidigi A15T" to "",
            "Umidigi bison X10" to "", "Umidigi bison Pro" to ""
        ), "auto2")

        addDevs("BlackView", listOf(
            "Wave 8" to "", "Wave8C" to "", "Color 8" to "", "Shark 9" to ""
        ), "auto2")

        addDevs("Oukitel", listOf(
            "Oukitel C51" to "", "Oukitel C53" to "", "Oukitel P1" to ""
        ), "auto2")

        addDevs("DooGee", listOf(
            "N50" to "", "N55" to ""
        ), "auto2")

        addDevs("Trecfone", listOf(
            "Trecfone Stylo 7" to "", "Trecfone 17 Pro Max" to "", "Trecfone 16 Pro Max" to "",
            "Trecfone 15 Pro Max" to "", "Trecfone T20" to ""
        ), "auto2")

        addDevs("ZTE", listOf(
            "ZTE Nubia RedMagic 8 Pro" to "", "ZTE Nubia RedMagic 9 Pro" to "", "ZTE Nubia RedMagic 9 Pro Plus" to "",
            "ZTE Nubia RedMagic 10 Pro" to "", "ZTE Nubia RedMagic 10 Pro Plus" to "", "ZTE Nubia Z50s Pro" to "",
            "ZTE Nubia Z50 Ultra" to "", "ZTE Nubia Z60s Pro" to "", "ZTE Nubia Z60 Ultra" to "",
            "ZTE Nubia Z70s Pro" to "", "ZTE Nubia Z70 Ultra" to ""
        ), "auto2")

        addDevs("Huawei / Honor", listOf(
            "Honor x60" to "", "Honor x60 Pro" to "", "Honor 90" to "", "Honor 200 Pro" to "",
            "Honor Magic 6 Pro" to "", "Honor Magic 7 Pro" to "", "Huawei Nova10" to ""
        ), "auto2")

        // --- Category App (B): Requires Yemen Mobile App Activation ---
        addDevs("OPPO", listOf(
            "Oppo Find N2 Flip" to "", "Oppo Find N3" to "", "Oppo Find N3 Flip" to "", "Oppo Find N5" to "",
            "Oppo Reno 13 5G" to "", "Oppo Find X7 Ultra" to "", "Oppo Find X8 Pro" to "", "Oppo Find X8 Ultra" to "", "Oppo Find N2" to ""
        ), "app")

        addDevs("OnePlus", listOf(
            "OnePlus Ace 2" to "", "OnePlus Ace 2 Pro" to "", "OnePlus Ace 3" to "", "OnePlus Ace 3 Pro" to "",
            "OnePlus Ace 5" to "", "OnePlus Ace 5 Pro" to "", "OnePlus Nord 3" to "", "OnePlus Nord CE3" to "",
            "OnePlus Nord 4" to "", "OnePlus Nord CE4" to "", "Oneplus 9" to "", "Oneplus 9 Pro" to "",
            "Oneplus 9RT" to "", "Oneplus 10" to "", "Oneplus 10 Pro" to "", "Oneplus 10T" to "",
            "Oneplus 11" to "", "Oneplus 11T" to "", "Oneplus 12" to "", "Oneplus 12R" to "",
            "Oneplus 13" to "", "Oneplus 13T" to "", "Oneplus 13R" to ""
        ), "app")

        addDevs("Realme", listOf(
            "Realme 13 Pro Plus" to "", "Realme 14" to "", "Realme 14 Pro Plus" to "", "Realme Neo 7" to "",
            "Realme GT7 Pro" to "", "Realme 11" to "", "Realme 11 Pro" to "", "Realme 11 Pro Plus" to "",
            "Realme 12" to "", "Realme 12 Pro Plus" to "", "Realme 13" to ""
        ), "app")

        addDevs("Xiaomi", listOf(
            "Xiaomi 11" to "", "Xiaomi 11 Pro" to "", "Xiaomi 11 Ultra" to "", "Xiaomi 11 Lite" to "",
            "Xiaomi Black Shark 4" to "", "Xiaomi Black Shark 4 Pro" to "", "Xiaomi Black Shark 5" to "",
            "Xiaomi Black Shark 5 Pro" to "", "Xiaomi Black Shark 6" to "", "Xiaomi Black Shark 6 Pro" to "",
            "Xiaomi CC9" to "", "Xiaomi CC9 Pro" to "", "Xiaomi Mix Fold 2" to "", "Xiaomi Mix Fold 3" to "",
            "Xiaomi Mix Fold 4" to "", "Xiaomi Mix Flip" to "", "Xiaomi 9" to "", "Xiaomi 9 SE" to "",
            "Xiaomi 9 Explorer" to "", "Xiaomi 10 5G" to "", "Xiaomi 10 Pro 5G" to "", "Xiaomi 10 Ultra" to "", "Xiaomi 10S" to ""
        ), "app")

        addDevs("Google", listOf(
            "Pixel 9 Pro" to "", "Pixel 9" to "", "Pixel 8 Pro" to "", "Pixel 8" to "",
            "Pixel 7 Pro" to "", "Pixel 7" to "", "Pixel 6 Pro" to "", "Pixel 6" to "", "Pixel 5" to ""
        ), "app")

        addDevs("Nokia", listOf(
            "Nokia X100" to ""
        ), "app")

        // --- Category Tools (Extra): Additional devices outside official PDFs ---
        addDevs("Motorola", listOf(
            "Edge 50 Ultra" to "", "Edge 50 Pro" to "", "Edge 50 Fusion" to "", "Edge 40 Pro" to "",
            "Edge 40" to "", "Edge 40 Neo" to "", "Moto G85" to "", "Moto G75" to "", "Moto G54" to "",
            "Moto G34" to "", "Moto G24" to "", "Moto G04" to "", "Razr 50 Ultra" to "", "Razr 50" to "",
            "Razr 40 Ultra" to "", "Razr 40" to "", "Moto G Power (2024)" to "", "Moto G Stylus (2024)" to "",
            "ThinkPhone by Motorola" to ""
        ), "tools", "extra")

        addDevs("Sony", listOf(
            "Xperia 1 VI" to "", "Xperia 10 VI" to "", "Xperia 5 V" to "", "Xperia 1 V" to "", "Xperia 10 V" to ""
        ), "tools", "extra")

        addDevs("Asus", listOf(
            "Zenfone 11 Ultra" to "", "ROG Phone 8" to "", "ROG Phone 8 Pro" to "", "Zenfone 10" to ""
        ), "tools", "extra")

        addDevs("LG", listOf(
            "LG Wing" to "", "LG Velvet" to "", "LG G8 ThinQ" to ""
        ), "tools", "extra")

        list
    }

    private fun guessChipset(manufacturer: String, model: String): String {
        val m = model.lowercase()
        val co = manufacturer.lowercase()
        fun has(vararg arr: String) = arr.any { m.contains(it) }

        return when {
            co == "samsung" -> when {
                has("s25") -> "Snapdragon 8 Elite (تقديري)"
                has("s24") -> "Snapdragon 8 Gen 3 / Exynos 2400 (تقديري)"
                has("s23") -> "Snapdragon 8 Gen 2 (تقديري)"
                has("s22") -> "Snapdragon 8 Gen 1 / Exynos 2200 (تقديري)"
                has("s21") -> "Snapdragon 888 / Exynos 2100 (تقديري)"
                has("s20") -> "Snapdragon 865 / Exynos 990 (تقديري)"
                has("note20") -> "Snapdragon 865+ / Exynos 990 (تقديري)"
                has("note10") -> "Snapdragon 855 / Exynos 9825 (تقديري)"
                has("s10") -> "Snapdragon 855 / Exynos 9820 (تقديري)"
                has("z fold 5", "z flip 5") -> "Snapdragon 8 Gen 2 (تقديري)"
                has("z fold 4", "z flip 4") -> "Snapdragon 8+ Gen 1 (تقديري)"
                has("z fold 3", "z flip 3") -> "Snapdragon 888 (تقديري)"
                m.startsWith("galaxy a") || m.startsWith("a0") || m.startsWith("a1") || m.startsWith("a2") || m.startsWith("a3") -> "Snapdragon أو Exynos متوسطة الفئة (تقديري)"
                has("tab s9") -> "Snapdragon 8 Gen 2 for Galaxy (تقديري)"
                has("tab s8") -> "Snapdragon 8 Gen 1 (تقديري)"
                else -> "غير متوفر في المصدر"
            }
            co == "iphone" -> when {
                has("16") -> "Apple A18 (تقديري)"
                has("15") -> "Apple A16/A17 Pro (تقديري)"
                has("14") -> "Apple A15/A16 Bionic (تقديري)"
                has("13") -> "Apple A15 Bionic (تقديري)"
                has("12") -> "Apple A14 Bionic (تقديري)"
                has("11") -> "Apple A13 Bionic (تقديري)"
                else -> "غير متوفر في المصدر"
            }
            co == "google" -> when {
                has("pixel 9") -> "Google Tensor G4 (تقديري)"
                has("pixel 8") -> "Google Tensor G3 (تقديري)"
                has("pixel 7") -> "Google Tensor G2 (تقديري)"
                has("pixel 6") -> "Google Tensor (تقديري)"
                has("pixel 5") -> "Snapdragon 765G (تقديري)"
                else -> "غير متوفر في المصدر"
            }
            co in listOf("redmi", "xiaomi", "poco") -> when {
                has("ultra", "pro+", "turbo") -> "Snapdragon 8 Gen / Dimensity راقٍ (تقديري)"
                has("note") -> "Snapdragon 6/7 Series أو Dimensity (تقديري)"
                else -> "Snapdragon / Dimensity متوسطة الفئة (تقديري)"
            }
            co == "vivo" -> when {
                has("x200", "x100") -> "Dimensity 9000 series (تقديري)"
                else -> "Snapdragon / Dimensity متوسطة الفئة (تقديري)"
            }
            co in listOf("oppo", "realme") -> when {
                has("find x", "gt") -> "Snapdragon 8 series (تقديري)"
                else -> "Dimensity / Snapdragon متوسطة الفئة (تقديري)"
            }
            co == "oneplus" -> "Snapdragon فئة عالية (تقديري)"
            co in listOf("itel", "infinix", "tecno") -> "Unisoc / MediaTek Helio (تقديري)"
            co == "motorola" -> "Snapdragon (تقديري)"
            else -> "غير متوفر في المصدر"
        }
    }

    val secretCodes: List<SecretCodeItem> = listOf(
        // VoLTE Activation Codes
        SecretCodeItem("*#467#", "Samsung", "هواتف Samsung", "غير متوفر في المصدر", "تفعيل VoLTE", "كود تفعيل خدمة VoLTE لهواتف Samsung"),
        SecretCodeItem("*#*#9646633#*#*", "Unisoc", "هواتف بمعالج Unisoc (مثل itel و Realme)", "Unisoc", "تفعيل VoLTE", "كود تفعيل خدمة VoLTE للهواتف ذات معالجات Unisoc"),
        SecretCodeItem("*#*#3646633#*#*", "Unisoc", "هواتف بمعالج Unisoc (مثل itel و Realme)", "Unisoc", "تفعيل VoLTE", "كود بديل لتفعيل خدمة VoLTE للهواتف ذات معالجات Unisoc"),
        SecretCodeItem("*#*#83781#*#*", "Unisoc", "هواتف بمعالج Unisoc (مثل itel و Realme)", "Unisoc", "تفعيل VoLTE", "كود بديل آخر لتفعيل خدمة VoLTE للهواتف ذات معالجات Unisoc"),
        SecretCodeItem("*#*#9646633#*#*", "MTK", "هواتف بمعالج MTK (مثل Infinix و TracFone وبعض الهواتف الصينية)", "MTK", "تفعيل VoLTE", "كود تفعيل خدمة VoLTE للهواتف ذات معالجات MTK"),
        SecretCodeItem("*#*#3646633#*#*", "MTK", "هواتف بمعالج MTK (مثل Infinix و TracFone وبعض الهواتف الصينية)", "MTK", "تفعيل VoLTE", "كود بديل لتفعيل خدمة VoLTE للهواتف ذات معالجات MTK"),
        SecretCodeItem("*#*#86583#*#*", "Redmi", "هواتف Redmi", "غير متوفر في المصدر", "تفعيل VoLTE", "كود تفعيل خدمة VoLTE لهواتف Redmi"),

        // Diagnostic Mode Codes
        SecretCodeItem("##366633#", "Samsung", "هواتف Samsung – شركة الاتصال VZW أو USC", "غير متوفر في المصدر", "وضع Diagnostic", "كود تفعيل وضع الدياج لهواتف Samsung (كارير VZW/USC)"),
        SecretCodeItem("##3424#", "Samsung", "هواتف Samsung – شركة الاتصال Sprint", "غير متوفر في المصدر", "وضع Diagnostic", "كود تفعيل وضع الدياج لهواتف Samsung (كارير Sprint)"),
        SecretCodeItem("*#0808#", "Samsung", "هواتف Samsung – أي شركة اتصال أخرى", "غير متوفر في المصدر", "وضع Diagnostic", "كود تفعيل وضع الدياج لهواتف Samsung (شركات اتصال أخرى)"),
        SecretCodeItem("*#558#", "Vivo", "أجهزة Vivo", "غير متوفر في المصدر", "وضع Diagnostic", "كود تفعيل وضع الدياج لأجهزة Vivo"),
        SecretCodeItem("#801#", "OnePlus", "أجهزة OnePlus", "غير متوفر في المصدر", "وضع Diagnostic", "كود تفعيل وضع الدياج لأجهزة OnePlus"),
        SecretCodeItem("*#8011#", "OnePlus", "أجهزة OnePlus", "غير متوفر في المصدر", "وضع Diagnostic", "كود بديل لتفعيل وضع الدياج لأجهزة OnePlus"),
        SecretCodeItem("*#*#13491#*#*", "أجهزة صينية", "الأجهزة الصينية مثل 3n أو 5n", "غير متوفر في المصدر", "وضع Diagnostic", "كود تفعيل وضع الدياج لبعض الأجهزة الصينية"),
        SecretCodeItem("*#*#46368676#*#*", "أجهزة صينية", "الأجهزة الصينية مثل 3n أو 5n", "غير متوفر في المصدر", "وضع Diagnostic", "كود بديل لتفعيل وضع الدياج لبعض الأجهزة الصينية"),
        SecretCodeItem("##3424#", "Coolpad", "هواتف Coolpad", "غير متوفر في المصدر", "وضع Diagnostic", "كود تفعيل وضع الدياج لهواتف Coolpad"),

        // Other Codes
        SecretCodeItem("*#0*#", "Samsung", "أجهزة Samsung", "غير متوفر في المصدر", "أكواد أخرى / فحص", "فحص شامل لأجهزة سامسونج"),
        SecretCodeItem("*#0011#", "Samsung / أجهزة صينية قديمة", "أجهزة سامسونج بمعالج MTK وبعض الهواتف الصينية القديمة", "MTK", "أكواد أخرى", "تفعيل الفورجي لأجهزة سامسونج ذات معالجات MTK، وأيضاً فك شفرة الهواتف الصينية القديمة")
    )

    val problemsList: List<ProblemItem> = listOf(
        ProblemItem(
            n = 1,
            title = "خروج الجهاز عن التغطية وعند الاتصال به يرد مشغول",
            desc = "مشكلة كبيرة تحدث نتيجة التفعيل اليدوي للخدمة (بطريقة GCF Mode) لأجهزة السامسونج الأمريكية الداعمة للخدمة (آندرويد 14 وما بعده) وكذلك غير الداعمة (آندرويد 13 وما قبله). بصورة متكررة ودون سابق إنذار يخرج الجهاز عن التغطية ويدخل في حالة خمول، فيرفض أي مكالمة واردة ويظهر عند المتصل أن الرقم مشغول. تستمر هذه الحالة دون علم صاحب الجهاز، ولا يخرج منها إلا بإنشاء مكالمة صادرة أو تفعيل وإلغاء وضع الطيران.",
            solution = listOf(
                "بالنسبة لأجهزة السامسونج الأمريكية الداعمة للخدمة (آندرويد 14 وما بعده): فتح قائمة إعدادات الـ IMS في الجهاز",
                "إلغاء اختيار GCF Mode",
                "الضغط على Reset to Default",
                "إعادة تشغيل الجهاز",
                "بالنسبة لأجهزة السامسونج الأمريكية غير الداعمة للخدمة (آندرويد 13 وما قبله): لا يُنصح إطلاقاً بالتفعيل اليدوي بطريقة GCF Mode لهذه الأجهزة، ويُنصح بالتراجع عن تفعيل الفولتي والاكتفاء بتشغيلها على شبكة CDMA"
            ),
            manufacturer = "Samsung",
            model = "أجهزة سامسونج الأمريكية",
            warning = "طريقة GCF Mode اليدوية غير مُعتمدة ولا يُنصح بها على الإطلاق، لأنها تؤدي إلى عدم استقرار الخدمة وخروج الجهاز عن التغطية."
        ),
        ProblemItem(
            n = 2,
            title = "لا يُلغي الجهاز المكالمة الخاطئة رغم الضغط على زر الإغلاق",
            desc = "مشكلة أخرى تحدث نتيجة التفعيل اليدوي للخدمة (بطريقة GCF Mode) لأجهزة السامسونج الداعمة للـ VoLTE (آندرويد 14 وما بعده). عند إجراء اتصال من الجهاز برقم آخر بصورة خاطئة، ثم إلغاء المكالمة مباشرة، يستمر الاتصال ويرن الطرف الآخر لفترة وجيزة (تقريباً 5 إلى 8 ثوان) رغم الضغط على زر إغلاق الاتصال.",
            solution = listOf(
                "فتح قائمة إعدادات الـ IMS في الجهاز",
                "إلغاء اختيار GCF Mode",
                "الضغط على Reset to Default",
                "إعادة تشغيل الجهاز"
            ),
            manufacturer = "Samsung",
            model = "أجهزة سامسونج الداعمة للـ VoLTE (آندرويد 14 وما بعده)",
            warning = "ناتجة عن التفعيل اليدوي غير المعتمد بطريقة GCF Mode."
        ),
        ProblemItem(
            n = 3,
            title = "الرسائل الطويلة في بعض أجهزة السامسونج تصل مقطعة",
            desc = "بعض أجهزة السامسونج الخليجية تقوم بتقطيع الرسائل العربية إذا تجاوزت الرسالة الواحدة 70 حرفاً. لا توجد هذه المشكلة في أجهزة سامسونج الأمريكية، وأساس المشكلة يكمن في السوفتوير المحمّل في الموديلات الخليجية.",
            solution = listOf(
                "تحميل تطبيق يمن موبايل (Yemen Mobile) من متجر الأندرويد",
                "تحميل تطبيق الرسائل الخاص بجوجل (Google Messages) من متجر الأندرويد",
                "ضبط تطبيق Google Messages ليكون التطبيق الافتراضي للرسائل"
            ),
            manufacturer = "Samsung",
            model = "الموديلات الخليجية"
        ),
        ProblemItem(
            n = 4,
            title = "عدم التمكن من إلغاء تثبيت الكارير الأمريكي في أجهزة السامسونج والعودة إلى الوضع التلقائي",
            desc = "يقوم أغلب المبرمجين بتثبيت الكارير CSC في أجهزة سامسونج الأمريكية على VZW أو Sprint مثلاً؛ لمنع الجهاز من التحول إلى الكارير العالمي (XAA) عند إدخال الشريحة. ينتج عن ذلك صعوبة إعادة الجهاز إلى الوضع التلقائي السابق. للتأكد من نوع الكارير المفعّل: الإعدادات >> حول الهاتف >> معلومات السوفتوير >> Service provider software version، أول ثلاث خانات من اليسار تمثل الكارير المفعّل في الجهاز.",
            solution = listOf(
                "ربط الجهاز بالكمبيوتر واستخدام أوامر ADB Command",
                "تنفيذ: adb shell",
                "ثم تنفيذ: pm install-existing --user 0 com.samsung.android.cidmanager"
            ),
            manufacturer = "Samsung",
            model = "الموديلات الأمريكية"
        ),
        ProblemItem(
            n = 5,
            title = "عدم التمكن من تحديث بعض أجهزة السامسونج كي تدعم خدمة VoLTE",
            desc = "يقوم أغلب المبرمجين بتثبيت الكارير CSC في أجهزة سامسونج الأمريكية على VZW أو Sprint، وهو عُرف سابق كان لديهم لتشغيل خدمات CDMA. تثبيت الكارير على أحد المشغلين الأمريكيين مثل VZW يؤدي إلى تقييد عملية تحديث السوفتوير للجهاز كونه يعمل خارج شبكة المشغل.",
            solution = listOf(
                "تحويل الكارير في الجهاز إلى XAA عبر استخدام برنامج SamFw واتباع الخطوات الموضحة في المصدر",
                "عمل التحديث اللازم من أجل دعم خدمة VoLTE"
            ),
            manufacturer = "Samsung",
            model = "الموديلات الأمريكية",
            warning = "يُنصح بالاعتماد على الكارير XAA كونه عالمي ومفتوح: يسمح بتحديث نظام التشغيل ويدعم جميع الترددات العاملة بشبكة يمن موبايل، وبالتالي تكون التغطية في الجهاز أفضل."
        ),
        ProblemItem(
            n = 6,
            title = "عدم التمكن من تشغيل خدمة VoLTE في جهاز سامسونج S21 بالشريحة الجديدة",
            desc = "اختفاء خدمة VoLTE نتيجة تعارض السوفتوير المفعّل (sprint/usc) مع الشريحة الجديدة (ذات العلامة VoLTE)، وخصوصاً في حال وجود تطبيق يمن موبايل في الجهاز. بينما عند استخدام شريحة فولتي قديمة، تظهر الخدمة وتعمل بشكل طبيعي. تم فحص أحد العينات وكان الكارير المفعّل في الجهاز هو Sprint.",
            solution = listOf(
                "تحويل الكارير في الجهاز إلى XAA عبر إحدى الطريقتين: إلغاء تثبيت الكارير عبر ADB Command (راجع المشكلة رقم 4)، أو استخدام برنامج SamFw (راجع المشكلة رقم 5)",
                "التأكد من أن نظام التشغيل محدّث إلى Android 14",
                "إضافة نقاط الوصول في حال لم تكن مضافة"
            ),
            manufacturer = "Samsung",
            model = "Galaxy S21 (الموديلات الأمريكية)",
            warning = "يُنصح بالاعتماد على الكارير XAA كونه عالمي ومفتوح."
        ),
        ProblemItem(
            n = 7,
            title = "عدم التمكن من تحديث النظام في بعض أجهزة شاومي وريدمي",
            desc = "السبب أن نظام التشغيل المحمّل على الجهاز هو سوفتوير مُعدَّل غير رسمي من الشركة المصنعة. للتأكد من ذلك يجب فحص رقم إصدار MIUI: إذا لم تكن الخانة الأخيرة له = 0، فإن السوفتوير مُعدَّل وغير رسمي.",
            solution = listOf(
                "تحميل السوفتوير الرسمي من الشركة المصنعة",
                "تحديث الجهاز بالسوفتوير الرسمي"
            ),
            manufacturer = "Xiaomi / Redmi",
            model = "غير متوفر في المصدر"
        ),
        ProblemItem(
            n = 8,
            title = "لا تعمل خدمة الرسائل في بعض الأجهزة الصينية مثل هواوي وأونر",
            desc = "لا يمكن إرسال أو استقبال الرسائل النصية، ولا يمكن استقبال أي إشعارات من الشبكة، بينما تعمل بقية الخدمات (المكالمات الصوتية وتصفح الإنترنت) بشكل طبيعي. لوحظ أن تلك الأجهزة ليست موجهة لسوق الشرق الأوسط، فهي محمّلة بسوفتوير خاص ومغلق على سوق الصين والشركات الصينية، ويحتوي على قيود برمجية تمنع تشغيل بعض الخدمات (مثل رسائل الفولتي) إلا في المناطق الصينية.",
            solution = listOf(
                "التأكد من دعم جهاز الموبايل للخدمات الأساسية (صوت، رسائل، إنترنت) قبل تفعيل VoLTE في الجهاز"
            ),
            manufacturer = "Huawei / Honor",
            model = "أجهزة صينية موجهة للسوق الصيني"
        ),
        ProblemItem(
            n = 9,
            title = "عدم التمكن من تفعيل الخدمة بالشرائح الجديدة في بعض أجهزة Pixel 8 و Pixel 9",
            desc = "لا تظهر إشارة الخدمة ولا يتحسس الجهاز الشرائح الجديدة (ذات العلامة VoLTE)، بينما عند استخدام شرائح فولتي قديمة تظهر الإشارة وتعمل الخدمة بشكل طبيعي. السبب: اعتماد بعض المبرمجين على أدوات غير موثوقة لتشغيل خدمة VoLTE في أجهزة بيكسل مثل Shizuku و Pixel IMS، وهذا خطأ فادح يؤدي إلى عدم توافق الجهاز مع الشرائح الحديثة.",
            solution = listOf(
                "حذف الأدوات (Shizuku, Pixel IMS) المستخدمة في الجهاز",
                "تحميل تطبيق يمن موبايل في الجهاز",
                "اتباع التعليمات الموضحة في دليل التفعيل اليدوي عبر التطبيق"
            ),
            manufacturer = "Google",
            model = "Pixel 8 / Pixel 9",
            warning = "أدوات Shizuku و Pixel IMS غير موثوقة ولا يُنصح باستخدامها لتفعيل VoLTE."
        )
    )

    val activationRules: List<ActivationRule> = listOf(
        ActivationRule("SAMSUNG (الموديلات الخليجية/العالمية)", "S24 وما بعده وكل الموديلات الأخرى Android 14/15", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "في Android 15 تعمل الخدمة تلقائياً دون الحاجة لإضافة نقاط وصول"),
        ActivationRule("SAMSUNG (الموديلات الخليجية/العالمية)", "S23, S22, S21", "تعمل بشكل تلقائي بعد تحديث النظام إلى Android 14", "غير متوفر في المصدر"),
        ActivationRule("SAMSUNG (الموديلات الخليجية/العالمية)", "Note10, S10/S10+/S10E, Note20, S20/S20 FE, A02, A04, A06, A12, A13, A14, A16, A22, A23, A32, A33, A34, A52, A53, A54, A71, A72, A73, Fold3/Flip3, Fold4/Flip4, Fold5/Flip5", "تعمل بشكل تلقائي بعد تحديث النظام إلى Android 13 (إصدار يوليو 2023م وما بعده)", "غير متوفر في المصدر"),
        ActivationRule("SAMSUNG (الموديلات الأمريكية)", "S24 وما بعده", "إضافة نقاط الوصول (في حال لم تكن موجودة)", "لا يُنصح بتثبيت الكارير الأمريكي لأنه لا يدعم بعض الترددات؛ عند إدخال الشريحة يتحول الـ CSC إلى XAA وهو الكارير الأفضل"),
        ActivationRule("SAMSUNG (الموديلات الأمريكية)", "S23, S22, S21, S20 FE, S10E, A02, A04, A06, A12, A13, A14, A16, A22, A23, A32, A33, A34, A52, A53, A54, A71, A72, Fold3/Flip3, Fold4/Flip4, Fold5/Flip5", "تحديث النظام إلى Android 14 + إضافة نقاط الوصول", "غير متوفر في المصدر"),
        ActivationRule("APPLE", "iPhone 14 وما بعده", "إضافة نقاط الوصول", "غير متوفر في المصدر"),
        ActivationRule("APPLE", "iPhone 12, iPhone 13", "إضافة نقاط الوصول والتأكد من إلغاء ملف التغطية الخاص بالـ cdma", "غير متوفر في المصدر"),
        ActivationRule("APPLE", "iPhone 8, iPhone X/XR/XS, iPhone 11", "التحديث إلى iOS 14 + إضافة نقاط الوصول", "يجب إلغاء ملف التغطية الخاص بالـ cdma من أجل تفعيل الـ Bundle الافتراضي"),
        ActivationRule("ViVO", "كل الموديلات المتوفرة بالسوق", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "التأكد من تحديث النظام في الموديلات القديمة"),
        ActivationRule("Xiaomi", "Xiaomi 14 وما بعده", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "غير متوفر في المصدر"),
        ActivationRule("Xiaomi", "Xiaomi 12 / Xiaomi 13", "تعمل بشكل تلقائي بعد تحديث النظام إلى HyperOS 2.0", "غير متوفر في المصدر"),
        ActivationRule("Redmi", "Redmi 14C / A3 / Note 14 وما بعده", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "غير متوفر في المصدر"),
        ActivationRule("Redmi", "Redmi K60 / Note 12 / Note 13", "تعمل بشكل تلقائي بعد تحديث النظام إلى HyperOS 2.0", "غير متوفر في المصدر"),
        ActivationRule("POCO", "POCO X6 / F6 / M6 / C65 / C75 وما بعده", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "غير متوفر في المصدر"),
        ActivationRule("POCO", "POCO X4 / X5 / F5", "تعمل بشكل تلقائي بعد تحديث النظام إلى HyperOS 2.0", "غير متوفر في المصدر"),
        ActivationRule("LT / Cool Pad / Trecfone", "الموديلات الموضحة في القائمة (A-2) وما بعدها", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "غير متوفر في المصدر"),
        ActivationRule("itel / Infinix / TECNO", "كل الموديلات المدرجة", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "غير متوفر في المصدر"),
        ActivationRule("BlackView / UMIDIGI / Oukitel / DooGee", "كل الموديلات المدرجة", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "غير متوفر في المصدر"),
        ActivationRule("Realme / OPPO", "الموديلات الموضحة في القائمة (A-1) وما بعدها", "تعمل بشكل تلقائي بمجرد ادخال الشريحة", "غير متوفر في المصدر"),
        ActivationRule("بقية الأجهزة والموديلات", "الموديلات الموضحة في القائمة (B)", "التفعيل عبر استخدام تطبيق يمن موبايل", "يرجى الاطلاع على دليل التفعيل الخاص بتطبيق يمن موبايل")
    )

    val apnList: List<ApnItem> = listOf(
        ApnItem("ims", "ims", "ims", "pdf"),
        ApnItem("ymdata", "ymdata", "default", "pdf"),
        ApnItem("xcap", "xcap", "xcap", "extra")
    )

    val guideYemenMobileApp = GuideContent(
        title = "دليل التفعيل اليدوي عبر تطبيق يمن موبايل — لأجهزة بيكسل والأجهزة الصينية",
        intro = "يجب أن تكون شريحة يمن موبايل حديثة تدعم الخدمة، مثل تلك ذات العلامة VoLTE.",
        steps = listOf(
            "افتح تطبيق يمن موبايل الشامل واضغط على تسجيل الدخول",
            "اضغط على الدخول كزائر إذا لم يكن لديك حساب",
            "الذهاب إلى أيقونة الخدمات من نافذة التطبيق",
            "اختيار الخدمات الأساسية من القائمة",
            "الضغط على ضبط الإعدادات في خدمة VoLTE",
            "في حال كانت الشريحة تدعم الخدمة فإنه ستظهر رسالة \"تم ضبط الإعدادات بنجاح، يرجى إعادة تشغيل هاتفك\""
        )
    )

    val guideIphone = GuideContent(
        title = "دليل التفعيل اليدوي لأجهزة الآيفون",
        intro = "يجب أن تكون شريحة يمن موبايل حديثة تدعم الخدمة، مثل تلك ذات العلامة VoLTE.",
        steps = listOf(
            "من قائمة الإعدادات >> خلوي >> الصوت والبيانات: يتم اختيار LTE والتأكد من تفعيل مفتاح VoLTE",
            "ثم العودة إلى قائمة الإعدادات >> خلوي >> شبكة البيانات الخلوية: إضافة نقطة الوصول (ymdata)",
            "من قائمة الإعدادات >> الهاتف >> رقمي: يتم إضافة رقم المشترك مع مفتاح البلد ثم حفظ الإعدادات"
        )
    )
}
