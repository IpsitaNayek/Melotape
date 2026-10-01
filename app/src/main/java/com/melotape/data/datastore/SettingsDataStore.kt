package com.melotape.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        val KEY_REPEAT_MODE = intPreferencesKey("repeat_mode") // 0 = OFF, 1 = ALL, 2 = ONE
        val KEY_SHUFFLE = booleanPreferencesKey("shuffle_enabled")
        val KEY_AUDIO_QUALITY = stringPreferencesKey("audio_quality") // "HIGH", "MEDIUM", "LOW"
        val KEY_WIFI_ONLY = booleanPreferencesKey("wifi_only_downloads")
        val KEY_HISS_FILTER = booleanPreferencesKey("hiss_filter_active") // Dolby B emulation
        val KEY_TAPE_COUNTER_BASELINE = intPreferencesKey("tape_counter_baseline")
        val KEY_THEME = stringPreferencesKey("deck_aesthetic_theme") // "SLATE"
    }

    val repeatMode: Flow<Int> = dataStore.data
        .catchPreferencesErrors()
        .map { it[KEY_REPEAT_MODE] ?: 0 }

    val isShuffleEnabled: Flow<Boolean> = dataStore.data
        .catchPreferencesErrors()
        .map { it[KEY_SHUFFLE] ?: false }

    val audioQuality: Flow<String> = dataStore.data
        .catchPreferencesErrors()
        .map { it[KEY_AUDIO_QUALITY] ?: "HIGH" }

    val isWifiOnlyDownloads: Flow<Boolean> = dataStore.data
        .catchPreferencesErrors()
        .map { it[KEY_WIFI_ONLY] ?: true }

    val isHissFilterActive: Flow<Boolean> = dataStore.data
        .catchPreferencesErrors()
        .map { it[KEY_HISS_FILTER] ?: true }

    val tapeCounterBaseline: Flow<Int> = dataStore.data
        .catchPreferencesErrors()
        .map { it[KEY_TAPE_COUNTER_BASELINE] ?: 42 }

    val theme: Flow<String> = dataStore.data
        .catchPreferencesErrors()
        .map { it[KEY_THEME] ?: "WARM_RETRO_SLATE" }

    suspend fun setRepeatMode(mode: Int) {
        dataStore.edit { it[KEY_REPEAT_MODE] = mode }
    }

    suspend fun setShuffleEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_SHUFFLE] = enabled }
    }

    suspend fun setAudioQuality(quality: String) {
        dataStore.edit { it[KEY_AUDIO_QUALITY] = quality }
    }

    suspend fun setWifiOnlyDownloads(wifiOnly: Boolean) {
        dataStore.edit { it[KEY_WIFI_ONLY] = wifiOnly }
    }

    suspend fun setHissFilterActive(active: Boolean) {
        dataStore.edit { it[KEY_HISS_FILTER] = active }
    }

    suspend fun setTapeCounterBaseline(baseline: Int) {
        dataStore.edit { it[KEY_TAPE_COUNTER_BASELINE] = baseline }
    }

    suspend fun setTheme(themeName: String) {
        dataStore.edit { it[KEY_THEME] = themeName }
    }

    private fun Flow<Preferences>.catchPreferencesErrors(): Flow<Preferences> = catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
}
