package ort.proyectofinal;

/**
 * Created by Schuster on 25/05/2016.
 */
public class Evento {
    int idNombre;
    String fecha;
    String descripcion;
    String nombre;
    String lugar;

    public Evento(int idNombre, String fecha, String descripción, String nombre, String lugar) {
        this.idNombre = idNombre;
        this.fecha = fecha;
        this.descripcion = descripción;
        this.nombre = nombre;
        this.lugar = lugar;
    }

    public int getIdNombre() {
        return idNombre;
    }

    public void setIdNombre(int idNombre) {
        this.idNombre = idNombre;
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

    public void setDescripción(String descripción) {
        this.descripcion = descripción;
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

