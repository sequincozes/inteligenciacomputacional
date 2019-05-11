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

    public static Instances[] loadAndFilter(boolean printSelection) throws Exception {

        Instances trainInstances = new Instances(Util.readDataFile(Parameters.TRAIN_FILE));
        Instances testAttackInstances = new Instances(Util.readDataFile(Parameters.TEST_ATTACK_FILE));
        Instances testNormalInstances = new Instances(Util.readDataFile(Parameters.TEST_NORMAL_FILE));

        if (Parameters.FEATURE_SELECTION.length > 0) {
            testAttackInstances = Util.applyFilterKeep(testAttackInstances);
            trainInstances = Util.applyFilterKeep(trainInstances);
            testNormalInstances = Util.applyFilterKeep(testNormalInstances);
            if (printSelection) {
                System.out.print(Arrays.toString(Parameters.FEATURE_SELECTION) + " - ");
                System.out.println("trainInstances: " + trainInstances.numAttributes());
                System.out.print("testAttackInstances: " + testAttackInstances.numAttributes());
                System.out.print("testNormalInstances: " + testNormalInstances.numAttributes());
            }
            trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
            testAttackInstances.setClassIndex(testAttackInstances.numAttributes() - 1);
            testNormalInstances.setClassIndex(testNormalInstances.numAttributes() - 1);
        }

        return new Instances[]{trainInstances, testAttackInstances, testNormalInstances};

    }

//    public static Instances trainInstances createPartition(Instances trainInstances, int i) {
//        Instances partition = trainInstances.
//                
//                return partition;
//    }
}
