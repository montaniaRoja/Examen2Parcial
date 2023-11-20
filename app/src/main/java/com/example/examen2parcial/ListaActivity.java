package com.example.examen2parcial;


import static java.util.Locale.filter;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class ListaActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> originalList;
    SQLiteConexion conexion;
    ListView listView;
    ArrayList<Persona> listPersonas;
    private EditText editTextSearch;
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
        editTextSearch=findViewById(R.id.editTextSearch);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        arregloPersona = new ArrayList<>();


        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, arregloPersonas);
        adapter=adp;
        listView.setAdapter(adp);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filtra la lista al cambiar el texto
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Persona personaSeleccionada = listPersonas.get(position);

                // Abrir la VisualizarLocalizacion Activity con la latitud y longitud de la persona
                abrirVisualizarLocalizacion(personaSeleccionada,personaSeleccionada.getLatitud(), personaSeleccionada.getLongitud());
            }
        });




        GetPersons();

    }

    private void filter(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String item : originalList) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }


    private void abrirVisualizarLocalizacion(Persona persona, String latitud, String longitud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ver Localizacion de " + persona.getNombre());

        builder.setMessage("¿Desea ver la localizacion o el video?");

        builder.setPositiveButton("Localizacion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ListaActivity.this, VisualizarLocalizacion.class);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Envía el ID en lugar del video directamente
                int personaId = persona.getId();

                Intent intent = new Intent(ListaActivity.this, TemporalActiivity.class);
                intent.putExtra("personaId", personaId);
                startActivity(intent);
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

        // Inicializar originalList antes de FillList()
        originalList = new ArrayList<>(arregloPersonas);

        FillList();
    }



    private void FillList() {
        for (int i = 0; i < listPersonas.size(); i++) {
            byte[] videoBytes = listPersonas.get(i).getVideo();
            int videoLength = (videoBytes != null) ? videoBytes.length : 0;

            arregloPersonas.add(listPersonas.get(i).getNombre() + "-" +
                    listPersonas.get(i).getLatitud() + listPersonas.get(i).getLongitud() + " " +
                    videoLength + " **");
        }
    }




}

