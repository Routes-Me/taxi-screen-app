package com.routesme.taxi_screen.kotlin.View.RegistrationScreen

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
    private val officesListViewModel: OfficesListViewModel? = null
    private val officePlatesListViewModel: OfficePlatesListViewModel? = null
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

    }
}