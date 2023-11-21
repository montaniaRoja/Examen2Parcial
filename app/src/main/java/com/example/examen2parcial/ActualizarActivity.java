package com.example.examen2parcial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

public class ActualizarActivity extends AppCompatActivity {


    Button btnActualizar;

    EditText txtActNombre, txtActTelefono, txtActLatitud, txtActLongitud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);


        btnActualizar = (Button) findViewById(R.id.btnActualizar);
        txtActNombre = findViewById(R.id.txtActNombre);
        txtActTelefono = findViewById(R.id.txtActTelefono);
        txtActLatitud = findViewById(R.id.txtActLatitud);
        txtActLongitud = findViewById(R.id.txtActLongitud);

        btnActualizar.setOnClickListener(e->ejecutarActualizar());



        Intent intent=getIntent();
        int idPersona = (intent.getIntExtra("ContactoId", -1));


        if (idPersona != -1) {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.nameDB, null, 1);
            Persona persona = conexion.getContactoById(idPersona);

            if (persona != null) {
                // Establecer los valores en los EditText
                txtActNombre.setText(persona.getNombre());
                txtActTelefono.setText(persona.getTelefono());
                txtActLatitud.setText(persona.getLatitud());
                txtActLongitud.setText(persona.getLongitud());
            }
        }


    }

        private void ejecutarActualizar() {
            String nombre = txtActNombre.getText().toString().trim();
            String telefono = txtActTelefono.getText().toString().trim();
            String latitud = txtActLatitud.getText().toString().trim();
            String longitud = txtActLongitud.getText().toString().trim();

            // Obtener el ID del contacto de la intención
            Intent intent = getIntent();
            int idPersona = intent.getIntExtra("ContactoId", -1);

            if (idPersona != -1) {
                SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.nameDB, null, 1);


                boolean actualizacionExitosa = conexion.actualizarContacto(idPersona, nombre, telefono, latitud, longitud);

                if (actualizacionExitosa) {

                    Toast.makeText(this, "Actualización exitosa", Toast.LENGTH_SHORT).show();


                    finish(); //
                } else {

                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



