/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inteligenciacomputacional;

import static inteligenciacomputacional.Apuracao.readDataFile;
import java.io.BufferedReader;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.OneRAttributeEval;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.pmml.producer.AbstractPMMLProducerHelper;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;

/**
 *
 * @author sequi'
 */
public class Validacao {

    // File locations
//    private static final String DIRETORIO = "C:\\Users\\sequi\\Dropbox\\A - Doutorado\\Inteligencia Computacional\\Artigo Final\\";
    private static final String DIRETORIO = "C:\\Users\\sequi\\Google Drive\\2019\\GRASPIDS";

    private static final String TRAIN_FILE = "treino.csv";
    private static final String TEST_FILE = "personificacao_teste.csv";
    private static final String NORMAL_FILE = "normal_teste.csv";

    // Setup
    //private static final Attack attackTest = new Attack("C15", 20079, 20079, Run.INCLUDE);
    private static final Attack attackTest = new Attack("", 20079, 20079, Run.INCLUDE);
//    private static final IBk nb = new IBk();
    private static final RandomTree nb = new RandomTree();

    private static final ClassifierExtended nbC = new ClassifierExtended(true, nb, "IBk");

    // Run Settings
    private static final ClassifierExtended[] CLASSIFIERS = {nbC};
    private static final boolean TEST_NORMALS = true;
    private static final boolean TEST_ATTACKS = true;
    private static final int TOTAL_FEATURES = 154;

    private static final Attack[] ATTACKS_TYPES = {attackTest};

