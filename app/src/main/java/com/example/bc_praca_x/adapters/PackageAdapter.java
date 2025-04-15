package com.example.bc_praca_x.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bc_praca_x.R;
import com.example.bc_praca_x.models.PackageItem;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.CardViewHolder>{

    private List<PackageItem> packageItems;
    private PackageAdapter.OnItemClickListener listener;
    private PackageAdapter.OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(PackageItem item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view,PackageItem item);
    }

    public PackageAdapter(List<PackageItem> packageItems) {
        this.packageItems = packageItems;
    }

    @NonNull
    @Override
    public PackageAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);
        return new PackageAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageAdapter.CardViewHolder holder, int position) {
        PackageItem packageItem = packageItems.get(position);
        holder.packageName.setText(packageItem.getPackageName());
        holder.packageItemsCount.setText(packageItem.getLastLearnedDate());

        if (packageItem.isFavorite()) {
            holder.favourite.setVisibility(View.VISIBLE);
        } else {
            holder.favourite.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(packageItem);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(v, packageItem);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return packageItems.size();
    }

    public void setOnItemClickListener(PackageAdapter.OnItemClickListener listener) { this.listener = listener; }
    public void setOnItemLongClickListener(PackageAdapter.OnItemLongClickListener listener) { this.longClickListener = listener;}
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView packageName, packageItemsCount;
        ImageView favourite;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.cardPackageTitle);
            packageItemsCount = itemView.findViewById(R.id.infoBox);
            favourite = itemView.findViewById(R.id.favouritePackageStar);
        }
    }
}
