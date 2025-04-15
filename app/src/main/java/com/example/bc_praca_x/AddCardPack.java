package com.example.bc_praca_x;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.bc_praca_x.database.entity.CardPack;
import com.example.bc_praca_x.database.viewmodel.CardPackViewModal;

public class AddCardPack extends ActivitySetup {
    private long categoryId, packId;
    private EditText packName;
    private String packNameValue;
    private RadioButton languagePackButton;
    private CardPackViewModal cardPackViewModel;
    private Spinner frontLanguageSpinner, backLanguageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_pack);

        Intent intent = getIntent();
        categoryId = intent.getLongExtra("categoryId", 0);
        packId = intent.getLongExtra("packId", -1);
        packNameValue = intent.getStringExtra("packName");

        //toolbar
        Toolbar toolbar = findViewById(R.id.addPackTool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        if(packId != -1) {
            getSupportActionBar().setTitle(R.string.edit_package);
        }

        //init
        cardPackViewModel = new CardPackViewModal(getApplication());
        packName = findViewById(R.id.packageName);
        languagePackButton = findViewById(R.id.languagePackButton);
        frontLanguageSpinner = findViewById(R.id.languageAspinner);
        backLanguageSpinner = findViewById(R.id.languageBspinner);

        if (packId != -1 && packNameValue != null) {
            packName.setText(packNameValue);
        }

        attachButtonListeners();
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
            if (validate()) savePack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePack() {
        if (packId != -1) {
            CardPack cardPack = new CardPack(packId,packName.getText().toString(), categoryId);
            cardPackViewModel.updateCardPack(cardPack);
        } else {
            CardPack cardPack = new CardPack(packName.getText().toString(), categoryId);
            cardPackViewModel.insert(cardPack);
        }
        finish();
    }

    private boolean validate() {
        if (packName.getText().toString().isEmpty()) {
            packName.setError(getString(R.string.required));
            packName.requestFocus();
            return false;
        }
        return true;
    }

    private void attachButtonListeners() {
        languagePackButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.frontLanguageTextView).setVisibility(TextView.VISIBLE);
                    backLanguageSpinner.setVisibility(Spinner.VISIBLE);
                    frontLanguageSpinner.setVisibility(Spinner.VISIBLE);
                    findViewById(R.id.backLanguageTextView).setVisibility(TextView.VISIBLE);
                }
            }
        });
    }
}