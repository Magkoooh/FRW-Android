package io.outblock.lilico.page.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityRootAlertBinding
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.isNightMode

class RootAlertActivity : BaseActivity() {

    private lateinit var binding: ActivityRootAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UltimateBarX.with(this).fitWindow(false).colorRes(R.color.background).light(!isNightMode(this)).applyStatusBar()

        setupToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = R.string.wallet.res2String()
        binding.root.addStatusBarTopPadding()
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, RootAlertActivity::class.java))
        }
    }
}