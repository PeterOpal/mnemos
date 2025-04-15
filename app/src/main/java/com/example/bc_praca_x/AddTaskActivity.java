package com.example.bc_praca_x;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import com.example.bc_praca_x.database.entity.Task;
import com.example.bc_praca_x.database.viewmodel.CardPackViewModal;
import com.example.bc_praca_x.database.viewmodel.CategoryViewModel;
import com.example.bc_praca_x.database.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTaskActivity extends ActivitySetup implements AdapterView.OnItemSelectedListener, DatePickerFragment.DatePickerListener {

    private Spinner categorySelectSpinner, packageSelectSpinner;
    private ArrayList<String> categories, packages;
    private ArrayList<Long> categoryIds, packageIds;
    private ArrayAdapter<String> category_adapter, package_adapter;
    private CategoryViewModel categoryViewModel;
    private CardPackViewModal packageViewModel;
    private TaskViewModel taskViewModel;
    private Button selectDate;
    private String selectedDate;
    private EditText taskName;
    private long selectedPackageId, selectedCategoryId;
    private TextView categoryError, packageError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = findViewById(R.id.addTaskTool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        categorySelectSpinner = findViewById(R.id.categorySpinner);
        packageSelectSpinner = findViewById(R.id.packageSpinner);
        selectDate = findViewById(R.id.dateSelectButton);
        selectedDate = "";
        categorySelectSpinner.setOnItemSelectedListener(this);
        packageSelectSpinner.setOnItemSelectedListener(this);
        packageSelectSpinner.setEnabled(false);
        categoryViewModel = new CategoryViewModel(getApplication());
        taskViewModel = new TaskViewModel(getApplication());
        packageViewModel = new CardPackViewModal(getApplication());
        taskName = findViewById(R.id.customTaskName);

        categoryError = findViewById(R.id.categoryError);
        packageError = findViewById(R.id.packageError);

        categories = new ArrayList<>();
        categoryIds = new ArrayList<>();
        packages = new ArrayList<>();
        packageIds = new ArrayList<>();

        selectDate.setOnClickListener(v -> { openCalendar(); });

        category_adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        package_adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                packages
        );

        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        package_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySelectSpinner.setAdapter(category_adapter);
        packages.add(getString(R.string.first_select_category));

        packageSelectSpinner.setAdapter(package_adapter);

        getAllCategories();
    }

    private void getAllCategories(){
        categoryViewModel.getAllCategories().observe(this, categories -> {
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


    private void openCalendar() {
        DatePickerFragment newFragment = new DatePickerFragment();
        if(!selectedDate.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate);
            newFragment.setArguments(bundle);
        }

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //spinners / select
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinnerId = parent.getId();

        if (spinnerId == R.id.categorySpinner) {
            handleCategorySelection(position);
        } else if (spinnerId == R.id.packageSpinner) {
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
            packages.add(getString(R.string.first_select_category));
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


    @Override
    public void onDateSelected(int year, int month, int day) {
        selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
        selectDate.setText(selectedDate);
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_category_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveTask();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveTask(){
        if(valdate()){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(selectedDate.split("-")[0]),
                         Integer.parseInt(selectedDate.split("-")[1])-1,
                         Integer.parseInt(selectedDate.split("-")[2]));

            taskViewModel.insertTask(new Task(taskName.getText().toString(), calendar.getTime(), -1, false, selectedPackageId));

            finish();
        }
    }

    private boolean valdate(){

        if(taskName.getText().toString().isEmpty()){
            taskName.setError(getString(R.string.required));
            taskName.requestFocus();
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

        if(selectedDate.isEmpty()){
            selectDate.setError(getString(R.string.required));
            selectDate.callOnClick();
            return false;
        }

        return true;
    }
}