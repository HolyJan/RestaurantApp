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
    private String jmeno;
    private String prijmeni;
    private String casObjednani;
    private String vyzvednuti;
    private String nazevPolozky;
    private int cenaPolozky;

    public Objednavka(String jmeno, String prijmeni, String casObjednani, String vyzvednuti, String nazevPolozky, int cenaPolozky) {
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.casObjednani = casObjednani;
        this.vyzvednuti = vyzvednuti;
        this.nazevPolozky = nazevPolozky;
        this.cenaPolozky = cenaPolozky;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public String getCasObjednani() {
        return casObjednani;
    }

    public String getVyzvednuti() {
        return vyzvednuti;
    }

    public String getNazevPolozky() {
        return nazevPolozky;
    }

    public int getCenaPolozky() {
        return cenaPolozky;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public void setCasObjednani(String casObjednani) {
        this.casObjednani = casObjednani;
    }

    public void setVyzvednuti(String vyzvednuti) {
        this.vyzvednuti = vyzvednuti;
    }

    public void setNazevPolozky(String nazevPolozky) {
        this.nazevPolozky = nazevPolozky;
    }

    public void setCenaPolozky(int cenaPolozky) {
        this.cenaPolozky = cenaPolozky;
    }
    
    
}
