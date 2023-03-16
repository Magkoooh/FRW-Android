package io.outblock.lilico.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import io.outblock.lilico.firebase.auth.firebaseCustomLogin
import io.outblock.lilico.manager.account.BalanceManager
import io.outblock.lilico.manager.coin.FlowCoinListManager
import io.outblock.lilico.manager.coin.TokenStateManager
import io.outblock.lilico.manager.nft.NftCollectionStateManager
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.network.model.AccountKey
import io.outblock.lilico.network.model.RegisterRequest
import io.outblock.lilico.utils.clearCacheDir
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.setMeowDomainClaimed
import io.outblock.lilico.utils.updateAccountTransactionCountLocal
import io.outblock.lilico.wallet.getPublicKey
import kotlinx.coroutines.delay


private const val TAG = "UserRegisterUtils"

suspend fun registerOutblockUser(
    username: String,
    callback: (isSuccess: Boolean) -> Unit,
) {
    registerOutblockUserInternal(username, callback)
}

suspend fun clearUserCache() {
    clearCacheDir()
    setMeowDomainClaimed(false)
    TokenStateManager.reload()
    delay(100)
    FlowCoinListManager.reload()
    NftCollectionStateManager.reload()
    TransactionStateManager.reload()
    BalanceManager.reload()
    updateAccountTransactionCountLocal(0)
    delay(1000)
}

private suspend fun registerOutblockUserInternal(
    username: String,
    callback: (isSuccess: Boolean) -> Unit,
) {
    val service = retrofit().create(ApiService::class.java)
    val user = service.register(
        RegisterRequest(
            username = username,
            accountKey = AccountKey(publicKey = getPublicKey(removePrefix = true))
        )
    )
    logd(TAG, user.toString())

    if (user.status > 400) {
        callback(false)
        return
    }

    logd(TAG, "start delete user")
    FirebaseMessaging.getInstance().deleteToken()
    Firebase.auth.currentUser?.delete()?.addOnCompleteListener {
        logd(TAG, "delete user finish exception:${it.exception}")
        if (it.isSuccessful) {
            firebaseCustomLogin(user.data.customToken) { isSuccessful, _ ->
                if (isSuccessful) {
                    callback(true)
                } else callback(false)
            }
        } else callback(false)
    }
}