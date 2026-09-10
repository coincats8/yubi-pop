package com.yubipop.game

import android.net.Uri
import android.util.Base64
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "YubiWallet")
class YubiWalletPlugin : Plugin() {
    private var resultSender: ActivityResultSender? = null
    private lateinit var walletAdapter: MobileWalletAdapter

    override fun load() {
        super.load()
        activity?.let { resultSender = ActivityResultSender(it) }
        walletAdapter = MobileWalletAdapter(
            connectionIdentity = ConnectionIdentity(
                identityUri = Uri.parse("https://coincats8.github.io/yubi-pop/"),
                iconUri = Uri.parse("icon.png"),
                identityName = "Yubi Pop!"
            )
        )
    }

    @PluginMethod
    fun connect(call: PluginCall) {
        val sender = resultSender ?: run {
            call.reject("Please close and reopen Yubi Pop, then try again.")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                when (val result = walletAdapter.connect(sender)) {
                    is TransactionResult.Success -> {
                        val account = result.authResult.accounts.firstOrNull()
                        if (account == null) {
                            call.reject("No wallet account was selected")
                            return@launch
                        }
                        val response = JSObject()
                        response.put("address", Base64.encodeToString(account.publicKey, Base64.NO_WRAP))
                        call.resolve(response)
                    }
                    is TransactionResult.NoWalletFound -> call.reject("No Mobile Wallet Adapter wallet found")
                    is TransactionResult.Failure -> call.reject(result.e.message ?: "Wallet connection failed")
                }
            } catch (error: Exception) {
                call.reject(error.message ?: "Wallet connection failed", error)
            }
        }
    }

    @PluginMethod
    fun signDailyPop(call: PluginCall) {
        val sender = resultSender ?: run {
            call.reject("Please close and reopen Yubi Pop, then try again.")
            return
        }
        val message = call.getString("message") ?: "Yubi Pop Daily Pop"

        CoroutineScope(Dispatchers.Main).launch {
            try {
                when (val result = walletAdapter.transact(sender) { authResult ->
                    val account = authResult.accounts.firstOrNull()
                        ?: throw IllegalStateException("No wallet account was selected")
                    signMessagesDetached(
                        arrayOf(message.toByteArray()),
                        arrayOf(account.publicKey)
                    )
                }) {
                    is TransactionResult.Success -> {
    val response = JSObject()
    response.put("signed", true)
    call.resolve(response)
}
                    is TransactionResult.NoWalletFound -> call.reject("No Mobile Wallet Adapter wallet found")
                    is TransactionResult.Failure -> call.reject(result.e.message ?: "Daily Pop signature failed")
                }
            } catch (error: Exception) {
                call.reject(error.message ?: "Daily Pop signature failed", error)
            }
        }
    }
}
