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
import com.routesme.taxi_screen.java.Class.App;
import com.routesme.taxi_screen.java.Model.Office;
import com.routesme.taxi_screen.java.Model.OfficePlatesList;
import com.routesme.taxi_screen.java.Model.OfficePlatesListViewModel;
import com.routesme.taxi_screen.java.Model.OfficesListViewModel;
import com.routesme.taxi_screen.java.Model.TaxiOfficeList;
import com.routesme.taxi_screen.java.Model.TaxiPlate;
import com.routesme.taxi_screen.java.Class.OfficesAdapterMultibleViews;
import com.routesme.taxi_screen.java.Model.ItemType;
import com.routesme.taxiscreen.R;
import java.util.ArrayList;

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
    private ArrayList<ItemType> listOfficesArrayList, listOfficePlatesArrayList;

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
            getlist();
        }
    }

    private void getlist() {
        switch (listType){
            case Offices_STR:
                getOfficesList_Sections();
                break;
            case Office_Plates_STR:
                getOfficePlatesList_Sections();
                break;
        }
    }


    private void getOfficesList_Sections() {
            officesListViewModel = ViewModelProviders.of(TaxiInformationListScreen.this).get(OfficesListViewModel.class);
            officesListViewModel.getTaxiOfficesList(this, "recent").observe((LifecycleOwner) this, new Observer<TaxiOfficeList>() {
                @Override
                public void onChanged(TaxiOfficeList taxiOfficeList) {
                    listOfficesArrayList = new ArrayList<>();
                    ArrayList<Office> mostRecentOffices = new ArrayList<>() ;
                    mostRecentOffices.addAll(taxiOfficeList.getOfficesIncluded().getRecentOffices());
                    ArrayList<Office> allOffices = new ArrayList<>();
                    allOffices.addAll(taxiOfficeList.getOfficesData());
                    //Get Most Offices
                    if (mostRecentOffices.size() > 0) {
                        listOfficesArrayList.add(new ItemType("Most recent", true, false, 0));
                        for (int i=0; i <mostRecentOffices.size() ; i++){
                            listOfficesArrayList.add(new ItemType(mostRecentOffices.get(i).getTaxiOfficeName(),false, false,mostRecentOffices.get(i).getTaxiOfficeID()));
                        }
                    }
                    //Get all Offices
                    if (allOffices.size() > 0) {
                        listOfficesArrayList.add(new ItemType("Offices", true, false, 0));

                        for (int i = 0; i < allOffices.size(); i++) {

                            listOfficesArrayList.add(new ItemType(allOffices.get(i).getTaxiOfficeName(), false, true, allOffices.get(i).getTaxiOfficeID()));
                        }
                    }
                    adapter = new OfficesAdapterMultibleViews(TaxiInformationListScreen.this, listOfficesArrayList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    adapter.setOnItemClickListener(new OfficesAdapterMultibleViews.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                            app.setTaxiOfficeId(listOfficesArrayList.get(position).getOfficeId());
                            app.setTaxiOfficeName(listOfficesArrayList.get(position).getItemName());
                            app.setTaxiPlateNumber(null);
                            finish();
                        }
                    });
                }
            });
    }


    private void getOfficePlatesList_Sections() {
            officePlatesListViewModel = ViewModelProviders.of(TaxiInformationListScreen.this).get(OfficePlatesListViewModel.class);
            officePlatesListViewModel.getOfficePlatesList(this,app.getTaxiOfficeId()).observe((LifecycleOwner) this, new Observer<OfficePlatesList>() {
                @Override
                public void onChanged(OfficePlatesList officePlatesList) {
                    listOfficePlatesArrayList = new ArrayList<>();
                    ArrayList<TaxiPlate> allOfficePlates = new ArrayList<>();
                    allOfficePlates.addAll(officePlatesList.getOfficePlatesData());
                    //Get all Office Plates
                    if (allOfficePlates.size() > 0) {
                        listOfficePlatesArrayList.add(new ItemType("Office plates", true, false, 0));

                        for (int i=0; i <allOfficePlates.size() ; i++){
                            listOfficePlatesArrayList.add(new ItemType(allOfficePlates.get(i).getTabletCarPlateNo(),false, true));
                        }
                    }

                    adapter = new OfficesAdapterMultibleViews(TaxiInformationListScreen.this, listOfficePlatesArrayList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    adapter.setOnItemClickListener(new OfficesAdapterMultibleViews.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            app.setTaxiPlateNumber(listOfficePlatesArrayList.get(position).getItemName());
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