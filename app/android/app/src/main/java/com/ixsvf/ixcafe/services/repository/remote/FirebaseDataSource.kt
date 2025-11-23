
package com.ixsvf.ixcafe.services.repository.remote

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.ixsvf.ixcafe.BuildConfig
import com.ixsvf.ixcafe.services.repository.EmpregadosProfile
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun getEmpregados(): List<EmpregadosProfile> {
        // O ID do café vem da configuração da Build
        val cafeId = BuildConfig.CAFE_ID

        // Verificação extra de segurança (opcional, mas ajuda no debug)
        if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isEmpty()) {
            throw IllegalStateException("Firebase não inicializado! Verifique o google-services.json")
        }


        return try {
            // --- A MUDANÇA ESTÁ AQUI ---
            // Em vez de db.collection("empregados")
            // Vamos a: cafes -> ID_DO_CAFE -> empregados
            val result = db.collection("cafes")
                .document(cafeId)
                .collection("empregados")
                .get()
                .await()

            result.documents.map { document ->
                EmpregadosProfile(
                    id = document.id,
                    name = document.getString("nome") ?: "Sem Nome",
                    role = document.getString("cargo") ?: "Indefinido",
                    pinHash = document.getString("pin_hash") ?: "Sem PIN",
                    isActive = document.getBoolean("is_active") ?: false
                )
            }
        } catch (e: Exception) {
            // Dica: Em debug, é útil saber se falhou porque o ID não existe
            println("Erro ao buscar empregados para o café: $cafeId. Erro: ${e.message}")
            throw e
        }
    }


    fun listenToEmpregadosChanges(onUpdate: (List<EmpregadosProfile>) -> Unit) {
        val cafeId = BuildConfig.CAFE_ID

        db.collection("cafes").document(cafeId).collection("empregados")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (snapshots != null) {
                    val profiles = snapshots.documents.map { document ->
                        // ... seu código de mapeamento ...
                        EmpregadosProfile(
                            id = document.id,
                            name = document.getString("nome") ?: "",
                            role = document.getString("cargo") ?: "",
                            pinHash = document.getString("pin_hash"),
                            isActive = document.getBoolean("is_active") ?: true
                        )
                    }
                    onUpdate(profiles)
                }
            }
    }
}
