package com.example.routesapp.View.LastHomeScreen.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.Class.ItemsAdapterMultibleViews;
import com.example.routesapp.Model.ItemsModel;
import com.example.routesapp.Model.ItemsViewModel;

import com.example.routesapp.R;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment  {



    private Operations operations;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedLanguage = null;
    private String savedToken = null;

    Bundle itemBundle;




    private RecyclerView.SmoothScroller smoothScroller;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private ItemsAdapterMultibleViews adapter;


    private int itemPosition = 0;


    private LinearLayout BtnViewLinkItem,BtnViewMapItem,BtnViewNewsItem,BtnViewQRCodeItem;


    //To showing rating of Map Place....
   // private Place place;
    //private Double placeRating = 0.0;
    private RatingBar mapRatingBar;


    private View nMainView;


    public RecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

       // initialize();

        initialize_new();


        return nMainView;

    }

    private void initialize_new() {

        operations =  new Operations(getActivity());

        itemBundle = this.getArguments();

        smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        if (itemBundle != null){

            int position = itemBundle.getInt("itemPosition_KEY");

            if (position >= 0){
                itemPosition = position;
            }else {
                itemPosition = 0;
            }

        }else {
            itemPosition = 0;
        }
        smoothScroller.setTargetPosition(itemPosition);

        //sharedPreference Storage
        sharedPreferences = getActivity().getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedLanguage = sharedPreferences.getString("Language", "English");
        savedToken = "Bearer " + sharedPreferences.getString("tabToken", null);








        loadRecyclerViewData();
        recyclerView = nMainView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }


    private void initialize() {



       // BtnViewLinkItem = nMainView.findViewById(R.id.BtnViewLinkItem);
       // BtnViewLinkItem.setOnClickListener(this);

       // BtnViewMapItem = nMainView.findViewById(R.id.BtnViewMapItem);
       // BtnViewMapItem.setOnClickListener(this);



        //To showing rating of Map Place....
        //place = new Place("ChIJuR4vWYKEzz8RyFxE34vuWXQ");
      //  placeRating = place.getRating();
       // mapRatingBar = nMainView.findViewById(R.id.mapRatingBar);

        try {
         //   mapRatingBar.setRating(Float.valueOf(String.valueOf(place.getRating())));
        }catch (Exception e){

        }

       // Toast.makeText(getActivity(), "name: "+place.getName()  +  "  , rating: "+place.getRating(), Toast.LENGTH_SHORT).show();



        BtnViewNewsItem = nMainView.findViewById(R.id.BtnViewNewsItem);
      //  BtnViewNewsItem.setOnClickListener(this);

        BtnViewQRCodeItem = nMainView.findViewById(R.id.BtnViewQRCodeItem);
      //  BtnViewQRCodeItem.setOnClickListener(this);





   /*
        BtnViewLinkItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewItemFragment()).commit();
            }
        });
*/
    }

