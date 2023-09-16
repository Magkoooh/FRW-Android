package io.outblock.lilico.page.collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityCollectionBinding
import io.outblock.lilico.page.collection.model.CollectionContentModel
import io.outblock.lilico.page.collection.presenter.CollectionContentPresenter
import io.outblock.lilico.utils.isNightMode

class CollectionActivity : BaseActivity() {

    private lateinit var presenter: CollectionContentPresenter
    private lateinit var viewModel: CollectionViewModel
    private lateinit var binding: ActivityCollectionBinding

    private val contractName by lazy { intent.getStringExtra(EXTRA_CONTRACT_NAME).orEmpty() }
    private val collectionLogo by lazy { intent.getStringExtra(EXTRA_COLLECTION_LOGO).orEmpty() }
    private val collectionName by lazy { intent.getStringExtra(EXTRA_COLLECTION_NAME).orEmpty() }
    private val collectionSize by lazy { intent.getIntExtra(EXTRA_COLLECTION_SIZE, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UltimateBarX.with(this).fitWindow(false).light(!isNightMode(this)).applyStatusBar()

        presenter = CollectionContentPresenter(this, binding).apply {
            bindInfo(collectionLogo, collectionName, collectionSize)
        }
        viewModel = ViewModelProvider(this)[CollectionViewModel::class.java].apply {
            dataLiveData.observe(this@CollectionActivity) {
                presenter.bind(
                    CollectionContentModel(
                        data = it
                    )
                )
            }
            collectionLiveData.observe(this@CollectionActivity) {
                presenter.bind(
                    CollectionContentModel(collection = it)
                )
            }
            load(contractName)
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
        private const val EXTRA_CONTRACT_NAME = "extra_address"
        private const val EXTRA_COLLECTION_LOGO = "extra_collection_logo"
        private const val EXTRA_COLLECTION_NAME = "extra_collection_name"
        private const val EXTRA_COLLECTION_SIZE = "extra_collection_size"

        fun launch(
            context: Context,
            contractName: String,
            logo: String? = "",
            name: String? = "",
            size: Int? = 0
        ) {
            context.startActivity(Intent(context, CollectionActivity::class.java).apply {
                putExtra(EXTRA_CONTRACT_NAME, contractName)
                putExtra(EXTRA_COLLECTION_LOGO, logo)
                putExtra(EXTRA_COLLECTION_NAME, name)
                putExtra(EXTRA_COLLECTION_SIZE, size)
            })
        }
    }
}