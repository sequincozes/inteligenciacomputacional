/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CICIDS2017;

import com.sun.management.OperatingSystemMXBean;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.vladium.utils.SystemInformation;
import inteligenciacomputacional.Apuracao;
import inteligenciacomputacional.Attack;
import inteligenciacomputacional.ClassifierExtended;
import inteligenciacomputacional.FeatureAvaliada;
import inteligenciacomputacional.Resultado;
import inteligenciacomputacional.Run;
import inteligenciacomputacional.Util;
import static inteligenciacomputacional.Apuracao.readDataFile;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.security.AccessControlException;
import java.util.Scanner;
import javax.swing.SpringLayout;
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
import weka.classifiers.trees.J48;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 *
 * @author sequi'
 */
public class ValidacaoCICIDS2017 {

    // File locations
    private static final String DIRETORIO = "C:\\Users\\sequi\\Google Drive\\2019\\Datasets\\CICIDS2017\\sexta_full";

    // CICIDS
    private static final String TRAIN_FILE = "treino_binario.csv";
    private static final String TEST_FILE = "ataque_binario.csv";
    private static final String NORMAL_FILE = "normal.csv";

    private static final Attack attackTest = new Attack("", 260228, 258263, Run.INCLUDE);

    private static final RandomTree rt = new RandomTree();
    private static final NBTree nbt = new NBTree();
    private static final NaiveBayes nb = new NaiveBayes();
    private static final REPTree rep = new REPTree();
    private static final IBk knn = new IBk();
    private static final J48 j48 = new J48();
    private static final RandomForest randomForest = new RandomForest();

    private static final ClassifierExtended eRT = new ClassifierExtended(true, rt, "RandomTree");
    private static final ClassifierExtended eRF = new ClassifierExtended(true, randomForest, "RandomForest");
    private static final ClassifierExtended eNB = new ClassifierExtended(true, nb, "NaiveBayes");
    private static final ClassifierExtended eRepTree = new ClassifierExtended(true, rep, "REPTree");
    private static final ClassifierExtended ej48 = new ClassifierExtended(true, j48, "J48");
    private static final ClassifierExtended eKNN = new ClassifierExtended(true, knn, "KNN");
    private static final ClassifierExtended eNBT = new ClassifierExtended(true, nbt, "NBTree");

    // Run Settings
    private static final ClassifierExtended[] CLASSIFIERS_FOREACH = {eNBT, eNB, eRT, eKNN, ej48, eRF, eRepTree};
//    private static final ClassifierExtended[] CLASSIFIERS_FOREACH = {eRF, eRepTree};

    private static final ClassifierExtended[] CLASSIFIERS = new ClassifierExtended[1];
    private static final boolean TEST_NORMALS = true;
    private static final boolean TEST_ATTACKS = true;
    private static final int TOTAL_FEATURES = 78;
//    private static final Attack[] ATTACKS_TYPES = {attackBlackhole, attackFlooding, attackGrayhole, attackScheduling};
    private static final Attack[] ATTACKS_TYPES = {attackTest};
    private static final boolean NORMALIZE = false;

    public static long t1 = System.currentTimeMillis();

    public static Instances mormalizar(Instances instanceBulk) throws Exception {

        if (NORMALIZE) {
            Normalize n = new Normalize();
            n.setInputFormat(instanceBulk);
            Instances normalizedInstances = Filter.useFilter(instanceBulk, n);
            return normalizedInstances;
        }
        return instanceBulk;
    }

    static int[] OneRCICIDS = new int[]{5, 35, 9, 41, 7}; //1, 5, 21, 24, 35}; << tava //, 36, 53, 56, 64, 67}; << ns, mas é old
    static int[] IGCICIDS = new int[]{5, 64, 53, 35, 56};//, 67, 9, 54, 7, 41};
    static int[] GRCICIDS = new int[]{53, 5, 64, 40, 7};//, 70, 9, 54, 41, 42};
    //
    static int[] GRASPRCLCICIDS = new int[]{53, 5, 64, 40, 7, 70, 9, 54, 41, 42, 43, 67, 35, 56, 49, 6, 66, 13, 55, 11};

    public static void main(String[] args) throws Exception {
        avaliarESelecionar(5);
        if (1 == 0) {
            for (int i = 1; i <= 2; i++) {
                for (ClassifierExtended c : CLASSIFIERS_FOREACH) {
                    CLASSIFIERS[0] = c;
                    grasp(GRASPRCLCICIDS, 5, c.getClassifierName() + "CIDIDS_RODADA_" + i + "_5F", 100);
                }
            }
        }

        CLASSIFIERS[0] = eNB;
        executar(OneRCICIDS);

    }

