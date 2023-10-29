package io.outblock.lilico.page.profile.subpage.wallet

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.storageInfoCache
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_STORAGE_INFO
import io.outblock.lilico.manager.flowjvm.executeCadence
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.utils.extensions.toSafeLong
import io.outblock.lilico.utils.ioScope


fun queryStorageInfo() {
    ioScope {
        val address = WalletManager.selectedWalletAddress()
        if (address.isEmpty()) {
            return@ioScope
        }
        val response = CADENCE_QUERY_STORAGE_INFO.executeCadence {
            arg { address(address) }
        }

        response?.stringValue.let {

        }
        if (response?.stringValue.isNullOrBlank()) {
            return@ioScope
        }
        val data = Gson().fromJson(response?.stringValue, StorageInfoResult::class.java)
        val info = StorageInfo(
            data.getValueByName("available"),
            data.getValueByName("used"),
            data.getValueByName("capacity"),
        )
        storageInfoCache().cache(info)
    }
}

private fun StorageInfoResult.getValueByName(name: String) =
    this.value?.find { it.key?.value == name }?.value?.value.toSafeLong()


data class StorageInfoResult(
    @SerializedName("type")
    val type: String?,
    @SerializedName("value")
    val value: List<Item>?
) {
    data class Item(
        @SerializedName("key")
        val key: Value?,
        @SerializedName("value")
        val value: Value?
    ) {
        data class Value(
            @SerializedName("type")
            val type: String?,
            @SerializedName("value")
            val value: String?
        )
    }
}

data class StorageInfo(
    @SerializedName("available")
    val available: Long,
    @SerializedName("used")
    val used: Long,
    @SerializedName("capacity")
    val capacity: Long,
)