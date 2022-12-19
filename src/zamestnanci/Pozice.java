/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zamestnanci;

/**
 *
 * @author Notebook
 */
public class Pozice {
    private int idPozice;
    private String nazev;

    public Pozice(int idPozice, String nazev) {
        this.idPozice = idPozice;
        this.nazev = nazev;
    }

    public int getIdPozice() {
        return idPozice;
    }

    public void setIdPozice(int idPozice) {
        this.idPozice = idPozice;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    @Override
    public String toString() {
        return nazev;
    }
    
    
}
