package com.example.examen2parcial;

public class Persona {
    private int id;
    private String nombre;
    private String telefono;
    private String latitud;
    private String longitud;
    private byte[] video;


    public Persona( int id, String nombre,String telefono, String latitud, String longitud,byte[] video ){

        this.id=id;
        this.nombre=nombre;
        this.telefono=telefono;
        this.latitud=latitud;
        this.longitud=longitud;
        this.video= video;

    }
    public Persona(){

    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public byte[] getVideo() {
        return video;
    }

    public void setVideo(byte[] video) {
        this.video = video;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
