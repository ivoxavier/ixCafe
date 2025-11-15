package com.ixsvf.ixcafe

import android.app.Application
import com.ixsvf.ixcafe.services.repository.Settings
import com.ixsvf.ixcafe.services.repository.remote.RetrofitClient

class IxCafeApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        val settings = Settings(applicationContext)


        RetrofitClient.initialize(settings)
    }
}