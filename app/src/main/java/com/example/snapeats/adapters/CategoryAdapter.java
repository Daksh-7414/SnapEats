package com.example.snapeats.adapters;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.interfaces.OnCategoryActionListener;
import com.example.snapeats.models.CategoriesModel;
import com.example.snapeats.ui.ViewCategoryActivity;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<CategoriesModel> categories_list;
    OnCategoryActionListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public CategoryAdapter(Context context, OnCategoryActionListener listener) {
        this.context = context;
        categories_list = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoriesModel categories_model = categories_list.get(position);

        Glide.with(context)
                .load(categories_model.getCategoryImage())
                .into(holder.category_image);

        holder.category_name.setText(categories_model.getCategoryName());

        // ✅ Highlight selected item
        if (position == selectedPosition) {
            holder.categoryBack.setCardBackgroundColor(context.getColor(R.color.coloraddicon));
        } else {
            holder.categoryBack.setCardBackgroundColor(context.getColor(R.color.white));
        }

        holder.categoryLayout.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) listener.onCategoryClick(categories_model);
        });
    }

    @Override
    public int getItemCount() {
        return categories_list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<CategoriesModel> categories) {
        this.categories_list.clear();
        this.categories_list.addAll(categories);
        notifyDataSetChanged();
        selectedPosition = RecyclerView.NO_POSITION;
    }

    // ✅ Helper function to select a category programmatically
    public void setSelectedCategory(String categoryName) {
        for (int i = 0; i < categories_list.size(); i++) {
            if (categories_list.get(i).getCategoryName().equalsIgnoreCase(categoryName)) {
                int previousSelected = selectedPosition;
                selectedPosition = i;
                if (previousSelected != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousSelected);
                }
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView category_name;
        ImageView category_image;
        ConstraintLayout categoryLayout;
        CardView categoryBack;

        public ViewHolder(View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.textView);
            category_image = itemView.findViewById(R.id.imageView3);
            categoryLayout = itemView.findViewById(R.id.categoryLayout);
            categoryBack = itemView.findViewById(R.id.categoryBack);
        }
    }
}
