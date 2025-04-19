package com.example.project3cs360;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText dateInput, weightInput;
    Button addBtn, updateBtn, deleteBtn;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> dataList;
    int selectedId = -1;

    private static final int SMS_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dbHelper = new DBHelper(this);

        dateInput = findViewById(R.id.dateInput);
        weightInput = findViewById(R.id.weightInput);
        addBtn = findViewById(R.id.addBtn);
        updateBtn = findViewById(R.id.updateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        listView = findViewById(R.id.listView);

        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        loadData();

        addBtn.setOnClickListener(v -> {
            addData();
            checkAndSendSMS("New weight entry added!");
        });
        updateBtn.setOnClickListener(v -> updateData());
        deleteBtn.setOnClickListener(v -> deleteData());

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String[] parts = dataList.get(i).split(" - ");
            dateInput.setText(parts[0]);
            weightInput.setText(parts[1]);
            selectedId = i + 1;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    private void addData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO weights (date, weight) VALUES (?, ?)",
                new String[]{dateInput.getText().toString(), weightInput.getText().toString()});
        loadData();
    }

    private void updateData() {
        if (selectedId != -1) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE weights SET date = ?, weight = ? WHERE id = ?",
                    new String[]{dateInput.getText().toString(), weightInput.getText().toString(), String.valueOf(selectedId)});
            loadData();
        }
    }

    private void deleteData() {
        if (selectedId != -1) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("DELETE FROM weights WHERE id = ?", new String[]{String.valueOf(selectedId)});
            loadData();
        }
    }

    private void loadData() {
        dataList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM weights", null);
        while (cursor.moveToNext()) {
            dataList.add(cursor.getString(1) + " - " + cursor.getDouble(2));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void checkAndSendSMS(String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("1234567890", null, message, null, null);
            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}