/*
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        ViewItemFragment viewItemFragment = new ViewItemFragment();

        switch (v.getId()){

            case R.id.BtnViewLinkItem:

                bundle.putString("itemType_KEY","LinkView");
                bundle.putString("Link_KEY","https://www.pepsikuwait.com/");
                break;

            case R.id.BtnViewMapItem:
                bundle.putString("itemType_KEY","MapView");
               //bundle.putString("MapLink_KEY","https://www.google.com/maps/contrib/115796680298470570211/place/ChIJe5_isiXdRT4RR0nrZTINgXc/@25.2956754,51.4914159,12z/data=!4m6!1m5!8m4!1e2!2s115796680298470570211!3m1!1e1");
               bundle.putString("MapLink_KEY","https://goo.gl/maps/Pk2ttzBX3Br");
                break;

            case R.id.BtnViewNewsItem:
                bundle.putString("itemType_KEY","NewsView");
                bundle.putString("News_KEY","https://www.xcite.com/");
                break;

            case R.id.BtnViewQRCodeItem:
                bundle.putString("itemType_KEY","QRCodeView");
                bundle.putString("QRCode_Pic_KEY","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesADS%2Fqrcode_Burger%20King.png?alt=media&token=868ecb00-af43-4064-97b6-24a438c92540");
                bundle.putString("QRCodeTitle_KEY","Burger King Discount 30%");
                bundle.putString("QRCodeDiscountAmount_KEY","Discount Amount: 0.3");
                break;

        }
        viewItemFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, viewItemFragment).commit();
    }

*/
    private void loadRecyclerViewData() {


try {

    final ItemsViewModel model = ViewModelProviders.of(this).get(ItemsViewModel.class);

    model.getItems(1, getActivity(), savedToken).observe(this, new Observer<List<ItemsModel>>() {
        @Override
        public void onChanged(@Nullable final List<ItemsModel> itemsList) {

            //  itemsList.get(new Random().nextInt(itemsList.size()));


            adapter = new ItemsAdapterMultibleViews(getActivity(), itemsList);
            recyclerView.setAdapter(adapter);

            // runLayoutAnimation(recyclerView);

            recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);


            // OnItemClickListener on Item
            adapter.setOnItemClickListener(new ItemsAdapterMultibleViews.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    //
                    //
                    //   Toast.makeText(getActivity(), "Title: " + itemsList.get(position).getItemList_Title_En() + " ,Type: " + itemsList.get(position).getItem_Type(), Toast.LENGTH_SHORT).show();

                    viewItem(position, itemsList.get(position).getItem_Type(), itemsList.get(position).getItemList_Title_En(), itemsList.get(position).getItemList_Title_Ar(), itemsList.get(position).getItemList_Title_Or(), itemsList.get(position).getItemList_Title_Ta(), itemsList.get(position).getItemList_Discount_Amount(), itemsList.get(position).getItemList_Page_URL(), itemsList.get(position).getItemList_Pic_URL());

                }
            });


        }
    });


    // Stopping swipe refresh
    mSwipeRefreshLayout.setRefreshing(false);
}catch (Exception e){
    Crashlytics.logException(e);
}
    }

    private void viewItem(int itemPosition,String item_type, String itemList_title_en, String itemList_title_ar, String itemList_title_or, String itemList_title_ta, int itemList_discount_amount, String itemList_page_url, String itemList_pic_url) {

        Bundle bundle = new Bundle();
        ViewItemFragment viewItemFragment = new ViewItemFragment();

        switch (item_type){

            case "Web":

                bundle.putString("itemType_KEY","LinkView");
                bundle.putString("Link_KEY",itemList_page_url);
                bundle.putInt("itemPosition_KEY",itemPosition);
                break;

            case "Map":
                bundle.putString("itemType_KEY","MapView");
                //bundle.putString("MapLink_KEY","https://www.google.com/maps/contrib/115796680298470570211/place/ChIJe5_isiXdRT4RR0nrZTINgXc/@25.2956754,51.4914159,12z/data=!4m6!1m5!8m4!1e2!2s115796680298470570211!3m1!1e1");
                bundle.putString("MapLink_KEY",itemList_page_url);
                bundle.putInt("itemPosition_KEY",itemPosition);
                break;

            case "News":
                bundle.putString("itemType_KEY","NewsView");
                bundle.putString("News_KEY",itemList_page_url);
                bundle.putInt("itemPosition_KEY",itemPosition);
                break;

            case "QRcode":

                String Qrcode_Title  = operations.getTitle_ofItems(itemList_title_en, itemList_title_ar, itemList_title_or, itemList_title_ta, savedLanguage);
                String Qrcode_Discount  = operations.getDiscount_ofItems(itemList_discount_amount, savedLanguage);



                bundle.putString("itemType_KEY","QRCodeView");
                bundle.putString("QRCode_Pic_KEY",itemList_pic_url);
                bundle.putString("QRCodeTitle_KEY",Qrcode_Title);
                bundle.putString("QRCodeDiscountAmount_KEY",Qrcode_Discount);
                bundle.putInt("itemPosition_KEY",itemPosition);
                break;

        }
        viewItemFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, viewItemFragment).commit();




    }

/*
    @Override
    public void onRefresh() {
        // Fetching data from server
       // loadRecyclerViewData();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecyclerViewFragment()).commit();
    }
*/



    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
