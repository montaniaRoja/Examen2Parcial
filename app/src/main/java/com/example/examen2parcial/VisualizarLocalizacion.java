package com.example.examen2parcial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class VisualizarLocalizacion extends AppCompatActivity {

    private GoogleMap mMap;
    EditText txtLat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_localizacion);
        txtLat=findViewById(R.id.txtLat);
        // Obtener el fragmento de mapa
        MapView mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Necesario para que el mapa se muestre de inmediato

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // El mapa está listo
                mMap = googleMap;

                // Obtener la latitud y longitud de los extras
                Intent intent = getIntent();
                double latitud = Double.parseDouble(intent.getStringExtra("latitud"));
                double longitud = Double.parseDouble(intent.getStringExtra("longitud"));
                txtLat.setText(String.valueOf(latitud));                // Mover la cámara del mapa a la ubicación deseada
                LatLng ubicacion = new LatLng(latitud, longitud);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
            }
        });
    }
}
