/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inteligenciacomputacional;

/**
 *
 * @author sequi
 */
public class Atributo {

    double ig;
    int pos;

    public Atributo(double ig, int pos) {
        this.ig = ig;
        this.pos = pos;
    }

    public Atributo(double calcularaIG) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double getIg() {
        return ig;
    }

    public void setIg(double ig) {
        this.ig = ig;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

}
