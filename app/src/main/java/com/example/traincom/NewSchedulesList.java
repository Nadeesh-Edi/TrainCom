package com.example.traincom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traincom.adapters.MySchedulesAdapter;
import com.example.traincom.adapters.NewSchedulesAdapter;
import com.example.traincom.models.Schedule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewSchedulesList extends AppCompatActivity {
    RecyclerView schedulesList;
    ProgressBar progressBar;
    Constants constants;

    private NewSchedulesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_booking_list);

        constants = new Constants();
        progressBar = findViewById(R.id.progressBar);
        schedulesList = findViewById(R.id.createBookingList);
        setProgressBar(false);

        getData();
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

    public void getData() {
        setProgressBar(true);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES) // read timeout
                .build();    // socket timeout

//        Get the choices
        SharedPreferences sharedPreferences = getSharedPreferences("BookingChoices", Context.MODE_PRIVATE);
        String date = sharedPreferences.getString("date", "");
        String start = sharedPreferences.getString("start", "");
        String end = sharedPreferences.getString("end", "");
        String pax = sharedPreferences.getString("pax", "");

        // Build the GET request
        Request request = new Request.Builder()
                .url(constants.getBaseUrl() + "/api/Schedule/filter?start=" + start + "&end=" + end + "&date=" + date)
                .get()
                .addHeader("ngrok-skip-browser-warning", "2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("My Schedules", e.toString());
                setProgressBar(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    setProgressBar(false);
                    Log.e("Schedules res", responseBody);

                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Schedule>>() {}.getType();
                        List<Schedule> schedules = gson.fromJson(responseBody, listType);

                        if (schedules.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new NewSchedulesAdapter(NewSchedulesList.this, schedules);
                                    schedulesList.setAdapter(adapter);
                                    schedulesList.setLayoutManager(new LinearLayoutManager(NewSchedulesList.this));
                                }
                            });
                        } else {
                            Intent i = new Intent(NewSchedulesList.this, EmptyListActivity.class);
                            startActivity(i);
                        }
                    } else {

                    }
                } else {
                }
            }
        });
    }
}
