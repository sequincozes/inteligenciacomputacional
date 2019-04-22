///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package inteligenciacomputacional;
//
//import static inteligenciacomputacional.Apuracao.readDataFile;
//import java.io.BufferedReader;
//import weka.attributeSelection.ASEvaluation;
//import weka.attributeSelection.CorrelationAttributeEval;
//import weka.attributeSelection.InfoGainAttributeEval;
//import weka.classifiers.Classifier;
//import weka.classifiers.bayes.NaiveBayes;
//import weka.classifiers.evaluation.Evaluation;
//import weka.classifiers.lazy.IBk;
//import weka.classifiers.lazy.KStar;
//import weka.classifiers.lazy.LWL;
//import weka.classifiers.rules.JRip;
//import weka.classifiers.trees.REPTree;
//import weka.classifiers.trees.RandomTree;
//import weka.core.Instances;
//
///**
// *
// * @author sequi'
// */
//public class Validacao_Standalone {
//
//    // File locations
//    static String conjunto = "15";
//    private static final String DIRETORIO = "C:\\Users\\sequi\\Dropbox\\Silvio\\pesquisa\\novos_experimentos\\c" + conjunto;
////    private static final String DIRETORIO = "C:\\Users\\sequi\\OneDrive\\Inteligencia Computacional Datasets\\dataset\\";
////    private static final String TRAIN_FILE = "c" + conjunto + "Treinamento_20079.txt";
////    private static final String TRAIN_FILE = "EXP_TRAIN_RAW.txt";
//    private static final String TRAIN_FILE = "c" + conjunto + "Treinamento.txt";
//    private static final String TEST_FILE = "c" + conjunto + "Teste.txt";
//    private static final String NORMAL_FILE = "c" + conjunto + "normal.txt";
//
////    private static final String TEST_FILE = "EXP_TEST_ATTACK.txt";
////    private static final String NORMAL_FILE = "EXP_TEST_NORMAL.txt";
//
//    // Setup
//    private static final Attack attackTest = new Attack("C15", 20079, 20079, Run.INCLUDE);
////    private static final Attack attackTest = new Attack("", 2, 1, Run.INCLUDE);
//
//    // Run Settings
//    private static final ClassifierExtended[] CLASSIFIERS = {
//        new ClassifierExtended(true, new IBk(), "KNN"),
//        new ClassifierExtended(true, new JRip(), "JRip"),
//        new ClassifierExtended(true, new KStar(), "KStar"),
//        new ClassifierExtended(true, new LWL(), "LWL"),
//        new ClassifierExtended(true, new NaiveBayes(), "Naive Bayes"),
//        new ClassifierExtended(true, new REPTree(), "REP Tree"),
//        new ClassifierExtended(true, new RandomTree(), "Random Tree")
//    };
//
//    private static final boolean TEST_NORMALS = true;
//    private static final boolean TEST_ATTACKS = true;
//    private static final Attack[] ATTACKS_TYPES = {attackTest};
//
//    public static long t1 = System.currentTimeMillis();
//
//    public static Resultado executar(int[] filter) throws Exception {
//        Resultado resultado = null;
//        for (Attack attack : ATTACKS_TYPES) {
//            if (attack.isIncludeOnTests()) {
//                Apuracao a = new Apuracao(
//                        attack.getNumberAttacks(),
//                        attack.getNumberNormals(),
//                        attack.getAttackName(),
//                        true,
//                        DIRETORIO,
//                        TRAIN_FILE,
//                        TEST_FILE,
//                        NORMAL_FILE
//                );
//
//                for (ClassifierExtended CLASSIFIERS_AUX : CLASSIFIERS) {
//                    ClassifierExtended classifierExtended = CLASSIFIERS_AUX;
//                    Classifier classifier = classifierExtended.getClassifier();
//                    String name = classifierExtended.getClassifierName();
//                    if (classifierExtended.isIncludeOnTests()) {
//                        if (TEST_NORMALS) {
//                            a.runWithNormals(classifier, name);
//                        }
//                        if (TEST_ATTACKS) {
//                            a.runWithAttacks(classifier, name);
//                        }
//                        resultado = a.getResults(name);
//                        /**
//                         * @TODO: Acumular resultados de múltiplos ataques e
//                         * classificadores
//                         */
//                        resultado.printResults();
//
//                    } else {
//                        System.out.println("----" + classifierExtended.getClassifierName() + ": OFF");
//                    }
//                }
//            }
//        }
////        System.out.println("Tempo de Recuperação: " + (System.currentTimeMillis() - t1) / 1000 + " segundos.");
//        return resultado;
//    }
//
//    public static void main(String[] args) throws Exception {
////        executar(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34});
//   executar(new int[]{});
////        executar();
//
////        for (int i = 0; i < 145; i++) {
////            System.out.println(i + 1 + "|" + calculateCoor(i));
////        }
////        getAttributesToKeep(35);
//    }
//
//    public static void executar() throws Exception {
//        for (Attack attack : ATTACKS_TYPES) {
//            if (attack.isIncludeOnTests()) {
//                Apuracao a = new Apuracao(
//                        attack.getNumberAttacks(),
//                        attack.getNumberNormals(),
//                        attack.getAttackName(),
//                        true,
//                        DIRETORIO,
//                        TRAIN_FILE,
//                        TEST_FILE,
//                        NORMAL_FILE
//                );
//
//                long time = System.currentTimeMillis();
//                a.loadAndFilter(new int[]{});
//                System.out.println("Treinamento: " + (System.currentTimeMillis() - time));
//                time = System.currentTimeMillis();
//                for (ClassifierExtended CLASSIFIERS_AUX : CLASSIFIERS) {
//                    System.out.println(" #### " + CLASSIFIERS_AUX.getClassifierName());
//                    ClassifierExtended classifierExtended = CLASSIFIERS_AUX;
//                    Classifier classifier = classifierExtended.getClassifier();
//                    String name = classifierExtended.getClassifierName();
//                    if (classifierExtended.isIncludeOnTests()) {
//                        if (TEST_NORMALS) {
//                            a.runWithNormals(classifier, name);
//                        }
//                        if (TEST_ATTACKS) {
//                            a.runWithAttacks(classifier, name);
//                        }
//                        Resultado resultado = a.getResults(name);
//                        resultado.printResults();
//
//                    } else {
//                        System.out.println("----" + classifierExtended.getClassifierName() + ": OFF");
//                    }
//                }
//                System.out.println("Teste: " + (System.currentTimeMillis() - time) / 1000);
//            }
//        }
//        System.out.println("Tempo de Recuperação: " + (System.currentTimeMillis() - t1) / 1000 + " segundos.");
//    }
//
//    public static double calculateGI(int featureIndice) throws Exception {
//        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
//        Instances instances = new Instances(dataset);
//        InfoGainAttributeEval ase = new InfoGainAttributeEval();
//        instances.setClassIndex(instances.numAttributes() - 1);
//        ase.buildEvaluator(instances);
//        return ase.evaluateAttribute(featureIndice);
//    }
//
//    public static double calculateCoor(int featureIndice) throws Exception {
//        BufferedReader dataset = readDataFile(DIRETORIO + ATTACKS_TYPES[0].getAttackName() + "\\" + ATTACKS_TYPES[0].getAttackName() + TRAIN_FILE);
//        Instances instances = new Instances(dataset);
//        CorrelationAttributeEval ase = new CorrelationAttributeEval();
//        instances.setClassIndex(instances.numAttributes() - 1);
////        System.out.println(Evaluation.getAllEvaluationMetricNames());
//
//        ase.buildEvaluator(instances);
//        return ase.evaluateAttribute(featureIndice);
//    }
//
//    public static Atributo[] getAttributesToKeep(int total) throws Exception {
////        Atributo[] full = new Atributo[total];
////        for (int i = 0; i < total; i++) {
////            full[i] = new Atributo(calculate(i), i);
////        }
////        Util.quickSort(full, 0, full.length - 1);
////        int corte = total / 2;
////        Atributo[] filter = new Atributo[corte];
////        int i = 0;
////        for (int j = full.length - 1; j > corte; j--) {
////            filter[i++] = full[j];
////        }
//////        for (int g = 0; g < filter.length; g++) {
//////            System.out.println(filter[g].getPos() + "-" + filter[g].getIg());
//////        }
//////        {26,22,23,24,20,9,7,15,34,5,30,32,2,8,1,4,0}
////        return filter;
//        Integer[] RCL = {66, 9, 10, 12, 14, 20, 23, 37, 46, 56, 83, 104, 3, 6, 32, 132, 115, 42, 117, 113, 64, 133, 134, 88, 118, 119, 120, 121, 122, 125, 126, 87, 84, 92, 101, 112, 111, 114, 116, 136, 69, 74, 60, 67, 45, 44, 72, 62, 41, 70, 71, 65, 55, 4, 5, 61, 58, 142, 76, 73, 7, 8};
//        Atributo[] full = new Atributo[total];
//        for (int i = 0; i < RCL.length; i++) {
//            full[i] = new Atributo(RCL[i], i);
//        }
//        return full;
//
//    }
//
//}
