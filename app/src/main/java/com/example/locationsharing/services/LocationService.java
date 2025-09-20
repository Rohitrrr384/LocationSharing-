package com.example.locationsharing.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.locationsharing.R;
import com.example.locationsharing.models.LocationData;
import com.google.android.gms.location.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 2000;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("locations");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    updateLocationInFirebase(location);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            userId = intent.getStringExtra("userId");
            startLocationUpdates();
        }

        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(TAG, "Lost location permission. " + e);
        }
    }

    private void updateLocationInFirebase(Location location) {
        if (userId != null) {
            LocationData locationData = new LocationData(
                    userId,
                    location.getLatitude(),
                    location.getLongitude(),
                    System.currentTimeMillis(),
                    true
            );
            databaseReference.child(userId).setValue(locationData);
            Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Location Service Channel", NotificationManager.IMPORTANCE_LOW);
            serviceChannel.setDescription("Channel for location sharing service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Sharing")
                .setContentText("Sharing your location...")
                .setSmallIcon(R.drawable.ic_notification)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);

        if (userId != null) {
            databaseReference.child(userId).child("isSharing").setValue(false).addOnCompleteListener(task -> stopSelf());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
