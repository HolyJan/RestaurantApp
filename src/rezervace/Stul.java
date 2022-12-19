/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rezervace;

/**
 *
 * @author Notebook
 */
public class Stul {
    private int idStolu;
    private int cisloStolu;
    private int pocetMist;

    public Stul(int idStolu, int cisloStolu, int pocetMist) {
        this.idStolu = idStolu;
        this.cisloStolu = cisloStolu;
        this.pocetMist = pocetMist;
    }

    public int getIdStolu() {
        return idStolu;
    }

    public void setIdStolu(int idStolu) {
        this.idStolu = idStolu;
    }

    public int getCisloStolu() {
        return cisloStolu;
    }

    public void setCisloStolu(int cisloStolu) {
        this.cisloStolu = cisloStolu;
    }

    public int getPocetMist() {
        return pocetMist;
    }

    public void setPocetMist(int pocetMist) {
        this.pocetMist = pocetMist;
    }

    @Override
    public String toString() {
        return "Číslo stolu: " + cisloStolu + ", Počet míst: " + pocetMist;
    }
    
    
}
