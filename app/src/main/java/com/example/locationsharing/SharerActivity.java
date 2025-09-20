package com.example.locationsharing;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.locationsharing.services.LocationService;
import com.example.locationsharing.utils.PermissionHelper;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import java.util.List;

public class SharerActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private TextView tvUserId, tvStatus;
    private Button btnStartSharing, btnStopSharing;
    private String userId;
    private boolean isSharing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharer);

        userId = getIntent().getStringExtra("userId");

        initViews();
        setupClickListeners();
        updateUI();
    }

    private void initViews() {
        tvUserId = findViewById(R.id.tvUserId);
        tvStatus = findViewById(R.id.tvStatus);
        btnStartSharing = findViewById(R.id.btnStartSharing);
        btnStopSharing = findViewById(R.id.btnStopSharing);

        tvUserId.setText("Sharing as: " + userId);
    }

    private void setupClickListeners() {
        btnStartSharing.setOnClickListener(v -> startLocationSharing());
        btnStopSharing.setOnClickListener(v -> stopLocationSharing());
    }

    @AfterPermissionGranted(PermissionHelper.LOCATION_PERMISSION_REQUEST_CODE)
    private void startLocationSharing() {
        if (PermissionHelper.hasLocationPermissions(this)) {
            if (!PermissionHelper.hasBackgroundLocationPermission(this)) {
                PermissionHelper.requestBackgroundLocationPermission(this);
                return;
            }

            Intent serviceIntent = new Intent(this, LocationService.class);
            serviceIntent.putExtra("userId", userId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            isSharing = true;
            updateUI();
            Toast.makeText(this, "Started sharing location", Toast.LENGTH_SHORT).show();
        } else {
            PermissionHelper.requestLocationPermissions(this);
        }
    }

    private void stopLocationSharing() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);

        isSharing = false;
        updateUI();
        Toast.makeText(this, "Stopped sharing location", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        if (isSharing) {
            tvStatus.setText("Status: Sharing Location");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnStartSharing.setEnabled(false);
            btnStopSharing.setEnabled(true);
        } else {
            tvStatus.setText("Status: Not Sharing");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnStartSharing.setEnabled(true);
            btnStopSharing.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Location permissions are required to share your location", Toast.LENGTH_LONG).show();
    }
}
