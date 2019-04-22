/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inteligenciacomputacional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author sequi
 */
public class Solucao {

    private  ArrayList<Integer> featuresExcluidas;
    private final ArrayList<Integer> featuresSelecionadas;

    public ArrayList<Integer> getFeaturesRestantes() {
        return featuresSelecionadas;
    }

    private double acuracia = 0;

    public Solucao() {
        featuresExcluidas = new ArrayList<>();
        featuresSelecionadas = new ArrayList<>();
    }

    public Solucao(ArrayList<Integer> RCL, int[] keepTheseFeatures) {
        featuresExcluidas = new ArrayList<>();
        featuresSelecionadas = new ArrayList<>();

//        System.out.print("S <- Seleção: {");
        for (int i = 0; i < keepTheseFeatures.length; i++) {
            int feature = keepTheseFeatures[i];
            featuresSelecionadas.add(feature);
//            System.out.print(feature + ",");
        }
//        System.out.print("} - ");
//        System.out.print("Exclusão: {");
        for (int j = 0; j < RCL.size(); j++) {
            int feature = RCL.get(j);
            if (!featuresSelecionadas.contains(feature)) {
                featuresExcluidas.add(feature);
//                System.out.print(feature + ",");
            }
        }
//        System.out.println("}");
    }

    public void addFeature(int novaFeature) {
        if (!featuresExcluidas.contains(novaFeature)) {
            featuresExcluidas.add(novaFeature);
        }
    }

    public boolean isBest(Solucao solucao) {
        return this.getAcuracia() >= solucao.getAcuracia();
    }

    public int[] getExcludedFeatures() {
        int[] featuresVector = new int[this.featuresExcluidas.size()];
        for (int i = 0; i < featuresExcluidas.size() - 1; i++) {
            featuresVector[i] = featuresExcluidas.get(i);
        }
        quickSort(featuresVector, 0, this.featuresExcluidas.size() - 1);
        return featuresVector;
    }

    public int[] getKeepFeatures() {
        int[] featuresVector = new int[this.featuresSelecionadas.size()];
        for (int i = 0; i < featuresSelecionadas.size(); i++) {
            featuresVector[i] = featuresSelecionadas.get(i);
        }
        quickSort(featuresVector, 0, this.featuresSelecionadas.size() - 1);
        return featuresVector;
    }

    // Sorting
    public ArrayList<Integer> getFeaturesList() {
        return this.featuresExcluidas;
    }

    public void setFeaturesArray(ArrayList<Integer> features) {
        this.featuresExcluidas = features;
    }

    public double getAcuracia() {
        return acuracia;
    }

    public void setAcuracia(double acuracia) {
        this.acuracia = acuracia;
    }

    private void quickSort(int[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int posicaoPivo = separar(vetor, inicio, fim);
            quickSort(vetor, inicio, posicaoPivo - 1);
            quickSort(vetor, posicaoPivo + 1, fim);
        }
    }

    private int separar(int[] vetor, int inicio, int fim) {
        int pivo = vetor[inicio];
        int i = inicio + 1, f = fim;
        while (i <= f) {
            if (vetor[i] <= pivo) {
                i++;
            } else if (pivo < vetor[f]) {
                f--;
            } else {
                int troca = vetor[i];
                vetor[i] = vetor[f];
                vetor[f] = troca;
                i++;
                f--;
            }
        }
        vetor[inicio] = vetor[f];
        vetor[f] = pivo;
        return f;
    }

    public ArrayList featuresSelecionadas(ArrayList<Integer> RCL) {
        System.out.print("Seleção: {");
        boolean printComma = false;
        for (int i = 0; i < RCL.size(); i++) {
            int feature = RCL.get(i);
            if (!featuresExcluidas.contains(feature) && !featuresSelecionadas.contains(feature)) {
                featuresSelecionadas.add(feature);
                if (!printComma) {
                    printComma = true;
                    System.out.print(feature);
                } else {
                    System.out.print(",");
                    System.out.print(feature);
                }
            }
        }
        System.out.println("}");
        return featuresSelecionadas;
    }

    public void printSelection() {
        System.out.print("Seleção: {");
        boolean printComma = false;
        for (int feature : featuresSelecionadas) {
            if (!printComma) {
                printComma = true;
                System.out.print(feature);
            } else {
                System.out.print(",");
                System.out.print(feature);
            }
        }
        System.out.print("} - ");
    }

    public Solucao bitFlip() {
        Random r = new Random();
        int posExcluida = r.nextInt(featuresExcluidas.size() - 1);
        int posSelecionadas = r.nextInt(featuresSelecionadas.size() - 1);
        System.out.print("# bit-flip [" + posExcluida + "][" + posSelecionadas + "] ");
        int substituta = featuresExcluidas.remove(posExcluida);
        int substituida = featuresSelecionadas.remove(posSelecionadas);
        featuresSelecionadas.add(substituta);
        featuresExcluidas.add(substituida);
        printSelection();
        return this;
    }

    public void getRCL() {
        ArrayList<Integer> RCL = featuresExcluidas;
        for (int i : featuresSelecionadas) {
            RCL.add(i);
        }
    }
}
