package com.flowfoundation.wallet.page.backup.multibackup.presenter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.flowfoundation.wallet.R
import com.flowfoundation.wallet.databinding.FragmentBackupPinCodeBinding
import com.flowfoundation.wallet.page.backup.multibackup.viewmodel.BackupGoogleDriveWithPinViewModel
import com.flowfoundation.wallet.page.walletcreate.fragments.pincode.pin.KeyboardItem
import com.flowfoundation.wallet.page.walletcreate.fragments.pincode.pin.widgets.KeyboardType
import com.flowfoundation.wallet.page.walletcreate.fragments.pincode.pin.widgets.keyboardT9Normal
import com.flowfoundation.wallet.utils.extensions.res2String
import com.flowfoundation.wallet.utils.extensions.res2color
import com.flowfoundation.wallet.utils.getPinCode
import com.flowfoundation.wallet.utils.savePinCode


class BackupPinCodePresenter(
    private val fragment: Fragment,
    private val binding: FragmentBackupPinCodeBinding
) {

    private val withPinViewModel by lazy {
        ViewModelProvider(fragment.requireParentFragment())[BackupGoogleDriveWithPinViewModel::class.java]
    }

    private var isVerifyPinCode = false

    init {
        isVerifyPinCode = getPinCode().isNotBlank()

        with(binding) {
            setPinText(false)
            if (isVerifyPinCode.not()) {
                pinInput.setCheckCallback { passed ->
                    savePinCode(pinInput.keys().joinToString("") { "${it.number}" })
                    if (passed) {
                        withPinViewModel.startBackup()
                    }
                }
            }
            pinKeyboard.setOnKeyboardActionListener { onKeyPressed(it) }
            pinKeyboard.bindKeys(keyboardT9Normal, KeyboardType.T9)
            pinKeyboard.show()
        }
    }

    private fun FragmentBackupPinCodeBinding.onKeyPressed(key: KeyboardItem) {
        if (key.type == KeyboardItem.TYPE_DELETE_KEY || key.type == KeyboardItem.TYPE_CLEAR_KEY) {
            pinInput.delete()
        } else {
            pinInput.input(key)
            if (isVerifyPinCode) {
                if (pinInput.keys().size == 6) {
                    val pinCode = pinInput.keys().joinToString("") { "${it.number}" }
                    if (getPinCode() == pinCode) {
                        withPinViewModel.startBackup()
                    } else {
                        binding.pinInput.shakeAndClear(keysCLear = true)
                    }
                }
            } else {
                if (pinInput.keys().size == 6 && !pinInput.isChecking()) {
                    pinInput.checking()
                    setPinText(true)
                }
            }
        }
    }

    private fun setPinText(isCheckMode: Boolean) {
        with(binding) {
            pinTitle.text = SpannableString(
                if (isVerifyPinCode) {
                    R.string.verify_pin.res2String()
                } else if (isCheckMode) {
                    R.string.check_pin.res2String()
                } else {
                    R.string.create_pin.res2String()
                }
            ).apply {
                val protection = R.string.pin.res2String()
                val index = indexOf(protection)
                setSpan(
                    ForegroundColorSpan(R.color.accent_green.res2color()),
                    index,
                    index + protection.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            pinTip.text = SpannableString(
                if (isVerifyPinCode) {
                    R.string.verify_pin_tip.res2String()
                } else if (isCheckMode) {
                    R.string.check_pin_tip.res2String()
                } else {
                    R.string.backup_pin_tip.res2String()
                }
            ).apply {
                val protection = R.string.pin.res2String()
                val index = indexOf(protection)
                setSpan(
                    ForegroundColorSpan(R.color.accent_green.res2color()),
                    index,
                    index + protection.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}