package ort.proyectofinal.Clases;

/**
 * Created by Schuster on 04/10/2016.
 */
public class Votacion {
    int idPregunta;
    int idEvento;
    String pregunta;
    int afirmativos;
    int negativos;
    int voto;

    public Votacion(int idPregunta, int idEvento, String pregunta, int afirmativos, int negativos, int voto) {
        this.idPregunta = idPregunta;
        this.idEvento = idEvento;
        this.pregunta = pregunta;
        this.afirmativos = afirmativos;
        this.negativos = negativos;
        this.voto = voto;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public int getAfirmativos() {
        return afirmativos;
    }

    public void setAfirmativos(int afirmativos) {
        this.afirmativos = afirmativos;
    }

    public int getNegativos() {
        return negativos;
    }

    public void setNegativos(int negativos) {
        this.negativos = negativos;
    }

    public int getVoto() {
        return voto;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }
}
