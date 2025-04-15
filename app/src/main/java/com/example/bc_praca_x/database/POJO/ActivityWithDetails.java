package com.example.bc_praca_x.database.POJO;

import androidx.room.Embedded;

import com.example.bc_praca_x.database.entity.Activity;
public class ActivityWithDetails {
    @Embedded
    public Activity activity;
    public String packageName;
    public String categoryName;
}
