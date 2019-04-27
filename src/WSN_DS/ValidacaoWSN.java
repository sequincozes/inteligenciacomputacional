/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WSN_DS;

import CICIDS2017.Output;
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
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 *
 * @author sequi'
 */
public class ValidacaoWSN {

    // File locations
//    private static final String DIRETORIO = "C:\\Users\\sequi\\Google Drive\\2019\\Datasets\\Datadet WSN\\";
    private static final String DIRETORIO = "/home/silvio/datasets/WSN/";
    public static final String SEPARATOR = "/";

    // WSN_BINARIO
    private static final String TRAIN_FILE = "binario_treino_1.csv";
    private static final String TEST_FILE = "binario_ataque_99.csv";
    private static final String NORMAL_FILE = "binario_normal_99.csv";

    // Setup 
    private static final Attack attackTest = new Attack("binario", 34253, 36666, Run.INCLUDE);
//    private static final Attack attackBlackhole = new Attack("blackhole", 9045, 9000, Run.INCLUDE);
//    private static final Attack attackFlooding = new Attack("flooding", 2981, 9000, Run.INCLUDE);
//    private static final Attack attackScheduling = new Attack("scheduling", 5974, 9000, Run.INCLUDE);
//    private static final Attack attackGrayhole = new Attack("grayhole", 13136, 9000, Run.INCLUDE);

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
    private static final ClassifierExtended[] CLASSIFIERS_FOREACH = {eNBT, eRT, eKNN, ej48, eRF, eRepTree}; //eNB
//    private static final ClassifierExtended[] CLASSIFIERS_FOREACH = {eNB};

    private static final ClassifierExtended[] CLASSIFIERS = new ClassifierExtended[1];
    private static final boolean TEST_NORMALS = true;
    private static final boolean TEST_ATTACKS = true;
    private static final int TOTAL_FEATURES = 18;
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

    static int[] OneRCICIDS = new int[]{18, 4, 13, 3, 6};//, 36, 53, 56, 64, 67};
    static int[] IGCICIDS = new int[]{5, 64, 53, 35, 56};//, 67, 9, 54, 7, 41};
    static int[] GRCICIDS = new int[]{53, 5, 64, 40, 7};//, 70, 9, 54, 41, 42};
    //
    static int[] GRASPRCLCICIDS = new int[]{53, 5, 64, 40, 7, 70, 9, 54, 41, 42, 43, 67, 35, 56, 49, 6, 66, 13, 55, 11};
    // WSN:
    static int[] OneR18WSN = new int[]{18, 7, 6, 1, 4, 2, 3, 5, 10, 11, 12, 13, 15, 16, 17, 14, 9, 8};//
    static int[] IG18WSN = new int[]{18, 6, 3, 7, 13, 12, 8, 17, 5, 10, 9, 14, 4, 1, 11, 15, 16, 2};
    static int[] GR18WSN = new int[]{3, 6, 10, 9, 13, 18, 7, 12, 8, 17, 5, 11, 15, 16, 14, 4, 1, 2};
    static int[] OneR10WSN = new int[]{18, 7, 6, 1, 4, 2, 3, 5, 10, 11};//, 12, 13, 15, 16, 17, 14, 9, 8};//
    static int[] IG10WSN = new int[]{18, 6, 3, 7, 13, 12, 8, 17, 5, 10};//, 9, 14, 4, 1, 11, 15, 16, 2};
    static int[] GR10WSN = new int[]{3, 6, 10, 9, 13, 18, 7, 12, 8, 17};//, 5, 11, 15, 16, 14, 4, 1, 2};
    static int[] OneR5WSN = new int[]{18, 7, 6, 1, 4};
    static int[] IG5WSN = new int[]{18, 6, 3, 7, 13};
    static int[] GR5WSN = new int[]{3, 6, 10, 9, 13};

