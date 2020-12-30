package com.routesme.taxi.AdminConsolePanel.View

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi.AdminConsolePanel.Class.AdminConsoleHelper
import com.routesme.taxi.AdminConsolePanel.Class.AdminConsoleLists
import com.routesme.taxi.AdminConsolePanel.Class.MasterItemsAdapter
import com.routesme.taxi.AdminConsolePanel.Model.LogOff
import com.routesme.taxi.MVVM.Model.UnlinkResponse
import com.routesme.taxi.MVVM.View.activity.HomeActivity
import com.routesme.taxi.MVVM.View.activity.LoginActivity
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.R
import com.routesme.taxi.helper.SharedPreferencesHelper
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.admin_console_panel.*
import kotlinx.android.synthetic.main.item_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.net.HttpURLConnection
import java.sql.SQLException

class AdminConsolePanel : AppCompatActivity() {
    private var adminConsoleHelper : AdminConsoleHelper?=null
    private var sharedPreferences :SharedPreferences?=null
    private var dialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_console_panel)
        initialize()
    }
    private fun initialize(){
        adminConsoleHelper = AdminConsoleHelper(this)
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
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

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
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
            this.apply { startActivity(Intent(this, HomeActivity::class.java)); finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe()
    fun onEvent(isLogOff: LogOff){
        if(isLogOff.isLogOff){
            try {
                dialog?.show()
                val contentViewModel : ContentViewModel by viewModels()
                adminConsoleHelper?.vehicleId()?.let {vehicleId ->

                    adminConsoleHelper?.deviceId()?.let {deviceId ->

                        contentViewModel.unlinkDevice(vehicleId, deviceId,this).observe(this, Observer<UnlinkResponse> {
                            if (it.isSuccess) {
                                dialog?.hide()
                                sharedPreferences?.edit()?.clear()?.apply()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }else{

                                dialog?.hide()

                            }
                        })

                    }

                }


            } catch (e: ClassNotFoundException) {
                Log.d("TAG","ClassNotFoundException ${e.message}")
            } catch (e: SQLException) {
                Log.d("TAG","SQLException ${e.message}")
            } catch (e: Exception) {
                Log.d("TAG","Exception ${e.message}")
            }
        }

    }
}