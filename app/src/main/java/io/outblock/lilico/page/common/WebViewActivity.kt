package io.outblock.lilico.page.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.page.browser.widgets.LilicoWebView

class WebViewActivity : BaseActivity() {

    private val url by lazy { intent.getStringExtra(URL) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        findViewById<LilicoWebView>(R.id.webview).apply {
            loadUrl(this@WebViewActivity.url!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        private const val URL = "url"
        fun launch(context: Context, url: String?) {
            url ?: return
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra(URL, url)
            })
        }
    }
}