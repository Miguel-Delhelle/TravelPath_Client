package fr.um.ico.mobile.travelpath.domain.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {

    private final FusedLocationProviderClient fusedLocationClient;
    private final Activity activity;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public interface LocationCallback {
        void onLocationFound(double lat, double lon);
    }

    public LocationHelper(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void getLastLocation(LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Demande la permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission OK, on récupère la position
            fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if (location != null) {
                    callback.onLocationFound(location.getLatitude(), location.getLongitude());
                } else {
                    Toast.makeText(activity, "GPS activé mais position inconnue", Toast.LENGTH_SHORT).show();
                    // Fallback sur Montpellier (ou null selon ta logique)
                    callback.onLocationFound(43.6112, 3.8767);
                }
            });
        }
    }
}