package com.rivers.headFoldingRecyclerView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rivers.headFoldingRecyclerView.R;
import com.rivers.headFoldingRecyclerView.model.ModelData;

import java.util.ArrayList;

/**
 * Created by Rivers on 2018/10/9.
 * Description:
 */

public class RecyclerAdapter extends RecyclerView.Adapter {

    private  Context mContext;
    private  ArrayList<ModelData> data;

    public RecyclerAdapter(Context context,ArrayList<ModelData> data) {
        this.data=data;
        this.mContext=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_layout, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemHolder itemHolder= (ItemHolder) holder;
        ModelData modelData = data.get(position);
        itemHolder.title.setText(modelData.title);
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView title;
        public ItemHolder(View view){
            super(view);
            this.title=view.findViewById(R.id.title);
        }
    }
}
