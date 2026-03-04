package com.example.economix_android.Model.ingresos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ingresosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ingresosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}