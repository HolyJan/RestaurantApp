/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

import zakaznici.Zakaznik;

/**
 *
 * @author jenik
 */
public class Objednavka {

    private int idObjednavky;
    private Zakaznik zakaznik;
    private int idDoruceni;
    private String casObjednani;
    private String vyzvednuti;
    private int idPolozky;
    private String nazevPolozky;
    private int cenaPolozky;

    public Objednavka(int idObjednavky, Zakaznik zakaznik, int idDoruceni, String casObjednani, String vyzvednuti, int idPolozky, String nazevPolozky, int cenaPolozky) {
        this.idObjednavky = idObjednavky;
        this.zakaznik = zakaznik;
        this.idDoruceni = idDoruceni;
        this.casObjednani = casObjednani;
        this.vyzvednuti = vyzvednuti;
        this.idPolozky = idPolozky;
        this.nazevPolozky = nazevPolozky;
        this.cenaPolozky = cenaPolozky;
    }

    public int getIdObjednavky() {
        return idObjednavky;
    }

    public void setIdObjednavky(int idObjednavky) {
        this.idObjednavky = idObjednavky;
    }

    public Zakaznik getZakaznik() {
        return zakaznik;
    }

    public void setZakaznik(Zakaznik zakaznik) {
        this.zakaznik = zakaznik;
    }

    public int getIdDoruceni() {
        return idDoruceni;
    }

    public void setIdDoruceni(int idDoruceni) {
        this.idDoruceni = idDoruceni;
    }

    public String getCasObjednani() {
        return casObjednani;
    }

    public void setCasObjednani(String casObjednani) {
        this.casObjednani = casObjednani;
    }

    public String getVyzvednuti() {
        return vyzvednuti;
    }

    public void setVyzvednuti(String vyzvednuti) {
        this.vyzvednuti = vyzvednuti;
    }

    public int getIdPolozky() {
        return idPolozky;
    }

    public void setIdPolozky(int idPolozky) {
        this.idPolozky = idPolozky;
    }

    public String getNazevPolozky() {
        return nazevPolozky;
    }

    public void setNazevPolozky(String nazevPolozky) {
        this.nazevPolozky = nazevPolozky;
    }

    public int getCenaPolozky() {
        return cenaPolozky;
    }

    public void setCenaPolozky(int cenaPolozky) {
        this.cenaPolozky = cenaPolozky;
    }

    public String getJmeno(){
        return zakaznik.getJmeno();
    }
    
    public String getPrijmeni(){
        return zakaznik.getPrijmeni();
    }
    
    
}
