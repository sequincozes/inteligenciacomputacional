/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CICIDS2017;

/**
 *
 * @author sequi
 */
public class CustomFS {

    String name;
    int[] customRcl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getCustomRcl() {
        return customRcl;
    }

    public void setCustomRcl(int[] customRcl) {
        this.customRcl = customRcl;
    }

    public CustomFS(String name, int[] customRcl) {
        this.name = name;
        this.customRcl = customRcl;
    }

}
