package com.example.bc_praca_x;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.bc_praca_x.adapters.PackageAdapter;
import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.database.entity.CardPack;
import com.example.bc_praca_x.database.viewmodel.ActivityViewModel;
import com.example.bc_praca_x.database.viewmodel.CardPackViewModal;
import com.example.bc_praca_x.database.viewmodel.CardViewModel;
import com.example.bc_praca_x.helpers.FilterItems;
import com.example.bc_praca_x.models.PackageItem;

import java.util.ArrayList;
import java.util.List;

public class CardPackage extends FragmentSetup implements FilterItems<PackageItem> {

    private CardPackViewModal cardPackViewModel;
    private ActivityViewModel activityViewModel;
    private CardViewModel cardViewModel;
    private long categoryId;
    private SearchView searchView;
    private List<PackageItem> items;
    private List<PackageItem> originalPackageItems;
    private PackageAdapter adapter;
    private String packTitle, lastSearchQuery="";
    private List<Long> cardPackIds;
    private Button filterButton;
    private TextView filterName;

    @Override
    public void onResume() {
        super.onResume();
        if (floatingButtonController != null) {
            floatingButtonController.updateFloatingButton(
                    new Intent(getActivity(), AddCardPack.class).putExtra("categoryId", categoryId),
                    null,
                    null,
                    true,
                    R.drawable.baseline_add_24
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            packTitle = args.getString("pack_title");
            categoryId = args.getLong("category_id");
        }
    }

    public CardPackage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card_package, container, false);
        originalPackageItems = new ArrayList<>();

        cardPackViewModel = new ViewModelProvider(this).get(CardPackViewModal.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        searchView = view.findViewById(R.id.packageSearchBar);
        filterButton = view.findViewById(R.id.packageFilterButton);
        filterName = view.findViewById(R.id.packageFilterName);

        setInfoBar(view);

        // PACKS - LISTING
        RecyclerView recyclerView = view.findViewById(R.id.package_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        items = new ArrayList<>();
        cardPackIds = new ArrayList<>();
        adapter = new PackageAdapter(items);
        recyclerView.setAdapter(adapter);

        loadPackages(view);
        setPackageItemListener(adapter);
        attachFilterListeners();
        attachFilterDialogListener(); //FROM Filter dialog, implemented in FilterItems interface
        return view;
    }

    private void loadPackages(View view){
        cardPackViewModel.getCardPacksByCategoryId(categoryId).observe(getViewLifecycleOwner(), cardPacks -> {
            if (cardPacks != null) {
                items.clear();
                originalPackageItems.clear();
                cardPackIds.clear();

                for (CardPack cardPack : cardPacks) {
                    PackageItem packageItem = new PackageItem(
                            cardPack.getId(),
                            cardPack.getName(),
                            0,
                            cardPack.isFavourite(),
                            "",
                            ""
                    );
                    items.add(packageItem);
                    cardPackIds.add(cardPack.getId());
                    originalPackageItems.add(packageItem);

                    cardViewModel.getCardCount(cardPack.getId()).observe(getViewLifecycleOwner(), count -> {

                        activityViewModel.getLastTimeLeartDate(cardPack.getId()).observe(getViewLifecycleOwner(), date -> {
                            packageItem.setPackageItemsCount(count);

                            if(date == null){
                                date = getString(R.string.never);
                            }

                            packageItem.setItemsCount(String.valueOf(count));
                            packageItem.setLastLearnedDate(getString(R.string.package_info_message, count, date));
                            adapter.notifyDataSetChanged();
                            filterList("", getFilterShowType(), getFilterSortType());
                        });

                    });
                }

                TextView timeSpentText = view.findViewById(R.id.learning_time_count_package);
                activityViewModel.getCategoryTotalTimeSpentFormatted(cardPackIds).observe(getViewLifecycleOwner(), timeSpentText::setText);
            }
        });
    }

    private void setInfoBar(View view){
        TextView packageName = view.findViewById(R.id.breadcrumbs);
        packageName.setText(packTitle);

        TextView packagesCount = view.findViewById(R.id.cards_count);
        cardPackViewModel.getActiveCardPackCountByCategoryId(categoryId).observe(getViewLifecycleOwner(), count -> {
            packagesCount.setText(String.valueOf(count));
        });
    }

    private void setPackageItemListener(PackageAdapter adapter) {
        adapter.setOnItemClickListener(item -> {
            //we need to check if the package has cards
            if (item.getPackageItemsCount()>0) {
                openPackageViewerFragment(item.getId(), item.getPackageName());
            } else {
                Intent cardBuilderIntent = new Intent(getActivity(), CardBuilderActivity.class);
                cardBuilderIntent.putExtra("cardPackId", item.getId());
                startActivityForResult(cardBuilderIntent, 1001);
            }
        });

        adapter.setOnItemLongClickListener((view, item) -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.category_popup_menu, popupMenu.getMenu());

            MenuItem categoryItem = popupMenu.getMenu().getItem(0);
            categoryItem.setTitle(item.getPackageName());

            MenuItem favouriteItem = popupMenu.getMenu().getItem(1);
            favouriteItem.setTitle(item.isFavorite() ? getString(R.string.remove_from_favories) : getString(R.string.add_to_favorites));

            MenuItem editItem = popupMenu.getMenu().getItem(2);
            editItem.setTitle(getString(R.string.edit_package));

            MenuItem deleteItem = popupMenu.getMenu().getItem(3);
            deleteItem.setTitle(getString(R.string.delete_package));

            //style for package title
            SpannableString boldTitle = new SpannableString(item.getPackageName());
            boldTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, boldTitle.length(), 0);
            categoryItem.setTitle(boldTitle);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getOrder()) {
                    case 1: //favourite
                        cardPackViewModel.setPackageFavorite(item.getId(), !item.isFavorite());
                        break;
                    case 2: //update
                        Intent intent = new Intent(getContext(), AddCardPack.class);
                        intent.putExtra("packName", item.getPackageName());
                        intent.putExtra("packId", item.getId());
                        intent.putExtra("categoryId", categoryId);
                        startActivity(intent);
                        break;
                    case 3: //delete
                        CustomDialog dialog = CustomDialog.deleteDialogInstance("package", item.getId(), item.getPackageName());
                        dialog.show(getChildFragmentManager(), "custom_dialog");
                        break;
                }
                return true;
            });

            popupMenu.show();
        });
    }

    //if card builder has been saved
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            long cardPackId = data.getLongExtra("cardPackId", -1);
            if (cardPackId != -1) {
                openPackageViewerFragment(cardPackId, "from card builder");
            }
        }
    }

    private void openPackageViewerFragment(long packId, String packageName) {
        Fragment packageViewerFragment = new PackageOverview();
        Bundle args = new Bundle();
        args.putLong("package_id", packId);
        args.putString("package_name", packageName);
        args.putString("category_name", packTitle);
        packageViewerFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, packageViewerFragment)
                .addToBackStack(null)
                .commit();
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
    public List<PackageItem> getItems() {
        return items;
    }

    @Override
    public List<PackageItem> getOriginalItems() {
        return originalPackageItems;
    }

    @Override
    public PackageAdapter getAdapter() {
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
        return "package";
    }
}