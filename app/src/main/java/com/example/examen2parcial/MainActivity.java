package com.example.examen2parcial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.location.Location;
import android.location.LocationManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Button btnVideo, btnSalvar, btnLista;
    VideoView videoView;
    EditText txtNombre, txtTelefono, txtLatitud, txtLongitud;
    Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnLista = (Button) findViewById(R.id.btnLista);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        videoView = findViewById(R.id.videoView);

        btnLista.setOnClickListener(e->verLista());


        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        btnSalvar.setOnClickListener(e -> guardarPersona());

    }

    private void verLista() {

        iniciarNuevoActivity();

    }

    private void iniciarNuevoActivity() {
        Intent intent=new Intent(MainActivity.this, ListaActivity.class);
        startActivity(intent);
    }


    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            capturarVideo();
        }
    }


    private void capturarVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 102);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && resultCode == RESULT_OK) {
            videoUri = data.getData();

            videoView.setVideoURI(videoUri);

            // Convierte el video a bytes
            byte[] videoBytes = convertirVideoABytes(videoUri);
            videoView.start();

            checkLocationPermission();

        }
    }

    private byte[] convertirVideoABytes(Uri videoUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void guardarPersona() {
        String nombre = txtNombre.getText().toString();
        String telefono = txtTelefono.getText().toString();

        String latitud = txtLatitud.getText().toString();
        String longitud = txtLongitud.getText().toString();

        byte[] videoEnBytes = readVideoFromFile(videoUri);


        try {

            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.nameDB, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();


            ContentValues valores = new ContentValues();
            valores.put(Transacciones.nombre, nombre);
            valores.put(Transacciones.telefono, telefono);
            valores.put(Transacciones.latitud, latitud);
            valores.put(Transacciones.longitud, longitud);

            valores.put(Transacciones.video, videoEnBytes);

            Long Result = db.insert(Transacciones.Tabla1, Transacciones.id, valores);

            Toast.makeText(this, getString(R.string.Respuesta), Toast.LENGTH_SHORT).show();
            db.close();
            videoView.setVideoURI(null);
            txtNombre.setText("");
            txtTelefono.setText("");
            txtLatitud.setText("");
            txtLongitud.setText("");

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.ErrorDB), Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(this, getString(R.string.ErrorResp), Toast.LENGTH_SHORT).show();
        }
    }


        private byte[] readVideoFromFile(Uri videoUri) {
            InputStream inputStream = null;

            try {
                inputStream = getContentResolver().openInputStream(videoUri);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null; // Retornar null en caso de error
        }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted.
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with getting the location.
                getLocation();
            } else {
                // Permission denied, handle accordingly.
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Ahora tienes la latitud y longitud, puedes usar estos valores en tu aplicación.
                // Por ejemplo, puedes mostrarlos en TextViews o guardarlos en tu base de datos.
                txtLatitud.setText(String.valueOf(latitude));
                txtLongitud.setText(String.valueOf(longitude));
            } else {
                // Si la ubicación no está disponible, puedes solicitar actualizaciones de ubicación.
                // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                // Ten en cuenta que este código es solo un ejemplo básico.
            }
        }
    }

// Puedes agregar un LocationListener si necesitas actualizaciones de ubicación continuas.
// private final LocationListener locationListener = new LocationListener() {
//     @Override
//     public void onLocationChanged(Location location) {
//         // Aquí manejas las actualizaciones de ubicación en tiempo real.
//     }
// };

}



