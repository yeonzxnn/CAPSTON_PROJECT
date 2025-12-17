package com.example.beautyinside.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.beautyinside.R;

public class ResultActivity extends AppCompatActivity {

    private ImageView resultImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultImageView = findViewById(R.id.resultImageView);

        String imageUrl = getIntent().getStringExtra("result_url");

        if (imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(this, "결과 이미지 URL 없음", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔥 Glide 최소 버전 (에러 원인 제거)
        Glide.with(this)
                .load(imageUrl)
                .into(resultImageView);
    }
}
