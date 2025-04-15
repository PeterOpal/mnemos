package com.example.bc_praca_x.helpers;

public interface FilterableItem {
    String getTitle();
    boolean isFavorite();
    String getSubtitle();
    String getItemsCount();
    long getId();
}
