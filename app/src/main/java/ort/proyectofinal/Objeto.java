package ort.proyectofinal;

/**
 * Created by Schuster on 29/05/2016.
 */
public class Objeto {

    int idObjeto;
    String nombre;
    int precio;
    int idParticipante;
    boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Objeto(String nombre, int precio, int idobjeto, boolean checked, int idParticipante) {
        this.nombre = nombre;
        this.precio = precio;
        this.idObjeto = idobjeto;
        this.checked = checked;
        this.idParticipante = idParticipante;
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

    public int getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(int idParticipante) {
        this.idParticipante = idParticipante;
    }
}
