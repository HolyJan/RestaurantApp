/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

/**
 *
 * @author jenik
 */
public class User {
    private String jmeno;
    private String prijmeni;
    private String login;
    private String heslo;
    private String role;

    public User(String jmeno, String prijmeni, String login, String heslo, String role) {
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.login = login;
        this.heslo = heslo;
        this.role = role;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public String getLogin() {
        return login;
    }

    public String getHeslo() {
        return heslo;
    }

    public String getRole() {
        return role;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setHeslo(String heslo) {
        this.heslo = heslo;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    
    

    
    
}
