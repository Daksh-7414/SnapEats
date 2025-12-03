package com.example.snapeats.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapeats.R;
import com.example.snapeats.models.OfferModel;

import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private ArrayList<OfferModel> offerList;
    private static final int MULTIPLIER = 1000;

    public OfferAdapter(ArrayList<OfferModel> offerList) {
        this.offerList = offerList;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        // Fix: Calculate actual position and use offerList (not offer)
        int actualPosition = position % offerList.size(); // Use offerList, not offer
        OfferModel offer = offerList.get(actualPosition); // Get from offerList

        holder.offerfoodImage.setImageResource(offer.getImageRes());
        holder.specialOffer.setText(offer.getTitle());
        holder.specialOfferText.setText(offer.getSubTitle());

        GradientDrawable bg = (GradientDrawable) holder.specialCard.getBackground().mutate();

        if (actualPosition == 0) {
            bg.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.offercard_color_1));
        }
        else if (actualPosition == 1) {
            bg.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.offercard_color_2));
        }
        else if (actualPosition == 2) {
            bg.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.offercard_color_3));
        }
        else if (actualPosition == 3) {
            bg.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.offercard_color_4));
        }

        //Log.d("position value out", String.valueOf(position));

    }

    @Override
    public int getItemCount() {
        return offerList.size() * MULTIPLIER;
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView offerfoodImage;
        TextView specialOffer;
        TextView specialOfferText;
        RelativeLayout specialCard;
        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            offerfoodImage = itemView.findViewById(R.id.offerfoodImage);
            specialOffer = itemView.findViewById(R.id.specialOffer);
            specialOfferText = itemView.findViewById(R.id.specialOfferText);
            specialCard = itemView.findViewById(R.id.specialCard);
        }
    }
}
