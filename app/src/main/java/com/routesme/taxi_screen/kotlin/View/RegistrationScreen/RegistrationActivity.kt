package com.routesme.taxi_screen.kotlin.View.RegistrationScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.routesme.taxi_screen.java.Model.TabletInfoViewModel
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.DateOperations
import com.routesme.taxi_screen.kotlin.Class.Operations
import com.routesme.taxi_screen.kotlin.Class.SharedPreference
import com.routesme.taxi_screen.kotlin.Model.*
import com.routesme.taxi_screen.kotlin.View.LoginScreens.LoginActivity
import com.routesme.taxi_screen.kotlin.View.ModelPresenter
import com.routesme.taxi_screen.kotlin.ViewModel.RoutesViewModel
import com.routesme.taxiscreen.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_registration.*
import java.io.IOException
import java.util.*

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {

    private val app = App.instance
    private var registrationCredentials = RegistrationCredentials()
    private val operations = Operations.instance
    private val READ_PHONE_STATE_REQUEST_CODE = 101
    //private val listTypeKey = getString(R.string.list_type_key)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var telephonyManager: TelephonyManager
    private var dialog: AlertDialog? = null
    private var institutionId = -999
    private var showRationale = true
    private  var getDeviceInfo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initialize()
    }

    @SuppressLint("CommitPrefEdits")
    private fun initialize() {
        sharedPreferences = getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences.edit()
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        // requestRuntimePermissions();
        toolbarSetUp()
        initializeViews()
        getTabletInfo()
    }

    private fun requestRuntimePermissions() {
        val permissionsList = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
        if (!hasPermissions(*permissionsList)) {
            ActivityCompat.requestPermissions(this, permissionsList, 1)
        }
    }
    private fun hasPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun toolbarSetUp() {
        setSupportActionBar(MyToolBar)
        supportActionBar?.apply {
            title = welcomeString()
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_grey)
        }
    }
    @SuppressLint("DefaultLocale")
    private fun welcomeString(): String? {
        val username = app.signInCredentials?.Username
        return "Welcome,  " + if (!username.isNullOrEmpty())username.split(" ").joinToString(" ") { it.capitalize() }.trimEnd() else ""
    }
    private fun initializeViews() {
        taxiOffice_tv.setOnClickListener(this)
        taxiPlateNumber_tv.setOnClickListener(this)
        deviceSerialNumber_tv.setOnClickListener(this)
        SimCardNumber_tv.setOnClickListener(this)
        register_btn.setOnClickListener(this)
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
    }

    override fun onRestart() {
        taxiOffice_tv.isEnabled = true
        taxiPlateNumber_tv.isEnabled = true
        operations.enableNextButton(register_btn, true)
        getTabletInfo()
        institutionId = app.institutionId
        //deviceInfo.setTaxiOfficeId(app.getTaxiOfficeId());
        //deviceInfo.setTaxiPlateNumber(app.getTaxiPlateNumber());
        registrationCredentials.VehicleId = app.vehicleId
        taxiOffice_tv.text = showTaxiOfficeName(app.institutionName)
        taxiPlateNumber_tv.text = showTaxiPlateNumber(app.taxiPlateNumber)
        super.onRestart()
    }

    private fun showTaxiOfficeName(taxiOfficeName: String?) = if (!taxiOfficeName.isNullOrEmpty()) taxiOfficeName else null

    private fun showTaxiPlateNumber(taxiPlateNumber: String?) = if (!taxiPlateNumber.isNullOrEmpty()) taxiPlateNumber else null

    @SuppressLint("HardwareIds")
    private fun getTabletInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE)
            return
        } else {
            registrationCredentials.apply {
                DeviceSerialNumber = telephonyManager.imei
                SimSerialNumber = telephonyManager.simSerialNumber
                deviceSerialNumber_tv.text = DeviceSerialNumber
                SimCardNumber_tv.text = SimSerialNumber
            }
            showTabletInfoError(false)
        }
    }

    @SuppressLint("HardwareIds")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
           READ_PHONE_STATE_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE)
                    return
                }
                getTabletInfo()
                //showTabletInfoError(false);
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                    showTabletInfoError(true)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.deviceSerialNumber_tv, R.id.SimCardNumber_tv -> clickOnGetDeviceInfo()
            R.id.taxiOffice_tv -> openInstitutionsList()
            R.id.taxiPlateNumber_tv -> openVehiclesList()
            R.id.register_btn ->   registration() //register()
        }
    }

    private fun openInstitutionsList() {
        taxiOffice_tv.isEnabled = false
        openDataList(VehicleInformationListType.Institution)
        showInputError(false, 0)
    }

    private fun openVehiclesList() {
        if (institutionId >= 0) {
            taxiPlateNumber_tv.isEnabled = false
            openDataList(VehicleInformationListType.Vehicle)
        } else {
            showInputError(true, 1)
            return
        }
        showInputError(false, 0)
    }

    private fun openDataList(listType: VehicleInformationListType) {
        val listTypeKey = getString(R.string.list_type_key)
        startActivity(Intent(this, VehicleInformationActivity::class.java).putExtra(listTypeKey, listType))
    }

    private fun register() {
        if (token() != null && allDataExist()) {
            operations.enableNextButton(register_btn, false)
            dialog!!.show()
            val tabletInfoViewModel = ViewModelProviders.of((this as FragmentActivity)).get(TabletInfoViewModel::class.java)
            tabletInfoViewModel.getTabletInfo(this, token(), registrationCredentials, dialog, register_btn).observe((this as LifecycleOwner), Observer { response ->
                saveTabletInfoIntoSharedPreferences(response)
                openModelPresenterScreen()
            })
        }
    }

    private fun registration(){
        if (token() != null && allDataExist()) {
            operations.enableNextButton(register_btn, false)
            dialog?.show()
            val model: RoutesViewModel by viewModels()
            model.getRegistrationResponse(registrationCredentials, this).observe(this, Observer<ApiResponse> {
                dialog?.dismiss()
                operations.enableNextButton(register_btn, true)
                if (it != null) {
                    val throwable = it.throwable
                    val registrationSuccessResponse = it.registrationSuccessResponse
                    val badRequestResponse = it.badRequestResponse
                    val errorResponse = it.errorResponse
                    if (throwable != null) {
                        if (throwable is IOException) {
                            Toast.makeText(this, "Failure: Network Issue !", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failure: Conversion Issue !", Toast.LENGTH_SHORT).show()
                        }

                    } else if (registrationSuccessResponse != null) {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        saveTabletInfoIntoSharedPreferences(registrationSuccessResponse)
                        openModelPresenterScreen()
                    } else if (badRequestResponse != null) {
                        if (!badRequestResponse.errors.isNullOrEmpty()) {
                            for (error in badRequestResponse.errors) {
                                Toast.makeText(this, "Error.. Code: ${error.code}, Detail: ${error.detail}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if (errorResponse != null) {
                        Toast.makeText(this, "Error.. Code: ${errorResponse.code}, Message: ${errorResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Api Response : Null", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this, "Complete required data, please!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun token(): String? {
        val savedToken = sharedPreferences.getString(SharedPreference.token, null)
        return if (!savedToken.isNullOrEmpty()) "Bearer $savedToken" else null
    }

    private fun allDataExist() = institutionIdExist() && vehicleIdExist() && tabletInformationExist()

    private fun institutionIdExist(): Boolean {
        institutionId = app.institutionId
        return if (institutionId < 0) {
            showInputError(true, 1)
            false
        } else {
            true
        }
    }

    private fun vehicleIdExist(): Boolean {
        val vehicleId = app.vehicleId
        return if (vehicleId < 0) {
            showInputError(true, 2)
            false
        } else {
            true
        }
    }

    private fun tabletInformationExist(): Boolean {
        val tabletSerialNumber = registrationCredentials.DeviceSerialNumber
        val simCardNumber = registrationCredentials.SimSerialNumber
        return if (tabletSerialNumber.isNullOrEmpty() || simCardNumber.isNullOrEmpty()) {
            showTabletInfoError(true)
            false
        } else {
            true
        }
    }

    private fun saveTabletInfoIntoSharedPreferences(registrationSuccessResponse: RegistrationSuccessResponse) {
        editor.apply {
            putString(SharedPreference.technician_username, app.signInCredentials?.Username)
            putString(SharedPreference.registration_date, DateOperations().registrationDate(Date()))
            putInt(SharedPreference.institution_id, app.institutionId)
            putString(SharedPreference.institution_name, app.institutionName)
            putInt(SharedPreference.vehicle_id, app.vehicleId)
            putString(SharedPreference.vehicle_plate_number, app.taxiPlateNumber)
            putInt(SharedPreference.device_id, registrationSuccessResponse.deviceId)
            putString(SharedPreference.device_serial_number, registrationCredentials.DeviceSerialNumber)
            putString(SharedPreference.sim_serial_number, registrationCredentials.SimSerialNumber)
        }.apply()
    }

    private fun openModelPresenterScreen() {
        val authorization = Authorization(true, 200)
        val modelPresenter = Intent(this, ModelPresenter::class.java)
        modelPresenter.putExtra("authorization", authorization)
        startActivity(modelPresenter)
        finish()
    }

    private fun clickOnGetDeviceInfo() {
        if (!showRationale && !getDeviceInfo) {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.phone_permission_required))
                    .setMessage(getString(R.string.enable_phone_permission_from_app_settings_is_required_to_get_device_information))
                    .setPositiveButton(getString(R.string.open_settings)) { dialog, which ->
                        showTabletInfoError(false)
                        startActivity(Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", packageName, null)))
                    }
                    .setNegativeButton(getString(R.string.later)) { dialog, which -> showTabletInfoError(true) }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show()
        } else if (!getDeviceInfo) {
            getTabletInfo()
        }
    }


    private fun showInputError(show: Boolean, requireField: Int) {
        if (show) {
            when (requireField) {
                1 -> {
                    taxiOffice_error_tv.visibility = View.VISIBLE
                    taxiPlateNumber_error_tv.visibility = View.INVISIBLE
                }
                2 -> {
                    taxiOffice_error_tv.visibility = View.INVISIBLE
                    taxiPlateNumber_error_tv.visibility = View.VISIBLE
                }
            }
        } else {
            taxiOffice_error_tv.visibility = View.INVISIBLE
            taxiPlateNumber_error_tv.visibility = View.INVISIBLE
        }
    }

    private fun showTabletInfoError(show: Boolean) {
        if (show) {
            deviceSerialNumber_tv.apply {
                error = getString(R.string.click_here_to_get_serial_number)
                requestFocus()
            }
            SimCardNumber_tv.apply {
                error = getString(R.string.click_here_to_get_sim_card_number)
                requestFocus()
            }
            getDeviceInfo = false
            return
        } else {
            deviceSerialNumber_tv.apply {
                error = null
                clearFocus()
                isClickable = false
            }
            SimCardNumber_tv.apply {
                error = null
                clearFocus()
                isClickable = false
            }
            getDeviceInfo = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            app.isNewLogin = true
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}