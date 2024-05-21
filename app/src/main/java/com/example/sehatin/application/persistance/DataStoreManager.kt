package com.example.sehatin.application.persistance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

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

}