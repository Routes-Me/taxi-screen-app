package com.example.routesapp.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.routesapp.Model.ItemType;
import com.example.routesapp.R;

import java.util.ArrayList;

public class OfficesAdapterMultibleViews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        }
        else {

            MyViewHolderChild vaultItemHolder = (MyViewHolderChild) holder;
            vaultItemHolder.itemName.setText(listItemArrayList.get(position).getItemName());

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

        public MyViewHolderChild(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.itemName);

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


    public void setOnItemClickListener(OfficesAdapterMultibleViews.OnItemClickListener listener){
        this.listener = listener;
    }


}