package com.example.permissionsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static android.content.pm.PackageManager.GET_META_DATA;

public class MainActivity extends AppCompatActivity {

    private static final String GET_APPS_KEY = "Get apps";

    private Map<String, String> permissions;
    private List<String> chosenPerList;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView listView = findViewById(R.id.listview);

        // Reset the activity state
        this.chosenPerList = new ArrayList<>();
        this.permissions = findAppPermissions();

        final ArrayAdapter<String> adapter = createAdapter(permissions);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this::selectAppOrLoadPermissions);
    }

    private Map<String, String> findAppPermissions() {
        return Arrays.stream(Manifest.permission.class.getFields())
                .map(this::findPermissionName)
                .filter(Objects::nonNull)
                .sequential()
                .collect(Collectors.toMap(
                        MainActivity::extractId,
                        Function.identity(),
                        (a, b) -> {
                            throw new UnsupportedOperationException("No parallel reduction");
                        },
                        // Retain the order for consistency between different calls
                        LinkedHashMap::new));
    }

    private @Nullable
    String findPermissionName(final Field field) {
        try {
            final String permName = (String) field.get(Manifest.permission.class);
            final PermissionInfo permissionInfo = getPackageManager().getPermissionInfo(permName, GET_META_DATA);
            Log.d("permissions", "Adding permission " + permissionInfo.name);
            return permissionInfo.name;
        } catch (final PackageManager.NameNotFoundException | IllegalAccessException e) {
            Log.e("permissions", "Cannot list permission of " + field, e);
            return null; // Using null to indicate a value that cannot be processed
        }
    }

    private static String extractId(final String permissionName) {
        final String[] trimmedPerm = permissionName.split("\\.", 3);
        return trimmedPerm[trimmedPerm.length - 1];
    }

    private ArrayAdapter<String> createAdapter(
            final Map<String, String> permissions) {
        final List<String> inputList = new ArrayList<>();
        inputList.add(GET_APPS_KEY); // Set the special permission first in the list
        inputList.addAll(permissions.keySet());
        return new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                inputList);
    }

    private void selectAppOrLoadPermissions(
            final AdapterView<?> parent,
            final View view,
            final int position,
            final long l) {
        final String selected_perm = (String) parent.getItemAtPosition(position);
        if (selected_perm.equals(GET_APPS_KEY)) {
            showAppPermissions();
        } else {
            recordPermissionChoice(selected_perm);
        }
    }

    private void showAppPermissions() {
        try {
            final String[] packages = collectChosenPackages(this.chosenPerList);
            Log.e("packages", packages.length + "");
            final Intent intent = createIntent(packages);
            startActivity(intent);
        } catch (final RuntimeException e) {
            Log.e("packages", "Error fetching info", e);
        }
    }

    @NonNull
    private String[] collectChosenPackages(final List<String> chosenPermissions) {
        final String[] Permissions = chosenPermissions.toArray(new String[0]);
        final List<PackageInfo> listOfApplications =
                getPackageManager().getPackagesHoldingPermissions(
                        Permissions,
                        PackageManager.GET_META_DATA);
        return listOfApplications.stream()
                .map(app -> app.packageName)
                .toArray(String[]::new);
    }

    private Intent createIntent(String[] packages) {
        final Bundle bundle = new Bundle();
        bundle.putStringArray("packages", packages);

        final Intent intent = new Intent(this, DisplayApps.class);
        intent.putExtras(bundle);

        return intent;
    }

    private void recordPermissionChoice(final String choice) {
        this.chosenPerList.add(permissions.get(choice));
    }

}



