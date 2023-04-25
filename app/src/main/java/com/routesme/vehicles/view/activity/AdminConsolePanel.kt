package com.routesme.vehicles.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.helper.AdminConsoleHelper
import com.routesme.vehicles.helper.AdminConsoleLists
import com.routesme.vehicles.helper.Operations
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.room.AdvertisementDatabase
import com.routesme.vehicles.room.ResponseBody
import com.routesme.vehicles.room.entity.AdvertisementTracking
import com.routesme.vehicles.room.factory.ViewModelFactory
import com.routesme.vehicles.room.helper.DatabaseHelperImpl
import com.routesme.vehicles.room.viewmodel.RoomDBViewModel
import com.routesme.vehicles.uplevels.VehicleReferral
import com.routesme.vehicles.view.adapter.MasterItemsAdapter
import com.routesme.vehicles.view.fragment.ItemDetailFragment
import com.routesme.vehicles.viewmodel.BusActivationViewModel
import com.routesme.vehicles.viewmodel.ContentViewModel
import com.routesme.vehicles.viewmodel.VehicleReferralInformationViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.admin_console_panel.*
import kotlinx.android.synthetic.main.item_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.sql.SQLException

class AdminConsolePanel : AppCompatActivity() {
    private val operations = Operations.instance
    private var twoPane: Boolean = false
    private val SEND_ANALYTICS_REPORT = "SEND_ANALYTICS_REPORT"
    private var adminConsoleHelper: AdminConsoleHelper? = null
    private var sharedPreferences: SharedPreferences? = null
    private  var vehicleId:String?=null
    private var dialog: AlertDialog? = null
    val contentViewModel: ContentViewModel by viewModels()
    private lateinit var viewModel: RoomDBViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_console_panel)
        viewModel = ViewModelProvider(this, ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(applicationContext)))).get(RoomDBViewModel::class.java)
        initialize()
    }

    private fun initialize() {
        adminConsoleHelper = AdminConsoleHelper(this)
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        vehicleId = sharedPreferences?.getString(SharedPreferencesHelper.vehicle_id, null)
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
        toolbarSetUp()

        twoPane =  (item_detail_container != null)
        if (twoPane) setUpItemDetailFragment()
        setupRecyclerView(masterRecyclerView)
    }
    private fun toolbarSetUp() {
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
        dialog?.dismiss()
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun setUpItemDetailFragment() {
        val fragment = ItemDetailFragment(this).apply { Bundle().apply { putInt(ItemDetailFragment.ARG_ITEM_ID, 0) } }
        supportFragmentManager.beginTransaction().replace(R.id.item_detail_container, fragment).commit()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply { adapter = MasterItemsAdapter(this@AdminConsolePanel, AdminConsoleLists(this@AdminConsolePanel).masterItems, twoPane) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.apply { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(isLogOff: LogOff) {
        if (isLogOff.isLogOff) {
            try {
                dialog?.show()
                adminConsoleHelper?.deviceId()?.let { deviceID ->

                    adminConsoleHelper?.vehicleId()?.let { vehicleId ->

                        observeAnalytics(deviceID, vehicleId)

                    }
                }
            } catch (e: ClassNotFoundException) {
                Log.d("TAG", "ClassNotFoundException ${e.message}")
            } catch (e: SQLException) {
                Log.d("TAG", "SQLException ${e.message}")
            } catch (e: Exception) {
                Log.d("TAG", "Exception ${e.message}")
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(updateReferralInfo: UpdateReferralInfo) {
        if (updateReferralInfo.isUpdateReferralInfo) {
        vehicleId?.let { vehicleId ->
            dialog?.show()
            val vehicleReferralInformationViewModel: VehicleReferralInformationViewModel by viewModels()
            vehicleReferralInformationViewModel.getVehicleReferralInformation(vehicleId, this).observe(this, Observer<VehicleReferralInformationModel.VehicleReferralResponse> {
                dialog?.dismiss()
                if (it != null) {
                    if (it.isSuccess) {
                        val list = it.data ?: run {
                            operations.displayAlertDialog(this, getString(R.string.vehicle_referral_information_error_title), getString(R.string.no_data_found))
                            return@Observer
                        }

                        it.data?.let { referralInfo ->
                            saveReferralInfo(referralInfo)
                            recreate()
                        }
                    } else {
                        if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                            it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                        } else if (it.mThrowable != null) {
                            if (it.mThrowable is IOException) {
                                operations.displayAlertDialog(this, getString(R.string.vehicle_referral_information_error_title), getString(R.string.network_Issue))
                            } else {
                                operations.displayAlertDialog(this, getString(R.string.vehicle_referral_information_error_title), getString(R.string.conversion_Issue))
                            }
                        }
                    }
                } else {
                    operations.displayAlertDialog(this, getString(R.string.vehicle_referral_information_error_title), getString(R.string.unknown_error))
                }
            })

        }
        }
    }

    private fun saveReferralInfo(referralInfo: VehicleReferralInformationModel.VehicleData) {
        VehicleReferral().apply {
            referralCode = referralInfo.referralCode
            referralUrl = referralInfo.referralURL
        }
    }

    private fun unlinkDeviceFromServer(deviceId: String, vehicleId: String) {
        contentViewModel.unlinkDevice(vehicleId, deviceId, this).observe(this, Observer<UnlinkResponse> {
            if (it.isSuccess) {
                if (BuildConfig.FLAVOR == "bus") deactivateBus(vehicleId)
                else {
                    dialog?.hide()
                    WorkManager.getInstance().cancelAllWorkByTag(SEND_ANALYTICS_REPORT)
                    adminConsoleHelper?.logOff()
                }
            } else {
                dialog?.hide()
            }
        })
    }

    private fun deactivateBus(vehicleId: String){
        Log.d("BusProcessTesting", "Deactivate bus vehicleId: $vehicleId")
        val busActivationCredentials = BusActivationCredentials(SecondID = vehicleId)
        val busActivationViewModel: BusActivationViewModel by viewModels()
        busActivationViewModel.deactivate(busActivationCredentials, this).observe(this, Observer<DeactivateBusResponse> {
            dialog?.hide()
            if (it != null) {
                if (it.isSuccess) {
                    if (it.isBusDeactivatedSuccessfully == true) {
                        adminConsoleHelper?.logOff()
                    }else{
                        operations.displayAlertDialog(this, getString(R.string.logout_error_title), "${it.deactivateBusDescription?.message}")
                    }
                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            operations.displayAlertDialog(this, getString(R.string.logout_error_title), getString(R.string.network_Issue))
                        } else {
                            operations.displayAlertDialog(this, getString(R.string.logout_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                operations.displayAlertDialog(this, getString(R.string.logout_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun displayErrors(errors: List<Error>) {
        for (error in errors) { operations.displayAlertDialog(this, getString(R.string.action_error_title), "Error message: ${error.detail}") }
    }

    private fun observeAnalytics(deviceId: String, vehicleId: String) {

        viewModel.getAllList().observe(this, Observer {

            when (it.status) {

                ResponseBody.Status.SUCCESS -> {
                    if(!it.data.isNullOrEmpty()){
                        contentViewModel.postReport(this, getJsonArray(it.data), deviceId).observe(this, Observer<ReportResponse> {
                            if (it.isSuccess) {
                                observeDeleteTable(deviceId, vehicleId)
                            } else {
                                dialog?.hide()
                            }
                        })
                    }else{
                        observeDeleteTable(deviceId, vehicleId)
                    }
                }
                ResponseBody.Status.ERROR -> {
                    dialog?.hide()
                }
            }
        })
    }

    private fun observeDeleteTable(deviceId: String, vehicleId: String) {

        viewModel.deleteAllData().observe(this, Observer {
            when (it.status) {
                ResponseBody.Status.SUCCESS -> {
                    unlinkDeviceFromServer(deviceId, vehicleId)
                }
                ResponseBody.Status.ERROR -> {
                    dialog?.hide()
                }
            }
        })
    }

    private fun getJsonArray(list: List<AdvertisementTracking>): JsonArray {
        val jsonArray = JsonArray()
        list.forEach {

            val jsonObject = JsonObject().apply {
                addProperty("date", it.date / 1000)
                addProperty("advertisementId", it.advertisementId)
                addProperty("mediaType", it.media_type)
                add("slots", getJsonArrayOfSlot(it.morning, it.noon, it.evening, it.night))
            }
            jsonArray.add(jsonObject)
        }

        return jsonArray

    }

    private fun getJsonArrayOfSlot(morning: Int, noon: Int, evening: Int, night: Int): JsonArray {
        val jsonArray = JsonArray()
        if (morning != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "mo")
            jsonObject.addProperty("value", morning)
            jsonArray.add(jsonObject)
        }
        if (noon != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "no")
            jsonObject.addProperty("value", noon)
            jsonArray.add(jsonObject)
        }
        if (evening != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "ev")
            jsonObject.addProperty("value", evening)
            jsonArray.add(jsonObject)
        }
        if (night != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "ni")
            jsonObject.addProperty("value", night)
            jsonArray.add(jsonObject)
        }
        return jsonArray

    }
}

