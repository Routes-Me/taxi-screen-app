package com.routesme.taxi_screen.kotlin.AdminConsolePanel.View

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.AdminConsolePanel.Class.MasterItemsAdapter
import com.routesme.taxi_screen.kotlin.AdminConsolePanel.Class.AdminConsoleLists
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Activity.HomeScreen
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.admin_console_panel.*
import kotlinx.android.synthetic.main.item_list.*

class AdminConsolePanel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_console_panel)

        initialize()
    }
    private fun initialize(){
        toolbarSetUp()
    }
    override fun onResume() {
        super.onResume()
        setUpItemDetailFragment()
        setupRecyclerView(masterRecyclerView)
    }
    private fun toolbarSetUp(){
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.apply { setDisplayHomeAsUpEnabled(true); setDisplayShowHomeEnabled(true); setHomeAsUpIndicator(R.drawable.ic_arrow_back) }
        }
    }
    private fun setUpItemDetailFragment(){
        val fragment = ItemDetailFragment(this).apply { Bundle().apply { putInt(ItemDetailFragment.ARG_ITEM_ID, 0) } }
        supportFragmentManager.beginTransaction().replace(R.id.item_detail_container, fragment).commit()
    }
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply { adapter = MasterItemsAdapter(this@AdminConsolePanel, AdminConsoleLists(this@AdminConsolePanel).masterItems) }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.apply { startActivity(Intent(this,HomeScreen::class.java)); finish() }
        }
        return super.onOptionsItemSelected(item)
    }

}