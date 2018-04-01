package com.example.stige.articoli;

/**
 * Created by Stige on 10/03/2018.
 */

class Articolo {
    private int id;
    private String descrizione;
    private String prezzo;
    private String stagione;

    public Articolo()
    {

    }
    public Articolo(int id, String descrizione, String prezzo, String stagione)
    {
        this.id = id;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.stagione = stagione;
    }
    public Articolo(String descrizione, String prezzo, String stagione)
    {
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.stagione = stagione;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public String getPrezzo() {
        return prezzo;
    }
    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public void setStagione(String stagione) {
        this.stagione = stagione;
    }

    public String getStagione() {
        return stagione;
    }
}
