package com.example.bc_praca_x;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.database.viewmodel.CardPackViewModal;
import com.example.bc_praca_x.database.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImportFragment extends FragmentSetup implements AdapterView.OnItemSelectedListener {

    private ArrayList<String> categories, packages;
    private ArrayList<Long> categoryIds, packageIds;
    private CategoryViewModel categoryViewModel;
    private CardPackViewModal packageViewModel;
    private ArrayAdapter<String> category_adapter, package_adapter;
    private long selectedPackageId, selectedCategoryId;
    private Spinner categorySelectSpinner, packageSelectSpinner;
    private EditText sheetUrl;
    private String csvUrl;
    private TextView categoryError, packageError;
    private String stringFirstSelectCategory;
    private LinearLayout instructionBtn;

    public ImportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if (floatingButtonController != null) {
            floatingButtonController.updateFloatingButton(
                    null,
                    null,
                    this::fetchSpreadsheetData,
                    true,
                    R.drawable.baseline_save_24
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        stringFirstSelectCategory = getString(R.string.first_select_category);
        Log.d("ImportFragment", "onCreateView: " + stringFirstSelectCategory);

        categoryError = view.findViewById(R.id.import_category_error);
        packageError = view.findViewById(R.id.import_package_error);
        categorySelectSpinner = view.findViewById(R.id.import_category_spinner);
        packageSelectSpinner = view.findViewById(R.id.import_package_spinner);
        instructionBtn = view.findViewById(R.id.importTitleHolder);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        packageViewModel = new ViewModelProvider(this).get(CardPackViewModal.class);
        categories = new ArrayList<>();
        categoryIds = new ArrayList<>();
        packages = new ArrayList<>();
        packageIds = new ArrayList<>();
        sheetUrl = view.findViewById(R.id.sheets_url);


        instructionBtn.setOnClickListener(v -> {
            CustomDialog dialog = CustomDialog.instructionDialogInstance("instructionDialog");
            dialog.show(getChildFragmentManager(), "custom_dialog");
        });

        packageSelectSpinner.setEnabled(false);

        categorySelectSpinner.setOnItemSelectedListener(this);
        packageSelectSpinner.setOnItemSelectedListener(this);

        category_adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );

        package_adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                packages
        );

        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        package_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySelectSpinner.setAdapter(category_adapter);
        packages.add(stringFirstSelectCategory);

        packageSelectSpinner.setAdapter(package_adapter);
        getAllCategories();

        return view;
    }

    private void getAllCategories(){
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories.clear();
            this.categoryIds.clear();
            this.categories.add(getString(R.string.first_select_category));
            this.categoryIds.add(-1L);
            for (int i = 0; i < categories.size(); i++) {
                this.categories.add(categories.get(i).name);
                this.categoryIds.add(categories.get(i).id);
            }
            category_adapter.notifyDataSetChanged();
        });
    }

    private void fetchSpreadsheetData() {
        if(!validate()) return;

        CustomDialog dialog = CustomDialog.loadingSpinnerInstance("loadingSpinner");
        dialog.show(getChildFragmentManager(), "custom_dialog");

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, csvUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String importCount = settings.getSetting(UserSettingsManager.IMPORTED_FLASHCARDS);
                        int importCountInt = Integer.parseInt(importCount);
                        importCountInt += 1;
                        settings.saveSetting(UserSettingsManager.IMPORTED_FLASHCARDS, String.valueOf(importCountInt));

                        Intent intent = new Intent(getContext(), CardBuilderActivity.class);
                        intent.putExtra("csvData", response);
                        intent.putExtra("cardPackId", selectedPackageId);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ImportFragment", "onErrorResponse: " + error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinnerId = parent.getId();

        if (spinnerId == R.id.import_category_spinner) {
            handleCategorySelection(position);
        } else if (spinnerId == R.id.import_package_spinner) {
            handlePackageSelection(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void handleCategorySelection(int position) {
        selectedCategoryId = categoryIds.get(position);

        if (position == 0) {
            packageSelectSpinner.setEnabled(false);
            packages.clear();
            packages.add(stringFirstSelectCategory);
            package_adapter.notifyDataSetChanged();
        } else {
            packageSelectSpinner.setEnabled(true);
            loadPackagesForCategory(selectedCategoryId);
        }
    }

    private void handlePackageSelection(int position) {
        if (position > 0) {
            selectedPackageId = packageIds.get(position);
        } else {
            selectedPackageId = -1;
        }
    }

    private void loadPackagesForCategory(long catId) {
        packageViewModel.getCardPacksByCategoryId(catId).observe(this, packageList -> {
            packages.clear();
            packageIds.clear();

            if(packageList.isEmpty()){
                packages.add(getString(R.string.no_packages_in_this_category));
                packageIds.add(-1L);
                packageSelectSpinner.setEnabled(false);
            } else {
                packageIds.add(-1L);
                packages.add(getString(R.string.select_package_spinner));
                for (int i = 0; i < packageList.size(); i++) {
                    packages.add(packageList.get(i).name);
                    packageIds.add(packageList.get(i).id);
                }
                packageSelectSpinner.setEnabled(true);
            }
            package_adapter.notifyDataSetChanged();
        });
    }

    private boolean validate() {

        if(!networkIsOn()) {
            sheetUrl.setError(getString(R.string.internet_connection_error));
            sheetUrl.requestFocus();
            return false;
        }

        if (sheetUrl.getText().toString().isEmpty()) {
            sheetUrl.setError(getString(R.string.URL_required));
            sheetUrl.requestFocus();
            return false;
        }

        Pattern pattern = Pattern.compile("/d/([^/]+)");
        Matcher matcher = pattern.matcher(sheetUrl.getText().toString());

        if (matcher.find()) {
            String id = matcher.group(1);
            csvUrl = "https://docs.google.com/spreadsheets/d/" + id + "/export?format=csv&id=" + id + "&gid=0";
        } else {
            sheetUrl.setError(getString(R.string.invalid_URL_format));
            sheetUrl.requestFocus();
            return false;
        }

        if(selectedCategoryId == -1){
            categoryError.setVisibility(View.VISIBLE);
            return false;
        } else categoryError.setVisibility(View.GONE);

        if(selectedPackageId == -1){
            packageError.setVisibility(View.VISIBLE);
            return false;
        } else packageError.setVisibility(View.GONE);

        return true;
    }


    private boolean networkIsOn() {
        boolean returnValue = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                returnValue = true;
            }
        }

        return returnValue;
    }
}