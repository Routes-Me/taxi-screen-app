package com.routesme.taxi_screen.java.Class;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.routesme.taxi_screen.kotlin.Model.ItemType;
import com.routesme.taxiscreen.R;
import java.util.ArrayList;

public class OfficesAdapterMultibleViews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int row_index = -1;
    private static final int LAYOUT_HEADER= 0, LAYOUT_ITEM_NORMAL = 1, LAYOUT_ITEM_MOST_RECENT = 2;
    private LayoutInflater inflater;
    private ArrayList<ItemType> listItemArrayList;
    private OfficesAdapterMultibleViews.OnItemClickListener listener;

    public OfficesAdapterMultibleViews(Context context, ArrayList<ItemType> listItemArrayList){
        inflater = LayoutInflater.from(context);
        this.listItemArrayList = listItemArrayList;
    }

    @Override
    public int getItemCount() {
        return listItemArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(listItemArrayList.get(position).isHeader()) {
            return LAYOUT_HEADER;
        }else{
            if (listItemArrayList.get(position).isNormalItem()){
                return LAYOUT_ITEM_NORMAL;
            }else {
                return LAYOUT_ITEM_MOST_RECENT;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType==LAYOUT_HEADER){
            View view = inflater.inflate(R.layout.header_row, parent, false);
            holder = new MyViewHolderHeader(view);
        }else if (viewType==LAYOUT_ITEM_NORMAL){
            View view = inflater.inflate(R.layout.normal_list_row, parent, false);
            holder = new MyViewHolderChild(view);
        }else {
            View view = inflater.inflate(R.layout.most_recent_list_row, parent, false);
            holder = new MyViewHolderChild(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType()== LAYOUT_HEADER) {
            MyViewHolderHeader vaultItemHolder = (MyViewHolderHeader) holder;
            vaultItemHolder.headerTitle.setText(listItemArrayList.get(position).getItemName());
        } else {
            final MyViewHolderChild vaultItemHolder = (MyViewHolderChild) holder;
            vaultItemHolder.itemName.setText(listItemArrayList.get(position).getItemName());
            vaultItemHolder.rowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index=position;
                    notifyDataSetChanged();
                    listener.onItemClick(position);
                }
            });
            if(row_index==position){
                vaultItemHolder.rowLayout.setBackgroundColor(Color.parseColor("#d8d8d8"));
            } else {
                vaultItemHolder.rowLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
    }


    class MyViewHolderHeader extends RecyclerView.ViewHolder{
        TextView headerTitle;
        public MyViewHolderHeader(View itemView) {
            super(itemView);
            headerTitle = (TextView) itemView.findViewById(R.id.headerTitle);
        }
    }

    class MyViewHolderChild extends RecyclerView.ViewHolder{

        TextView itemName;
        LinearLayout rowLayout;

        public MyViewHolderChild(View itemView) {
            super(itemView);

            itemName =  itemView.findViewById(R.id.itemName);
            rowLayout = itemView.findViewById(R.id.rowLayout);

        }

    }


    //onClick Interface to use [onClick method] in GasStationsAdmin Fragment
    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OfficesAdapterMultibleViews.OnItemClickListener listener){
        this.listener = listener;
    }


}