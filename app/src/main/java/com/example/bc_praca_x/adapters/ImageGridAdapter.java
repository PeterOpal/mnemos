package com.example.bc_praca_x.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.database.entity.Media;

import java.util.List;

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder>{

    private List<Media> images;
    private Context context;
    private OnImageSelectedListener listener;
    private int selectedPosition = -1;

    public interface OnImageSelectedListener {
        void onImageSelected(String mediaID, int position);
    }

    public ImageGridAdapter(Context context, List<Media> images, OnImageSelectedListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String path = images.get(position).path;
        Glide.with(context)
                .load(path)
                //.placeholder(R.drawable.placeholder)
                //.error(R.drawable.error)
                .into(holder.imageView);

        // HIGHLIGHT SELECTED
        if (position == selectedPosition) {
            holder.selectionOverlay.setVisibility(View.VISIBLE);
        } else {
            holder.selectionOverlay.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onImageSelected(images.get(position).path, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View selectionOverlay;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            selectionOverlay = itemView.findViewById(R.id.selectionOverlay);
        }
    }
}
