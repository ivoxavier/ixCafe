package com.ixsvf.ixcafe.constants

import com.ixsvf.ixcafe.BuildConfig

object IxCafeConstants {

    object APP_SETTINGS {
        const val SETTINGS_FILE_NAME = "settings"
        const val IS_FIRST_RUN = "is_first_run"
        const val AUTH_TOKEN_KEY = "auth_token"
        const val USER_NAME_KEY = "nome_empregado_key"
        const val LOGIN_KEY = "login"

        const val DEMO_KEY = "Demonstração"
    }

    object ENDPOINTS_ROUTES{
        const val BASE_URL = BuildConfig.BASE_URL

        const val LOGIN = "api/empregados/login"

        const val LISTAR_EMPREGADOS = "api/empregados/listar"
    }


    object NAV_ROUTES{
        const val LOGIN_SCREEN = "login_screen"
        const val TABLE_SCREEN = "mesas_screen"

    }
}