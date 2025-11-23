package com.ixsvf.ixcafe.services.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.services.repository.local.dao.EmpregadosDao
import com.ixsvf.ixcafe.services.repository.local.model.EmpregadosEntity


@Database(entities = [EmpregadosEntity::class], version = 4, exportSchema = false)
abstract class IxCafeDatabase: RoomDatabase() {

    abstract fun empregadosDao() : EmpregadosDao

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
                INSTANCE = instance
                instance
            }
        }

    }
}