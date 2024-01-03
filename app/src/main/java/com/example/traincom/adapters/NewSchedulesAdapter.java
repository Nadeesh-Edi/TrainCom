package com.example.traincom.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traincom.ConfirmPage;
import com.example.traincom.Constants;
import com.example.traincom.NewSchedulesList;
import com.example.traincom.R;
import com.example.traincom.models.Schedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewSchedulesAdapter extends RecyclerView.Adapter<NewSchedulesAdapter.ViewHolder> {
    private List<Schedule> schedules;
    private Context context;
    Constants constants;

    @NonNull
    @Override
    public NewSchedulesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSchedulesAdapter.ViewHolder holder, int position) {
        Schedule currentSchedule = schedules.get(position);

        holder.getTrainName().setText(currentSchedule.getTrainName());
        holder.getDate().setText(currentSchedule.getDate());
        holder.getStartTime().setText(currentSchedule.getStartTime());
        holder.getPax().setText(currentSchedule.getPax());

        holder.getSubmitBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh = context.getSharedPreferences("BookingChoices", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sh.edit();

                myEdit.putString("trainName", currentSchedule.getTrainName());
                myEdit.putString("scheduleId", currentSchedule.getId());
                myEdit.apply();
                confirmBooking();
            }
        });
    }
    public NewSchedulesAdapter(Context context, List<Schedule> schedules) {
        this.schedules = schedules;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView trainName, date, startTime, pax;
        private final Button submitBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            constants = new Constants();

            trainName = itemView.findViewById(R.id.trainNameMySchedule);
            date = itemView.findViewById(R.id.dateMySchedule);
            startTime = itemView.findViewById(R.id.startTimeMySchedule);
            pax = itemView.findViewById(R.id.paxMySchedule);
            submitBtn = itemView.findViewById(R.id.reserveBtn);
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

        public Button getSubmitBtn() { return submitBtn; }
    }

    public void confirmBooking() {
        Intent i = new Intent(context, ConfirmPage.class);
        context.startActivity(i);
    }
}
