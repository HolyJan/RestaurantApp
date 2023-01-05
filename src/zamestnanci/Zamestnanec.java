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
    private int idNadrizeneho;

    public Zamestnanec(int id, String jmeno, String prijmeni, String telefon, int idPozice, String pozice, int idNadrizeneho) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.telefon = telefon;
        this.idPozice = idPozice;
        this.pozice = pozice;
        this.idNadrizeneho = idNadrizeneho;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public int getIdPozice() {
        return idPozice;
    }

    public void setIdPozice(int idPozice) {
        this.idPozice = idPozice;
    }

    public String getPozice() {
        return pozice;
    }

    public void setPozice(String pozice) {
        this.pozice = pozice;
    }

    public int getIdNadrizeneho() {
        return idNadrizeneho;
    }

    public void setIdNadrizeneho(int idNadrizeneho) {
        this.idNadrizeneho = idNadrizeneho;
    }

    

    @Override
    public String toString() {
        return jmeno + " " + prijmeni;
    }

}
