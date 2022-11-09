/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

/**
 *
 * @author jenik
 */
public class Polozka {
    private int idPolozky;
    private String nazevPolozky;
    private int cenaPolozky;
    private int idReceptu;
    private String nazevReceptu;
    private int idMenu;
    private String nazevMenu;

    public Polozka(int idPolozky, String nazevPolozky, int cenaPolozky, int idReceptu, String nazevReceptu, int idMenu, String nazevMenu) {
        this.idPolozky = idPolozky;
        this.nazevPolozky = nazevPolozky;
        this.cenaPolozky = cenaPolozky;
        this.idReceptu = idReceptu;
        this.nazevReceptu = nazevReceptu;
        this.idMenu = idMenu;
        this.nazevMenu = nazevMenu;
    }

    public int getIdPolozky() {
        return idPolozky;
    }

    public String getNazevPolozky() {
        return nazevPolozky;
    }

    public int getCenaPolozky() {
        return cenaPolozky;
    }

    public int getIdReceptu() {
        return idReceptu;
    }

    public String getNazevReceptu() {
        return nazevReceptu;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public String getNazevMenu() {
        return nazevMenu;
    }

    public void setIdPolozky(int idPolozky) {
        this.idPolozky = idPolozky;
    }

    public void setNazevPolozky(String nazevPolozky) {
        this.nazevPolozky = nazevPolozky;
    }

    public void setCenaPolozky(int cenaPolozky) {
        this.cenaPolozky = cenaPolozky;
    }

    public void setIdReceptu(int idReceptu) {
        this.idReceptu = idReceptu;
    }

    public void setNazevReceptu(String nazevReceptu) {
        this.nazevReceptu = nazevReceptu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public void setNazevMenu(String nazevMenu) {
        this.nazevMenu = nazevMenu;
    }
    
    
    
    
}
