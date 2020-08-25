package com.routesme.taxi_screen.java.View.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuItem;
import com.routesme.taxi_screen.java.Model.OfficePlatesListViewModel;
import com.routesme.taxi_screen.java.Model.OfficesListViewModel;
import com.routesme.taxi_screen.java.Class.OfficesAdapterMultibleViews;
import com.routesme.taxi_screen.kotlin.Class.App;
import com.routesme.taxi_screen.kotlin.Model.InstitutionData;
import com.routesme.taxi_screen.kotlin.Model.Institutions;
import com.routesme.taxi_screen.kotlin.Model.ItemType;
import com.routesme.taxi_screen.kotlin.Model.VehicleData;
import com.routesme.taxi_screen.kotlin.Model.Vehicles;
import com.routesme.taxiscreen.R;
import java.util.ArrayList;
import java.util.List;

public class TaxiInformationListScreen extends AppCompatActivity {

    private App app;
    private static final String   List_Type_STR = "List_Type_Key", Offices_STR = "Offices", Office_Plates_STR = "Office_Plates";
    private String listType;
    private Toolbar myToolbar;
    //sharedPreference Storage
    private OfficesListViewModel officesListViewModel;
    private OfficePlatesListViewModel officePlatesListViewModel;
    private RecyclerView recyclerView;
    //Section recyclerView ...
    private OfficesAdapterMultibleViews adapter;
    private ArrayList<ItemType> institutionsArrayList, vehiclesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_list_screen);
        initialize();
    }
    private void initialize() {
        app = (App) getApplicationContext();
        if (getIntent().hasExtra(List_Type_STR)){
            listType = getIntent().getStringExtra(List_Type_STR);
            ToolbarSetUp();
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            getList();
        }
    }

    private void getList() {
        switch (listType){
            case Offices_STR:
                getInstitutionsList_Sections();
                break;
            case Office_Plates_STR:
                getVehiclesList_Sections();
                break;
        }
    }

    private void getInstitutionsList_Sections() {
            officesListViewModel = ViewModelProviders.of(TaxiInformationListScreen.this).get(OfficesListViewModel.class);
            officesListViewModel.getInstitutions(this, 1,40).observe((LifecycleOwner) this, new Observer<Institutions>() {
                @Override
                public void onChanged(Institutions institutions) {
                    institutionsArrayList = new ArrayList<>();
                   // ArrayList<Office> mostRecentOffices = new ArrayList<>() ;
                   // mostRecentOffices.addAll(taxiOfficeList.getOfficesIncluded().getRecentOffices());
                    //ArrayList<Office> allOffices = new ArrayList<>();
                    //allOffices.addAll(institutions.getOfficesData());

                    //Get Most Offices
                    /*
                    if (mostRecentOffices.size() > 0) {
                        institutionsArrayList.add(new ItemType("Most recent", true, false, 0));
                        for (int i=0; i <mostRecentOffices.size() ; i++){
                            institutionsArrayList.add(new ItemType(mostRecentOffices.get(i).getTaxiOfficeName(),false, false,mostRecentOffices.get(i).getTaxiOfficeID()));
                        }
                    }
                    */
                    List<InstitutionData> institutionList = institutions.getData();
                    if (institutionList.size() > 0) {
                        institutionsArrayList.add(new ItemType("Institutions", true, false, 0));

                        for (int i = 0; i < institutionList.size(); i++) {
                            institutionsArrayList.add(new ItemType(institutionList.get(i).getName(), false, true, institutionList.get(i).getInstitutionId()));
                        }
                    }
                    adapter = new OfficesAdapterMultibleViews(TaxiInformationListScreen.this, institutionsArrayList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    adapter.setOnItemClickListener(new OfficesAdapterMultibleViews.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                            app.setInstitutionId(institutionsArrayList.get(position).getId());
                            app.setInstitutionName(institutionsArrayList.get(position).getItemName());
                            app.setVehicleId(-999);
                            app.setTaxiPlateNumber(null);
                            finish();
                        }
                    });
                }
            });
    }

    private void getVehiclesList_Sections() {
            officePlatesListViewModel = ViewModelProviders.of(TaxiInformationListScreen.this).get(OfficePlatesListViewModel.class);
            officePlatesListViewModel.getVehicles(this,1,150,app.getInstitutionId()).observe((LifecycleOwner) this, new Observer<Vehicles>() {
                @Override
                public void onChanged(Vehicles vehicles) {
                    vehiclesArrayList = new ArrayList<>();
                   // ArrayList<TaxiPlate> allOfficePlates = new ArrayList<>();
                    //allOfficePlates.addAll(vehicles.getOfficePlatesData());
                    List<VehicleData> vehiclesList = vehicles.getData();
                    if (vehiclesList.size() > 0) {
                        vehiclesArrayList.add(new ItemType("Vehicles", true, false, -1));

                        for (int i=0; i < vehiclesList.size() ; i++){
                            vehiclesArrayList.add(new ItemType(vehiclesList.get(i).getPlateNumber(),false, true,vehiclesList.get(i).getVehicleId()));
                        }
                    }

                    adapter = new OfficesAdapterMultibleViews(TaxiInformationListScreen.this, vehiclesArrayList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    adapter.setOnItemClickListener(new OfficesAdapterMultibleViews.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            app.setVehicleId(vehiclesArrayList.get(position).getId());
                            app.setTaxiPlateNumber(vehiclesArrayList.get(position).getItemName());
                            finish();
                        }
                    });
                }
            });
    }

    private void ToolbarSetUp() {
        //Toolbar..
        myToolbar = findViewById(R.id.MyToolBar);
        setSupportActionBar(myToolbar);
        switch (listType){
            case Offices_STR:
                getSupportActionBar().setTitle("Search for taxi offices");
                break;

            case Office_Plates_STR:
                getSupportActionBar().setTitle("Search plate numbers");
                break;
        }
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_grey);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}