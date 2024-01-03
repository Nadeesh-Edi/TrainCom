package com.example.traincom.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traincom.Constants;
import com.example.traincom.EmptyListActivity;
import com.example.traincom.LoginActivity;
import com.example.traincom.R;
import com.example.traincom.SchedulesActivity;
import com.example.traincom.adapters.MySchedulesAdapter;
import com.example.traincom.databinding.FragmentDashboardBinding;
import com.example.traincom.models.Schedule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.gson.reflect.TypeToken;

public class DashboardFragment extends Fragment {
    Constants constants;
    ProgressBar progressBar;
    private MySchedulesAdapter adapter;
    RecyclerView schedulesList;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        progressBar = binding.progressBar;

        setProgressBar(false);

        schedulesList = binding.myBookingsList;

        constants = new Constants();
        getMySchedules();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setProgressBar(boolean isVisible) {
        requireActivity().runOnUiThread(new Runnable() {
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

    public void getMySchedules() {
        setProgressBar(true);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES) // read timeout
                .build();    // socket timeout
//        Get the userId
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        // Build the POST request
        Request request = new Request.Builder()
                .url(constants.getBaseUrl() + "/api/Reservation/getByUser?id=" + token)
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
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new MySchedulesAdapter(requireContext() ,schedules);
                                    schedulesList.setAdapter(adapter);
                                    schedulesList.setLayoutManager(new LinearLayoutManager(requireActivity()));
                                }
                            });
                        } else {
                            Intent i = new Intent(getContext(), EmptyListActivity.class);
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