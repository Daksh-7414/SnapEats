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

import com.example.snapeats.R;
import com.example.snapeats.models.Categories_model;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<Categories_model> categories_list;

    public CategoryAdapter(Context context, ArrayList<Categories_model> categories_list) {
        this.context = context;
        this.categories_list = categories_list;
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
        Categories_model categories_model = categories_list.get(position);
        holder.category_image.setImageResource(categories_model.image);
        holder.category_name.setText(categories_model.name);
    }

    @Override
    public int getItemCount() {
        return categories_list.size();
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
