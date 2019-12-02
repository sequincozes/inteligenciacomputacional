/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obsoleto;

import static NSLKDD.ValidacaoNSLKDD.calcularGainRatioAttributeEval;
import static NSLKDD.ValidacaoNSLKDD.mormalizar;
import static inteligenciacomputacional.Apuracao.readDataFile;
import inteligenciacomputacional.FeatureAvaliada;
import inteligenciacomputacional.FeatureAvaliada;
import inteligenciacomputacional.Util;
import java.io.BufferedReader;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.OneRAttributeEval;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.core.Instances;

/**
 *
 * @author sequi
 */
public class FeatureRanking {

    private static final String DATASET = "Datadet WSN";
    private static final String ATAQUE = "binario";
    private static final String DIRETORIO = "/home/silvio/";
    private static final String TRAIN_FILE = DIRETORIO + "feature_selection_triple_teste.txt";
    private static final int TOTAL_FEATURES = 128;
    private static final boolean NORMALIZE = false;

    public static enum METODO {
        GR, IG, Relief, OneR;
    };

    public static void main(String[] args) throws Exception {
        System.out.println("Ataque: " + ATAQUE);
//        avaliarESelecionar(18, METODO.GR, false); // {5, 12, 7, 9, 41, 3, 13, 6, 11, 39, 8, 35, 40, 1, 36, 4, 22, 14, 21, 10, 25, }
//        avaliarESelecionar(18, METODO.IG, false); // IG: 6, 2, 7, 15, 3
        FeatureAvaliada fs[] = avaliarESelecionar(128, METODO.OneR, false); // OneR: 6, 7, 2, 8, 17
        for (FeatureAvaliada f : fs) {
            System.out.println(f.getIndiceFeature() + " - " + f.getValorFeature());
        }
    }

    public static FeatureAvaliada[] avaliarESelecionar(int featuresSelecionar, METODO metodo, boolean debug) throws Exception {
        System.out.println("Método: " + metodo);
        Instances instances = new Instances(readDataFile(TRAIN_FILE));
        instances.setClassIndex(instances.numAttributes() - 1);
        if (NORMALIZE) {
            instances = mormalizar(instances);
        }
        FeatureAvaliada[] allFeatures = new FeatureAvaliada[TOTAL_FEATURES];
        switch (metodo) {
            case IG:
                System.out.println("IG:");
                for (int i = 0; i < TOTAL_FEATURES; i++) {
                    allFeatures[i] = new FeatureAvaliada(calcularaIG(instances, i), i + 1);
                }
                break;
            case Relief:
                System.out.println("Relief:");
                for (int i = 0; i < TOTAL_FEATURES; i++) {
                    allFeatures[i] = new FeatureAvaliada(calcularReliefF(instances, i), i + 1);
                }
                break;
            case GR:
                System.out.println("GR:");
                for (int i = 0; i < TOTAL_FEATURES; i++) {
                    allFeatures[i] = new FeatureAvaliada(calcularGainRatioAttributeEval(instances, i), i + 1);
                }
                break;
            case OneR:
                System.out.println("OneR:");
                for (int i = 0; i < TOTAL_FEATURES; i++) {
                    allFeatures[i] = new FeatureAvaliada(calcularOneRAttributeEval(instances, i), i + 1);
                }
                break;
            default:
                System.out.println("Método incorreto.");
        }

        Util.quickSort(allFeatures, 0, allFeatures.length - 1);
        FeatureAvaliada[] filter = new FeatureAvaliada[featuresSelecionar];
        int i = 0;

//        for (FeatureAvaliada feature : allFeatures) {
//            System.out.println(feature.getIndiceFeature() + "-" + feature.getValorFeature());
//        }
        for (int j = allFeatures.length; j > allFeatures.length - featuresSelecionar; j--) {
//            System.out.println(featuresSelecionar + "[" + i + "]" + "/[" + (j - 1) + "]" + allFeatures.length);
            filter[i++] = allFeatures[j - 1];
        }

        if (debug) {
            for (FeatureAvaliada filter1 : filter) {
                System.out.println(filter1.getIndiceFeature() + "-" + filter1.getValorFeature());
            }
        } else {
            System.out.print("{");
            for (FeatureAvaliada filter1 : filter) {
                System.out.print(filter1.getIndiceFeature() + ", ");
            }
            System.out.println("}");
        }

        return filter;
    }

    public static double calcularaIG(Instances instances, int featureIndice) throws Exception {
        InfoGainAttributeEval ase = new InfoGainAttributeEval();
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularOneRAttributeEval(Instances instances, int featureIndice) throws Exception {
        OneRAttributeEval ase = new OneRAttributeEval();
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularReliefF(Instances instances, int featureIndice) throws Exception {
        System.out.println("Chegou aq? 1");
        ReliefFAttributeEval ase = new ReliefFAttributeEval();
        System.out.println("Chegou aq? 2");
        instances.setClassIndex(instances.numAttributes() - 1);
        System.out.println("Chegou aq? 3");
        ase.buildEvaluator(instances);
        System.out.println("Chegou aq? 4");
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularGainRatioAttributeEval(Instances instances, int featureIndice) throws Exception {
        GainRatioAttributeEval ase = new GainRatioAttributeEval();
        instances.setClassIndex(instances.numAttributes() - 1);
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

}
