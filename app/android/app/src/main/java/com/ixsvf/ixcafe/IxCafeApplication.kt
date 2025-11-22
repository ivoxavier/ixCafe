package com.ixsvf.ixcafe

import android.app.Application
import com.google.firebase.FirebaseApp
import com.ixsvf.ixcafe.services.repository.Settings
import com.ixsvf.ixcafe.services.repository.local.IxCafeDatabase
import com.ixsvf.ixcafe.services.repository.remote.RetrofitClient

class IxCafeApplication : Application() {

    val database : IxCafeDatabase by lazy {
        IxCafeDatabase.getDatabase(this)
    }


    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)


        val settings = Settings(applicationContext)


        RetrofitClient.initialize(settings)
    }
}