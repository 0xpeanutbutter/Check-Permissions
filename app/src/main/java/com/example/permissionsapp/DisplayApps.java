package com.example.permissionsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayApps extends AppCompatActivity {
    private ListView appListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_apps);

        appListView = findViewById(R.id.applistview);
        final Bundle bundle = getIntent().getExtras();
        final String [] str = bundle.getStringArray("packages");
        try{
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(DisplayApps.this, android.R.layout.simple_list_item_1,str);
            appListView.setAdapter(adapter);
            } catch (Exception exception) {
            exception.printStackTrace();
        }
        appListView.setOnItemClickListener(this::showApplicationDetails);
    }

    private void showApplicationDetails(
            final AdapterView<?> parent,
            final View view,
            final int position,
            final long l) {
        final Intent settingIntent = createIntent(parent, position);
        startActivity(settingIntent);
    }

    private Intent createIntent(final AdapterView<?> parent, final int position) {
        final String selectedApp = (String) parent.getItemAtPosition(position);
        final Intent settingIntent = new Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", selectedApp, null));
        return settingIntent;
    }
}
