package Entitati;

import java.io.Serializable;

public class Piesa implements Serializable {
    private int id;
    private String formatie;
    private String titlu;
    private String genMuzical;
    private String durata; // format: MM:SS

    public Piesa(int id, String formatie, String titlu, String genMuzical, String durata) {
        this.id = id;
        this.formatie = formatie;
        this.titlu = titlu;
        this.genMuzical = genMuzical;
        this.durata = durata;
    }

    public Piesa() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormatie() {
        return formatie;
    }

    public void setFormatie(String formatie) {
        this.formatie = formatie;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getGenMuzical() {
        return genMuzical;
    }

    public void setGenMuzical(String genMuzical) {
        this.genMuzical = genMuzical;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    // Converteste durata din MM:SS in secunde
    public int getDurataInSecunde() {
        try {
            String[] parts = durata.split(":");
            int minute = Integer.parseInt(parts[0]);
            int secunde = Integer.parseInt(parts[1]);
            return minute * 60 + secunde;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Piesa{" +
                "id=" + id +
                ", formatie='" + formatie + '\'' +
                ", titlu='" + titlu + '\'' +
                ", genMuzical='" + genMuzical + '\'' +
                ", durata='" + durata + '\'' +
                '}';
    }
}