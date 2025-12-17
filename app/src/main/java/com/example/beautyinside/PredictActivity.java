package com.example.beautyinside;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyinside.network.EyeApiService;
import com.example.beautyinside.network.RetrofitClient;
import com.example.beautyinside.network.InferResponse;
import com.example.beautyinside.ui.ResultActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE &&
                resultCode == Activity.RESULT_OK &&
                data != null) {

            File imageFile = uriToFile(data.getData());
            if (imageFile != null) {
                sendImageToServer(imageFile);
            } else {
                Toast.makeText(this, "이미지 처리 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            File file = File.createTempFile("upload_", ".jpg", getCacheDir());
            FileOutputStream out = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendImageToServer(File imageFile) {

        RequestBody req =
                RequestBody.create(imageFile, MediaType.parse("image/*"));

        MultipartBody.Part body =
                MultipartBody.Part.createFormData(
                        "file",
                        imageFile.getName(),
                        req
                );

        EyeApiService api = RetrofitClient.getApiService();

        Call<InferResponse> call = api.uploadImage(body);

        call.enqueue(new Callback<InferResponse>() {
            @Override
            public void onResponse(
                    Call<InferResponse> call,
                    Response<InferResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PredictActivity.this,
                            "서버 응답 오류",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                InferResponse body = response.body();

                String fullUrl =
                        RetrofitClient.BASE_URL + body.result_image_url;



                Intent intent =
                        new Intent(PredictActivity.this, ResultActivity.class);
                intent.putExtra("result_url", fullUrl);
                startActivity(intent);
            }

            @Override
            public void onFailure(
                    Call<InferResponse> call,
                    Throwable t) {

                Toast.makeText(PredictActivity.this,
                        "서버 연결 실패",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
