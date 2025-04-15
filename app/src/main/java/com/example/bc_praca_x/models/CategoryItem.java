package com.example.bc_praca_x.models;

import com.example.bc_praca_x.helpers.FilterableItem;

public class CategoryItem implements FilterableItem {
    private long id;
    private String title;
    private String subtitle;
    private String imagePath;
    private String description;
    private boolean isFavorite;
    private String itemsCount;

    public CategoryItem(long id, String title, String subtitle, String imagePath, boolean isFavorite, String itemsCount) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.imagePath = imagePath;
        this.isFavorite = isFavorite;
        this.itemsCount = itemsCount;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getImagePath() { return imagePath; }

    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getItemsCount() { return itemsCount; }
    public void setItemsCount(String itemsCount) { this.itemsCount = itemsCount; }

}
