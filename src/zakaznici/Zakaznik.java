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
    private Adresa adresa;

    public Zakaznik(int id, String jmeno, String prijmeni, String telefon, String email, Adresa adresa) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.telefon = telefon;
        this.email = email;
        this.adresa = adresa;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Adresa getAdresa() {
        return adresa;
    }

    public void setAdresa(Adresa adresa) {
        this.adresa = adresa;
    }

    public String getUlice() {
        return adresa.getUlice();
    }

    public String getCisloPop() {
        return adresa.getCisloPop();
    }

    public String getPsc() {
        return adresa.getPsc();
    }

    public String getMesto() {
        return adresa.getMesto();
    }
   

    @Override
    public String toString() {
        return jmeno + " " + prijmeni;
    }

    
   }
