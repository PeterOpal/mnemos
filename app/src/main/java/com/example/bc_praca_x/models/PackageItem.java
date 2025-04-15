package com.example.bc_praca_x.models;

import com.example.bc_praca_x.helpers.FilterableItem;

public class PackageItem implements FilterableItem {

    private long id;
    private String packageName;
    private int packageItemsCount;
    private boolean favorite;
    private String lastLearnedDate;
    private String itemsCount;

    public PackageItem(long id, String packageName, int packageItemsCount, boolean favorite, String lastLearnedDate, String itemsCount) {
        this.id = id;
        this.packageName = packageName;
        this.packageItemsCount = packageItemsCount;
        this.favorite = favorite;
        this.lastLearnedDate = lastLearnedDate;
        this.itemsCount = itemsCount;
    }

    public long getId() {
        return id;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getPackageItemsCount() {
        return packageItemsCount;
    }

    @Override
    public String getTitle() { return packageName;}

    public boolean isFavorite() { return favorite; }

    @Override
    public String getSubtitle() {
        return "0";
    }

    public void setPackageItemsCount(int packageItemsCount) {
        this.packageItemsCount = packageItemsCount;
    }

    public String getLastLearnedDate() {
        return lastLearnedDate;
    }

    public void setLastLearnedDate(String lastLearnedDate) {
        this.lastLearnedDate = lastLearnedDate;
    }

    public String getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(String itemsCount) {
        this.itemsCount = itemsCount;
    }
}
