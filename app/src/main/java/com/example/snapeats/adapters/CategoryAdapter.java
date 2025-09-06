package com.example.snapeats.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.models.CategoriesModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<CategoriesModel> categories_list;

    public CategoryAdapter(Context context) {
        this.context = context;
        categories_list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_activity,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoriesModel categories_model = categories_list.get(position);
        Glide.with(context)
                .load(categories_model.getCategoryImage())
                .into(holder.category_image);
        //holder.category_image.setImageResource(categories_model.getCategoryImage());
        holder.category_name.setText(categories_model.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return categories_list.size();
    }

    public void updateData(ArrayList<CategoriesModel> categories) {
        this.categories_list.clear();
        this.categories_list.addAll(categories);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView category_name ;
        ImageView category_image;

        public ViewHolder(View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.textView);
            category_image = itemView.findViewById(R.id.imageView3);
        }

    }
}
