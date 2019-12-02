/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RedeDeConselhos;

import inteligenciacomputacional.Attack;
import inteligenciacomputacional.ClassifierExtended;
import inteligenciacomputacional.Run;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

/**
 *
 * @author silvio
 */
public class Parameters {
//[cluster:9];NaiveBayes;99,42837524414062%;25,0%;99,4395523071289%;8694.0;3.0;49.0;1.0

    public static final String DIRETORIO = "/home/silvio/datasets/CICIDS2017_RC/";
    public static final String DETECTOR_1 = "DETECTOR_UM_WEDNESDAY";
    public static final String DETECTOR_2 = "DETECTOR_DOIS_FRIDAY";
    static final int[] oneR_Detector1 = new int[]{79, 40, 68, 13, 55};
    static final int[] oneR_Detector2 = new int[]{79, 64, 5, 53, 35};

    /* Detector Selection */
    static final int[] FEATURE_SELECTION = oneR_Detector2;
    public static final String DETECTOR_SELECIONADO = DETECTOR_2;

    public static final String SEPARATOR = "/";
    public static final String FILE_TRAIN = DIRETORIO + DETECTOR_SELECIONADO + SEPARATOR + "10_train_files" + SEPARATOR + "compilado_train.csv"; //treino_binario_1000_1000
    public static final String FILE_EVALUATION = DIRETORIO + DETECTOR_SELECIONADO + SEPARATOR + "10_evaluation_files" + SEPARATOR + "compilado_evaluation.csv"; //ataque_binario_10k
    public static final String FILE_TEST = DIRETORIO + DETECTOR_SELECIONADO + SEPARATOR + "80_test_files" + SEPARATOR + "compilado_test_160.csv"; //ataque_binario_10k

    public static final String FILE_TRAIN_D1 = DIRETORIO + DETECTOR_1 + SEPARATOR + "10_train_files" + SEPARATOR + "compilado_train.csv"; //treino_binario_1000_1000
    public static final String FILE_EVALUATION_D1 = DIRETORIO + DETECTOR_1 + SEPARATOR + "10_evaluation_files" + SEPARATOR + "compilado_evaluation.csv"; //ataque_binario_10k
    public static final String FILE_TEST_D1 = DIRETORIO + DETECTOR_1 + SEPARATOR + "80_test_files" + SEPARATOR + "compilado_test_160.csv"; //ataque_binario_10k
    
    public static final String FILE_TRAIN_D2 = DIRETORIO + DETECTOR_2 + SEPARATOR + "10_train_files" + SEPARATOR + "compilado_train.csv"; //treino_binario_1000_1000
    public static final String FILE_EVALUATION_D2 = DIRETORIO + DETECTOR_2 + SEPARATOR + "10_evaluation_files" + SEPARATOR + "compilado_evaluation.csv"; //ataque_binario_10k
    public static final String FILE_TEST_D2 = DIRETORIO + DETECTOR_2 + SEPARATOR + "80_test_files" + SEPARATOR + "compilado_test_160.csv"; //ataque_binario_10k
    
    public static final ClassifierExtended RANDOM_TREE = new ClassifierExtended(true, new RandomTree(), "RandomTree");
    public static final ClassifierExtended RANDOM_FOREST = new ClassifierExtended(true, new RandomForest(), "RandomForest");
    public static final ClassifierExtended NAIVE_BAYES = new ClassifierExtended(true, new NaiveBayes(), "NaiveBayes");
    public static final ClassifierExtended REP_TREE = new ClassifierExtended(true, new REPTree(), "REPTree");
    public static final ClassifierExtended J48 = new ClassifierExtended(true, new J48(), "J48");
    public static final ClassifierExtended KNN = new ClassifierExtended(true, new IBk(), "KNN");
    public static final ClassifierExtended NBTREE = new ClassifierExtended(true, new NBTree(), "NBTree");

    // Run Settings
    public static final ClassifierExtended[] CLASSIFIERS_FOREACH = {NBTREE, RANDOM_TREE, J48, RANDOM_FOREST, REP_TREE, NAIVE_BAYES};// KNN está fora
//    public static final ClassifierExtended[] CLASSIFIERS_FOREACH = {NBTREE, RANDOM_TREE};// KNN está fora
    public static final boolean TEST_NORMALS = false;
    public static final boolean TEST_ATTACKS = true;
    public static final int TOTAL_FEATURES = 78;
    public static final boolean NORMALIZE = false;

}
