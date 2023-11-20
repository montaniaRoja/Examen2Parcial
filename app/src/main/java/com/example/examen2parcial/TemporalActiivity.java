package com.example.examen2parcial;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TemporalActiivity extends AppCompatActivity {

    VideoView videoView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal_actiivity);

        videoView2 = findViewById(R.id.videoView2);

        // Recupera el ID de la persona de la Intent
        int personaId = getIntent().getIntExtra("personaId", -1);

        if (personaId != -1) {

            byte[] videoBlob = getVideoFromDatabase(personaId);

            if (videoBlob != null) {
                Toast.makeText(this, "guardar video temporalmente", Toast.LENGTH_SHORT).show();
                File tempVideoFile;
                try {
                    tempVideoFile = File.createTempFile("temp_video", ".mp4", getCacheDir());
                    FileOutputStream fos = new FileOutputStream(tempVideoFile);
                    fos.write(videoBlob);
                    fos.close();


                    playVideo(tempVideoFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No se encontró el video en la base de datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] getVideoFromDatabase(int personaId) {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.nameDB, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT video FROM " + Transacciones.Tabla1 + " WHERE id = ?", new String[]{String.valueOf(personaId)});

        if (cursor.moveToFirst()) {
            return cursor.getBlob(0); //
        }

        return null;
    }

    private void playVideo(String videoPath) {
        videoView2.setVideoPath(videoPath);
        videoView2.start();
    }
}
