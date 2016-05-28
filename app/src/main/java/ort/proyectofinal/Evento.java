package ort.proyectofinal;

import java.io.Serializable;

/**
 * Created by Schuster on 25/05/2016.
 */
public class Evento implements Serializable{
    int idEvento;
    String fecha;
    String descripcion;
    String nombre;
    String lugar;

    public Evento(int idEvento, String nombre, String fecha, String descripcion, String lugar) {
        this.idEvento = idEvento;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.lugar = lugar;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}

