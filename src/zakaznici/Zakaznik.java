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
public class Zakaznik {

    private int id;
    private String jmeno;
    private String prijmeni;
    private String telefon;
    private String email;
    private String ulice;
    private String cisloPop;
    private String psc;
    private String mesto;

    public Zakaznik(int id, String jmeno, String prijmeni, String telefon, String email, String ulice, String cisloPop, String psc, String mesto) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.telefon = telefon;
        this.email = email;
        this.ulice = ulice;
        this.cisloPop = cisloPop;
        this.psc = psc;
        this.mesto = mesto;
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

    public void setEmail(String email) {
        this.email = email;
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

    public String getEmail() {
        return email;
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
