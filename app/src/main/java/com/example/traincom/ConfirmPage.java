package com.example.traincom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traincom.ui.home.HomeFragment;

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

public class ConfirmPage extends AppCompatActivity {
    Constants constants;
    TextView trainName, startStation, endStation, pax;
    Button submitBtn;
    String uid, train, date, scheduleId, start, end, paxNo;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);

        constants = new Constants();

        trainName = findViewById(R.id.trainNameConfirm);
        startStation = findViewById(R.id.startStationEdit);
        endStation = findViewById(R.id.endStationEdit);
        pax = findViewById(R.id.paxEdit);
        submitBtn = findViewById(R.id.EditPageBtn);
        progressBar = findViewById(R.id.progressBar);

        setData();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmBooking();
            }
        });
    }

    public void showToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConfirmPage.this, text, Toast.LENGTH_SHORT).show();
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

    public void setData() {
        SharedPreferences userSh = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        uid = userSh.getString("token", "");

        SharedPreferences sh = getSharedPreferences("BookingChoices", Context.MODE_PRIVATE);
        date = sh.getString("date", "");
        train = sh.getString("trainName", "");
        scheduleId = sh.getString("scheduleId", "");
        start = sh.getString("start", "");
        end = sh.getString("end", "");
        paxNo = sh.getString("pax", "");

        trainName.setText(train);
        startStation.setText(start);
        endStation.setText(end);
        pax.setText(paxNo);

        setProgressBar(false);
    }

    public void confirmBooking() {
        setProgressBar(true);
        AlertDialog.Builder alert = new AlertDialog.Builder(ConfirmPage.this);
        alert.setTitle("Reserve Seats?");
        alert.setMessage("Are you sure you want to Reserve seats in the selected train?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OkHttpClient client = new OkHttpClient();
                Log.e("scheduleId", scheduleId);

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("travellerId", uid);
                    jsonBody.put("scheduleId", scheduleId);
                    jsonBody.put("reservationStart", start);
                    jsonBody.put("reservationEnd", end);
                    jsonBody.put("pax", paxNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Build the request body
                RequestBody requestBody = RequestBody.create(
                        jsonBody.toString(), MediaType.parse("application/json"));

                // Build the POST request
                Request request = new Request.Builder()
                        .url(constants.getBaseUrl() + "/api/Reservation/create")
                        .post(requestBody)
                        .addHeader("ngrok-skip-browser-warning", "2")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("Reserve", e.toString());
                        showToast(e.toString());
                        dialogInterface.dismiss();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        Log.e("Profile res", responseBody);
                        if (response.isSuccessful()) {
                            if (response.code() == 201) {
                                showToast("Reservation Successful");
                                setProgressBar(false);
                                dialogInterface.dismiss();

                                Intent i = new Intent(ConfirmPage.this, HomeFragment.class);
                                startActivity(i);
                            } else {
                                showToast(responseBody);
                                setProgressBar(false);
                                dialogInterface.dismiss();
                            }
                        } else {
                            showToast(responseBody);
                            setProgressBar(false);
                            dialogInterface.dismiss();
                        }
                    }
                });
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setProgressBar(false);
                dialogInterface.dismiss();
            }
        });

        alert.show();
    }
}