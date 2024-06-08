package com.example.sehatin.application.persistance

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "DataStoreManager"
private const val KEY_LAST_KNOWN_LATITUDE = "last_known_latitude"
private const val KEY_LAST_KNOWN_LONGITUDE = "last_known_longitude"
class DataStoreManager @Inject constructor(private val dataStore: DataStore<Preferences>) {
    private val usernamePref = stringPreferencesKey("username")
    private val tokenPref = stringPreferencesKey("token")


//    suspend fun setUserPref() {
//        dataStore.edit { userPref ->
//            userPref[usernamePref] = user.name
//            userPref[tokenPref] = user.token
//            userPref[usernameId] = user.userId
//        }
//    }

    suspend fun getUserToken() : String? =
        dataStore.data.first()[tokenPref]

//    suspend fun userLogout(){
//        dataStore.edit {
//            it.clear()
//        }
//    }

    suspend fun saveLastKnownLocation(latitude: Double, longitude: Double) {
        try {
            dataStore.edit { preferences ->
                preferences[doublePreferencesKey(KEY_LAST_KNOWN_LATITUDE)] = latitude
                preferences[doublePreferencesKey(KEY_LAST_KNOWN_LONGITUDE)] = longitude
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving last known location: ${e.message}")
        }
    }

    val lastKnownLocation: Flow<Pair<Double, Double>> = dataStore.data.map { preferences ->
        val latitude = preferences[doublePreferencesKey(KEY_LAST_KNOWN_LATITUDE)] ?: 0.0
        val longitude = preferences[doublePreferencesKey(KEY_LAST_KNOWN_LONGITUDE)] ?: 0.0
        Pair(latitude, longitude)
    }

}