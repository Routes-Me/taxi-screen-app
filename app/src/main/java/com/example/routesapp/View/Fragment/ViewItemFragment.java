package com.example.routesapp.View.Fragment;



import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.routesapp.Class.Operations;

import com.example.routesapp.R;
import com.example.routesapp.View.Activity.MainActivity;



public class ViewItemFragment extends Fragment implements View.OnClickListener {



 //   private Place place;


    //call Operations.java class
    private Operations operations;

    private Bundle bundle;

    //Toolbar Items
    private ImageView btnAddToProfile, btnBackToRecyclerViewFragment;
    //Link Layout
    private RelativeLayout Link_Layout;
    private WebView webView_Link;
    //News Layout
    private RelativeLayout News_Layout;
    private WebView webView_News;

    //Map Layout
    private RelativeLayout Map_Layout;
    private WebView webView_Map;

    //QRCode Layout
    private LinearLayout QRCode_Layout;
    private TextView QRCodeTitle,QRCodeDiscountAmount;
    private ImageView QRCode_Pic;

    private String itemType;
    private int itemPosition;


    //add_phone_number_dialog.xml Elements...
    private AlertDialog dialog;
    private Button btnClose_Dialog, btnSave_Dialog;
    private EditText editText_phoneNumber_Dialog;



    //To Test Retrofit...
    private Button btnGetData;
    private TextView txtViewData;

    private View nMainView;


