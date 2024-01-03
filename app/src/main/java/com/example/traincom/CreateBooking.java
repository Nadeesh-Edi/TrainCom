package com.example.traincom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateBooking extends AppCompatActivity {
    DatePicker createDate;
    EditText departure, arrival, pax;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_booking);

        createDate = findViewById(R.id.createDatePicker);
        departure = findViewById(R.id.startStation);
        arrival = findViewById(R.id.endStation);
        pax = findViewById(R.id.createPax);
        searchBtn = findViewById(R.id.filterBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = departure.getText().toString();
                String end = arrival.getText().toString();
                String persons = pax.getText().toString();

                if (start == "" || end == "" || persons == "") {
                    Toast.makeText(CreateBooking.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
//                  Save the input info in shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("BookingChoices", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    myEdit.putString("date", getCurrentDate());
                    myEdit.putString("start", start);
                    myEdit.putString("end", end);
                    myEdit.putString("pax", persons);
                    myEdit.apply();

                    Intent i = new Intent(CreateBooking.this, NewSchedulesList.class);
                    startActivity(i);
                }
            }
        });
    }

    public String getCurrentDate(){
        StringBuilder builder=new StringBuilder();
        builder.append(createDate.getDayOfMonth()+"-");
        builder.append((createDate.getMonth() + 1)+"-");
        builder.append(createDate.getYear());
        return builder.toString();
    }
}
