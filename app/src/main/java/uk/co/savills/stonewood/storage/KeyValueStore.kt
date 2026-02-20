package uk.co.savills.stonewood.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class KeyValueStore(private val appContext: Context) {

    fun getEncryptedSharedPreferences(): SharedPreferences {
        val context = appContext
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            SHARED_PREFERENCES_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    inline fun <reified T : Any> set(key: String, value: T?) {
        val sharedPreferences = getEncryptedSharedPreferences()

        with(sharedPreferences.edit()) {
            val converter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = converter.adapter(T::class.java)
            val jsonData = jsonAdapter.toJson(value)

            putString(key, jsonData)
            commit()
        }
    }

    inline fun <reified T : Any> get(key: String): T? {
        val sharedPreferences = getEncryptedSharedPreferences()
        val jsonData = sharedPreferences.getString(key, null)
        var json: T? = null

        if (jsonData != null) {
            val converter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = converter.adapter(T::class.java)
            json = jsonAdapter.fromJson(jsonData)
        }

        return json
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "secret_shared_prefs"
    }
}
