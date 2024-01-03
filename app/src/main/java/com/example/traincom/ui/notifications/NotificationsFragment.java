package com.example.traincom.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.traincom.Constants;
import com.example.traincom.EditProfile;
import com.example.traincom.LoginActivity;
import com.example.traincom.SchedulesActivity;
import com.example.traincom.databinding.FragmentNotificationsBinding;

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

public class NotificationsFragment extends Fragment {
    TextView name, nic, email, phone, pwrd;
    Button deactivateMe, logout;
    ImageButton editProfile;
    Constants constants;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = binding.profileName;
        nic = binding.profileNic;
        email = binding.profileEmail;
        phone = binding.profilePhone;
        pwrd = binding.profilePassword;
        deactivateMe = binding.deactivateBtn;
        logout = binding.logoutBtn;
        editProfile = binding.editProfileBtn;

        constants = new Constants();

//      Get user info
        getInfo();

        deactivateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivateUcer();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireActivity(), EditProfile.class);
                requireActivity().startActivity(i);
            }
        });

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void logoutAndFinish() {
        Intent i = new Intent(requireActivity(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().startActivity(i);
        requireActivity().finish();
    }

    public void logout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Logout");
        alert.setMessage("Are you sure you want to logout?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putString("token", "");
                logoutAndFinish();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert.show();
    }

    public void deactivateUcer() {
        Log.e("deactivate", "Deactivate");
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Deactivate Me");
        alert.setMessage("Are you sure you want to deactivate your account? Once deactivated, You can no longer Login.");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sh = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                String token = sh.getString("token", "");

                OkHttpClient client = new OkHttpClient();

                JSONObject jsonBody = new JSONObject();
                // Build the request body
                RequestBody requestBody = RequestBody.create(
                        jsonBody.toString(), MediaType.parse("application/json"));

                // Build the POST request
                Request request = new Request.Builder()
                        .url(constants.getBaseUrl() + "/api/Traveller/deactivate?id=" + token)
                        .post(requestBody)
                        .addHeader("ngrok-skip-browser-warning", "2")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("Profile", e.toString());
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        Log.e("Profile res", responseBody);
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                Toast.makeText(getContext(), "Successfully Deactivated", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                                logoutAndFinish();
                            } else {
                                Toast.makeText(getContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        } else {
                            Toast.makeText(getContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    }
                });
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert.show();
    }

    public void getInfo() {
        SharedPreferences sh = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
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
                Log.e("Profile", e.toString());
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.e("Profile res", responseBody);
                if (response.isSuccessful()) {
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
                }
            }
        });
    }
}