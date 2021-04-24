package com.example.emsapp.base;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.emsapp.R;

public abstract class BaseFragment extends Fragment {
    protected void doNavigate(@IdRes int navId, Bundle bundle) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(navId, bundle);
    }
}
