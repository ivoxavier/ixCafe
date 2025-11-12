package com.ixsvf.ixcafe.constants

object IxCafeConstants {

    object APP_SETTINGS {
        const val SETTINGS_FILE_NAME = "settings"
        const val IS_FIRST_RUN = "is_first_run"
        const val AUTH_TOKEN_KEY = "auth_token"
        const val USER_NAME_KEY = "nome_empregado_key"
        const val LOGIN_KEY = "login"
    }

    object ENDPOINTS_ROUTES{
        //const val BASE_URL = BuildConfig.BASE_URL

        const val LOGIN = "api/empregados/login"

        const val LISTAR_EMPREGADOS = "api/empregados/listar"
    }

}