    public static void main(String[] args) throws Exception {
//        avaliarESelecionar(10);
        int it = 50;
        if (1 == 1) {
            for (int i = 1; i <= 1; i++) {
                for (ClassifierExtended c : CLASSIFIERS_FOREACH) {
                    CLASSIFIERS[0] = c;
                    graspVND(GR18WSN, 5, c.getClassifierName() + "WSN_RODADA_" + i + "_5F", it);
                }
            }
        } else {
//        executar(new int[]{53,5,40,6,56});// 98.4011 RandomTree (VN: 256614.0 VP: 253587.0 FN: 6641.0 FP: 1649.0)
//        executar(new int[]{6,64,66,35,5});// 99.1347 KNN (VN: 256760.0 VP: 257245.0 FN: 2983.0 FP: 1503.0)
//        CLASSIFIERS[0] = eRepTree;
//        executar(new int[]{64,70,42,53,56}); //(82.1541748046875%)

//        grasp(GR18WSN, 5, "T2");
//        computarEGravar();
//        System.out.println("----------------------- GR");
//        executar(GR5WSN);
//        System.out.println("----------------------- IG");
//        executar(IG5WSN);
//            System.out.println("----------------------- OneR");
//            CLASSIFIERS[0] = eNB;
//        executar(OneR5WSN);
        }
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
//                System.out.println("Filtro aplicado: " + filterParaManter.length + " features mantidas.");
                for (ClassifierExtended CLASSIFIERS_AUX : CLASSIFIERS) {

                    Classifier classifier;
                    ClassifierExtended classifierExtended;
                    try {
                        classifierExtended = CLASSIFIERS_AUX;
                        classifier = classifierExtended.getClassifier();
                    } catch (NullPointerException n) {
//                        System.out.println("NullPointer: Classificador Padrão: " + eNB.getClassifierName());
                        CLASSIFIERS[0] = eNB;
                        classifierExtended = CLASSIFIERS[0];
                        classifier = classifierExtended.getClassifier();
                    }

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
//            double resultado = calcularOneRAttributeEval(i);
//            double resultado = calcularReliefF(i);
            double resultado = calcularGainRatioAttributeEval(i);

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
        String location = DIRETORIO + ATTACKS_TYPES[0].getAttackName() + SEPARATOR + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE;
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
        BufferedReader dataset = readDataFile(TRAIN_FILE);
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
        BufferedReader dataset = readDataFile(TRAIN_FILE);
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
        String location = DIRETORIO + ATTACKS_TYPES[0].getAttackName() + SEPARATOR + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE;
        BufferedReader dataset = readDataFile(location);
        Instances instances = new Instances(dataset);
        if (NORMALIZE) {
            instances = mormalizar(instances);
        }
        GainRatioAttributeEval ase = new GainRatioAttributeEval();
        instances.setClassIndex(instances.numAttributes() - 1);
        ase.buildEvaluator(instances);
        return ase.evaluateAttribute(featureIndice);
    }

    private static void grasp(int[] rcl, int tamanhoSelecao, String nome, int iteracoes, int ls) throws Exception, Exception {
        GraspWSN grasp = new GraspWSN();
        System.out.println("* Iniciou GRASP... *");

        SolucaoWSN best = grasp.runGrasp(rcl, tamanhoSelecao, nome, iteracoes, ls);
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("Conjunto: " + best.getFeaturesSelecionadas().toString());
        System.out.println("Acurácia: " + best.getAcuracia());
    }

    private static void graspVND(int[] rcl, int tamanhoSelecao, String nome, int iteracoes) throws Exception, Exception {
        GraspWSN grasp = new GraspWSN();
        System.out.println("* Iniciou GRASP VND... *");

        SolucaoWSN best = grasp.runGraspVND(rcl, tamanhoSelecao, nome, iteracoes);
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("Conjunto: " + best.getFeaturesSelecionadas().toString());
        System.out.println("Acurácia: " + best.getAcuracia());
    }

    private static void graspRVND(int[] rcl, int tamanhoSelecao, String nome, int iteracoes) throws Exception, Exception {
        GraspWSN grasp = new GraspWSN();
        System.out.println("* Iniciou GRASP RVND... *");

        SolucaoWSN best = grasp.runGraspRVND(rcl, tamanhoSelecao, nome, iteracoes);
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("*");
        System.out.println("Conjunto: " + best.getFeaturesSelecionadas().toString());
        System.out.println("Acurácia: " + best.getAcuracia());
    }
}
