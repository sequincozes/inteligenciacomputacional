///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package CICIDS2017;
//
///**
// *
// * @author sequi
// */
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.util.List;
//
//import weka.clusterers.FarthestFirst;
//import weka.clusterers.SimpleKMeans;
//import weka.core.Instance;
//import weka.core.Instances;
//
//public class Cluster {
//
//    private static final String DIRETORIO = "C:\\Users\\sequi\\Google Drive\\2019\\Datasets\\Datadet WSN\\binario\\";
//    // WSN_BINARIO
////    private static final String TRAIN_FILE = "binario_treino_1.csv";
////    private static final String TEST_FILE = "_ataque_99.csv"; // 336687
////    private static final String NORMAL_FILE = "binario_normal_99.csv"; // 34253
//    private static final String MIX_FILE = DIRETORIO + "ataque_e_normal_99_binario.csv";
//
//    public static BufferedReader readDataFile(String filename) {
//        BufferedReader inputReader = null;
//
//        try {
//            inputReader = new BufferedReader(new FileReader(filename));
//        } catch (FileNotFoundException ex) {
//            System.err.println("File not found: " + filename);
//        }
//
//        return inputReader;
//    }
//
//    public static void main(String[] args) throws Exception {
//        FarthestFirst kmeans = new FarthestFirst();
//        boolean binario = false;
//
//        kmeans.setSeed(10);
//
//        //important parameter to set: preserver order, number of cluster.
////        kmeans.setPreserveInstancesOrder(true);
//        if (binario) {
//            kmeans.setNumClusters(2);
//        } else {
//            kmeans.setNumClusters(2);
//        }
//
//        BufferedReader datafile = readDataFile(MIX_FILE);
//        Instances data = new Instances(datafile);
//
//        kmeans.buildClusterer(data);
//
//        // This array returns the cluster number (starting with 0) for each instance
//        // The array has as many elements as the number of instances
//        int[] assignments = new int[data.()];
////        int p = 0;
////        for (Instance inst : data) {
////            assignments[p++] = kmeans.clusterInstance(inst);
////        }
//        int i = 0;
//        int VP = 0;
//        int FP = 0;
//        int VN = 0;
//        int FN = 0;
//
//        if (binario) {
//            for (long clusterNum : assignments) {
//                if (i <= 34253) { // ATAQUE
//                    if (clusterNum == 1) {
//                        VP = VP + 1; // É ataque mesmo!
//                    } else {
//                        FN = FN + 1; // ERROU, FN
//                    }
//                } else {
//                    if (clusterNum == 0) { // NORMAL
//                        VN = VN + 1; // realmente é normal
//                    } else {
//                        FP = FP + 1; // Falso, não é ataque!
//                    }
//                }
//                i++;
//            }
//        } else {
//            for (long clusterNum : assignments) {
//                System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
//                if (i <= 34253) { // ATAQUE
//                    if (clusterNum == 1) {
//                        VP = VP + 1; // DETECTOU, VP
//                    } else {
//                        FN = FN + 1; // ERROU, FN
//                    }
//                } else {
//                    if (clusterNum != 1) { // NORMAL
//                        VN = VN + 1; // NAO DETECTOU, VN
//                    } else {
//                        FP = FP + 1; // Falso, não é normal
//                    }
//                }
//                i++;
//            }
//        }
//
//        System.out.printf("VP: %d, VN: %d, FP: %d, FN: %d", VP, VN, FP, FN);
//
//        //                System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
//    }
//
//    public static void testeKmeans(String[] args) throws Exception {
//        SimpleKMeans kmeans = new SimpleKMeans();
//        boolean binario = false;
//
//        kmeans.setSeed(10);
//
//        //important parameter to set: preserver order, number of cluster.
//        kmeans.setPreserveInstancesOrder(true);
//        if (binario) {
//            kmeans.setNumClusters(2);
//        } else {
//            kmeans.setNumClusters(5);
//        }
//
//        BufferedReader datafile = readDataFile(MIX_FILE);
//        Instances data = new Instances(datafile);
//
//        kmeans.buildClusterer(data);
//
//        // This array returns the cluster number (starting with 0) for each instance
//        // The array has as many elements as the number of instances
//        int[] assignments = kmeans.getAssignments();
//
//        int i = 0;
//        int VP = 0;
//        int FP = 0;
//        int VN = 0;
//        int FN = 0;
//
//        if (binario) {
//            for (long clusterNum : assignments) {
//                if (i <= 34253) { // ATAQUE
//                    if (clusterNum == 1) {
//                        VP = VP + 1; // É ataque mesmo!
//                    } else {
//                        FN = FN + 1; // ERROU, FN
//                    }
//                } else {
//                    if (clusterNum == 0) { // NORMAL
//                        VN = VN + 1; // realmente é normal
//                    } else {
//                        FP = FP + 1; // Falso, não é ataque!
//                    }
//                }
//                i++;
//            }
//        } else {
//            for (long clusterNum : assignments) {
////                System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
//                if (i <= 34253) { // ATAQUE
//                    if (clusterNum == 1) {
//                        VP = VP + 1; // DETECTOU, VP
//                    } else {
//                        FN = FN + 1; // ERROU, FN
//                    }
//                } else {
//                    if (clusterNum != 1) { // NORMAL
//                        VN = VN + 1; // NAO DETECTOU, VN
//                    } else {
//                        FP = FP + 1; // Falso, não é normal
//                    }
//                }
//                i++;
//            }
//        }
//
//        System.out.printf("VP: %d, VN: %d, FP: %d, FN: %d", VP, VN, FP, FN);
//
//        //                System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
//    }
//
//}
