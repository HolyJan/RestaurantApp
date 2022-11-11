/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

/**
 *
 * @author jenik
 */
public class Zamestnanec {

    private int id;
    private String jmeno;
    private String prijmeni;
    private String telefon;
    private int idPozice;
    private String pozice;

    public Zamestnanec(int id, String jmeno, String prijmeni, String telefon, int idPozice, String pozice) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.telefon = telefon;
        this.idPozice = idPozice;
        this.pozice = pozice;
    }

    public int getId() {
        return id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public String getTelefon() {
        return telefon;
    }

    public int getIdPozice() {
        return idPozice;
    }

    public String getPozice() {
        return pozice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setIdPozice(int idPozice) {
        this.idPozice = idPozice;
    }

    public void setPozice(String pozice) {
        this.pozice = pozice;
    }

    @Override
    public String toString() {
        return jmeno + " " + prijmeni;
    }

}
