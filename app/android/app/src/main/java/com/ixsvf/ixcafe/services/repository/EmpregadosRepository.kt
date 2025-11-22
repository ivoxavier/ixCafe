package com.ixsvf.ixcafe.services.repository


import com.ixsvf.ixcafe.services.repository.local.dao.EmpregadosDao

import com.ixsvf.ixcafe.services.repository.local.model.EmpregadosEntity
import com.ixsvf.ixcafe.services.repository.local.model.toEmpregadosProfile
import com.ixsvf.ixcafe.services.repository.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmpregadosRepository(
    private val empregadosDao: EmpregadosDao, // O DAO local
    private val firebaseDataSource: FirebaseDataSource // <--- AQUI: Mudou de ApiInterface para FirebaseDataSource
) {

    // --- 1. FONTE DE VERDADE (Room) ---
    val empregados: Flow<List<EmpregadosProfile>> = empregadosDao.getAllEmpregados()
        .map { entities ->
            entities.map { it.toEmpregadosProfile() }
        }

    // --- 2. OBTER UM ÚNICO EMPREGADO (Para o AuthScreen) ---
    suspend fun getEmpregadoById(id: String): EmpregadosProfile? {
        return empregadosDao.getEmpregadosById(id)?.toEmpregadosProfile()
    }

    // --- 3. SINCRONIZAÇÃO (Firebase -> Room) ---
    suspend fun refreshEmpregados() {
        // 1. Busca dados ao Firestore
        val firebaseUsers = firebaseDataSource.getEmpregados()

        // 2. Converte para Entidades da Base de Dados Local
        val userEntities = firebaseUsers.map { user ->
            EmpregadosEntity(
                id = user.id,
                name = user.name,
                role = user.role,
                pinHash = user.pinHash // Agora incluímos o pinHash
            )
        }

        // 3. Salva no Room (Offline cache)
        empregadosDao.updateEmpregados(userEntities)
    }
}