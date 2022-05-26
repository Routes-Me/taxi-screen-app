package com.routesme.vehicles.view.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.helper.AdminConsoleHelper
import com.routesme.vehicles.helper.AdminConsoleLists
import com.routesme.vehicles.helper.Operations
import com.routesme.vehicles.view.fragment.ItemDetailFragment
import com.routesme.vehicles.viewmodel.BusActivationViewModel
import com.routesme.vehicles.viewmodel.ContentViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_admin_console_item_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.sql.SQLException

class AdminConsoleItemDetailActivity : AppCompatActivity() {

    private val operations = Operations.instance
    private var adminConsoleHelper: AdminConsoleHelper? = null
    private var dialog: AlertDialog? = null
    private val contentViewModel: ContentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_console_item_detail)

        adminConsoleHelper = AdminConsoleHelper(this)
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()

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

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        dialog?.dismiss()
        EventBus.getDefault().unregister(this)
        super.onStop()
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(isLogOff: LogOff) {
        // Log.d("LogoutFunctionTest","AdminConsolePanel.. logout called")
        if (isLogOff.isLogOff && BuildConfig.FLAVOR == "bus") {
            try {
                dialog?.show()
                adminConsoleHelper?.deviceId()?.let { deviceID -> adminConsoleHelper?.vehicleId()?.let { vehicleId -> unlinkDeviceFromServer(deviceID, vehicleId) } }
            } catch (e: ClassNotFoundException) {
                Log.d("TAG", "ClassNotFoundException ${e.message}")
            } catch (e: SQLException) {
                Log.d("TAG", "SQLException ${e.message}")
            } catch (e: Exception) {
                Log.d("TAG", "Exception ${e.message}")
            }
        }
    }


    private fun unlinkDeviceFromServer(deviceId: String, vehicleId: String) {
        contentViewModel.unlinkDevice(vehicleId, deviceId, this).observe(this, Observer<UnlinkResponse> {
            if (it.isSuccess) {
                deactivateBus(vehicleId)
            } else {
                dialog?.hide()
                Toast.makeText(this, "Unlink failed!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deactivateBus(vehicleId: String){
        //Log.d("BusProcessTesting", "Deactivate bus vehicleId: $vehicleId")
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
        for (error in errors) { operations.displayAlertDialog(this, getString(R.string.logout_error_title), "Error message: ${error.detail}") }
    }
}