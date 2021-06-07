package com.routesme.vehicles.view.activity

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
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.routesme.vehicles.App
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.data.model.VehicleInformationModel.VehicleInformationListType
import com.routesme.vehicles.helper.DateHelper
import com.routesme.vehicles.helper.DateOperations
import com.routesme.vehicles.helper.Operations
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.uplevels.Account
import com.routesme.vehicles.uplevels.CarrierInformation
import com.routesme.vehicles.viewmodel.CarrierInformationViewModel
import com.routesme.vehicles.viewmodel.RegistrationViewModel
import com.routesme.vehicles.viewmodel.TerminalViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_registration.*
import java.io.IOException
import java.util.*

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {
    private val AUTHORIZATION_KAY = "authorization"
    private val app = App.instance
    private var registerCredentials = RegistrationCredentials()
    private val operations = Operations.instance
    private val READ_PHONE_STATE_REQUEST_CODE = 101
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var telephonyManager: TelephonyManager
    private var dialog: AlertDialog? = null
    private var institutionId: String? = null
    private var fcm_token: String? = null
    private var showRationale = true
    private var getDeviceInfo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initialize()
    }

    @SuppressLint("CommitPrefEdits")
    private fun initialize() {
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        //requestRuntimePermissions();
        toolbarSetUp()
        initializeViews()
        getTabletInfo()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                //Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            fcm_token = task.result
            Log.d("FCM_TOKEN",fcm_token)
        })
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
        val username = app.signInCredentials?.userName
        return "Welcome,  " + if (!username.isNullOrEmpty()) username.split(" ").joinToString(" ") { it.capitalize() }.trimEnd() else ""
    }

    private fun initializeViews() {
        taxiOffice_tv.setOnClickListener(this)
        taxiPlateNumber_tv.setOnClickListener(this)
        deviceId_tv.setOnClickListener(this)
        SimSerialNumber_tv.setOnClickListener(this)
        register_btn.setOnClickListener(this)
        operations.enableNextButton(register_btn, true)
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
    }

    override fun onRestart() {
        taxiOffice_tv.isEnabled = true
        taxiPlateNumber_tv.isEnabled = true
        operations.enableNextButton(register_btn, true)
        getTabletInfo()
        institutionId = app.institutionId
        registerCredentials.VehicleId = app.vehicleId
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
            registerCredentials.apply {
                serialNumber = telephonyManager.imei
                SimSerialNumber = telephonyManager.simSerialNumber
                deviceId_tv.text = serialNumber
                SimSerialNumber_tv.text = SimSerialNumber
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
            R.id.deviceId_tv, R.id.SimSerialNumber_tv -> clickOnGetDeviceInfo()
            R.id.taxiOffice_tv -> openInstitutionsList()
            R.id.taxiPlateNumber_tv -> openVehiclesList()
            R.id.register_btn -> register()
        }
    }

    private fun openInstitutionsList() {
        taxiOffice_tv.isEnabled = false
        openDataList(VehicleInformationListType.Institution)
        showInputError(false, 0)
    }

    private fun openVehiclesList() {
        if (institutionId != null) {
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
        if (Account().accessToken != null && allDataExist()) {
            operations.enableNextButton(register_btn, false)
            dialog?.show()
            val registrationViewModel: RegistrationViewModel by viewModels()
            registrationViewModel.register(registerCredentials, this).observe(this, Observer<RegistrationResponse> {
                dialog?.dismiss()
                operations.enableNextButton(register_btn, true)
                if (it != null) {
                    if (it.isSuccess) {
                        val deviceId = it.deviceId ?: run {
                            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.device_id_is_null_value))
                            return@Observer
                        }
                        FirebaseAnalytics.getInstance(this).setUserId(deviceId)
                        saveDeviceInfoIntoSharedPreferences(deviceId)
                        if (BuildConfig.FLAVOR == "bus"){ registerCredentials.VehicleId?.let { getCarrierInformation(it) } }
                        App.instance.startTrackingService()
                        //openModelPresenterScreen()
                        Log.d("FCM_TOKEN","${getParemeter(deviceId)}")
                        registerTerminal(getParemeter(deviceId))
                    } else {
                        if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                            it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                        } else if (it.mThrowable != null) {
                            if (it.mThrowable is IOException) {
                                operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.network_Issue))
                            } else {
                                operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.conversion_Issue))
                            }
                        }
                    }
                } else {
                    operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.unknown_error))
                }
            })
        } else {
            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.complete_required_data))
        }
    }



    private fun registerTerminal(parameter : Parameter) {
        dialog?.show()
        val terminalViewModel : TerminalViewModel by viewModels()
        terminalViewModel.createTerminal(parameter,this).observe(this, Observer<TerminalResponse> {
            if (it != null) {
                if (it.isSuccess) {
                    dialog?.dismiss()
                    editor.apply {
                        putString(SharedPreferencesHelper.terminal_id, it.terminalId)
                    }.apply()
                    openModelPresenterScreen()
                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.network_Issue))
                        } else {
                            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun getCarrierInformation(vehicleId: String) {
        val carrierInformationViewModel: CarrierInformationViewModel by viewModels()
        carrierInformationViewModel.getCarrierInformation(vehicleId, "currencies", this).observe(this, Observer<CarrierInformationModel.CarrierInformationResponse> {
            if (it != null) {
                if (it.isSuccess) {
                    it.carrierInformationModel?.let { saveCarrierInformationIntoSharePreferences(it) }
                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.network_Issue))
                        } else {
                            operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                operations.displayAlertDialog(this, getString(R.string.registration_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun saveCarrierInformationIntoSharePreferences(carrierInformationModel: CarrierInformationModel.CarrierInformationModel) {
        CarrierInformation().apply {
            routeNumber = carrierInformationModel.routeNumber
            destination = carrierInformationModel.destination
            tickets = carrierInformationModel.tickets
            currencies = carrierInformationModel.included.currencies
            lastUpdateDate = DateOperations().registrationDate(Date())
        }
    }

    private fun displayErrors(errors: List<Error>) {
        for (error in errors) {
            operations.displayAlertDialog(this, getString(R.string.registration_error_title), "Error message: ${error.detail}")
        }
    }

    private fun allDataExist() = institutionIdExist() && vehicleIdExist() && tabletInformationExist()

    private fun institutionIdExist(): Boolean {
        institutionId = app.institutionId
        return if (institutionId == null) {
            showInputError(true, 1)
            false
        } else {
            true
        }
    }

    private fun vehicleIdExist(): Boolean {
        val vehicleId = app.vehicleId
        return if (vehicleId == null) {
            showInputError(true, 2)
            false
        } else {
            true
        }
    }

    private fun tabletInformationExist(): Boolean {
        val tabletSerialNumber = registerCredentials.serialNumber
        val simCardNumber = registerCredentials.SimSerialNumber
        return if (tabletSerialNumber.isNullOrEmpty() || simCardNumber.isNullOrEmpty()) {
            showTabletInfoError(true)
            false
        } else {
            true
        }
    }

    private fun saveDeviceInfoIntoSharedPreferences(deviceId: String) {
        editor.apply {
            putString(SharedPreferencesHelper.username, app.signInCredentials?.userName)
            putString(SharedPreferencesHelper.registration_date, DateOperations().registrationDate(Date()))
            putString(SharedPreferencesHelper.institution_id, app.institutionId)
            putString(SharedPreferencesHelper.institution_name, app.institutionName)
            putString(SharedPreferencesHelper.vehicle_id, app.vehicleId)
            putString(SharedPreferencesHelper.vehicle_plate_number, app.taxiPlateNumber)
            putString(SharedPreferencesHelper.device_id, deviceId)
            putString(SharedPreferencesHelper.device_serial_number, registerCredentials.serialNumber)
            putString(SharedPreferencesHelper.sim_serial_number, registerCredentials.SimSerialNumber)
            putString(SharedPreferencesHelper.from_date, DateHelper.instance.getCurrentDate().toString())
        }.apply()
    }

    private fun openModelPresenterScreen() {
        val authorization = Authorization(true, 200)
        val modelPresenter = Intent(this, ModelPresenter::class.java)
        modelPresenter.putExtra(AUTHORIZATION_KAY, authorization)
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
            deviceId_tv.apply {
                error = getString(R.string.click_here_to_get_serial_number)
                requestFocus()
            }
            SimSerialNumber_tv.apply {
                error = getString(R.string.click_here_to_get_sim_card_number)
                requestFocus()
            }
            getDeviceInfo = false
            return
        } else {
            deviceId_tv.apply {
                error = null
                clearFocus()
                isClickable = false
            }
            SimSerialNumber_tv.apply {
                error = null
                clearFocus()
                isClickable = false
            }
            getDeviceInfo = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            app.apply {
                isNewLogin = true
                institutionId = null
                vehicleId = null
            }

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getParemeter(deviceId: String): Parameter {
        val parameter = Parameter()
        parameter.DeviceId = deviceId
        parameter.NotificationIdentifier = fcm_token

        return parameter
    }

    private fun getToken(){

    }
}