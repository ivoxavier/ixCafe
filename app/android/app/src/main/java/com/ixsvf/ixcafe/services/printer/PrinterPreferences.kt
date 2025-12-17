package com.ixsvf.ixcafe.services.printer

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Extensão para criar o DataStore (se ainda não tiver num ficheiro central)
val Context.dataStore by preferencesDataStore(name = "settings")

class PrinterPreferences(private val context: Context) {

    companion object {
        val PRINTER_IP = stringPreferencesKey("printer_ip")
        val PRINTER_PORT = intPreferencesKey("printer_port")
    }

    // Lê o IP (Padrão: 192.168.1.200)
    val printerIp: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PRINTER_IP] ?: "192.168.1.200"
        }

    // Lê a Porta (Padrão: 9100)
    val printerPort: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PRINTER_PORT] ?: 9100
        }

    // Grava os dados
    suspend fun savePrinterSettings(ip: String, port: Int) {
        context.dataStore.edit { preferences ->
            preferences[PRINTER_IP] = ip
            preferences[PRINTER_PORT] = port
        }
    }
}