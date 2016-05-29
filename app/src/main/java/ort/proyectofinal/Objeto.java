package ort.proyectofinal;

/**
 * Created by Schuster on 29/05/2016.
 */
public class Objeto {

    int idObjeto;
    String nombre;
    int precio;


    public Objeto(String nombre, int precio, int idobjeto) {
        this.nombre = nombre;
        this.precio = precio;
        this.idObjeto = idobjeto;
    }

    public int getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(int idObjeto) {
        this.idObjeto = idObjeto;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
