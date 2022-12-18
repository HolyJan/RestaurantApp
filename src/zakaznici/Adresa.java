/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zakaznici;

/**
 *
 * @author jenik
 */
public class Adresa {
    private int idAdresy;
    private String ulice;
    private String cisloPop;
    private String psc;
    private String mesto;

    public Adresa(int idAdresy, String ulice, String cisloPop, String psc, String mesto) {
        this.idAdresy = idAdresy;
        this.ulice = ulice;
        this.cisloPop = cisloPop;
        this.psc = psc;
        this.mesto = mesto;
    }

    public int getIdAdresy() {
        return idAdresy;
    }

    public void setIdAdresy(int idAdresy) {
        this.idAdresy = idAdresy;
    }

    public String getUlice() {
        return ulice;
    }

    public void setUlice(String ulice) {
        this.ulice = ulice;
    }

    public String getCisloPop() {
        return cisloPop;
    }

    public void setCisloPop(String cisloPop) {
        this.cisloPop = cisloPop;
    }

    public String getPsc() {
        return psc;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    @Override
    public String toString() {
        return ulice + " " + cisloPop + " " + mesto;
    }

    
    
    
    

}
