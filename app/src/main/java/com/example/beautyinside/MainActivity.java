package com.example.beautyinside;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.beautyinside.ui.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // ===============================
    // 권한 요청 결과 처리
    // ===============================
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "사진 권한 허용됨!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "사진 권한이 없어서 기능이 제한돼요 ㅠㅠ",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ===============================
    // onCreate
    // ===============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_favorite) {
                selectedFragment = new FavoriteFragment();
            } else if (itemId == R.id.nav_doctor) {
                selectedFragment = new DoctorFragment();
            } else if (itemId == R.id.nav_mypage) {
                if (SessionManager.isLoggedIn()) {
                    selectedFragment = new MyPageFragment();
                } else {
                    startActivity(
                            new Intent(MainActivity.this, LoginActivity.class));
                    return true;
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // 기본 화면
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }
    }
}
