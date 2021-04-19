package com.routesme.vehicles.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.MasterItem
import com.routesme.vehicles.helper.AdminConsoleLists
import com.routesme.vehicles.view.fragment.ItemDetailFragment
import kotlinx.android.synthetic.main.activity_admin_console_item_detail.*

class AdminConsoleItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_console_item_detail)

        toolbarSetUp()

        if (savedInstanceState == null) {
            if (intent.hasExtra(ItemDetailFragment.ARG_ITEM_ID)){
                val masterItemIndex = intent.getIntExtra(ItemDetailFragment.ARG_ITEM_ID,0)
                val masterItems = AdminConsoleLists(this@AdminConsoleItemDetailActivity).masterItems
                tabTitle_tv.text = masterItems[masterItemIndex].type.toString()
                val fragment = ItemDetailFragment(this).apply {
                    arguments = Bundle().apply { putInt(ItemDetailFragment.ARG_ITEM_ID, masterItemIndex) }
                }
                supportFragmentManager.beginTransaction().add(R.id.item_detail_container, fragment).commit()
            }
        }
    }

    private fun toolbarSetUp() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.apply { setDisplayHomeAsUpEnabled(true); setDisplayShowHomeEnabled(true); setHomeAsUpIndicator(R.drawable.ic_arrow_back) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.apply { finish() }
        }
        return super.onOptionsItemSelected(item)
    }
}