    public ViewItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.fragment_view_item, container, false);

        initialize();

        return nMainView;

    }

    private void initialize() {



//        place = new Place("ChIJuR4vWYKEzz8RyFxE34vuWXQ");


        //To Test Retrofit...
        btnGetData = nMainView.findViewById(R.id.btnGetData);
        txtViewData = nMainView.findViewById(R.id.txtViewData);
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getRetrofitData();

             //   Toast.makeText(getActivity(), "name: "+place.getName()  +  "  , rating: "+place.getRating(), Toast.LENGTH_SHORT).show();

               // Toast.makeText(getActivity(), "height: "+place.getHeight() + " , width: "+place.getWidth() + " ,photo_reference: "+ place.getPhoto_reference(), Toast.LENGTH_SHORT).show();
            }
        });




        operations = new Operations(getActivity());

        bundle = this.getArguments();

        btnAddToProfile = nMainView.findViewById(R.id.btnAddToProfile);
        btnAddToProfile.setOnClickListener(this);
        btnBackToRecyclerViewFragment = nMainView.findViewById(R.id.btnBackToRecyclerViewFragment);
        btnBackToRecyclerViewFragment.setOnClickListener(this);


        //Link Layout
        Link_Layout = nMainView.findViewById(R.id.Link_Layout);
        webView_Link = nMainView.findViewById(R.id.webView_Link);
        //News Layout
        News_Layout = nMainView.findViewById(R.id.News_Layout);
        webView_News = nMainView.findViewById(R.id.webView_News);
        //Map Layout
        Map_Layout = nMainView.findViewById(R.id.Map_Layout);
        webView_Map = nMainView.findViewById(R.id.webView_Map);
       //QRCode Layout
        QRCode_Layout = nMainView.findViewById(R.id.QRCode_Layout);
        QRCodeTitle = nMainView.findViewById(R.id.QRCodeTitle);
        QRCodeDiscountAmount = nMainView.findViewById(R.id.QRCodeDiscountAmount);
        QRCode_Pic = nMainView.findViewById(R.id.QRCode_Pic);



        //get ItemType

        if (bundle != null){

            itemPosition = bundle.getInt("itemPosition_KEY");
          // Toast.makeText(getActivity(), "Position: " + itemPosition , Toast.LENGTH_SHORT).show();

            itemType = bundle.getString("itemType_KEY");

           // Toast.makeText(getActivity(), itemType, Toast.LENGTH_SHORT).show();

            switch (itemType){
                case "LinkView":
                    LinkView();
                    break;

                case "MapView":
                     MapView();
                    break;

                case "NewsView":
                    NewsView();
                    break;

                case "QRCodeView":
                    QRCodeView();
                    break;
            }

        }

    }

    /*
    private void getRetrofitData() {

        try {
            ApiClient apiClient = new ApiClient();
            final ApiServices apiService = apiClient.getClient().create(ApiServices.class);
            try {
                // parameters.put("login", SearchText);
                Call<JsonObject> call = apiService.getPlaceData();
                //  Call<ItemResponse> call = apiService.getNews();
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        //Toast.makeText(getActivity(), ""+response.body().getNews().get(0).getName(), Toast.LENGTH_SHORT).show();
                        if (response.body() != null) {
                            //Toast.makeText(getActivity(), ""+response.body().getAsJsonObject("result").get("name"), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), ""+response.body().getAsJsonObject("result").get("rating"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }




        }catch (Exception e){}

    }
*/

    private void QRCodeView() {
        Link_Layout.setVisibility(View.GONE);
        News_Layout.setVisibility(View.GONE);
        Map_Layout.setVisibility(View.GONE);
        QRCode_Layout.setVisibility(View.VISIBLE);

        //show QRCode Details...
        String QRCode_Pic_s =  bundle.getString("QRCode_Pic_KEY");
        String QRCodeTitle_s =  bundle.getString("QRCodeTitle_KEY");
        String QRCodeDiscountAmount_s =  bundle.getString("QRCodeDiscountAmount_KEY");

        if (QRCode_Pic_s != null){ operations.setQRCodePic_In_imageView(QRCode_Pic_s,QRCode_Pic); }
        if (QRCodeTitle_s != null){ QRCodeTitle.setText(QRCodeTitle_s); }
        if (QRCodeDiscountAmount_s != null){ QRCodeDiscountAmount.setText(QRCodeDiscountAmount_s); }

    }

    private void NewsView() {
        Link_Layout.setVisibility(View.GONE);
        News_Layout.setVisibility(View.VISIBLE);
        Map_Layout.setVisibility(View.GONE);
        QRCode_Layout.setVisibility(View.GONE);

        //show News into WebView...
        String News_s =  bundle.getString("News_KEY");

        webView_News.setWebViewClient(new WebViewClient());
        webView_News.loadUrl(News_s);
        WebSettings webSettings = webView_News.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private void LinkView() {
        Link_Layout.setVisibility(View.VISIBLE);
        News_Layout.setVisibility(View.GONE);

        Map_Layout.setVisibility(View.GONE);
        QRCode_Layout.setVisibility(View.GONE);

        //show Link into WebView...
        String Link_s =  bundle.getString("Link_KEY");

        webView_Link.setWebViewClient(new WebViewClient());
        webView_Link.loadUrl(Link_s);
        WebSettings webSettings = webView_Link.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }

    private void MapView() {
        Link_Layout.setVisibility(View.GONE);
        News_Layout.setVisibility(View.GONE);
        Map_Layout.setVisibility(View.VISIBLE);
        QRCode_Layout.setVisibility(View.GONE);

        //show Map into WebView...
        String MapLink_s =  bundle.getString("MapLink_KEY");

        webView_Map.setWebViewClient(new WebViewClient());
        webView_Map.loadUrl(MapLink_s);
        WebSettings webSettings = webView_Map.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnAddToProfile :
                  // showAddToProfileDialog();


                ((MainActivity)getActivity()).VisibleLayout("AddItemLayout");

                  // onPause();
                break;

            case R.id.btnBackToRecyclerViewFragment :
                Bundle bundle = new Bundle();

                bundle.putInt("itemPosition_KEY",itemPosition);

                RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
                recyclerViewFragment.setArguments(bundle);


                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.fragment_container, recyclerViewFragment).commit();

             //   replaceFragment(recyclerViewFragment);

                break;


                //Buttons of add_phone_number_dialog.xml
            case R.id.btnClose_Dialog:
                  dialog.dismiss();
                  Operations.hideKeyboard(getActivity());
                onResume();
                break;

            case R.id.btnSave_Dialog:
                  saveItemIntoUserProfile();
                break;

        }

    }

    private void replaceFragment(Fragment fragment) {
     //   getActivity().getSupportFragmentManager().beginTransaction().apply {
            if (fragment.isAdded()) {
                getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commit();
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
            }

        getActivity().getSupportFragmentManager().beginTransaction().hide(new ViewItemFragment()).commit();
    }

    private void saveItemIntoUserProfile() {

            String phoneNumber = editText_phoneNumber_Dialog.getText().toString().trim();

            if (phoneNumber.isEmpty()){
                editText_phoneNumber_Dialog.setError("Phone Number Is Required");
                editText_phoneNumber_Dialog.requestFocus();
                return;
            }
            if (phoneNumber.length() != 8){
                editText_phoneNumber_Dialog.setError("Enter Valid Phone Number");
                editText_phoneNumber_Dialog.requestFocus();
                return;
            }

            Toast.makeText(getActivity(), "Your Phone Number Is : "+ phoneNumber, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        Operations.hideKeyboard(getActivity());

        onResume();
    }

    private void showAddToProfileDialog() {

        AlertDialog.Builder alert;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            alert = new AlertDialog.Builder(getContext(),android.R.style.Theme_Material_Dialog_Alert);
        }else {
            alert = new AlertDialog.Builder(getContext());
        }

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_phone_number_dialog,null);

        editText_phoneNumber_Dialog = view.findViewById(R.id.editText_phoneNumber_Dialog);
        btnClose_Dialog = view.findViewById(R.id.btnClose_Dialog);
        btnClose_Dialog.setOnClickListener(this);
        btnSave_Dialog = view.findViewById(R.id.btnSave_Dialog);
        btnSave_Dialog.setOnClickListener(this);


        alert.setView(view);
        alert.setCancelable(false);

        dialog = alert.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }



}
