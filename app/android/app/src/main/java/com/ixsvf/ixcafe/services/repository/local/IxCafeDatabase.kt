package com.ixsvf.ixcafe.services.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.services.repository.local.dao.CategoriaDao
import com.ixsvf.ixcafe.services.repository.local.dao.EmpregadosDao
import com.ixsvf.ixcafe.services.repository.local.dao.MesaDao
import com.ixsvf.ixcafe.services.repository.local.dao.ProdutoDao
import com.ixsvf.ixcafe.services.repository.local.dao.QueueOrderDao
import com.ixsvf.ixcafe.services.repository.local.model.EmpregadosEntity
import com.ixsvf.ixcafe.services.repository.local.model.MesaEntity
import com.ixsvf.ixcafe.services.repository.local.model.ProdutoEntity
import com.ixsvf.ixcafe.services.repository.local.model.QueueOrderEntity


@Database(
    entities = [
        EmpregadosEntity::class,
        ProdutoEntity::class,
        MesaEntity::class,
        QueueOrderEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class IxCafeDatabase: RoomDatabase() {

    abstract fun empregadosDao() : EmpregadosDao
    abstract fun produtoDao(): ProdutoDao
    abstract  fun mesaDao(): MesaDao
    abstract fun queueOrderDao(): QueueOrderDao

    abstract fun categoriaDao(): CategoriaDao



    companion object {
        @Volatile
        private var INSTANCE: IxCafeDatabase? = null

        fun getDatabase(context: Context): IxCafeDatabase
        {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IxCafeDatabase::class.java,
                    IxCafeConstants.LOCAL_DB.NAME
                ).build()
                    //.fallbackToDestructiveMigration()
                INSTANCE = instance
                instance

            }
        }

    }
}