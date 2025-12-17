package com.example.beautyinside;

import com.example.beautyinside.ui.LoginActivity;
import com.example.beautyinside.ui.ResultActivity;
import com.example.beautyinside.network.EyeApiService;
import com.example.beautyinside.network.RetrofitClient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.beautyinside.network.InferResponse;


public class HomeFragment extends Fragment {

    private static final String TAG = "AI_FLOW";

    private static final int REQUEST_PERMISSION = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    private static final int REQUEST_CODE_CAMERA = 102;

    private ViewPager2 bannerViewPager;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private int currentPage = 0;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("AI_FLOW", "BASE_URL = " + RetrofitClient.BASE_URL);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton btnAddImage = view.findViewById(R.id.btnAddImage);
        EditText searchBar = view.findViewById(R.id.search_bar);
        ImageView searchIcon = view.findViewById(R.id.search_icon);
        Button loginSignupButton = view.findViewById(R.id.btnLoginSignup);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);

        // ===== 배너 =====
        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner_1);
        bannerImages.add(R.drawable.banner_2);
        bannerImages.add(R.drawable.banner_3);
        bannerImages.add(R.drawable.banner_4);
        bannerImages.add(R.drawable.banner_5);

        BannerAdapter adapter =
                new BannerAdapter(requireContext(), bannerImages, R.layout.item_banner_image);
        bannerViewPager.setAdapter(adapter);

        sliderRunnable = () -> {
            currentPage = (currentPage + 1) % adapter.getItemCount();
            bannerViewPager.setCurrentItem(currentPage, true);
            sliderHandler.postDelayed(sliderRunnable, 3000);
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);

        loginSignupButton.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), LoginActivity.class)));

        searchIcon.setOnClickListener(v -> {
            String query = searchBar.getText().toString();
            if (!query.isEmpty()) {
                Intent intent =
                        new Intent(requireContext(), SearchResultActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
            }
        });

        btnAddImage.setOnClickListener(v -> requestImagePermissions());

        return view;
    }

    // ================= 권한 =================
    private void requestImagePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSION
                );
            } else {
                showImagePickerDialog();
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION
                );
            } else {
                showImagePickerDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showImagePickerDialog();
        } else {
            Toast.makeText(requireContext(), "권한 필요", Toast.LENGTH_SHORT).show();
        }
    }

    // ================= 이미지 선택 =================
    private void showImagePickerDialog() {
        String[] options = {"갤러리", "카메라"};

        new android.app.AlertDialog.Builder(requireContext())
                .setItems(options, (d, w) -> {
                    if (w == 0) openGallery();
                    else openCamera();
                }).show();
    }

    private void openGallery() {
        startActivityForResult(
                new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                REQUEST_CODE_GALLERY
        );
    }

    private void openCamera() {
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                REQUEST_CODE_CAMERA
        );
    }

    // ================= 결과 처리 =================
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {

        if (resultCode != Activity.RESULT_OK || data == null) return;

        if (requestCode == REQUEST_CODE_GALLERY) {
            File file = getFileFromUri(data.getData());
            if (file != null) sendImageToServer(file);
        }

        if (requestCode == REQUEST_CODE_CAMERA) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File file = saveBitmapToFile(bitmap);
            if (file != null) sendImageToServer(file);
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream in =
                    requireContext().getContentResolver().openInputStream(uri);
            File file =
                    new File(requireContext().getCacheDir(), "selected.jpg");

            FileOutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
            return file;

        } catch (Exception e) {
            Log.e(TAG, "파일 변환 실패", e);
            return null;
        }
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        try {
            File file =
                    new File(requireContext().getCacheDir(), "camera.jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return file;

        } catch (Exception e) {
            Log.e(TAG, "카메라 파일 저장 실패", e);
            return null;
        }
    }

    // ================= 서버 전송 (핵심) =================
    private void sendImageToServer(File imageFile) {

        Log.d(TAG, "sendImageToServer 호출됨");
        Log.d(TAG, "파일 경로: " + imageFile.getAbsolutePath());
        Log.d(TAG, "파일 크기: " + imageFile.length());

        RequestBody req =
                RequestBody.create(MediaType.parse("image/*"), imageFile);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData(
                        "file",
                        imageFile.getName(),
                        req
                );

        EyeApiService api = RetrofitClient.getApiService();

        api.uploadImage(body).enqueue(new Callback<InferResponse>() {

            @Override
            public void onResponse(
                    Call<InferResponse> call,
                    Response<InferResponse> response) {

                Log.d(TAG, "onResponse 진입");
                Log.d(TAG, "HTTP code = " + response.code());

                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "서버 오류: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                InferResponse body = response.body();

                if (body == null || body.result_image_url == null) {
                    Toast.makeText(requireContext(),
                            "결과 이미지 없음",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String fullUrl =
                        RetrofitClient.BASE_URL.replaceAll("/$", "")
                                + body.result_image_url;


                Log.d(TAG, "결과 이미지 URL = " + fullUrl);

                Intent intent =
                        new Intent(requireContext(), ResultActivity.class);
                intent.putExtra("result_url", fullUrl);
                startActivity(intent);

                Log.d(TAG, "ResultActivity 이동 완료");
            }

            @Override
            public void onFailure(
                    Call<InferResponse> call,
                    Throwable t) {

                Log.e(TAG, "onFailure 발생", t);
                Toast.makeText(requireContext(),
                        "서버 연결 실패",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}
