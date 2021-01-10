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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.AdminConsolePanel.Class.AdminConsoleHelper
import com.routesme.taxi.AdminConsolePanel.Class.AdminConsoleLists
import com.routesme.taxi.AdminConsolePanel.Class.MasterItemsAdapter
import com.routesme.taxi.AdminConsolePanel.Model.LogOff
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.LocationTrackingService.Class.AdvertisementDataLayer
import com.routesme.taxi.LocationTrackingService.Model.AdvertisementTracking
import com.routesme.taxi.MVVM.Model.ReportResponse
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
import org.json.JSONObject
import java.net.HttpURLConnection
import java.sql.SQLException

class AdminConsolePanel : AppCompatActivity() {
    private var adminConsoleHelper : AdminConsoleHelper?=null
    private var sharedPreferences :SharedPreferences?=null
    private var dialog: AlertDialog? = null
    val contentViewModel : ContentViewModel by viewModels()
    private val advertisementTracking = AdvertisementDataLayer()
    private var getList:List<AdvertisementTracking>?=null
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
            this.apply { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe()
    fun onEvent(isLogOff: LogOff){
        if(isLogOff.isLogOff){
            try {
                dialog?.show()
                adminConsoleHelper?.deviceId()?.let {deviceID ->

                    adminConsoleHelper?.vehicleId()?.let {vehicleId ->
                        //Log.d("Report","${getJsonArray()}")
                        contentViewModel.postReport(this,getJsonArray(),deviceID).observe(this , Observer<ReportResponse> {

                            if(it.isSuccess){

                                val records_deleted = advertisementTracking.deleteAllData()
                                //Log.d("Report","${records_deleted}")
                                unlinkDeviceFromServer(deviceID,vehicleId)

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

    private fun unlinkDeviceFromServer(deviceId:String,vehicleId:String){

                contentViewModel.unlinkDevice(vehicleId, deviceId,this).observe(this, Observer<UnlinkResponse> {
                    if (it.isSuccess) {
                        dialog?.hide()

                        adminConsoleHelper?.logOff()

                    }else{
                        dialog?.hide()
                    }
                })
    }

    private fun getJsonArray(): JSONObject {
        getList =  advertisementTracking.getAllList()
        val jsonObject = JSONObject()
        val jsonArray = JsonArray()
        getList?.forEach {

            val jsonObject = JsonObject().apply{
                addProperty("date",it.date)
                addProperty("advertisementId",it.advertisementId)
                add("slots",getJsonArrayOfSlot(it.morning,it.noon,it.evening,it.night))
            }
            jsonArray.add(jsonObject)
        }

        return jsonObject.put("analytics",jsonArray)

    }
    private fun getJsonArrayOfSlot(morning:Int,noon:Int,evening:Int,night:Int):JsonArray{
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        if(morning != 0){
            jsonObject.addProperty("mo",morning)
        }
        if(noon != 0){
            jsonObject.addProperty("no",noon)
        }
        if(evening != 0){
            jsonObject.addProperty("ev",evening)
        }
        if(night != 0){
            jsonObject.addProperty("ni",night)
        }
        jsonArray.add(jsonObject)

        return jsonArray

    }
}