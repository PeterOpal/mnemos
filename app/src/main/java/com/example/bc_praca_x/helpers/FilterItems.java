package com.example.bc_praca_x.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bc_praca_x.MainActivity;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.UserSettingsManager;
import com.example.bc_praca_x.custom_dialog.CustomDialog;

import java.util.Comparator;
import java.util.List;

public interface FilterItems<Type extends FilterableItem> {
    androidx.appcompat.widget.SearchView getSearchView();
    MainActivity getMainActivity();
    Button getFilterButton();
    FragmentManager getFragmentChild();
    TextView getFilterName();
    List<Type> getItems();
    List<Type> getOriginalItems();
    RecyclerView.Adapter<?> getAdapter();
    android.content.res.Resources getResourcesForFilter();
    Context requireContextForFilter();
    String getType();

    default String getFilterTypeSearch() {
        return getMainActivity().getResources().getString(R.string.filter_type_search_results);
    }
    default String getFilterTypeAll() {
        return getMainActivity().getResources().getString(R.string.filter_type_show_all);
    }

    default UserSettingsManager getSettings() {
        return new UserSettingsManager(requireContextForFilter());
    }

    default void attachFilterDialogListener(){
        getMainActivity().getSupportFragmentManager().setFragmentResultListener(
                "categories_filter", getMainActivity(), (requestKey, bundle) -> {
                    String filterText="";

                    String filterKey = bundle.getString("show");
                    String sortKey = bundle.getString("sort");
                    if (filterKey != null) {
                        int resId = getResourcesForFilter().getIdentifier(filterKey, "string", requireContextForFilter().getPackageName());
                        if (resId != 0) filterText = getResourcesForFilter().getString(resId);
                        else filterText = getResourcesForFilter().getString(R.string.filter_type_show_all);
                    }

                    if(sortKey != null) {
                        int resId = getResourcesForFilter().getIdentifier(sortKey, "string", requireContextForFilter().getPackageName());
                        if (resId != 0) filterText += " - " + getResourcesForFilter().getString(resId);
                        else filterText += " - " + "by newest";
                    }

                    setFilterText(filterText);
                    Log.d("FilterText", filterText);
                    filterList("", filterKey, sortKey);
                }
        );
    }

    default void attachFilterListeners(){
        getSearchView().setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getMainActivity().hideBottomNavigation();
            } else {
                getSearchView().postDelayed(() -> getMainActivity().showBottomNavigation(), 10);
            }
        });

        getSearchView().setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getMainActivity().hideBottomNavigation();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filterList(query, null, null);
                return true;
            }
        });

        getFilterButton().setOnClickListener(v -> {
            CustomDialog dialog = CustomDialog.filterDialogInstance("filterDialog", getType(), getFilterShowType(), getFilterSortType());
            dialog.show(getFragmentChild(), "custom_dialog");
        });
    }

    default void filterList(String query, String filterType, String sortType) {
        setLastSearchQuery(query);
        getItems().clear();

        if (query.isEmpty()) {
            getItems().addAll(getOriginalItems());
            setFilterText(getFilterTypeAll());
        } else {
            for (Type item : getOriginalItems()) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    getItems().add(item);
                }
            }
            setFilterText(getFilterTypeSearch());
        }

        if(filterType != null && sortType != null) {
            switch (filterType) {
                case "filter_type_favorites":
                    getItems().removeIf(item -> !item.isFavorite());
                    break;
                case "filter_type_non_empty":
                    getItems().removeIf(item -> item.getItemsCount().equals("0"));
                    break;
            }

            switch (sortType) {
                case "filter_sort_asc":
                    getItems().sort(Comparator.comparing(FilterableItem::getTitle, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "filter_sort_desc":
                    getItems().sort((o1, o2) -> o2.getTitle().compareToIgnoreCase(o1.getTitle()));
                    break;
                case "filter_sort_newest":
                    getItems().sort(Comparator.comparingLong(FilterableItem::getId).reversed());
                    break;
                case "filter_sort_oldest":
                    getItems().sort(Comparator.comparingLong(FilterableItem::getId));
                    break;
            }
            setFilterText(getFilterTranslation(filterType) + " - " + getFilterTranslation(sortType));
        }

        getAdapter().notifyDataSetChanged();
    }

    default void setFilterText(String filterType) {
        getFilterName().setText(filterType);
    }

    default String getFilterShowType(){
        Log.d("FilterItems", "getType: " + getType());
        Log.d("FilterItems", "getFilterShowType: " + getSettings().getSetting(getType() + "_filter_type"));
       return getSettings().getSetting(getType() + "_filter_type");
    }

    default String getFilterSortType(){
       return getSettings().getSetting(getType() + "_sort_type");
    }

    default String getFilterTranslation(String filterType) {
        int resId = getResourcesForFilter().getIdentifier(filterType, "string", requireContextForFilter().getPackageName());
        if (resId != 0) return getResourcesForFilter().getString(resId);
        else return getResourcesForFilter().getString(R.string.filter_type_show_all);
    }

    void setLastSearchQuery(String query);
}
