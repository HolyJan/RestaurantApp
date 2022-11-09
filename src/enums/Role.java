/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ondra
 */
public enum Role {
    NEREGISTROVANY(0), ZAKAZNIK(1), ZAMESTNANEC(2), ADMIN(3);
    private int value;
    private static Map map = new HashMap<>();

    private Role(int value) {
        this.value = value;
    }

    static {
        for (Role pageType : Role.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static Role valueOf(int value) {
        return (Role) map.get(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
