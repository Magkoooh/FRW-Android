package io.outblock.lilico.widgets.webview.fcl.dialog.authz

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogLinkAccountBinding
import io.outblock.lilico.manager.account.AccountManager
import io.outblock.lilico.manager.transaction.OnTransactionStateChange
import io.outblock.lilico.manager.transaction.TransactionStateManager
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.page.browser.loadFavicon
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel

class FclAuthzLinkAccountView : FrameLayout, OnTransactionStateChange {
    private val binding: DialogLinkAccountBinding

    private lateinit var data: FclDialogModel

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_link_account, this, false)
        addView(view)
        binding = DialogLinkAccountBinding.bind(view)
        TransactionStateManager.addOnTransactionStateChange(this)
    }

    fun setup(data: FclDialogModel, approveCallback: ((isApprove: Boolean) -> Unit)) {
        this.data = data
        binding.setup(data)

        binding.root.requestFocus()
        binding.startButton.setOnProcessing {
            showLinkingUI()
            approveCallback.invoke(true)
        }
        showDefaultUI()
    }

    override fun onTransactionStateChange() {
        val transactionState = TransactionStateManager.getTransactionStateList().lastOrNull() ?: return
        if (transactionState.isProcessing()) {
            // ignore
        } else if (transactionState.isFailed()) {
            showFailedUI()
        } else if (transactionState.isSuccess()) {
            showSuccessUI()
        }
    }

    private fun showFailedUI() {
        with(binding) {
            titleView.setText(R.string.link_fail)
            line.setBackgroundResource(R.drawable.bg_link_account_error_line)
            startButton.setVisible(false)
            tryAgainButton.setVisible(true)
            errorXIcon.setVisible(true)
            linkTipsWrapper.setVisible(false)
            point1.setVisible(false)
            point2.setVisible(false)
            tryAgainButton.setOnClickListener { FclAuthzDialog.dismiss(true) }
        }
    }

    private fun showSuccessUI() {
        with(binding) {
            descView.setVisible(true)
            descView.setText(R.string.link_account_success_desc)
            titleView.setText(R.string.successful)
            defaultLayout.setVisible(false)
            linkTipsWrapper.setVisible(false)
            successLayout.setVisible(true)
            successStartButton.setOnClickListener { FclAuthzDialog.dismiss(true) }
            WalletManager.refreshChildAccount()
        }
    }

    private fun showDefaultUI() {
        with(binding) {
            descView.setVisible(false)
            titleView.setText(R.string.link_account)
            line.setBackgroundResource(R.drawable.bg_link_account_line)
            defaultLayout.setVisible()
            startButton.setVisible()
            point1.setVisible()
            point2.setVisible()
            linkTipsWrapper.setVisible()
            successLayout.setVisible(false)
            tryAgainButton.setVisible(false)
            errorXIcon.setVisible(false)
            with(startButton.layoutParams as MarginLayoutParams) {
                topMargin = 18.dp2px().toInt()
                startButton.layoutParams = this
            }
        }
    }

    private fun showLinkingUI() {
        showDefaultUI()
        with(binding) {
            titleView.setText(R.string.account_linking)
            descView.setVisible(false)
            point1.setVisible(false, invisible = true)
            point2.setVisible(false, invisible = true)
            linkTipsWrapper.setVisible(false)
            with(startButton.layoutParams as MarginLayoutParams) {
                topMargin = 70.dp2px().toInt()
                startButton.layoutParams = this
            }
        }
    }
}

private fun DialogLinkAccountBinding.setup(fcl: FclDialogModel) {
    dappIcon.loadFavicon(fcl.logo ?: fcl.url?.toFavIcon())
    dappName.text = fcl.title
    ioScope {
        val userinfo = AccountManager.userInfo()
        uiScope {
            walletIcon.loadAvatar(userinfo?.avatar.orEmpty())
            walletName.text = userinfo?.nickname
        }
    }
}