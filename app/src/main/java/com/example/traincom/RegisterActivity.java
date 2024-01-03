package com.example.traincom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    EditText name, nic, email, phone, pwrd, rePwrd;
    Button registerBtn;
    ProgressBar progressBar;
    Constants constants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.nameInput);
        nic = findViewById(R.id.nicInput);
        email = findViewById(R.id.emailInput);
        phone = findViewById(R.id.pwrdInput);
        pwrd = findViewById(R.id.pwrdInput);
        rePwrd = findViewById(R.id.rePwrdInput);
        registerBtn = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        constants = new Constants();

        setProgressBar(false);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredName = name.getText().toString();
                String enteredNic = nic.getText().toString();
                String enteredEmail = email.getText().toString();
                String enteredPhone = phone.getText().toString();
                String enteredPwrd = pwrd.getText().toString();
                String enteredRePwrd = rePwrd.getText().toString();

                register(enteredName, enteredNic, enteredEmail, enteredPhone, enteredPwrd, enteredRePwrd);
            }
        });
    }

    public void showToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, text, Toast.LENGTH_SHORT).show();
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

    private void register(String name, String nic, String email, String phone, String pwrd, String rePwrd) {
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
//            API call to register
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
                    .url(constants.getBaseUrl() + "/api/Traveller/register")
                    .post(requestBody)
                    .addHeader("ngrok-skip-browser-warning", "2")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    setProgressBar(false);
                    showToast(e.toString());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    setProgressBar(false);
                    if (response.code() == 201) {
                        showToast("Successfully registered");
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        showToast("Failed");
                    }
                }
            });
        }
    }
}