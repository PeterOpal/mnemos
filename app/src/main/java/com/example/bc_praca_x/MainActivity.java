package com.example.bc_praca_x;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.View;

import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.databinding.ActivityMainBinding;
import com.example.bc_praca_x.helpers.Timer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends ActivitySetup implements FloatingButtonController {
    private ActivityMainBinding binding;
    AtomicReference<Intent> floatingButtonIntent;
    AtomicReference<Fragment> floatingButtonFragment;
    AtomicReference<Runnable> floatingButtonAction;
    FloatingActionButton addCategoryFloatingButton;
    Timer timer= new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        addCategoryFloatingButton = findViewById(R.id.addCategoryFloatingButton);
        floatingButtonIntent = new AtomicReference<>(new Intent(MainActivity.this, AddCategoryActivity.class));
        floatingButtonFragment = new AtomicReference<>();
        floatingButtonAction = new AtomicReference<>();

        binding.bottomNavigationView.setOnItemSelectedListener(item->{

            Fragment openedFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
            if (openedFragment instanceof CardPresenterFragment) {
                timer.pause();
                CustomDialog dialog = CustomDialog.pauseDialogInstance("pauseDialog");
                dialog.show(getSupportFragmentManager(), "pauseDialog");
                return false;
            }

            switch (item.getItemId()){
                case R.id.home_menu:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.import_menu:
                    replaceFragment(new ImportFragment());
                    break;
                case R.id.statics_menu:
                    replaceFragment(new StatisticsFragment());
                    break;
                case R.id.profile_menu:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
        });

        findViewById(R.id.addCategoryFloatingButton).setOnClickListener(v -> {
            if (floatingButtonIntent.get() != null) {
                startActivity(floatingButtonIntent.get());
                overridePendingTransition(0, 0);
                return;
            }

            if (floatingButtonFragment.get() != null){
                replaceFragment(floatingButtonFragment.get());
                return;
            }

            if (floatingButtonAction.get() != null) {
                floatingButtonAction.get().run();
            }
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, fragment);

        Fade exitFade = new Fade(Fade.MODE_IN);
        exitFade.setDuration(150);
        fragment.setEnterTransition(exitFade);

        if (fragmentManager.getBackStackEntryCount() > 0) { // if we are at the first fragment, we do not want to add it to the backstack
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        fragmentTransaction.commit();
    }

    //floating button implementation
    @Override
    public void updateFloatingButton(Intent intent, Fragment fragment, Runnable action, boolean isVisible, int iconResId) {
        if(intent != null) floatingButtonIntent.set(intent);
        else floatingButtonIntent = new AtomicReference<>();

        if(fragment != null){
            floatingButtonFragment.set(fragment);
        } else{
            floatingButtonFragment = new AtomicReference<>();
        }

        if (action != null) {
            floatingButtonAction.set(action);
        } else {
            floatingButtonAction.set(null);
        }

        addCategoryFloatingButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        if (isVisible && iconResId != 0) {
            addCategoryFloatingButton.setImageResource(iconResId);
        }
    }

    public void hideBottomNavigation() {
        binding.bottomNavigationView.setVisibility(View.GONE);
        addCategoryFloatingButton.setVisibility(View.GONE);
    }

    public void showBottomNavigation() {
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
        addCategoryFloatingButton.setVisibility(View.VISIBLE);
    }

    public Timer getTimer() {
        return timer;
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment != null) {
            updateBottomNavSelection(currentFragment);
        }
    }

    private void updateBottomNavSelection(Fragment fragment) {
        Menu menu = binding.bottomNavigationView.getMenu();

        if(fragment instanceof ImportFragment)  menu.findItem(R.id.import_menu).setChecked(true);
        else if (fragment instanceof StatisticsFragment) menu.findItem(R.id.statics_menu).setChecked(true);
        else if (fragment instanceof ProfileFragment) menu.findItem(R.id.profile_menu).setChecked(true);
        else  menu.findItem(R.id.home_menu).setChecked(true);
    }

}