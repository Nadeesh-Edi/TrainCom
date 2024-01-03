package com.example.traincom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditBooking extends AppCompatActivity {
    TextView trainName, date;
    EditText departure, arrival, pax;
    Button confirmBtn;
    Constants constants;
    ProgressBar progressBar;

    String resTrain, resDate, resDeparture, resArrival, resPax, reserveId, scheduleId, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        constants = new Constants();

        trainName = findViewById(R.id.trainNameEdit);
        date = findViewById(R.id.dateEdit);
        departure = findViewById(R.id.startStationEdit);
        arrival = findViewById(R.id.endStationEdit);
        pax = findViewById(R.id.paxEdit);
        confirmBtn = findViewById(R.id.EditPageBtn);
        progressBar = findViewById(R.id.progressBar);

        setProgressBar(false);

//        Get schedule data
        getScheduleById();
    }

    public void showToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EditBooking.this, text, Toast.LENGTH_SHORT).show();
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

    public void getScheduleById() {
        setProgressBar(true);
        OkHttpClient client = new OkHttpClient();

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        reserveId = sharedPreferences.getString("editReservationId", "");
        uid = sharedPreferences.getString("token", "");

        // Build the POST request
        Request request = new Request.Builder()
                .url(constants.getBaseUrl() + "/api/Reservation/get?id=" + reserveId)
                .get()
                .addHeader("ngrok-skip-browser-warning", "2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Profile", e.toString());
                showToast(e.toString());
                setProgressBar(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.e("Profile res", responseBody);
                setProgressBar(false);
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        try {
                            JSONObject data = new JSONObject(responseBody);

                            resTrain = data.getString("trainName");
                            resDate = data.getString("date");
                            resDeparture = data.getString("reservationStart");
                            resArrival = data.getString("reservationEnd");
                            resPax = data.getString("pax");
                            scheduleId = data.getString("scheduleId");

                            trainName.setText(resTrain);
                            date.setText(resDate);
                            departure.setText(resDeparture);
                            arrival.setText(resArrival);
                            pax.setText(resPax);;
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        showToast(responseBody);
                    }
                } else {
                    showToast(responseBody);
                }
            }
        });
    }

    public void editBooking(String depart, String arrival, int pax) {

    }
}