package com.routesme.screen.MVVM.View.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.routesme.screen.uplevels.App
import com.routesme.screen.Class.Operations
import com.routesme.screen.Class.VehicleInformationAdapter
import com.routesme.screen.MVVM.Model.Error
import com.routesme.screen.MVVM.Model.VehicleInformationModel.*
import com.routesme.screen.MVVM.ViewModel.VehicleInformationViewModel
import com.routesme.screen.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_vehicle_information.*
import java.io.IOException

class VehicleInformationActivity : AppCompatActivity() {

    private var app = App.instance
    private val operations = Operations.instance
    private var dialog: AlertDialog? = null
    private lateinit var listType: VehicleInformationListType
    private lateinit var vehicleInformationAdapter: VehicleInformationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_information)
        initialize()
    }

    private fun initialize() {
        val listTypeKey = getString(R.string.list_type_key)
        if (intent.hasExtra(listTypeKey)) {
            listType = intent.getSerializableExtra(listTypeKey) as VehicleInformationListType
            toolbarSetUp()
            dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@VehicleInformationActivity)
            }
            getList()
        }
    }
    private fun toolbarSetUp() {
        setSupportActionBar(MyToolBar)
        supportActionBar?.apply {
            title = when(listType){
                VehicleInformationListType.Institution -> getString(R.string.search_for_taxi_offices)
                else -> getString(R.string.search_plate_numbers)
            }
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_grey)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // handle arrow click here
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getList() {
        when (listType) {
            VehicleInformationListType.Institution -> getInstitutions()
            else -> getVehicles()
        }
    }
    private fun getInstitutions(){
            dialog?.show()
            val vehicleInformationViewModel: VehicleInformationViewModel by viewModels()
            vehicleInformationViewModel.getInstitutions(1,40,this).observe(this, Observer<InstitutionsResponse> {
                dialog?.dismiss()
                if (it != null) {
                    if (it.isSuccess) {
                        val list = it.data ?: run {
                            operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.no_data_found))
                            return@Observer
                        }
                        if (list.isEmpty()){
                            operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.no_data_found))
                            return@Observer
                        }else{
                            displayInstitutionList(list)
                        }
                    } else {
                        if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                            it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                        } else if (it.mThrowable != null) {
                            if (it.mThrowable is IOException) {
                                operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.network_Issue))
                            } else {
                                operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.conversion_Issue))
                            }
                        }
                    }
                } else {
                    operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.unknown_error))
                }
            })
    }

    private fun displayInstitutionList(list: List<InstitutionData>) {
        val institutions = mutableListOf<Item>().apply {
            add(Item(null, getString(R.string.institutions), true))
            for (institution in list){
                add(Item(institution.institutionId, institution.name, false))
            }
        }.toList()

        vehicleInformationAdapter = VehicleInformationAdapter(this, institutions)
        vehicleInformationAdapter.onItemClick = {
            app.apply {
                institutionId = it.id
                institutionName = it.itemName
                vehicleId = null
                taxiPlateNumber = null
            }
            finish()
        }
        recyclerView.apply {
            adapter = vehicleInformationAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getVehicles(){
        dialog?.show()
        val vehicleInformationViewModel: VehicleInformationViewModel by viewModels()
        vehicleInformationViewModel.getVehicles(app.institutionId.toString(),1,150,this).observe(this, Observer<VehiclesResponse> {
            dialog?.dismiss()
            if (it != null) {
                if (it.isSuccess) {
                    val list = it.data ?: run {
                        operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.no_data_found))
                        return@Observer
                    }
                    if (list.isEmpty()){
                        operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.no_data_found))
                        return@Observer
                    }else{
                        displayVehicleList(list)
                    }
                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.network_Issue))
                        } else {
                            operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun displayVehicleList(list: List<VehicleData>) {
        val vehicles = mutableListOf<Item>().apply {
            add(Item(null, getString(R.string.vehicles) , true))
            for (vehicle in list){
                add(Item(vehicle.vehicleId, vehicle.plateNumber, false))
            }
        }.toList()

        vehicleInformationAdapter = VehicleInformationAdapter(this, vehicles)
        vehicleInformationAdapter.onItemClick = {
                app.apply {
                    vehicleId = it.id
                    taxiPlateNumber = it.itemName
                }
                finish()
        }
        recyclerView.apply {
            adapter = vehicleInformationAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun displayErrors(errors: List<Error>) {
        for (error in errors) {
            operations.displayAlertDialog(this, getString(R.string.vehicle_information_error_title), "Error message: ${error.detail}")
        }
    }
}