package ort.proyectofinal;

/**
 * Created by Schuster on 22/06/2016.
 */
public class Persona {
    int idPersona;
    String Nombre;

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public Persona(int idPersona, String nombre) {
        this.idPersona = idPersona;
        Nombre = nombre;
    }
}
