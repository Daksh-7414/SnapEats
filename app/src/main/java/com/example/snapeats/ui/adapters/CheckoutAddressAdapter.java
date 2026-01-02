package com.example.snapeats.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapeats.R;
import com.example.snapeats.data.models.AddressModel;
import com.example.snapeats.ui.bottomsheets.AddAddressBottomSheet;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

public class CheckoutAddressAdapter
        extends RecyclerView.Adapter<CheckoutAddressAdapter.AddressViewHolder> {

    public interface OnAddressClick {
        void onEditClick(AddressModel item, int position);
    }

    private Context context;
    private List<AddressModel> addressList;
    private int selectedPosition = -1;
    private OnAddressClick listener;

    public CheckoutAddressAdapter(Context context, List<AddressModel> addressList, OnAddressClick listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_checkout_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {

        AddressModel model = addressList.get(position);

        holder.tvName.setText(model.getFullName());
        holder.tvPhone.setText(model.getPhoneNumber());
        holder.tvAddress.setText(model.getCompleteAddress());

        holder.radioSelect.setChecked(position == selectedPosition);

        View.OnClickListener clickListener = v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(selectedPosition);
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.radioSelect.setOnClickListener(clickListener);
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditClick(model,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public AddressModel getSelectedAddress() {
        if (selectedPosition != -1) {
            return addressList.get(selectedPosition);
        }
        return null;
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {

        MaterialRadioButton radioSelect;
        TextView tvName, tvPhone, tvAddress;
        ImageView ivEdit;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            radioSelect = itemView.findViewById(R.id.radioSelect);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivEdit = itemView.findViewById(R.id.ivEdit);
        }
    }
}
