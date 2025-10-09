package com.example.economix_android.Model.ahorro;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ahorroViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ahorroViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Ahorro fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}