package com.example.examen2parcial;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
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


        int personaId = getIntent().getIntExtra("personaId", -1);
        try {
            if (personaId != -1) {
                byte[] videoBlob = getVideoFromDatabase(personaId);

                if (videoBlob != null) {
                    String tempVideoPath = saveVideoToTempFile(videoBlob);
                    playVideo(tempVideoPath);
                } else {
                    Toast.makeText(this, "El video no está disponible", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir el video", Toast.LENGTH_LONG).show();
        }
    }

    private byte[] getVideoFromDatabase(int personaId) {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.nameDB, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT video FROM " + Transacciones.Tabla1 + " WHERE id = ?", new String[]{String.valueOf(personaId)});

        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getBlob(0);
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }



    private void playVideo(String videoPath) {
        try {

            Uri uri= Uri.parse(videoPath);
            videoView2.setVideoURI(uri);
            MediaController media = new MediaController(this);
            videoView2.setMediaController(media);
            media.setAnchorView(videoView2);

            //videoView2.setVideoPath(videoPath);
            //videoView2.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir el video", Toast.LENGTH_LONG).show();
        }
    }


    private String saveVideoToTempFile(byte[] videoBlob) throws IOException {
        File tempVideoFile = File.createTempFile("temp_video", ".mp4", getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempVideoFile);
        fos.write(videoBlob);
        fos.close();
        return tempVideoFile.getAbsolutePath();
    }


}
