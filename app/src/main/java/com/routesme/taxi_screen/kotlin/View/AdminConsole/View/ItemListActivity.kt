package com.routesme.taxi_screen.kotlin.View.AdminConsole.View

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.View.AdminConsole.Class.MasterItemsAdapter
import com.routesme.taxi_screen.kotlin.View.AdminConsole.Model.AdminConsoleLists
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*

class ItemListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        initialize()
    }
    private fun initialize(){
        toolbarSetUp()
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
        val fragment = ItemDetailFragment().apply { Bundle().apply { putInt(ItemDetailFragment.ARG_ITEM_ID, 0) } }
        supportFragmentManager.beginTransaction().replace(R.id.item_detail_container, fragment).commit()
    }
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply { adapter = MasterItemsAdapter(this@ItemListActivity, AdminConsoleLists.MASTER_ITEMS) }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
          Toast.makeText(this,"Back button clicked!",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}