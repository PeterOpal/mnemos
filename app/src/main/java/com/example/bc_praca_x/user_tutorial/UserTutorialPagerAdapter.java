package com.example.bc_praca_x.user_tutorial;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserTutorialPagerAdapter extends FragmentStateAdapter {
    public UserTutorialPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TutorialFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}