package com.example.bc_praca_x.user_tutorial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TutorialViewModel extends ViewModel {
    private final MutableLiveData<Boolean> addSampleContent = new MutableLiveData<>(false);

    public LiveData<Boolean> getAddSampleContent() {
        return addSampleContent;
    }

    public void setAddSampleContent(boolean value) {
        addSampleContent.setValue(value);
    }
}