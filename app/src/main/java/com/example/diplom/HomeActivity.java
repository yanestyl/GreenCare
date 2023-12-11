package com.example.diplom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.diplom.preferences.AppPreferences;
import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.MapKitFactory;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {


    private AppPreferences appPreferences;

    private ViewPager2 viewPager;

    public void logOut(View view) {
        appPreferences.deleteAuthToken();
        appPreferences.deleteRefreshToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private class Adapter extends FragmentStateAdapter {

        private List<Fragment> list = new ArrayList<>();

        public void addFragment(Fragment fragment) {
            list.add(fragment);
        }

        public Adapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return list.get(position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private BottomNavigationView bottomNavigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            MapKitFactory.setApiKey("b6a9788e-08db-4cc2-b3d2-ae0caed353c5");
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        MapKitFactory.initialize(this);

        Intent intent1 = getIntent();
        if (getIntent() != null) {
            boolean isRegister = intent1.getBooleanExtra("register", false);
            if (isRegister) {
                startFAQ();
            }
        }

        appPreferences = new AppPreferences(this);

        if (appPreferences.getAuthToken() == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }



        viewPager = findViewById(R.id.container);
        bottomNavigationView = findViewById(R.id.bottom_menu);




        Adapter adapter = new Adapter(this);
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new ApplicationsFragment());
        adapter.addFragment(new SettingsFragment());
        viewPager.setAdapter(adapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_menu_home){
                viewPager.setCurrentItem(0, true);}
            else if (item.getItemId() == R.id.bottom_menu_applications){
                viewPager.setCurrentItem(1, true);}
            else if (item.getItemId() == R.id.bottom_menu_settings){
                viewPager.setCurrentItem(2, true);}
            return true;
        });


            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_home);
                            break;
                        case 1:
                            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_applications);
                            break;
                        case 2:
                            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_settings);
                            break;
                    }
                }
            });

        }

        private void startFAQ() {
            startActivity(new Intent(this, FAQActivity.class));
        }

}
