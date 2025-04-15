package com.example.bc_praca_x;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public interface FloatingButtonController {
    void updateFloatingButton(Intent intent, Fragment fragment, Runnable action, boolean isVisible, int iconResId);
}
