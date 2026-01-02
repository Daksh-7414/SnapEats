package com.example.snapeats.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.snapeats.R;
import com.example.snapeats.data.models.AddressModel;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {


    public interface OnAddressClick {
        void onEditClick(AddressModel item, int position);
        void onDeleteClick(AddressModel item, int position);
    }


    private List<AddressModel> addressList;
    private OnAddressClick listener;

    public AddressAdapter(List<AddressModel> addressList,OnAddressClick listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address = addressList.get(position);
        holder.bind(address);
        holder.eiditaddressbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditClick(address,position);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(address,position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLocationType, tvCompleteAddress, tvPhoneNumber, name;
        private ImageButton deleteBtn,eiditaddressbtn;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationType = itemView.findViewById(R.id.locationType);
            tvCompleteAddress = itemView.findViewById(R.id.completeAdd);
            name = itemView.findViewById(R.id.name);
            tvPhoneNumber = itemView.findViewById(R.id.PhoneNo);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            eiditaddressbtn = itemView.findViewById(R.id.eiditaddressbtn);
            //tvDefaultBadge = itemView.findViewById(R.id.tvDefaultBadge);
        }

        public void bind(AddressModel address) {
            // Set location type
            tvLocationType.setText(address.getLocationType());

            // Set complete address
//            String completeAddress = formatCompleteAddress(address);
            tvCompleteAddress.setText(address.getCompleteAddress());

            // Set phone number
            tvPhoneNumber.setText(address.getPhoneNumber());
            name.setText(address.getFullName());


            // Show default badge if this is the default address
//            if (address.getIsDefault()) {
//                tvDefaultBadge.setVisibility(View.VISIBLE);
//            } else {
//                tvDefaultBadge.setVisibility(View.GONE);
//            }
        }

//        private String formatCompleteAddress(AddressModel address) {
//            StringBuilder completeAddress = new StringBuilder();
//
//            if (address.getHouseNumber() != null && !address.getHouseNumber().isEmpty()) {
//                completeAddress.append(address.getHouseNumber());
//            }
//
//            if (address.getStreet() != null && !address.getStreet().isEmpty()) {
//                if (completeAddress.length() > 0) completeAddress.append(", ");
//                completeAddress.append(address.getStreet());
//            }
//
//            if (address.getCity() != null && !address.getCity().isEmpty()) {
//                if (completeAddress.length() > 0) completeAddress.append(", ");
//                completeAddress.append(address.getCity());
//            }
//
//            if (address.getState() != null && !address.getState().isEmpty()) {
//                if (completeAddress.length() > 0) completeAddress.append(", ");
//                completeAddress.append(address.getState());
//            }
//
//            if (address.getPinCode() != null && !address.getPinCode().isEmpty()) {
//                if (completeAddress.length() > 0) completeAddress.append(" - ");
//                completeAddress.append(address.getPinCode());
//            }
//
//            return completeAddress.toString();
//        }
    }
}