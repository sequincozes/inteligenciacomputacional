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
public class FeatureAvaliada {

    double valorFeature;
    int indiceFeature;

    public FeatureAvaliada(double valorFeature, int indiceFeature) {
        this.valorFeature = valorFeature;
        this.indiceFeature = indiceFeature;
    }

    public double getValorFeature() {
        return valorFeature;
    }

    public int getIndiceFeature() {
        return indiceFeature;
    }
}
