/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RedeDeConselhos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

/**
 *
 * @author silvio
 */
public class Util {

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

    public static Instances applyFilterKeep(Instances instances) {
        int[] fs = Parameters.FEATURE_SELECTION;
        Arrays.sort(fs);
        int deletadas = 0;
        for (int i = instances.numAttributes() - 1; i > 0; i--) {
            if (instances.numAttributes() <= fs.length) {
                System.err.println("O número de features precisa ser maior que o filtro.");
                return instances;
            }
            boolean deletar = true;
            for (int j : fs) {
                if (i == j) {
                    deletar = false;
//                    System.out.println("Manter [" + i + "]:" + instances.attribute(i));;
                }
            }
            if (deletar) {
                instances.deleteAttributeAt(i - 1);
            }
        }
        return instances;
    }

    public static Instances[] loadAndFilter(boolean printSelection, boolean anomaly) throws Exception {

        if (anomaly) {
            Instances trainInstancesNoLabel = new Instances(Util.readDataFile(Parameters.FILE_TRAIN));
            Instances testInstancesNoLabel = new Instances(Util.readDataFile(Parameters.FILE_TEST));
            System.out.println("FILE_TRAIN: " + Parameters.FILE_TRAIN);
            System.out.println("FILE_TEST: " + Parameters.FILE_TEST);
            if (Parameters.FEATURE_SELECTION.length > 0) {
                trainInstancesNoLabel = Util.applyFilterKeep(trainInstancesNoLabel);
                testInstancesNoLabel = Util.applyFilterKeep(testInstancesNoLabel);
            }

            testInstancesNoLabel.deleteAttributeAt(testInstancesNoLabel.numAttributes() - 1); // Remove classe 
//            testInstancesNoLabel.setClassIndex(testInstancesNoLabel.numAttributes() - 1);

            trainInstancesNoLabel.deleteAttributeAt(trainInstancesNoLabel.numAttributes() - 1); // Remove classe 
//            trainInstancesNoLabel.setClassIndex(trainInstancesNoLabel.numAttributes() - 1);

            return new Instances[]{trainInstancesNoLabel, testInstancesNoLabel};

        } else {
            Instances trainInstances = new Instances(Util.readDataFile(Parameters.FILE_TRAIN));
            Instances evaluationInstances = new Instances(Util.readDataFile(Parameters.FILE_EVALUATION));
            Instances testInstances = new Instances(Util.readDataFile(Parameters.FILE_TEST));

            /* Não-Supervisionado: K-Means */
            Instances evaluationInstancesNoLabel = new Instances(Util.readDataFile(Parameters.FILE_EVALUATION));

            if (Parameters.FEATURE_SELECTION.length > 0) {
                trainInstances = Util.applyFilterKeep(trainInstances);
                evaluationInstances = Util.applyFilterKeep(evaluationInstances);
                testInstances = Util.applyFilterKeep(testInstances);
                evaluationInstancesNoLabel = Util.applyFilterKeep(evaluationInstancesNoLabel);
                if (printSelection) {
                    System.out.print(Arrays.toString(Parameters.FEATURE_SELECTION) + " - ");
                    System.out.println("trainInstances: " + trainInstances.numAttributes());
                    System.out.print("evaluationInstances: " + evaluationInstances.numAttributes());
                    System.out.print("testInstances: " + testInstances.numAttributes());
                    System.out.print("testInstances: " + evaluationInstancesNoLabel.numAttributes());
                }

                evaluationInstancesNoLabel.deleteAttributeAt(evaluationInstancesNoLabel.numAttributes() - 1); // Remove classe
                trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
                evaluationInstances.setClassIndex(evaluationInstances.numAttributes() - 1);
                testInstances.setClassIndex(testInstances.numAttributes() - 1);

            }

            return new Instances[]{trainInstances, evaluationInstances, testInstances, evaluationInstancesNoLabel};
        }

    }

    public static SimpleKMeans clusterData(Instances evaluation, int k) throws Exception {
        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setSeed(k * 2);
        kmeans.setPreserveInstancesOrder(true);
        kmeans.setNumClusters(k);
        kmeans.buildClusterer(evaluation);
        return kmeans;

    }

}
