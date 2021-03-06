package com.example.solom.hotel_csv_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.solom.hotel_csv_app.utils.PathUtil;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static String EXTRAS_CSV_PATH_NAME = "MainActivity.PathHolder";
    private static final int SMS_PERMISSION_CODE = 102;
    private static final String TAG = "PERMISSION";
    private static final int CSV_UPLOAD_REQUEST_CODE = 107;
    @BindView(R.id.readcsvfile)
    Button readCsvFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        readCsvFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, CSV_UPLOAD_REQUEST_CODE)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, CSV_UPLOAD_REQUEST_CODE);
                }
            }
        });
        if (checkPermission(Manifest.permission.SEND_SMS, SMS_PERMISSION_CODE)) {
            //Send SMS
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CSV_UPLOAD_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri pathUri = data.getData();
                    assert pathUri != null;
                    if (pathUri.toString().toLowerCase().contains(".csv")) {
                        String PathHolder = null;
                        try {
                            PathHolder = PathUtil.getPath(MainActivity.this, data.getData());
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        assert PathHolder != null;
                        String csvFileNname = PathHolder.substring(PathHolder.lastIndexOf('/')+1);
                        Toast.makeText(getApplicationContext(), csvFileNname, Toast.LENGTH_LONG).show();
                        Intent readAndDisplayIntent = new Intent(MainActivity.this, ReadAndDisplayActivity.class);
                        readAndDisplayIntent.putExtra(EXTRAS_CSV_PATH_NAME, PathHolder);
                        startActivity(readAndDisplayIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "File is not a csv", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    permission) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            if (requestCode == CSV_UPLOAD_REQUEST_CODE) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, CSV_UPLOAD_REQUEST_CODE);
            }
        }
    }
}
