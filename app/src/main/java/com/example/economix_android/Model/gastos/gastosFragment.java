package com.example.economix_android.Model.gastos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.economix_android.databinding.FragmentAhorroBinding;

public class gastosFragment extends Fragment {

    private FragmentAhorroBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gastosViewModel gastosViewModel =
                new ViewModelProvider(this).get(gastosViewModel.class);

        binding = FragmentAhorroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAhorro;
        gastosViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}