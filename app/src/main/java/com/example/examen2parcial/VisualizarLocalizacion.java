package com.example.examen2parcial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VisualizarLocalizacion extends AppCompatActivity {

    private GoogleMap mMap;
    EditText txtLat;
    EditText txtLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_localizacion);
        txtLat=findViewById(R.id.txtLat);
        txtLong=findViewById(R.id.txtLong);
        // Obtener el fragmento de mapa
        MapView mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Necesario para que el mapa se muestre de inmediato

        mapView.getMapAsync(googleMap -> {
            // El mapa está listo
            mMap = googleMap;

            // Obtener la latitud y longitud de los extras
            Intent intent = getIntent();
            double latitud = Double.parseDouble(intent.getStringExtra("latitud"));
            double longitud = Double.parseDouble(intent.getStringExtra("longitud"));
            txtLat.setText(String.valueOf(latitud));
            txtLong.setText(String.valueOf(longitud));
            LatLng ubicacion = new LatLng(latitud, longitud);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
            mMap.addMarker(new MarkerOptions().position(ubicacion).title("Marcador en la ubicación"));
        });

        mapView.setOnClickListener(v -> {
            // Obtener la latitud y longitud de los extras
            Intent intent = getIntent();
            double latitud = Double.parseDouble(intent.getStringExtra("latitud"));
            double longitud = Double.parseDouble(intent.getStringExtra("longitud"));

            // Crear una Uri con la ubicación
            Uri gmmIntentUri = Uri.parse("geo:" + latitud + "," + longitud + "?q=" + latitud + "," + longitud + "(Ubicación)");

            // Crear un Intent para abrir Google Maps
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Verificar si hay una aplicación que pueda manejar el Intent
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });
    }
}
