/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CICIDS2017;

import inteligenciacomputacional.*;
import WSN_DS.ValidacaoWSN;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sequi
 */
public class SolucaoCICIDS2017 {

    public final ArrayList<Integer> featuresSelecionadas;
    public final ArrayList<Integer> featuresRCL;
    private double acuracia = 0;
    private double taxa_detecao = 0;
    private double taxa_falsos_positivos = 0;

    public SolucaoCICIDS2017(ArrayList<Integer> featuresSelecionadas, ArrayList<Integer> featuresRCL) {
        this.featuresSelecionadas = featuresSelecionadas;
        this.featuresRCL = featuresRCL;
        try {
            Resultado res = ValidacaoWSN.executar(getArrayFeaturesSelecionadas());
            this.acuracia = res.getAcuracia();
            this.taxa_detecao = res.getTaxaDeteccao();
            this.taxa_falsos_positivos = res.getTaxaAlarmeFalsos();

        } catch (Exception ex) {
            Logger.getLogger(SolucaoCICIDS2017.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SolucaoCICIDS2017() {
        this.featuresSelecionadas = new ArrayList<>();
        this.featuresRCL = new ArrayList<>();
    }

    private SolucaoCICIDS2017(ArrayList<Integer> featuresSelecionadasAux, ArrayList<Integer> featuresRCL, double acuracia, double taxaDR, double taxaFR) {
        this.featuresSelecionadas = featuresSelecionadasAux;
        this.featuresRCL = featuresRCL;
        this.acuracia = acuracia;
        this.taxa_detecao = taxaDR;
        this.taxa_falsos_positivos = taxaFR;
    }

    public double getAcuracia() {
        return acuracia;
    }

    public void setAcuracia(double acuracia) {
        this.acuracia = acuracia;
    }

    public ArrayList<Integer> getFeaturesSelecionadas() {
        return featuresSelecionadas;
    }

    public int[] getArrayFeaturesSelecionadas() {
        int[] arrayFS = new int[featuresSelecionadas.size()];
        for (int i = 0; i < arrayFS.length; i++) {
            arrayFS[i] = featuresSelecionadas.get(i);
        }
        return arrayFS;
    }

    public ArrayList<Integer> getFeaturesBitFlip() {
        return featuresRCL;
    }

    public boolean isBest(SolucaoCICIDS2017 solucao) {
        return this.getAcuracia() >= solucao.getAcuracia();
    }

    public void printSelection(String selecaoName) {
        System.out.print("Seleção [" + selecaoName + "]: {");
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

    public SolucaoCICIDS2017 reconstruirNewSolucao(int num_features) {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> featuresRCLAux = new ArrayList<>(featuresRCL);;

        Random r = new Random();
        while (featuresSelecionadasAux.size() > 0) {
            int toRemove = featuresSelecionadasAux.remove(0);
            featuresRCLAux.add(toRemove); //devolve a RCL
        }

        while (featuresSelecionadasAux.size() < num_features) {
            int sorteio = r.nextInt(featuresRCLAux.size());
            int featureSorteada = featuresRCLAux.remove(sorteio);
//            System.out.println("Sorteio[" + sorteio + "]: " + featureSorteada);
            if (!featuresSelecionadasAux.contains(featureSorteada)) {
//                System.out.println("A feature " + featureSorteada + " não está em " + s.featuresSelecionadas);
                featuresSelecionadasAux.add(featureSorteada);
            } else {
//                System.out.println("A feature " + featureSorteada + " JÁ está em " + s.featuresSelecionadas);
            }
        }
        SolucaoCICIDS2017 newer = new SolucaoCICIDS2017(featuresSelecionadasAux, featuresRCLAux);
        return newer;
    }

    public SolucaoCICIDS2017 newClone() {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> getBitFlipSolution = new ArrayList<>(featuresRCL);
        double acuracia = getAcuracia();
        double taxaDR = getTaxa_detecao();
        double taxaFR = getTaxa_falsos_positivos();
        return new SolucaoCICIDS2017(featuresSelecionadasAux, getBitFlipSolution, acuracia, taxaDR, taxaFR);
    }

    public SolucaoCICIDS2017 bitFlipNewSolution() {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> getBitFlipSolution = new ArrayList<>(featuresRCL);

        Random r = new Random();
        // Movendo feature para RCL
        int saiPos = r.nextInt(featuresSelecionadasAux.size() - 1);
        int saiu = featuresSelecionadasAux.remove(saiPos);

        getBitFlipSolution.add(saiu);

        // Movendo feature para Seleção
        int entraPos = r.nextInt(getBitFlipSolution.size() - 1);
        int entrou = getBitFlipSolution.remove(entraPos);

        featuresSelecionadasAux.add(entrou);
        SolucaoCICIDS2017 newer = new SolucaoCICIDS2017(featuresSelecionadasAux, getBitFlipSolution);
        return newer;
    }

    public void addFeature(Integer fature) {
        featuresSelecionadas.add(fature);
    }

    public void addFeatureFlip(Integer fature) {
        featuresRCL.add(fature);
    }

    public void printRCL() {
        System.out.print("RCL: {");
        boolean printComma = false;
        for (int feature : featuresRCL) {
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

    public double getTaxa_detecao() {
        return taxa_detecao;
    }

    public void setTaxa_detecao(double taxa_detecao) {
        this.taxa_detecao = taxa_detecao;
    }

    public double getTaxa_falsos_positivos() {
        return taxa_falsos_positivos;
    }

    public void setTaxa_falsos_positivos(double taxa_falsos_positivos) {
        this.taxa_falsos_positivos = taxa_falsos_positivos;
    }

}
