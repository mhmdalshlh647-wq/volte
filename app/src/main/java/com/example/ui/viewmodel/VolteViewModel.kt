package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.VolteDatabase
import com.example.data.model.DeviceItem
import com.example.data.model.ProblemItem
import com.example.data.model.SecretCodeItem
import com.example.data.repository.VolteRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavTab {
    HOME, DEVICES, CODES, PROBLEMS, APN, GUIDES, FAVORITES, HISTORY, DEVICE_CHECK, SETTINGS, COMPANIES, SEARCH, DEVICE_DETAIL, PROBLEM_DETAIL, COMPANY_DETAIL
}

data class SearchResultState(
    val devices: List<DeviceItem> = emptyList(),
    val codes: List<SecretCodeItem> = emptyList(),
    val problems: List<ProblemItem> = emptyList(),
    val totalCount: Int = 0
)

class VolteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VolteDatabase.getDatabase(application)
    val repository = VolteRepository(db.volteDao())

    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _deviceCategoryFilter = MutableStateFlow("all")
    val deviceCategoryFilter: StateFlow<String> = _deviceCategoryFilter.asStateFlow()

    private val _deviceCompanyFilter = MutableStateFlow("all")
    val deviceCompanyFilter: StateFlow<String> = _deviceCompanyFilter.asStateFlow()

    private val _codeFilter = MutableStateFlow("all")
    val codeFilter: StateFlow<String> = _codeFilter.asStateFlow()

    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId: StateFlow<String?> = _selectedDeviceId.asStateFlow()

    private val _selectedProblemN = MutableStateFlow<Int?>(null)
    val selectedProblemN: StateFlow<Int?> = _selectedProblemN.asStateFlow()

    private val _selectedCompany = MutableStateFlow<String?>(null)
    val selectedCompany: StateFlow<String?> = _selectedCompany.asStateFlow()

    private val _guideView = MutableStateFlow("app") // "app" or "iphone"
    val guideView: StateFlow<String> = _guideView.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _fontSizeMode = MutableStateFlow("md") // "sm", "md", "lg"
    val fontSizeMode: StateFlow<String> = _fontSizeMode.asStateFlow()

    val favoriteDeviceIds = repository.favoriteDeviceIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val searchHistory = repository.searchHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val searchResults: StateFlow<SearchResultState> = _searchQuery
        .combine(_currentTab) { query, _ ->
            val q = repository.normalizeArabic(query)
            if (q.isBlank()) {
                SearchResultState()
            } else {
                val matchedDevs = repository.devicesList.filter { d ->
                    val hay = repository.normalizeArabic("${d.manufacturer} ${d.model} ${d.modelNumbers} ${d.chipset} ${d.chipsetGuess} ${d.category}")
                    hay.contains(q)
                }
                val matchedCodes = repository.secretCodes.filter { c ->
                    val hay = repository.normalizeArabic("${c.code} ${c.manufacturer} ${c.deviceType} ${c.chipset} ${c.description} ${c.codeType}")
                    hay.contains(q)
                }
                val matchedProbs = repository.problemsList.filter { p ->
                    val hay = repository.normalizeArabic("${p.title} ${p.desc} ${p.solution.joinToString(" ")} ${p.manufacturer} ${p.model}")
                    hay.contains(q)
                }
                SearchResultState(
                    devices = matchedDevs,
                    codes = matchedCodes,
                    problems = matchedProbs,
                    totalCount = matchedDevs.size + matchedCodes.size + matchedProbs.size
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchResultState()
        )

    fun navigateTo(tab: NavTab) {
        _currentTab.value = tab
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.trim().length > 1) {
            _currentTab.value = NavTab.SEARCH
            viewModelScope.launch {
                repository.addSearchHistory(newQuery)
            }
        } else if (_currentTab.value == NavTab.SEARCH) {
            _currentTab.value = NavTab.HOME
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        if (_currentTab.value == NavTab.SEARCH) {
            _currentTab.value = NavTab.HOME
        }
    }

    fun setDeviceCategoryFilter(category: String) {
        _deviceCategoryFilter.value = category
    }

    fun setDeviceCompanyFilter(company: String) {
        _deviceCompanyFilter.value = company
    }

    fun setCodeFilter(filter: String) {
        _codeFilter.value = filter
    }

    fun openDeviceDetail(deviceId: String) {
        val dev = repository.devicesList.find { it.id == deviceId }
        dev?.let {
            viewModelScope.launch {
                repository.addSearchHistory(it.model)
            }
        }
        _selectedDeviceId.value = deviceId
        _currentTab.value = NavTab.DEVICE_DETAIL
    }

    fun openProblemDetail(n: Int) {
        _selectedProblemN.value = n
        _currentTab.value = NavTab.PROBLEM_DETAIL
    }

    fun openCompanyDetail(company: String) {
        _selectedCompany.value = company
        _currentTab.value = NavTab.COMPANY_DETAIL
    }

    fun setGuideView(view: String) {
        _guideView.value = view
    }

    fun toggleFavorite(deviceId: String) {
        val isFav = favoriteDeviceIds.value.contains(deviceId)
        viewModelScope.launch {
            repository.toggleFavorite(deviceId, isFav)
        }
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun setFontSizeMode(mode: String) {
        _fontSizeMode.value = mode
    }

    fun deleteHistoryQuery(query: String) {
        viewModelScope.launch {
            repository.deleteSearchQuery(query)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            repository.clearFavorites()
        }
    }
}
