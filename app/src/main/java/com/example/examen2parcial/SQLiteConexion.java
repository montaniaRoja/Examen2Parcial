package com.example.examen2parcial;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConexion extends SQLiteOpenHelper
{
    public SQLiteConexion(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        // Crear los objectos de base de datos
        sqLiteDatabase.execSQL(Transacciones.CreateTableVideos);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL(Transacciones.DropTableVideos);

        onCreate(sqLiteDatabase);

    }

    @SuppressLint("Range")
    public Persona getContactoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Persona persona = null;

        String[] columns = {Transacciones.id, Transacciones.nombre, Transacciones.telefono, Transacciones.latitud, Transacciones.longitud};
        String selection = Transacciones.id + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(Transacciones.Tabla1, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {

            persona = new Persona();
            persona.setId(cursor.getInt(cursor.getColumnIndex(Transacciones.id)));
            persona.setNombre(cursor.getString(cursor.getColumnIndex(Transacciones.nombre)));
            persona.setTelefono(cursor.getString(cursor.getColumnIndex(Transacciones.telefono)));
            persona.setLatitud(cursor.getString(cursor.getColumnIndex(Transacciones.latitud)));
            persona.setLongitud(cursor.getString(cursor.getColumnIndex(Transacciones.longitud)));
        }

        cursor.close();
        db.close();

        return persona;
    }

    public boolean actualizarContacto(int id, String nombre, String telefono, String latitud, String longitud) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Transacciones.nombre, nombre);
        values.put(Transacciones.telefono, telefono);
        values.put(Transacciones.latitud, latitud);
        values.put(Transacciones.longitud, longitud);

        String whereClause = Transacciones.id + "=?";
        String[] whereArgs = {String.valueOf(id)};

        // Realiza la actualización y devuelve el número de filas afectadas
        int filasAfectadas = db.update(Transacciones.Tabla1, values, whereClause, whereArgs);

        db.close();


        return filasAfectadas > 0;
    }


}