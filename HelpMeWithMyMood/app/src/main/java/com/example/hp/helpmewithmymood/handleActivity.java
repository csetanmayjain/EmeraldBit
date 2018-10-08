package com.example.hp.helpmewithmymood;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class handleActivity extends AppCompatActivity {

    private EditText username;
    private Context mContext;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle);
        username = findViewById(R.id.handle);
        spinner=findViewById(R.id.progressBar);
        mContext = handleActivity.this;
    }

    public void LetsGo(View view) {

        spinner.setVisibility(View.VISIBLE);

        Intent intent = new Intent(mContext,process.class);

        intent.putExtra("user",username.getText().toString());

        startActivity(intent);
    }
}
