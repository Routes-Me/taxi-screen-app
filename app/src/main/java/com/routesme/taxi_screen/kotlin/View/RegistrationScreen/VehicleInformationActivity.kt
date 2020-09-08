package com.routesme.taxi_screen.kotlin.View.RegistrationScreen

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.routesme.taxi_screen.java.Class.OfficesAdapterMultibleViews
import com.routesme.taxi_screen.java.Model.OfficePlatesListViewModel
import com.routesme.taxi_screen.java.Model.OfficesListViewModel
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Model.ItemType
import com.routesme.taxi_screen.kotlin.Model.VehicleInformationListType
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.activity_vehicle_information.*
import java.util.*

class VehicleInformationActivity : AppCompatActivity() {

    private var app = App.instance
    //private val listTypeKey = getString(R.string.list_type_key)
    private lateinit var listType: VehicleInformationListType
    //private lateinit var myToolbar: Toolbar
    //sharedPreference Storage
    private var officesListViewModel: OfficesListViewModel? = null
    private var officePlatesListViewModel: OfficePlatesListViewModel? = null
    //Section recyclerView ...
    private lateinit var adapter: OfficesAdapterMultibleViews
    private lateinit var institutionsArrayList: ArrayList<ItemType>
    private  lateinit var vehiclesArrayList: ArrayList<ItemType>

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
            VehicleInformationListType.Institution -> getInstitutionsList_Sections()
            else -> getVehiclesList_Sections()
        }
    }
    private fun getInstitutionsList_Sections() {
        officesListViewModel = ViewModelProviders.of(this).get(OfficesListViewModel::class.java)
        officesListViewModel?.getInstitutions(this, 1, 40)?.observe((this as LifecycleOwner), Observer { (_, institutionList) ->
            institutionsArrayList = ArrayList()

            if (institutionList.isNotEmpty()) {
                institutionsArrayList.add(ItemType("Institutions", true, false, 0))
                for (i in institutionList.indices) {
                    institutionsArrayList.add(ItemType(institutionList[i].name, false, true, institutionList[i].institutionId))
                }
            }
            adapter = OfficesAdapterMultibleViews(this, institutionsArrayList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter.setOnItemClickListener { position ->
                app.institutionId = institutionsArrayList[position].id
                app.institutionName = institutionsArrayList[position].itemName
                app.vehicleId = -999
                app.taxiPlateNumber = null
                finish()
            }
        })
    }

    private fun getVehiclesList_Sections() {
        officePlatesListViewModel = ViewModelProviders.of(this).get(OfficePlatesListViewModel::class.java)
        officePlatesListViewModel?.getVehicles(this, 1, 150, app.institutionId)?.observe((this as LifecycleOwner), Observer { (_, vehiclesList) ->
            vehiclesArrayList = ArrayList()
            if (vehiclesList.isNotEmpty()) {
                vehiclesArrayList.add(ItemType("Vehicles", true, false, -1))
                for (i in vehiclesList.indices) {
                    vehiclesArrayList.add(ItemType(vehiclesList[i].plateNumber, false, true, vehiclesList[i].vehicleId))
                }
            }
            adapter = OfficesAdapterMultibleViews(this, vehiclesArrayList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter.setOnItemClickListener { position ->
                app.vehicleId = vehiclesArrayList[position].id
                app.taxiPlateNumber = vehiclesArrayList[position].itemName
                finish()
            }
        })
    }
}