    public static long t1 = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
//        executar(new int[]{4, 7, 8, 9, 38, 79, 82, 107, 142, 154}); // GI
//        executar(new int[]{4, 7, 38, 64, 67, 71, 79, 107, 142, 154});
        //        executar();
        avaliarESelecionar(10);
    }

    public static Resultado executar(int[] filter) throws Exception {
        Resultado resultado = null;
        for (Attack attack : ATTACKS_TYPES) {
            if (attack.isIncludeOnTests()) {
                Apuracao a = new Apuracao(
                        attack.getNumberAttacks(),
                        attack.getNumberNormals(),
                        attack.getAttackName(),
                        true,
                        DIRETORIO,
                        TRAIN_FILE,
                        TEST_FILE,
                        NORMAL_FILE,
                        TOTAL_FEATURES
                );

                a.loadAndFilter(filter, true);
                System.out.println("Filtro aplicado: " + filter.length + " features mantidas.");

                for (ClassifierExtended CLASSIFIERS_AUX : CLASSIFIERS) {
                    ClassifierExtended classifierExtended = CLASSIFIERS_AUX;
                    Classifier classifier = classifierExtended.getClassifier();

                    String name = classifierExtended.getClassifierName();
                    if (classifierExtended.isIncludeOnTests()) {
                        if (TEST_NORMALS) {
                            a.runWithNormals(classifier, name);
                        }
                        if (TEST_ATTACKS) {
                            a.runWithAttacks(classifier, name);
                        }
                        resultado = a.getResults(name);
                        /**
                         * @TODO: Acumular resultados de múltiplos ataques e
                         * classificadores
                         */
                        resultado.printResults(attack.getAttackName() + "/" + name);

                    } else {
                        System.out.println("----" + classifierExtended.getClassifierName() + ": OFF");
                    }
                }
            }
        }
//        System.out.println("Tempo de Recuperação: " + (System.currentTimeMillis() - t1) / 1000 + " segundos.");
        return resultado;
    }

    public static FeatureAvaliada[] avaliarESelecionar(int featuresSelecionar) throws Exception {
        long tempoTotal = 0;

        FeatureAvaliada[] features = new FeatureAvaliada[TOTAL_FEATURES];
        long tempoTotalFeature = 0;
        for (int i = 0; i < TOTAL_FEATURES; i++) {
            long tempo = System.currentTimeMillis();
            /* Método de Avaliação de Feature */
//            double resultado = calcularGI(i);
//            double resultado = calcularOneRAttributeEval(i);
            double resultado = calcularReliefF(i);
//            double resultado = calcularGainRatioAttributeEval(i);

            long tempoNovo = System.currentTimeMillis();
            tempoTotalFeature = tempoTotalFeature + tempoNovo;
            System.out.println("[" + (tempoNovo - tempo) + " ms] F" + (i + 1) + ": " + resultado);
            features[i] = new FeatureAvaliada(resultado, i);
        }

        Util.quickSort(features, 0, features.length - 1);
        FeatureAvaliada[] filter = new FeatureAvaliada[featuresSelecionar];
        int i = 0;
        for (int j = features.length - 1; j > featuresSelecionar - 1; j--) {
            filter[i++] = features[j];
        }
        for (FeatureAvaliada filter1 : filter) {
            System.out.println(filter1.getIndiceFeature() + "-" + filter1.getValorFeature());
        }
//        {26,22,23,24,20,9,7,15,34,5,30,32,2,8,1,4,0}
        long tempoFinal = System.currentTimeMillis();
        tempoTotal = tempoFinal - tempoTotal;
        System.out.println("Tempo Total (Avaliação e Filtro): " + tempoTotal + "ms");

        System.out.println("Filtrado: " + filter.toString());
        return filter;
    }

    public static void executar() throws Exception {
        for (Attack attack : ATTACKS_TYPES) {
            if (attack.isIncludeOnTests()) {
                Apuracao a = new Apuracao(
                        attack.getNumberAttacks(),
                        attack.getNumberNormals(),
                        attack.getAttackName(),
                        true,
                        DIRETORIO,
                        TRAIN_FILE,
                        TEST_FILE,
                        NORMAL_FILE,
                        TOTAL_FEATURES
                );

                a.loadAndFilter(new int[]{}, false);
                System.out.println("Nenhuma features removida.");

                for (ClassifierExtended CLASSIFIERS_AUX : CLASSIFIERS) {
                    ClassifierExtended classifierExtended = CLASSIFIERS_AUX;
                    Classifier classifier = classifierExtended.getClassifier();
                    String name = classifierExtended.getClassifierName();
                    if (classifierExtended.isIncludeOnTests()) {
                        if (TEST_NORMALS) {
                            a.runWithNormals(classifier, name);
                        }
                        if (TEST_ATTACKS) {
                            a.runWithAttacks(classifier, name);
                        }
                        Resultado resultado = a.getResults(name);
                        resultado.printResults(attack.getAttackName() + "/" + name);

                    } else {
                        System.out.println("----" + classifierExtended.getClassifierName() + ": OFF");
                    }
                }
            }
        }
        System.out.println("Tempo de Recuperação: " + (System.currentTimeMillis() - t1) / 1000 + " segundos.");
    }

    public static double calcularGI(int featureIndice) throws Exception {
        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
        Instances instances = new Instances(dataset);
        InfoGainAttributeEval ase = new InfoGainAttributeEval();
        instances.setClassIndex(instances.numAttributes() - 1);
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularOneRAttributeEval(int featureIndice) throws Exception {
        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
        Instances instances = new Instances(dataset);
        OneRAttributeEval ase = new OneRAttributeEval();
        instances.setClassIndex(instances.numAttributes() - 1);
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularReliefF(int featureIndice) throws Exception {
        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
        Instances instances = new Instances(dataset);
        System.out.println("Chegou aq? 1");
        ReliefFAttributeEval ase = new ReliefFAttributeEval();
        System.out.println("Chegou aq? 2");
        instances.setClassIndex(instances.numAttributes() - 1);
        System.out.println("Chegou aq? 3");
        ase.buildEvaluator(instances);
        System.out.println("Chegou aq? 4");
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularGainRatioAttributeEval(int featureIndice) throws Exception {
        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
        Instances instances = new Instances(dataset);
        GainRatioAttributeEval ase = new GainRatioAttributeEval();
        instances.setClassIndex(instances.numAttributes() - 1);
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }
}
