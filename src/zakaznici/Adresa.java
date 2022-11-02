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

    private String ulice;
    private String cisloPop;
    private String psc;
    private String mesto;

    
    
    public Adresa(String ulice, String cisloPop, String psc, String mesto) {
        this.ulice = ulice;
        this.cisloPop = cisloPop;
        this.psc = psc;
        this.mesto = mesto;
    }
    
    public void setUlice(String ulice) {
        this.ulice = ulice;
    }

    public void setCisloPop(String cisloPop) {
        this.cisloPop = cisloPop;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    public String getUlice() {
        return ulice;
    }

    public String getCisloPop() {
        return cisloPop;
    }

    public String getPsc() {
        return psc;
    }

    public String getMesto() {
        return mesto;
    }

    
    

}
