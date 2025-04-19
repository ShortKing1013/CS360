package com.example.project3cs360;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button loginBtn, registerBtn;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        dbHelper = new DBHelper(this);

        loginBtn.setOnClickListener(view -> login());
        registerBtn.setOnClickListener(view -> register());
    }

    private void login() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username.getText().toString(), password.getText().toString()});
        if (cursor.moveToFirst()) {
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void register() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO users (username, password) VALUES (?, ?)",
                new String[]{username.getText().toString(), password.getText().toString()});
        Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show();
    }
}
