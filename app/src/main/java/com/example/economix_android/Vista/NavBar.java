package com.example.economix_android.Vista;

import android.os.Bundle;

import com.example.economix_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.economix_android.databinding.NavbarBinding;

public class NavBar extends AppCompatActivity {

    private NavbarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mobile_navigation, R.id.navigation_gastos, R.id.navigation_ingresos, R.id.navigation_ahorro, R.id.navigation_graficas)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_usuario);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}