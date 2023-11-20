package com.example.examen2parcial;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;



public class ListaActivity extends AppCompatActivity {

    SQLiteConexion conexion;
    ListView listView;
    ArrayList<Persona> listPersonas;

    ArrayList<String> arregloPersonas;

    int selectedPosition = ListView.INVALID_POSITION;

    private byte[] selectedPersona;

    private ArrayList<byte[]> arregloPersona;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        conexion = new SQLiteConexion(this, Transacciones.nameDB, null, 1);
        arregloPersonas = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        arregloPersona = new ArrayList<>();

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, arregloPersonas);
        listView.setAdapter(adp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener la Persona correspondiente a la posición seleccionada
                Persona personaSeleccionada = listPersonas.get(position);

                // Abrir la VisualizarLocalizacion Activity con la latitud y longitud de la persona
                abrirVisualizarLocalizacion(personaSeleccionada,personaSeleccionada.getLatitud(), personaSeleccionada.getLongitud());
            }
        });




        GetPersons();

    }



    private void abrirVisualizarLocalizacion(Persona persona, String latitud, String longitud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ver Localizacion de " + persona.getNombre());

        builder.setMessage("¿Desea ver la localizacion ?");

        builder.setPositiveButton("Ver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ListaActivity.this, VisualizarLocalizacion.class);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void GetPersons() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Persona persona = null;
        listPersonas = new ArrayList<Persona>();

        Cursor cursor = db.rawQuery(Transacciones.SelectTableVideos, null);
        while (cursor.moveToNext()) {
            persona = new Persona();
            persona.setId(cursor.getInt(0));
            persona.setNombre(cursor.getString(1));
            persona.setTelefono(cursor.getString(2));
            persona.setLatitud(cursor.getString(3));
            persona.setLongitud(cursor.getString(4));
            persona.setVideo(cursor.getBlob(5));

            listPersonas.add(persona);
        }

        cursor.close();
        FillList();
    }


    private void FillList() {
        for (int i = 0; i < listPersonas.size(); i++) {
            arregloPersonas.add(listPersonas.get(i).getNombre() + "-" +
                    listPersonas.get(i).getLatitud() + listPersonas.get(i).getLongitud()+ "**" );
        }
    }



}

