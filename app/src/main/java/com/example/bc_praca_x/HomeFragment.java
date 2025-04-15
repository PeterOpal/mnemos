package com.example.bc_praca_x;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.example.bc_praca_x.adapters.CategoryAdapter;
import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.database.POJO.CategoryWithImage;
import com.example.bc_praca_x.database.viewmodel.ActivityViewModel;
import com.example.bc_praca_x.database.viewmodel.CardPackViewModal;
import com.example.bc_praca_x.database.viewmodel.CategoryViewModel;
import com.example.bc_praca_x.helpers.FilterItems;
import com.example.bc_praca_x.models.CategoryItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends FragmentSetup implements FilterItems<CategoryItem> {
    private CategoryViewModel categoryViewModel;
    private CardPackViewModal cardPackViewModel;
    private ActivityViewModel activityViewModel;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryItems = new ArrayList<>();
    private List<CategoryItem> originalCategoryItems = new ArrayList<>();
    private SearchView searchView;
    private TextView filterName, noItems;
    private Button filterButton;
    private String lastSearchQuery = "";
    private RecyclerView recyclerView;


    @Override
    public void onResume() {
        super.onResume();

        if (floatingButtonController != null) {
            Intent addCategoryIntent = new Intent(getActivity(), AddCategoryActivity.class);
            floatingButtonController.updateFloatingButton(
                    addCategoryIntent,
                    null,
                    null,
                    true,
                    R.drawable.baseline_add_24);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        cardPackViewModel = new ViewModelProvider(this).get(CardPackViewModal.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        searchView = view.findViewById(R.id.packageSearchBar);
        filterName = view.findViewById(R.id.packageFilterName);
        noItems = view.findViewById(R.id.noItems);
        filterButton = view.findViewById(R.id.packageFilterButton);

        setInfoBar(view);

        recyclerView = view.findViewById(R.id.package_items);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new CategoryAdapter(categoryItems);
        recyclerView.setAdapter(adapter);

        loadCategories();

        setCategoryItemListener(adapter);
        attachFilterListeners();
        attachFilterDialogListener(); //FROM Filter dialog, implemented in FilterItems interface

        return view;
    }

    private void setInfoBar(View view){
        TextView categoryCount = view.findViewById(R.id.cards_count);
        categoryViewModel.getActiveCategoryCount().observe(getViewLifecycleOwner(), count -> {
            categoryCount.setText(String.valueOf(count));
        });

        TextView cardPackCount = view.findViewById(R.id.packs_count);
        cardPackViewModel.getActiveCardPackCount().observe(getViewLifecycleOwner(), count -> {
            cardPackCount.setText(String.valueOf(count));
        });

        TextView timeSpentText = view.findViewById(R.id.learning_time_count_package);
        activityViewModel.getTotalTimeSpentFormatted().observe(getViewLifecycleOwner(), time -> {
            timeSpentText.setText(String.valueOf(time));
        });

        TextView breadCrumbs = view.findViewById(R.id.breadcrumbs);
        double random = Math.random();
        if (random > 0.5) {
            breadCrumbs.setText(getString(R.string.hello) + ", " + settings.getSetting("username") + "!");
        }
    }

    private void loadCategories() {
        //LISTING - categories with images from media table
        categoryViewModel.getActiveCategoriesWithImage().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryItems.clear();
                originalCategoryItems.clear();

                for (CategoryWithImage category : categories) {
                    CategoryItem categoryItem = new CategoryItem(category.category.id, category.category.name, "0", category.media != null ? category.media.path : null, category.category.isFavorite(), "0");

                    if(category.category.description != null) categoryItem.setDescription(category.category.description);
                    categoryItems.add(categoryItem);
                    originalCategoryItems.add(categoryItem);

                    cardPackViewModel.getActiveCardPackCountByCategoryId(category.category.id).observe(getViewLifecycleOwner(), count -> {
                        categoryItem.setSubtitle(String.valueOf(count) + " " + getResources().getString(R.string.category_item_description_package));
                        categoryItem.setItemsCount(String.valueOf(count));
                        adapter.notifyDataSetChanged();
                        filterList("", getFilterShowType(), getFilterSortType());
                    });
                }

                noItems.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else{
                noItems.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void setCategoryItemListener(CategoryAdapter adapter) {
        adapter.setOnItemClickListener(item -> {
            Fragment packageFragment = new CardPackage();

            Bundle args = new Bundle();
            args.putString("pack_title", item.getTitle());
            args.putLong("category_id", item.getId());
            packageFragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, packageFragment)
                    .addToBackStack(null)
                    .commit();
        });


        adapter.setOnItemLongClickListener((v, item) -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.category_popup_menu, popupMenu.getMenu());

            MenuItem categoryItem = popupMenu.getMenu().getItem(0);
            categoryItem.setTitle(item.getTitle());

            MenuItem favouriteItem = popupMenu.getMenu().getItem(1);
            favouriteItem.setTitle(!item.isFavorite() ? getString(R.string.add_to_favorites) : getString(R.string.remove_from_favories));

            //style for category title
            SpannableString boldTitle = new SpannableString(item.getTitle());
            boldTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, boldTitle.length(), 0);
            categoryItem.setTitle(boldTitle);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getOrder()) {
                    case 1: //favourite
                        categoryViewModel.setCategoryFavourite(item.getId(), !item.isFavorite());
                        break;
                    case 2: //update
                        Intent intent = new Intent(getContext(), AddCategoryActivity.class);
                        intent.putExtra("category_id", item.getId());
                        intent.putExtra("category_name", item.getTitle());
                        intent.putExtra("category_description", item.getDescription());
                        intent.putExtra("category_image", item.getImagePath());
                        startActivity(intent);
                        break;
                    case 3: //delete
                        CustomDialog dialog = CustomDialog.deleteDialogInstance("category", item.getId(), item.getTitle());
                        dialog.show(getChildFragmentManager(), "custom_dialog");
                        break;
                }
                    return true;
            });

            popupMenu.show();
        });
    }

    //FILTER IMPLEMENTATION
    @Override
    public androidx.appcompat.widget.SearchView getSearchView() {
        return searchView;
    }

    @Override
    public MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }

    @Override
    public Button getFilterButton() {
        return filterButton;
    }

    @Override
    public FragmentManager getFragmentChild() {
        return getChildFragmentManager();
    }

    @Override
    public TextView getFilterName() {
        return filterName;
    }

    @Override
    public void setLastSearchQuery(String query) {
        lastSearchQuery = query;
    }

    @Override
    public List<CategoryItem> getItems() {
        return categoryItems;
    }

    @Override
    public List<CategoryItem> getOriginalItems() {
        return originalCategoryItems;
    }

    @Override
    public CategoryAdapter getAdapter() {
        return adapter;
    }

    @Override
    public Resources getResourcesForFilter() {
        return requireContext().getResources();
    }

    @Override
    public Context requireContextForFilter() {
        return getContext();
    }

    @Override
    public String getType() {
        return "categories";
    }
}
