package com.example.myapplication.present.ui.fragment.home.detail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemStaffBinding;
import com.example.myapplication.domain.model.Staff;

import java.util.ArrayList;
import java.util.List;

public class ListStaffAdapter extends RecyclerView.Adapter<ListStaffAdapter.MyViewHolder> {
    private final String TAG = "StaffRecycleAdapterLog";

    private List<Staff> staffList;
    public ListStaffAdapter(List<Staff> staffList) {
        this.staffList = new ArrayList<>(staffList);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStaffBinding binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.bind(staff);
    }


    @Override
    public int getItemCount() {
        return staffList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemStaffBinding itemStaffBinding;


        public MyViewHolder(ItemStaffBinding itemStaffBinding) {
            super(itemStaffBinding.getRoot());
            this.itemStaffBinding = itemStaffBinding;
        }
        public void bind(Staff staff){
            itemStaffBinding.setStaff(staff);
        }
    }

}
