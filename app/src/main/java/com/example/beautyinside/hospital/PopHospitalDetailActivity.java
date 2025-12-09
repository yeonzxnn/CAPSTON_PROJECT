package com.example.beautyinside.hospital;

import android.content.Intent;
import android.widget.Button;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.beautyinside.R;
import com.example.beautyinside.BannerAdapter;
import com.example.beautyinside.DoctorListAdapter;
import com.example.beautyinside.ReviewAdapter;
import com.example.beautyinside.DoctorData;
import com.example.beautyinside.ReviewData;
import com.example.beautyinside.ReviewMoreActivity;
import java.util.Arrays;
import java.util.List;
import android.net.Uri;
import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.ImageView;
import com.example.beautyinside.HospitalData;
import com.example.beautyinside.FavoriteManager;

public class PopHospitalDetailActivity extends AppCompatActivity {

    private ViewPager2 bannerViewPager;
    private BannerAdapter bannerAdapter;
    private Handler handler = new Handler();
    private int currentPage = 0;
    private Runnable bannerRunnable;
    private List<Integer> imageList;
    private ImageButton buttonFavorite;
    private RecyclerView reviewRecyclerView;
    private RecyclerView recyclerDoctors;
    private TextView textHospitalName, textRating;
    private ReviewAdapter reviewAdapter;
    private DoctorListAdapter doctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_hospital_detail);

        //  툴바 설정 (이전 버튼 포함)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // 툴바를 액션바처럼 사용
        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // ← 누르면 뒤로가기

        // 후기 더보기 버튼
        Button showMore = findViewById(R.id.buttonShowMore);
        showMore.setOnClickListener(v -> {

            Intent intent = new Intent(PopHospitalDetailActivity.this, ReviewMoreActivity.class);
            intent.putExtra("hospitalName", "팝성형외과");
            startActivity(intent);
        });

        TextView textAddress = findViewById(R.id.textAddress);
        Button buttonCopyAddress = findViewById(R.id.buttonCopyAddress);
        ImageView imageMapPreview = findViewById(R.id.imageMapPreview);

// 주소 복사
        buttonCopyAddress.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("주소", textAddress.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "주소가 복사되었습니다", Toast.LENGTH_SHORT).show();
        });

// 지도 이미지 클릭 시 구글맵 이동
        imageMapPreview.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=서울 강남구 논현로 837 원방빌딩 1층");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });

        // 의료진 리스트

        recyclerDoctors = findViewById(R.id.recyclerDoctors);
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));
        List<DoctorData> doctors = Arrays.asList(
                // 1. 기존 김동길 원장
                new DoctorData(
                        "김동길",
                        R.drawable.doctor_pop_park, // 기존 이미지 (drawable 폴더에 있어야 함)
                        Arrays.asList("눈성형", "기타")
                ),
                // 🔥 2. 류안영 원장 추가
                new DoctorData(
                        "류안영",
                        R.drawable.doctor_pop_ryu, // 새 이미지 이름 설정 (drawable에 파일 필요)
                        Arrays.asList("리프팅", "눈성형", "기타") // 임시 전문 분야
                ),
                // 🔥 3. 서두헌 원장 추가
                new DoctorData(
                        "서두헌",
                        R.drawable.doctor_pop_seo, // 새 이미지 이름 설정 (drawable에 파일 필요)
                        Arrays.asList("리프팅", "눈성형", "기타") // 임시 전문 분야
                )
        );
        DoctorListAdapter doctorAdapter = new DoctorListAdapter(this, doctors);
        recyclerDoctors.setAdapter(doctorAdapter);

        // 3. 병원 이름, 별점, 찜 버튼 설정
        TextView textHospitalName = findViewById(R.id.textHospitalName);
        TextView textRating = findViewById(R.id.textRating);
        ImageButton buttonFavorite = findViewById(R.id.buttonFavorite);

        textHospitalName.setText("팝 성형외과");
        textRating.setText("★ 9.5");

        // 병원 정보 정의 (이름 + 배너 이미지 ID)
        HospitalData amondHospital = new HospitalData("팝 성형외과", R.drawable.pop_1);

// 최초 상태 반영
        final boolean[] isFavorite = {FavoriteManager.getInstance().isFavorite(amondHospital)};
        buttonFavorite.setImageResource(isFavorite[0] ? R.drawable.ic_heart_filled : R.drawable.ic_heart_border);

// 하트 클릭 시 동작
        buttonFavorite.setOnClickListener(v -> {
            isFavorite[0] = !isFavorite[0];

            if (isFavorite[0]) {
                buttonFavorite.setImageResource(R.drawable.ic_heart_filled);
                FavoriteManager.getInstance().addFavorite(amondHospital);
            } else {
                buttonFavorite.setImageResource(R.drawable.ic_heart_border);
                FavoriteManager.getInstance().removeFavorite(amondHospital);
            }
        });


        List<Integer> imageResIds = Arrays.asList(
                R.drawable.pop_1,
                R.drawable.pop_2,
                R.drawable.pop_3,
                R.drawable.pop_4,
                R.drawable.pop_5
        );



        bannerViewPager = findViewById(R.id.bannerViewPager);


        BannerAdapter adapter = new BannerAdapter(this, imageResIds, R.layout.item_banner);

        bannerViewPager.setAdapter(adapter);


        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                currentPage = (currentPage + 1) % imageResIds.size();
                bannerViewPager.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 2000); // ✅ 2초 간격
            }
        };
        handler.postDelayed(bannerRunnable, 2000);

    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(bannerRunnable); // memory leak 방지
    }
}
