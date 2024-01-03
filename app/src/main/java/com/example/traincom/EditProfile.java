package com.example.traincom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traincom.ui.notifications.NotificationsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfile extends AppCompatActivity {
    EditText name, email, phone, pwrd, rePwrd;
    TextView nic;
    Button confirmBtn;
    Constants constants;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        phone = findViewById(R.id.profilePhone);
        pwrd = findViewById(R.id.profilePassword);
        rePwrd = findViewById(R.id.reProfilePassword);
        nic = findViewById(R.id.profileNic);
        confirmBtn = findViewById(R.id.confirmEditBtn);
        progressBar = findViewById(R.id.progressBar);

        constants = new Constants();

        // Get profile info
        getInfo();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredName = name.getText().toString();
                String enteredNic = nic.getText().toString();
                String enteredEmail = email.getText().toString();
                String enteredPhone = phone.getText().toString();
                String enteredPwrd = pwrd.getText().toString();
                String enteredRePwrd = rePwrd.getText().toString();

                confirmEdit(enteredName, enteredNic, enteredEmail, enteredPhone, enteredPwrd, enteredRePwrd);
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

    public void showToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EditProfile.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    Edit API call to edit profile
    public void confirmEdit(String name, String nic, String email, String phone, String pwrd, String rePwrd) {
        if (name.isEmpty()) {
            showToast("Please enter your name");
        } else if (nic.isEmpty()) {
            showToast("Please enter your NIC number");
        } else if (email.isEmpty()) {
            showToast("Please enter an email address");
        } else if (phone.isEmpty()) {
            showToast("Please enter a phone number");
        } else if (pwrd.isEmpty()) {
            showToast("Please enter a password");
        } else if (rePwrd.isEmpty()) {
            showToast("Please re-enter your password");
        } else if (!pwrd.equals(rePwrd)) {
            showToast("Passwords do not match");
        } else {
            setProgressBar(true);

            SharedPreferences sh = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String token = sh.getString("token", "");
            Log.e("token", token);

            OkHttpClient client = new OkHttpClient();
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("name", name);
                jsonBody.put("nic", nic);
                jsonBody.put("email", email);
                jsonBody.put("phone", phone);
                jsonBody.put("pwrd", pwrd);
                jsonBody.put("status", 1);
            } catch (JSONException e) {
                setProgressBar(false);
                e.printStackTrace();
            }

            // Build the request body
            RequestBody requestBody = RequestBody.create(
                    jsonBody.toString(), MediaType.parse("application/json"));

            // Build the POST request
            Request request = new Request.Builder()
                    .url(constants.getBaseUrl() + "/api/Traveller/edit?id=" + token)
                    .post(requestBody)
                    .addHeader("ngrok-skip-browser-warning", "2")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    setProgressBar(false);
                    Log.e("Profile", e.toString());
                    showToast(e.toString());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    Log.e("Profile res", responseBody);
                    if (response.isSuccessful()) {
                        setProgressBar(false);
                        if (response.code() == 200) {
                            showToast("Successfully Edited");

                            Intent i = new Intent(EditProfile.this, NotificationsFragment.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            setProgressBar(false);
                            showToast("Error");
                        }
                    } else {
                        showToast("Error");
                        setProgressBar(false);
                    }
                }
            });
        }
    }

//    Get Profile info
    public void getInfo() {
        setProgressBar(true);
        SharedPreferences sh = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String token = sh.getString("token", "");
        Log.e("token", token);

        OkHttpClient client = new OkHttpClient();

        // Build the POST request
        Request request = new Request.Builder()
                .url(constants.getBaseUrl() + "/api/Traveller/get?id=" + token)
                .get()
                .addHeader("ngrok-skip-browser-warning", "2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                setProgressBar(false);
                Log.e("Profile", e.toString());
                Toast.makeText(EditProfile.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.e("Profile res", responseBody);
                if (response.isSuccessful()) {
                    setProgressBar(false);
                    if (response.code() == 200) {
                        try {
                            JSONObject data = new JSONObject(responseBody);
                            Log.e("name", data.getString("name"));

                            name.setText(data.getString("name"));
                            nic.setText(data.getString("nic"));
                            email.setText(data.getString("email"));
                            phone.setText(data.getString("phone"));
                            pwrd.setText(data.getString("pwrd"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                    }
                } else {
                    setProgressBar(false);
                }
            }
        });
    }
}