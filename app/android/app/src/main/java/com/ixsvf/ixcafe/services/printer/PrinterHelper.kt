package com.ixsvf.ixcafe.services.printer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object PrinterHelper {

    // Comandos ESC/POS (Standard Epson)
    private val CMD_INIT = byteArrayOf(0x1B, 0x40)
    private val CMD_CUT = byteArrayOf(0x1D, 0x56, 0x41, 0x10) // Cortar total
    private val CMD_BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
    private val CMD_BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
    private val CMD_ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
    private val CMD_ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)

    // Função para testar conexão nas definições
    suspend fun testPrint(ip: String, port: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 2000) // 2s timeout

                val outputStream = socket.getOutputStream()

                outputStream.write(CMD_INIT)
                outputStream.write(CMD_ALIGN_CENTER)
                outputStream.write(CMD_BOLD_ON)
                outputStream.write("IX CAFE - TESTE\n".toByteArray())
                outputStream.write(CMD_BOLD_OFF)
                outputStream.write("Conexao com sucesso!\n".toByteArray())
                outputStream.write("--------------------------------\n\n\n".toByteArray())
                outputStream.write(CMD_CUT)

                outputStream.flush()
                outputStream.close()
                socket.close()

                Result.success("Impressão de teste enviada!")
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}