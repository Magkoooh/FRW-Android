package com.flowfoundation.wallet.page.dialog.processing.send.presenter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import com.bumptech.glide.Glide
import com.nftco.flow.sdk.FlowTransactionStatus
import com.flowfoundation.wallet.R
import com.flowfoundation.wallet.base.presenter.BasePresenter
import com.flowfoundation.wallet.databinding.DialogSendConfirmBinding
import com.flowfoundation.wallet.manager.coin.FlowCoinListManager
import com.flowfoundation.wallet.manager.transaction.TransactionState
import com.flowfoundation.wallet.manager.transaction.TransactionState.Companion.TYPE_NFT
import com.flowfoundation.wallet.manager.transaction.TransactionState.Companion.TYPE_TRANSFER_COIN
import com.flowfoundation.wallet.manager.transaction.TransactionState.Companion.TYPE_TRANSFER_NFT
import com.flowfoundation.wallet.page.dialog.processing.send.SendProcessingDialog
import com.flowfoundation.wallet.page.dialog.processing.send.model.SendProcessingDialogModel
import com.flowfoundation.wallet.page.send.transaction.subpage.bindNft
import com.flowfoundation.wallet.page.send.transaction.subpage.bindUserInfo
import com.flowfoundation.wallet.utils.extensions.res2color
import com.flowfoundation.wallet.utils.extensions.setVisible
import com.flowfoundation.wallet.utils.formatNum
import com.flowfoundation.wallet.utils.formatPrice
import com.flowfoundation.wallet.utils.ioScope
import com.flowfoundation.wallet.utils.uiScope

class SendProcessingPresenter(
    private val fragment: SendProcessingDialog,
    private val binding: DialogSendConfirmBinding,
    private val transactionState: TransactionState,
) : BasePresenter<SendProcessingDialogModel> {

    private val contact by lazy { transactionState.contact() }

    init {
        binding.amountWrapper.setVisible(transactionState.type == TYPE_TRANSFER_COIN)
        binding.nftWrapper.setVisible(transactionState.type == TYPE_NFT || transactionState.type == TYPE_TRANSFER_NFT)
        binding.sendButton.setVisible(false)
        binding.titleView.setText(if (transactionState.type == TYPE_TRANSFER_COIN) R.string.coin_send_processing else R.string.nft_send_processing)
        updateState(transactionState)
    }

    override fun bind(model: SendProcessingDialogModel) {
        model.userInfo?.let {
            binding.bindUserInfo(it, contact)
            when (transactionState.type) {
                TYPE_TRANSFER_COIN -> setupAmount()
                TYPE_NFT, TYPE_TRANSFER_NFT -> ioScope {
                    val nft = transactionState.nftData().nft
                    uiScope { binding.bindNft(nft) }
                }
                else -> {}
            }
        }
        model.amountConvert?.let { updateAmountConvert(it) }
        model.stateChange?.let { updateState(it) }
    }

    private fun updateState(state: TransactionState) {
        with(binding) {
            progressText.setVisible()
            var textColor = R.color.salmon_primary
            var bgColor = R.color.salmon5
            var text = R.string.pending
            var lineRes = R.drawable.ic_transaction_line
            when (state.state) {
                FlowTransactionStatus.SEALED.num -> {
                    textColor = R.color.success3
                    bgColor = R.color.success5
                    text = R.string.success
                    lineRes = R.drawable.ic_transaction_line_success
                }
                FlowTransactionStatus.UNKNOWN.num, FlowTransactionStatus.EXPIRED.num -> {
                    textColor = R.color.warning2
                    bgColor = R.color.warning5
                    text = R.string.failed
                    lineRes = R.drawable.ic_transaction_line_failed
                }
                else -> {}
            }

            with(progressText) {
                setText(text)
                backgroundTintList = ColorStateList.valueOf(bgColor.res2color())
                setTextColor(textColor.res2color())
            }
            lineView.setImageResource(lineRes)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAmount() {
        ioScope {
            val coinData = transactionState.coinData()
            val coin = FlowCoinListManager.getCoin(coinData.coinSymbol) ?: return@ioScope
            val amount = coinData.amount.formatNum()
            uiScope {
                with(binding) {
                    amountView.text = "$amount ${coin.name}"
                    Glide.with(coinIconView).load(coin.icon).into(coinIconView)
                    coinNameView.text = coin.name
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateAmountConvert(amountConvert: Float) {
        binding.amountConvertView.text = "≈ ${amountConvert.formatPrice(includeSymbol = true, includeSymbolSpace = true)}"
    }

    private fun TransactionState.progressText() = when (state) {
        FlowTransactionStatus.SEALED.num -> R.string.success
        FlowTransactionStatus.UNKNOWN.num, FlowTransactionStatus.EXPIRED.num -> R.string.failed
        else -> R.string.pending
    }
}