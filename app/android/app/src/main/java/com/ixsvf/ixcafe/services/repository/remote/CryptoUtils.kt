package com.ixsvf.ixcafe.services.repository.remote

import java.security.MessageDigest

object CryptoUtils {
    fun hashPin(pin: String): String {
        val bytes = pin.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

}