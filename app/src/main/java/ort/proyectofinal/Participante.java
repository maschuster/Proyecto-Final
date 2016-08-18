package ort.proyectofinal;
import java.io.Serializable;

public class Participante implements Serializable {
    int idParticipante;
    String idFacebook;
    String nombre;

    public Participante(int idParticipante, String idFacebook, String nombre) {
        this.idParticipante = idParticipante;
        this.idFacebook = idFacebook;
        this.nombre = nombre;
    }

    public int getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(int idParticipante) {
        this.idParticipante = idParticipante;
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
