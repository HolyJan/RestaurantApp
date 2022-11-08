/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objednavky;

/**
 *
 * @author jenik
 */
public class Objednavka {

    private int idObjednavky;
    private String jmeno;
    private String prijmeni;
    private int idDoruceni;
    private String casObjednani;
    private String vyzvednuti;
    private int idPolozky;
    private String nazevPolozky;
    private int cenaPolozky;

    public Objednavka(int idObjednavky, String jmeno, String prijmeni, int idDoruceni, String casObjednani, String vyzvednuti, int idPolozky, String nazevPolozky, int cenaPolozky) {
        this.idObjednavky = idObjednavky;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
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

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public int getIdDoruceni() {
        return idDoruceni;
    }

    public String getCasObjednani() {
        return casObjednani;
    }

    public String getVyzvednuti() {
        return vyzvednuti;
    }

    public int getIdPolozky() {
        return idPolozky;
    }

    public String getNazevPolozky() {
        return nazevPolozky;
    }

    public int getCenaPolozky() {
        return cenaPolozky;
    }

    public void setIdObjednavky(int idObjednavky) {
        this.idObjednavky = idObjednavky;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public void setIdDoruceni(int idDoruceni) {
        this.idDoruceni = idDoruceni;
    }

    public void setCasObjednani(String casObjednani) {
        this.casObjednani = casObjednani;
    }

    public void setVyzvednuti(String vyzvednuti) {
        this.vyzvednuti = vyzvednuti;
    }

    public void setIdPolozky(int idPolozky) {
        this.idPolozky = idPolozky;
    }

    public void setNazevPolozky(String nazevPolozky) {
        this.nazevPolozky = nazevPolozky;
    }

    public void setCenaPolozky(int cenaPolozky) {
        this.cenaPolozky = cenaPolozky;
    }

    
}
