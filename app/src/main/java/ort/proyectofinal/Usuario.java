package ort.proyectofinal;

import java.io.Serializable;

public class Usuario implements Serializable {
    String idFacebook;
    String nombre;
    String email;

    public Usuario(String idFacebook, String nombre, String email) {
        this.idFacebook = idFacebook;
        this.nombre = nombre;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

