package com.example.permissionsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayApps extends AppCompatActivity {
    private ListView appListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_display_apps);

            this.appListView = findViewById(R.id.applistview);
            this.appListView.setAdapter(createAdapter());
            this.appListView.setOnItemClickListener(this::showApplicationDetails);
        } catch (final RuntimeException e) {
            e.printStackTrace();
        }
    }

    private ArrayAdapter<String> createAdapter() {
        return new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getAppPackages());
    }

    private String[] getAppPackages() {
        return getIntent().getExtras().getStringArray("packages");
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
        return new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", selectedApp, null));
    }
}
