package com.example.permissionsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.GET_META_DATA;

public class MainActivity extends AppCompatActivity {

    ArrayList <String> inputList = new ArrayList<>();
    List <String> chosen_per_list;
    Map<String, String> permApp = new HashMap<String, String>() {};

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listview);

        chosen_per_list= new ArrayList<>();
        Field[] fields = Manifest.permission.class.getFields();
        for (Field field : fields) {
            try {
                String permName = (String) field.get(Manifest.permission.class);
                PermissionInfo permissionInfo = this.getPackageManager().getPermissionInfo(permName, GET_META_DATA);
                String [] trimmedPerm = permissionInfo.name.split("\\.",3);
                if(trimmedPerm.length >=1){
                    permApp.put(trimmedPerm[trimmedPerm.length-1],permissionInfo.name);
                    inputList.add(trimmedPerm[trimmedPerm.length-1]);
                    Log.d("Value", trimmedPerm[trimmedPerm.length-1]);
                }

            }
            catch (PackageManager.NameNotFoundException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        inputList.add(0,"Get apps");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,inputList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, k, l) -> {

            String selected_perm= (String) parent.getItemAtPosition(k);
            if(selected_perm.equals("Get apps")) {
                String[] Permissions = chosen_per_list.toArray(new String[0]);
                try{
                    List<PackageInfo> listOfApplications = getPackageManager().getPackagesHoldingPermissions(Permissions,PackageManager.GET_META_DATA);
                    String[] packages = new String[listOfApplications.size()];
                    int j=0;
                    for(PackageInfo apps : listOfApplications) {
                        packages[j]=apps.packageName;
                        j++;
                    }
                    Log.e("packages",packages.length+"");
                    Intent intent = new Intent(MainActivity.this, DisplayApps.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("packages",packages);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
                catch (Exception e){
                    //
                }
                return;
            }
            chosen_per_list.add(permApp.get(selected_perm));
        });

        }

}



