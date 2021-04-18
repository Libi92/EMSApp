package com.example.emsapp.db;


import com.example.emsapp.model.AppUser;

import java.util.List;

public interface UserListener {
    void onGetUser(AppUser appUser);

    void onListUser(List<AppUser> appUsers);
}
