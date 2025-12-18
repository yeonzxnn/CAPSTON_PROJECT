package com.example.beautyinside.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.beautyinside.R;

public class ResultActivity extends AppCompatActivity {

    private ImageView resultImageView;

    private TextView tvEyeType;
    private TextView tvSymmetry;
    private TextView tvSize;
    private TextView tvRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // ===============================
        // View 연결
        // ===============================
        resultImageView = findViewById(R.id.resultImageView);

        tvEyeType = findViewById(R.id.tvEyeType);
        tvSymmetry = findViewById(R.id.tvSymmetry);
        tvSize = findViewById(R.id.tvSize);
        tvRecommendation = findViewById(R.id.tvRecommendation);

        // ===============================
        // Intent 데이터 받기
        // ===============================
        Intent intent = getIntent();

        String imageUrl = intent.getStringExtra("result_url");
        String eyeType = intent.getStringExtra("eye_type");
        String symmetry = intent.getStringExtra("symmetry");
        String sizeRatio = intent.getStringExtra("size_ratio");
        String recommendation = intent.getStringExtra("recommendation");

        // ===============================
        // 결과 이미지 표시
        // ===============================
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(resultImageView);
        }

        // ===============================
        // 분석 결과 표시
        // ===============================
        if (eyeType != null) {
            tvEyeType.setText("눈매: " + eyeType);
        }

        if (symmetry != null) {
            tvSymmetry.setText("대칭: " + symmetry);
        }

        if (sizeRatio != null) {
            tvSize.setText("크기: " + sizeRatio);
        }

        if (recommendation != null) {
            tvRecommendation.setText("추천: " + recommendation);
        }
    }
}
