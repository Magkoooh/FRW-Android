package io.outblock.lilico.page.dialog.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogBackupTipsBinding
import io.outblock.lilico.page.walletcreate.WALLET_CREATE_STEP_WARNING
import io.outblock.lilico.page.walletcreate.WalletCreateActivity

class BackupTipsDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBackupTipsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogBackupTipsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.requestFocus()

        binding.closeButton.setOnClickListener { dismiss() }
        binding.skipButton.setOnClickListener { dismiss() }
        binding.startButton.setOnClickListener {
            WalletCreateActivity.launch(requireContext(), WALLET_CREATE_STEP_WARNING)
            dismiss()
        }
    }

    companion object {

        fun show(fragmentManager: FragmentManager) {
            BackupTipsDialog().showNow(fragmentManager, "")
        }
    }
}