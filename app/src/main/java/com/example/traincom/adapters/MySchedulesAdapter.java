package com.example.traincom.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traincom.ConfirmPage;
import com.example.traincom.Constants;
import com.example.traincom.EditBooking;
import com.example.traincom.R;
import com.example.traincom.models.Schedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MySchedulesAdapter extends RecyclerView.Adapter<MySchedulesAdapter.ViewHolder> {
    private List<Schedule> schedules;
    private Context context;

    Constants constants;

    @NonNull
    @Override
    public MySchedulesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySchedulesAdapter.ViewHolder holder, int position) {
        Schedule currentSchedule = schedules.get(position);

        holder.getTrainName().setText(currentSchedule.getTrainName());
        holder.getDate().setText(currentSchedule.getDate());
        holder.getStartTime().setText(currentSchedule.getStartTime());
        holder.getPax().setText(currentSchedule.getPax());

        holder.getDeleteBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBooking(currentSchedule.getId());
            }
        });

        holder.getEditBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the token in shared preferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putString("editReservationId", currentSchedule.getId());
                myEdit.apply();

                Intent i = new Intent(context, EditBooking.class);
                context.startActivity(i);
            }
        });
    }

    public MySchedulesAdapter(Context context ,List<Schedule> schedules) {
        this.schedules = schedules;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView trainName, date, startTime, pax;
        private final Button editBtn, deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            constants = new Constants();

            trainName = itemView.findViewById(R.id.trainNameMySchedule);
            date = itemView.findViewById(R.id.dateMySchedule);
            startTime = itemView.findViewById(R.id.startTimeMySchedule);
            pax = itemView.findViewById(R.id.paxMySchedule);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

        public TextView getTrainName() {
            return trainName;
        }

        public TextView getDate() {
            return date;
        }

        public TextView getStartTime() {
            return startTime;
        }

        public TextView getPax() {
            return pax;
        }

        public Button getEditBtn() { return editBtn; }

        public Button getDeleteBtn() { return deleteBtn; }
    }

    public void showToast(String text) {

        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteBooking(String id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Delete Reservation?");
        alert.setMessage("Are you sure you want to Delete this reservation?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OkHttpClient client = new OkHttpClient();

                // Build the POST request
                Request request = new Request.Builder()
                        .url(constants.getBaseUrl() + "/api/Reservation/delete?id=" +id)
                        .delete()
                        .addHeader("ngrok-skip-browser-warning", "2")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("Delete", e.toString());
                        showToast(e.toString());
                        dialogInterface.dismiss();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        Log.e("Delete res", responseBody);
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                showToast("Successfully Deleted");
                                dialogInterface.dismiss();
                            } else {
                                showToast("Delete Unsuccessful");
                                dialogInterface.dismiss();
                            }
                        } else {
                            showToast("Delete Unsuccessful");
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
}
