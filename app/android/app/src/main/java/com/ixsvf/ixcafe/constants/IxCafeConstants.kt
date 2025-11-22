package com.ixsvf.ixcafe.constants

import com.ixsvf.ixcafe.BuildConfig

object IxCafeConstants {

    object APPSETTINGS {
        const val SETTINGS_FILE_NAME = "settings"
        const val IS_FIRST_RUN = "is_first_run"
        const val AUTH_TOKEN_KEY = "auth_token"
        const val USER_NAME_KEY = "nome_empregado_key"
        const val LOGIN_KEY = "login"

        const val DEMO_KEY = "Demonstração"

        const val MAX_PIN_LENGTH = 4
    }

    object ENDPOINTSROUTES{
        const val BASE_URL = BuildConfig.BASE_URL

        const val LOGIN = "api/empregados/login"

        const val LISTAR_EMPREGADOS = "api/empregados/listar"
    }


    object NAVROUTES{
        const val LOGIN_SCREEN = "login_screen"
        const val TABLE_SCREEN = "mesas_screen"

    }

    object DEMOCREDENTIALS
    {
        const val DEMO_PROFILE_PIN = "1234"
        const val MAX_PIN_LENGTH = 4
        const val DEMO_SETTINGS_USER = "admin"
        const val DEMO_SETTINGS_PASSWORD = "admin"
    }

    object LOCAL_DB{
        const val NAME = "ixcafe_database"
        object TABLES {
            const val EMPREGADOS = "empregados"

        }
    }
}