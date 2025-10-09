package com.example.economix_android.Model.ahorro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.economix_android.databinding.FragmentAhorroBinding;

public class ahorroFragment extends Fragment {

    private FragmentAhorroBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ahorroViewModel ahorroViewModel =
                new ViewModelProvider(this).get(ahorroViewModel.class);

        binding = FragmentAhorroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAhorro;
        ahorroViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}