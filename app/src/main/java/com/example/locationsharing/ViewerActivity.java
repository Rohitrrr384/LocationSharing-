package com.example.locationsharing;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.locationsharing.models.LocationData;
import com.google.firebase.database.*;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewerActivity extends AppCompatActivity {
    private MapView mapView;
    private TextView tvUserInfo, tvLastUpdate;
    private String userId;
    private DatabaseReference databaseReference;
    private ValueEventListener locationListener;
    private Marker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        userId = getIntent().getStringExtra("userId");

        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));
        initViews();
        setupMap();
        setupFirebaseListener();
    }

    private void initViews() {
        mapView = findViewById(R.id.mapView);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);

        tvUserInfo.setText("Tracking: " + userId);
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        GeoPoint defaultLocation = new GeoPoint(12.9716, 77.5946);
        mapView.getController().setCenter(defaultLocation);
    }

    private void setupFirebaseListener() {
        databaseReference = FirebaseDatabase.getInstance().getReference("locations").child(userId);

        locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationData locationData = dataSnapshot.getValue(LocationData.class);
                if (locationData != null) {
                    updateMapWithLocation(locationData);
                } else {
                    Toast.makeText(ViewerActivity.this, "No location data found for user: " + userId, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewerActivity.this, "Failed to load location data", Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(locationListener);
    }

    private void updateMapWithLocation(LocationData locationData) {
        GeoPoint userLocation = new GeoPoint(locationData.getLatitude(), locationData.getLongitude());

        if (userMarker != null) mapView.getOverlays().remove(userMarker);

        userMarker = new Marker(mapView);
        userMarker.setPosition(userLocation);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setTitle(userId);

        String status = locationData.isSharing() ? "Online" : "Offline";
        userMarker.setSnippet("Status: " + status);

        Drawable icon = ContextCompat.getDrawable(this,
                locationData.isSharing() ? R.drawable.ic_location_on : R.drawable.ic_location_off);
        userMarker.setIcon(icon);

        mapView.getOverlays().add(userMarker);
        mapView.getController().animateTo(userLocation);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        tvLastUpdate.setText("Last Update: " + sdf.format(new Date(locationData.getTimestamp())));
        tvLastUpdate.setTextColor(locationData.isSharing() ?
                ContextCompat.getColor(this, android.R.color.holo_green_dark) :
                ContextCompat.getColor(this, android.R.color.holo_red_dark));

        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && locationListener != null)
            databaseReference.removeEventListener(locationListener);
    }
}
