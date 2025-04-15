package com.example.bc_praca_x.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.models.CategoryItem;

import java.io.File;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CardViewHolder> {
    private List<CategoryItem> categoryItems;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(CategoryItem item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view,CategoryItem item);
    }

    public CategoryAdapter(List<CategoryItem> cardItems) {
        this.categoryItems = cardItems;
    }

    @NonNull
    @Override
    public CategoryAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_category_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CategoryItem cardItem = categoryItems.get(position);
        holder.title.setText(cardItem.getTitle());
        holder.subtitle.setText(cardItem.getSubtitle());

        if(cardItem.getImagePath() == null) {
            holder.image.setImageResource(R.drawable.placeholder);
            holder.image.setPadding(20,20,20,20);
        } else {
            holder.image.setPadding(0,0,0,0);
            Glide.with(holder.itemView.getContext())
                    .load(new File(cardItem.getImagePath()))
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.image);
        }

        if(cardItem.isFavorite()) holder.favorite.setVisibility(View.VISIBLE);
        else holder.favorite.setVisibility(View.GONE);


        //click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(cardItem);
            }
        });

        //long click
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(v, cardItem);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) { this.longClickListener = listener;}

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView image, favorite;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardTitle);
            subtitle = itemView.findViewById(R.id.cardSubtitle);
            image = itemView.findViewById(R.id.block_image);
            favorite = itemView.findViewById(R.id.favouriteImage);
        }
    }
}
