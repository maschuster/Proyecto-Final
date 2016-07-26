package ort.proyectofinal;

import java.io.Serializable;

public class Usuario implements Serializable {
    String idFacebook;
    String nombre;

    public Usuario(String idFacebook, String nombre) {
        this.idFacebook = idFacebook;
        this.nombre = nombre;
    }


    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

