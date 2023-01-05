/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uzivatele;

/**
 *
 * @author Notebook
 */
public class Uzivatel {
    private int idUzivatele;
    private String jmeno;
    private String prijmeni;
    private String telefon;
    private String login;
    private String heslo;
    private Role role;

    public Uzivatel(int idUzivatele, String jmeno, String prijmeni, String telefon, String login, String heslo, Role role) {
        this.idUzivatele = idUzivatele;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.telefon = telefon;
        this.login = login;
        this.heslo = heslo;
        this.role = role;
    }

    public int getIdUzivatele() {
        return idUzivatele;
    }

    public void setIdUzivatele(int idUzivatele) {
        this.idUzivatele = idUzivatele;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHeslo() {
        return heslo;
    }

    public void setHeslo(String heslo) {
        this.heslo = heslo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

   
    
}