    public static Resultado executar(int[] filterParaManter) throws Exception {
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

                a.loadAndFilter(filterParaManter, true);
                System.out.println("Filtro aplicado: " + filterParaManter.length + " features mantidas.");
                for (ClassifierExtended CLASSIFIERS_AUX : CLASSIFIERS) {

                    ClassifierExtended classifierExtended = CLASSIFIERS_AUX;
                    Classifier classifier = classifierExtended.getClassifier();

                    String name = classifierExtended.getClassifierName();
                    if (classifierExtended.isIncludeOnTests()) {
                        long cpu = System.currentTimeMillis();
                        if (TEST_NORMALS) {
                            a.runWithNormals(classifier, name);
                        }
                        if (TEST_ATTACKS) {
                            a.runWithAttacks(classifier, name);
                        }
                        resultado = a.getResults(attack.getAttackName() + "/" + name);
                        /**
                         * @TODO: Acumular resultados de múltiplos ataques e
                         * classificadores
                         */
                        cpu = System.currentTimeMillis() - cpu;
                        resultado.setTime(cpu);
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

    public static Resultado executar(Output modelo, int[] filterParaManter) throws Exception {
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

                a.loadAndFilter(filterParaManter, true);
//                System.out.println("Filtro aplicado: " + filterParaManter.length + " features mantidas.");
                for (ClassifierExtended CLASSIFIERS_AUX : CLASSIFIERS) {

                    ClassifierExtended classifierExtended = CLASSIFIERS_AUX;
                    Classifier classifier = classifierExtended.getClassifier();

                    String name = classifierExtended.getClassifierName();
                    if (classifierExtended.isIncludeOnTests()) {
                        long cpu = System.currentTimeMillis();
                        if (TEST_NORMALS) {
                            a.runWithNormals(classifier, name);
                        }
                        if (TEST_ATTACKS) {
                            a.runWithAttacks(classifier, name);
                        }
                        resultado = a.getResults(attack.getAttackName() + "/" + name);
                        /**
                         * @TODO: Acumular resultados de múltiplos ataques e
                         * classificadores
                         */
                        cpu = System.currentTimeMillis() - cpu;
                        resultado.setTime(cpu);
                        switch (modelo) {
                            case COMPLETO:
                                resultado.printResults(attack.getAttackName() + "/" + name);
                            case ITERACOES:
                                resultado.printIterations(attack.getAttackName() + "/" + name, DIRETORIO);
                            case NOPRINT: {
                                // Não printa
                            }
                        }

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
            double resultado = calcularOneRAttributeEval(i);
//            double resultado = calcularReliefF(i);
//            double resultado = calcularGainRatioAttributeEval(i);

            long tempoNovo = System.currentTimeMillis();
            tempoTotalFeature = tempoTotalFeature + tempoNovo;
//            System.out.println("[" + (tempoNovo - tempo) + " ms] F" + (i + 1) + ": " + resultado);
            System.out.println("F" + (i + 1) + ": " + resultado);

            features[i] = new FeatureAvaliada(resultado, i);

        }

        Util.quickSort(features, 0, features.length - 1);
        FeatureAvaliada[] filter = new FeatureAvaliada[featuresSelecionar];
        int i = 0;
        for (int j = features.length - 1; j >= featuresSelecionar; j--) {
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
                        long cpu = System.currentTimeMillis();
                        if (TEST_NORMALS) {
                            a.runWithNormals(classifier, name);
                        }
                        if (TEST_ATTACKS) {
                            a.runWithAttacks(classifier, name);
                        }
                        Resultado resultado = a.getResults(name);
                        cpu = System.currentTimeMillis() - cpu;
                        resultado.setTime(cpu);
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
        String location = DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE;
        BufferedReader dataset = readDataFile(location);
        Instances instances = new Instances(dataset);
        instances.setClassIndex(instances.numAttributes() - 1);
        if (NORMALIZE) {
            instances = mormalizar(instances);
        }
        InfoGainAttributeEval ase = new InfoGainAttributeEval();

        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularOneRAttributeEval(int featureIndice) throws Exception {
        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
        Instances instances = new Instances(dataset);
        if (NORMALIZE) {
            instances = mormalizar(instances);
        }
        OneRAttributeEval ase = new OneRAttributeEval();
        instances.setClassIndex(instances.numAttributes() - 1);
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    public static double calcularReliefF(int featureIndice) throws Exception {
        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
        Instances instances = new Instances(dataset);
        if (NORMALIZE) {
            instances = mormalizar(instances);
        }
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
        if (NORMALIZE) {
            instances = mormalizar(instances);
        }
        GainRatioAttributeEval ase = new GainRatioAttributeEval();
        instances.setClassIndex(instances.numAttributes() - 1);
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    private static void grasp(int[] rcl, int tamanhoSelecao, String nome, int iteracoes) throws Exception, Exception {
        GraspCICIDS2017 grasp = new GraspCICIDS2017();
        System.out.println("* Iniciou ... *");

        SolucaoCICIDS2017 best = grasp.runGrasp(rcl, tamanhoSelecao, nome, iteracoes);
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("Conjunto: " + best.getFeaturesSelecionadas().toString());
        System.out.println("Acurácia: " + best.getAcuracia());
    }
}
