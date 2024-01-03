package com.example.traincom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button loginBtn, directToRegister;
    ProgressBar progressBar;
    Constants constants;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        constants = new Constants();

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.pwrdInput);
        loginBtn = findViewById(R.id.loginBtn);
        directToRegister = findViewById(R.id.directRegister);
        progressBar = findViewById(R.id.progressBar);
        logo = findViewById(R.id.logoImage);

        AssetManager assetManager = getAssets();

        try {
            InputStream ims = assetManager.open("trainCom_icon.png");
            Drawable d = Drawable.createFromStream(ims, null);
            logo.setImageDrawable(d);
        } catch (IOException e) {
            Log.e("Image error", e.toString());
        }

        setProgressBar(false);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedEmail = email.getText().toString();
                String typedPwrd = password.getText().toString();

                login(typedEmail, typedPwrd);
            }
        });

        directToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public void showToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setProgressBar(boolean isVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isVisible) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void login(String uname, String pwrd) {
        setProgressBar(true);
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", uname);
            jsonBody.put("password", pwrd);
        } catch (JSONException e) {
            setProgressBar(false);
            e.printStackTrace();
        }

        // Build the request body
        RequestBody requestBody = RequestBody.create(
                jsonBody.toString(), MediaType.parse("application/json"));

        // Build the POST request
        Request request = new Request.Builder()
                .url(constants.getBaseUrl() + "/api/Traveller/login")
                .post(requestBody)
                .addHeader("ngrok-skip-browser-warning", "2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                setProgressBar(false);
                Log.e("Login", e.toString());
                showToast(e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                setProgressBar(false);
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    Log.e("Login res", responseBody);

                    if (response.code() == 200) {
//                      Save the token in shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();

                        myEdit.putString("token", responseBody);
                        myEdit.apply();

                        showToast("Login success");

                        Intent i = new Intent(LoginActivity.this, SchedulesActivity.class);
                        startActivity(i);
                    } else {
                        showToast(responseBody);
                    }
                } else {
                    showToast(responseBody);
                }
            }
        });
    }
}