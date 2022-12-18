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
public class Role {
    private int idRole;
    private String nazev;

    public Role(int idRole, String nazev) {
        this.idRole = idRole;
        this.nazev = nazev;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
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
