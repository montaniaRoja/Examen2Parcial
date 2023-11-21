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
import android.widget.Button;
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
    Button btnEliminar, btnAct;
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
        btnEliminar = (Button) findViewById(R.id.btnEliminar);
        btnAct = (Button) findViewById(R.id.btnAct);
        editTextSearch=findViewById(R.id.editTextSearch);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        arregloPersona = new ArrayList<>();


        btnAct.setOnClickListener(e -> abrirActualizar());

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, arregloPersonas);
        adapter=adp;
        listView.setAdapter(adp);

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedContactId = obtenerIDContactoSeleccionado();

                eliminarContacto(selectedContactId);
            }
        });


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

    private void abrirActualizar() {
        int selectedContactId = obtenerIDContactoSeleccionado();

        Intent intent=new Intent(ListaActivity.this, ActualizarActivity.class);
        intent.putExtra("ContactoId", selectedContactId);
        startActivity(intent);



    }

    private void eliminarContacto(final int contactId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar contacto");
        builder.setMessage("¿Está seguro de que desea eliminar este contacto?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SQLiteDatabase db = conexion.getWritableDatabase();
                String[] whereArgs = { String.valueOf(contactId) };


                db.delete(Transacciones.Tabla1, Transacciones.id + " = ?", whereArgs);

                db.close();
                arregloPersonas.clear();
                GetPersons();

                // Notifica al adaptador que los datos han cambiado
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario cancela la eliminación, simplemente cerramos el cuadro de diálogo
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private int obtenerIDContactoSeleccionado() {
        int selectedContactId = -1; // Valor predeterminado en caso de error o ningún elemento seleccionado
        int selectedItemPosition = listView.getCheckedItemPosition();

        if (selectedItemPosition != AdapterView.INVALID_POSITION) {
            String selectedContact = arregloPersonas.get(selectedItemPosition);
            // Dividir la cadena en partes: ID - Nombre Teléfono
            String[] parts = selectedContact.split("-");
            if (parts.length >= 1) {
                try {
                    selectedContactId = Integer.parseInt(parts[0].trim()); // Extraer el ID
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return selectedContactId;
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

            arregloPersonas.add(listPersonas.get(i).getId()+"-"+listPersonas.get(i).getNombre() + "-" +
                    listPersonas.get(i).getLatitud() + listPersonas.get(i).getLongitud() + " " +
                    videoLength + " **");
        }
    }




}

