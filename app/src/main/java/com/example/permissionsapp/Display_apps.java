package com.example.permissionsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class Display_apps extends AppCompatActivity {
    ListView appListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_apps);

        appListView=findViewById(R.id.applistview);
        Bundle bundle = getIntent().getExtras();
        String [] str = bundle.getStringArray("packages");
        try{
            final ArrayAdapter<String> adapter=new ArrayAdapter<>(Display_apps.this, android.R.layout.simple_list_item_1,str);
            appListView.setAdapter(adapter);
            } catch (Exception exception) {
            exception.printStackTrace();
        }
        appListView.setOnItemClickListener((parent, view, position, l) -> {
            String app_selected = (String) parent.getItemAtPosition(position);
            Intent settings_intent= new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",app_selected,null));
            startActivity(settings_intent);
        });
    }
    }
