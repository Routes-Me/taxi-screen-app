package com.example.routesapp.View.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.routesapp.Class.ItemsAdapterMultibleViews;
import com.example.routesapp.Model.Office;
import com.example.routesapp.Model.OfficesListViewModel;
import com.example.routesapp.Model.TaxiOfficeList;
import com.example.routesapp.R;
import com.example.routesapp.Class.OfficesAdapterMultibleViews;
import com.example.routesapp.Model.ItemType;

import java.util.ArrayList;

public class TaxiInformationListScreen extends AppCompatActivity {

    private static final String   List_Type_STR = "List_Type_Key", Offices_STR = "Offices", Office_Plates_STR = "Office_Plates";

    private String listType;

    private Toolbar myToolbar;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedToken = null;

    private OfficesListViewModel officesListViewModel;


    private RecyclerView recyclerView;


    //Section recyclerView ...
    private OfficesAdapterMultibleViews adapter;



    private ArrayList<ItemType> listItemArrayList;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_list_screen);


        initialize();

    }

    private void initialize() {
        if (getIntent().hasExtra(List_Type_STR)){

            listType = getIntent().getStringExtra(List_Type_STR);
          //  Toast.makeText(this, "List Of :  " + listType, Toast.LENGTH_SHORT).show();

            ToolbarSetUp();

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            //sharedPreference Storage
            sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);
            savedToken = "Bearer " + sharedPreferences.getString("tabToken", null);
            getlist();

        }
    }

    private void getlist() {

        switch (listType){
            case Offices_STR:

                getOfficesList_Sections();
                break;

            case Office_Plates_STR:

                break;
        }

    }


    private void getOfficesList_Sections() {


        officesListViewModel = ViewModelProviders.of(TaxiInformationListScreen.this).get(OfficesListViewModel.class);
        officesListViewModel.getTaxiOfficesList(this,savedToken).observe((LifecycleOwner) this, new Observer<TaxiOfficeList>() {
            @Override
            public void onChanged(TaxiOfficeList taxiOfficeList) {

                listItemArrayList = new ArrayList<>();



                listItemArrayList.add(new ItemType("Most recent",true, false,0));

                ArrayList<Office> mostRecentOffices = new ArrayList<>() ;
                mostRecentOffices.addAll(taxiOfficeList.getRecentOffices());
                ArrayList<Office> allOffices = new ArrayList<>();
                allOffices.addAll(taxiOfficeList.getOffices());


                for (int i=0; i <mostRecentOffices.size() ; i++){
                    listItemArrayList.add(new ItemType(mostRecentOffices.get(i).getTaxiOfficeName(),false, false,mostRecentOffices.get(i).getTaxiOfficeID()));
                }

                listItemArrayList.add(new ItemType("Offices",true, false,0));

                for (int i=0; i <allOffices.size() ; i++){

                    listItemArrayList.add(new ItemType(allOffices.get(i).getTaxiOfficeName(),false, true,allOffices.get(i).getTaxiOfficeID()));
                }

                adapter = new OfficesAdapterMultibleViews(TaxiInformationListScreen.this,listItemArrayList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


                // OnItemClickListener on Item
                adapter.setOnItemClickListener(new OfficesAdapterMultibleViews.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {




                        Toast.makeText(TaxiInformationListScreen.this, "Office ID:  "  + listItemArrayList.get(position).getOfficeId(), Toast.LENGTH_SHORT).show();



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
