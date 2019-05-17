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

    public static final String DIRETORIO = "/home/silvio/datasets/CICIDS2017_RC/DETECTOR_DOIS_WEDNESDAY";
    public static final String SEPARATOR = "/";
    public static final String FILE_TRAIN = DIRETORIO + SEPARATOR + "10_train_files" + SEPARATOR + "compilado_train.csv"; //treino_binario_1000_1000
    public static final String FILE_EVALUATION = DIRETORIO + SEPARATOR + "10_evaluation_files" + SEPARATOR + "compilado_evaluation.csv"; //ataque_binario_10k
    public static final String FILE_TEST = DIRETORIO + SEPARATOR + "80_test_files" + SEPARATOR + "compilado_test.csv"; //ataque_binario_10k

    public static final ClassifierExtended RANDOM_TREE = new ClassifierExtended(true, new RandomTree(), "RandomTree");
    public static final ClassifierExtended RANDOM_FOREST = new ClassifierExtended(true, new RandomForest(), "RandomForest");
    public static final ClassifierExtended NAIVE_BAYES = new ClassifierExtended(true, new NaiveBayes(), "NaiveBayes");
    public static final ClassifierExtended REP_TREE = new ClassifierExtended(true, new REPTree(), "REPTree");
    public static final ClassifierExtended J48 = new ClassifierExtended(true, new J48(), "J48");
    public static final ClassifierExtended KNN = new ClassifierExtended(true, new IBk(), "KNN");
    public static final ClassifierExtended NBTREE = new ClassifierExtended(true, new NBTree(), "NBTree");

    // Run Settings
    public static final ClassifierExtended[] CLASSIFIERS_FOREACH = {NBTREE, RANDOM_TREE, J48, RANDOM_FOREST, REP_TREE, NAIVE_BAYES};// KNN está fora
    public static final boolean TEST_NORMALS = false;
    public static final boolean TEST_ATTACKS = true;
    public static final int TOTAL_FEATURES = 78;
    public static final boolean NORMALIZE = false;

    //Selected by GR
//    static int[] FEATURE_SELECTION = new int[]{53, 5, 64, 40, 7, 70, 9, 54, 41, 42, 43, 67, 35, 49, 6, 66, 13, 55, 11, 1};
    //Selected by OneR
    static int[] FEATURE_SELECTION = new int[]{79, 40, 68, 13, 55};

}
