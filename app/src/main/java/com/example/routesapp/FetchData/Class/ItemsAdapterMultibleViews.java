package com.example.routesapp.FetchData.Class;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.routesapp.Class.Operations;
import com.example.routesapp.FetchData.Model.ItemsModel;
import com.example.routesapp.R;

import java.util.List;

public class ItemsAdapterMultibleViews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedLanguage = null;




    
    private static final int ITEM_QRCode = 1;
    private static final int ITEM_Map = 2;
    private static final int ITEM_Web = 3;
    private static final int ITEM_News = 4;





    Context mCtx;
    List<ItemsModel> itemsList;


    private OnItemClickListener listener;

    private Operations operations;


    public ItemsAdapterMultibleViews(Context mCtx, List<ItemsModel> itemsList) {
        this.mCtx = mCtx;
        this.itemsList = itemsList;

        this.operations = new Operations((Activity) mCtx);

        //sharedPreference Storage
        sharedPreferences = mCtx.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedLanguage = sharedPreferences.getString("Language", "English");

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView;


        if (viewType == ITEM_QRCode) {
            itemView = LayoutInflater.from(mCtx).inflate(R.layout.qrcode_layout,parent, false);
            return new QRCode_ViewHolder(itemView);
        } else if (viewType == ITEM_Map) {
            itemView = LayoutInflater.from(mCtx).inflate(R.layout.map_layout,parent, false);
            return new Map_ViewHolder(itemView);
        } else if (viewType == ITEM_Web) {
            itemView = LayoutInflater.from(mCtx).inflate(R.layout.web_layout,parent, false);
            return new Web_ViewHolder(itemView);
        } else if (viewType == ITEM_News) {
            itemView = LayoutInflater.from(mCtx).inflate(R.layout.news_layout,parent, false);
            return new News_ViewHolder(itemView);
        }else {
            itemView = LayoutInflater.from(mCtx).inflate(R.layout.qrcode_layout,parent, false);
            return new QRCode_ViewHolder(itemView);
        }

    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemsModel item = itemsList.get(position);


        final int itemType = getItemViewType(position);


        if (itemType == ITEM_QRCode) {

            QRCode_ViewHolder qrCode_viewHolder = (QRCode_ViewHolder) holder;

            operations.setImage_In_imageView(item.getItemList_Logo_URL(),qrCode_viewHolder.QRCode_Logo);
            operations.setTitle_In_textView(item.getItemList_Title_En(), item.getItemList_Title_Ar(), item.getItemList_Title_Or(), item.getItemList_Title_Ta(), savedLanguage, qrCode_viewHolder.QRCode_Title);
            operations.setDiscount_In_textView(item.getItemList_Discount_Amount(),savedLanguage,qrCode_viewHolder.QRCode_Discount);

        } else if (itemType == ITEM_Map) {

            Map_ViewHolder map_viewHolder = (Map_ViewHolder) holder;

            operations.setImage_In_imageView(item.getItemList_Logo_URL(),map_viewHolder.Map_Logo);
            operations.setTitle_In_textView(item.getItemList_Title_En(), item.getItemList_Title_Ar(), item.getItemList_Title_Or(), item.getItemList_Title_Ta(), savedLanguage, map_viewHolder.Map_Title);

        } else if (itemType == ITEM_Web) {

            Web_ViewHolder web_viewHolder = (Web_ViewHolder) holder;

            operations.setImage_In_imageView(item.getItemList_Logo_URL(),web_viewHolder.Web_Logo);
            operations.setTitle_In_textView(item.getItemList_Title_En(), item.getItemList_Title_Ar(), item.getItemList_Title_Or(), item.getItemList_Title_Ta(), savedLanguage, web_viewHolder.Web_Title);

        } else if (itemType == ITEM_News) {

            News_ViewHolder news_viewHolder = (News_ViewHolder) holder;

            operations.setImage_In_imageView(item.getItemList_Logo_URL(),news_viewHolder.News_Logo);
            operations.setTitle_In_textView(item.getItemList_Title_En(), item.getItemList_Title_Ar(), item.getItemList_Title_Or(), item.getItemList_Title_Ta(), savedLanguage, news_viewHolder.News_Title);

        }else {

            QRCode_ViewHolder qrCode_viewHolder = (QRCode_ViewHolder) holder;

            operations.setImage_In_imageView(item.getItemList_Logo_URL(),qrCode_viewHolder.QRCode_Logo);
            operations.setTitle_In_textView(item.getItemList_Title_En(), item.getItemList_Title_Ar(), item.getItemList_Title_Or(), item.getItemList_Title_Ta(), savedLanguage, qrCode_viewHolder.QRCode_Title);
            operations.setDiscount_In_textView(item.getItemList_Discount_Amount(),savedLanguage,qrCode_viewHolder.QRCode_Discount);

        }




    }




    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    @Override
    public int getItemViewType(int position) {

        ItemsModel item = itemsList.get(position);


        switch (item.getItem_Type()){

            case "QRcode":
                return ITEM_QRCode;

            case "Map":
                return ITEM_Map;

            case "Web":
                return ITEM_Web;

            case "News":
                return ITEM_News;

            default:
                return ITEM_QRCode;

        }



    }





    class QRCode_ViewHolder extends RecyclerView.ViewHolder {

        ImageView QRCode_Logo;
        TextView QRCode_Title, QRCode_Discount;

        public QRCode_ViewHolder(View itemView) {
            super(itemView);

            QRCode_Logo = itemView.findViewById(R.id.QRCode_Logo);
            QRCode_Title = itemView.findViewById(R.id.QRCode_Title);
            QRCode_Discount = itemView.findViewById(R.id.QRCode_Discount);


            //handel OnClickListener on Item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    // we put this condition because when we delete any item this take position[-1] ...... {RecyclerView.NO_POSITION = -1}
                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(position);

                    }
                }
            });

        }
    }

    class Map_ViewHolder extends RecyclerView.ViewHolder {

        ImageView Map_Logo;
        TextView Map_Title;

        public Map_ViewHolder(View itemView) {
            super(itemView);

            Map_Logo = itemView.findViewById(R.id.Map_Logo);
            Map_Title = itemView.findViewById(R.id.Map_Title);


            //handel OnClickListener on Item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    // we put this condition because when we delete any item this take position[-1] ...... {RecyclerView.NO_POSITION = -1}
                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(position);

                    }
                }
            });

        }
    }

    class Web_ViewHolder extends RecyclerView.ViewHolder {

        ImageView Web_Logo;
        TextView Web_Title;

        public Web_ViewHolder(View itemView) {
            super(itemView);

            Web_Logo = itemView.findViewById(R.id.Web_Logo);
            Web_Title = itemView.findViewById(R.id.Web_Title);


            //handel OnClickListener on Item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    // we put this condition because when we delete any item this take position[-1] ...... {RecyclerView.NO_POSITION = -1}
                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(position);

                    }
                }
            });

        }
    }

    class News_ViewHolder extends RecyclerView.ViewHolder {

        ImageView News_Logo;
        TextView News_Title;

        public News_ViewHolder(View itemView) {
            super(itemView);

            News_Logo = itemView.findViewById(R.id.News_Logo);
            News_Title = itemView.findViewById(R.id.News_Title);


            //handel OnClickListener on Item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    // we put this condition because when we delete any item this take position[-1] ...... {RecyclerView.NO_POSITION = -1}
                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(position);

                    }
                }
            });

        }
    }


    //onClick Interface to use [onClick method] in GasStationsAdmin Fragment
    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}

