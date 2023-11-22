package com.example.examen2parcial;


public class Transacciones
{
    // Nombre de la base de datos
    public static final String nameDB = "videosdb";

    //Tablas de la base de datos
    public static final String Tabla1  = "videos";


    // Campos de la tabla
    public static final String id = "id";

    public static final String video = "video";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String latitud = "latitud";
    public static final String longitud = "longitud";

    public static final String DeleteContact = "DELETE FROM " + Transacciones.Tabla1 + " WHERE " + Transacciones.id + " = ?";


    // Consultas de Base de datos
    //ddl


    public static final String CreateTableVideos = "CREATE TABLE videos " +
            "( id INTEGER PRIMARY KEY AUTOINCREMENT,nombre varchar(100),telefono varchar(100),latitud varchar(100), longitud varchar(100),video LONGBLOB)";


    public static final String DropTableVideos  = "DROP TABLE IF EXISTS videos";

    public static final String SelectTableVideos = "SELECT * FROM " + Transacciones.Tabla1;




}