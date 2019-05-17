/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RedeDeConselhos;

import inteligenciacomputacional.ClassifierExtended;
import inteligenciacomputacional.Resultado;
import java.io.BufferedReader;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author silvio
 */
public class BemSimples {

    static Instances[] allInstances;
    static Classifier selectedClassifier = Parameters.NAIVE_BAYES.getClassifier();
    static boolean rawOutput = false;
    static boolean debug = false;

    public static void main(String[] args) throws Exception {
        allInstances = Util.loadAndFilter(false);
//        SimpleKMeans kmeans = avaliacaoComClustering(6);
//        testeComClustering(kmeans);
        semClustering();

    }

    public static void testeComClustering(SimpleKMeans kmeans) throws Exception {
        Resultado finalResult = new Resultado("FinalResult", 0, 0, 0, 0);

        /* Prepare Header Conflicting Results */
        System.out.print("Instância,Cluster,Classe,");
        for (ClassifierExtended conflicting : Parameters.CLASSIFIERS_FOREACH) {
            if (conflicting.equals(Parameters.CLASSIFIERS_FOREACH[Parameters.CLASSIFIERS_FOREACH.length - 1])) {
                System.out.print(conflicting.getClassifierName());
            } else {
                System.out.print(conflicting.getClassifierName() + ",");
            }
        }
        System.out.println("");
        for (int i = 0; i < allInstances[2].numInstances(); i++) {
            String outputConflitos = "";
            Instance testando = allInstances[2].instance(i);

            /* Prepare Conflicting Results */
            Instance tempInstance = new Instance(allInstances[2].instance(i));
            tempInstance.isMissing(tempInstance.numAttributes() - 1);
            int clusterNum = kmeans.clusterInstance(tempInstance);
            outputConflitos = outputConflitos + i + "," + clusterNum + "," + testando.classValue() + ",";

            ArrayList<ClassifierExtended> errados = new ArrayList();
            ArrayList<ClassifierExtended> certos = new ArrayList();
            for (ClassifierExtended tempClassifier : Parameters.CLASSIFIERS_FOREACH) {
                selectedClassifier = tempClassifier.getClassifier();
                double res1 = selectedClassifier.classifyInstance(testando);
                tempClassifier.setTempDecision(res1);
                if (res1 != testando.classValue()) {
                    errados.add(tempClassifier);
                } else {
                    certos.add(tempClassifier);
                }

                /* Prepare Conflicting Results */
                if (tempClassifier.equals(Parameters.CLASSIFIERS_FOREACH[Parameters.CLASSIFIERS_FOREACH.length - 1])) {
                    outputConflitos = outputConflitos + tempClassifier.getTempDecision();
                } else {
                    outputConflitos = outputConflitos + tempClassifier.getTempDecision() + ",";
                }
            }
            
            boolean existemErros = (certos.size() < Parameters.CLASSIFIERS_FOREACH.length);
            boolean existemAcertos = (errados.size() < Parameters.CLASSIFIERS_FOREACH.length);
            if (existemAcertos && existemErros) {
                System.out.println(outputConflitos);
//                System.out.println("Total: " + Parameters.CLASSIFIERS_FOREACH.length + ", Certos: " + certos.size() + "/" + Parameters.CLASSIFIERS_FOREACH.length + ", Errados: " + errados.size());
                if (debug) {
                    for (ClassifierExtended conflicting : certos) {
                        System.out.println(conflicting.getClassifierName() + " [CERTO] = " + conflicting.getTempDecision());
                    }
                }
                if (debug) {
                    for (ClassifierExtended conflicting : errados) {
                        System.out.println(conflicting.getClassifierName() + " [ERRADO] = " + conflicting.getTempDecision());
                    }
                }
            }

        }

    }

    public static SimpleKMeans avaliacaoComClustering(int k) throws Exception {
        if (allInstances[1].numInstances() != allInstances[3].numInstances()) {
            System.out.println("Bases de avaliação com tamanhos diferentes.");
            return null;
        }
        SimpleKMeans kmeans = Util.clusterData(allInstances[3], k);
        for (ClassifierExtended CLASSIFIERS_FOREACH : Parameters.CLASSIFIERS_FOREACH) {
            selectedClassifier = CLASSIFIERS_FOREACH.getClassifier();
            selectedClassifier.buildClassifier(allInstances[0]);
            Resultado[] clustersResults = new Resultado[k];
            int numCluster = 0;

            for (int i = 0; i < k; i++) {
                clustersResults[i] = new Resultado(CLASSIFIERS_FOREACH.getClassifierName() + " - Cluster: " + String.valueOf(numCluster), 0, 0, 0, 0);
                numCluster++;
            }

            int[] assignments = kmeans.getAssignments(); // Avaliação No-Label
            for (int i = 0; i < assignments.length; i++) {
                int cluster = assignments[i];
//                System.out.println("Cluster " + cluster + ": " + i);
                // Classificação
                Instance testando = allInstances[1].instance(i); // Avaliação com Label
                double res1 = selectedClassifier.classifyInstance(testando);
                updateClusterResult(clustersResults, cluster, testando, res1);
            }
            for (Resultado r : clustersResults) {
                r.recalcular();
                System.out.println(String.valueOf(r.getCx() + ";" + String.valueOf(r.getAcuracia()).replace(".", ",") + "%" + ";" + String.valueOf(r.getTaxaAlarmeFalsos()).replace(".", ",") + "%" + ";" + String.valueOf(r.getTaxaDeteccao()).replace(".", ",") + "%" + ";" + r.getVP() + ";" + r.getVN() + ";" + r.getFN() + ";" + r.getFP()));
            }
        }

        return kmeans;
    }

    public static void updateClusterResult(Resultado[] clustersResults, int cluster, Instance instance, double resultingClasss) {
        Resultado r = clustersResults[cluster];
        if (resultingClasss == instance.classValue() && instance.classValue() == 0) {
            r.setVN(r.getVN() + 1);
        } else if (resultingClasss == instance.classValue() && instance.classValue() == 1) {
            r.setVP(r.getVP() + 1);
        } else if (resultingClasss != instance.classValue() && instance.classValue() == 0) {
            r.setFP(r.getFP() + 1);
        } else if (resultingClasss != instance.classValue() && instance.classValue() == 1) {
            r.setFN(r.getFN() + 1);
        } else {
            System.err.println("Classe estranha: " + resultingClasss);
        }

    }

    public static void semClustering() throws Exception {
        /* SEM CLUSTERING */
        if (true) {
            for (ClassifierExtended CLASSIFIERS_FOREACH : Parameters.CLASSIFIERS_FOREACH) {
                selectedClassifier = CLASSIFIERS_FOREACH.getClassifier();
                Resultado rs = avaliaEssaGalera(CLASSIFIERS_FOREACH.getClassifierName(), allInstances[0], allInstances[1]);
//            System.out.println(String.valueOf(rs.getCx() + " => " + " | Acurácia: " + rs.getAcuracia() + "Alarme Falso: " + rs.getTaxaAlarmeFalsos() + "Detecção: " + rs.getTaxaDeteccao() + "VP: " + rs.getVP() + ", VN: " + rs.getVN() + ", FN: " + rs.getFN() + ", FP: " + rs.getFP()));
                System.out.println(String.valueOf(rs.getCx() + ";" + String.valueOf(rs.getAcuracia()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaAlarmeFalsos()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaDeteccao()).replace(".", ",") + "%" + ";" + rs.getVP() + ";" + rs.getVN() + ";" + rs.getFN() + ";" + rs.getFP()));
            }
        }

    }

    public static Resultado avaliaEssaGalera(String descricao, Instances treino, Instances teste) throws Exception {
        selectedClassifier.buildClassifier(treino);

        // Resultados
        double acuracia = 0;
        double txDec = 0;
        double txAFal = 0;
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;

        // Validação de ataques
//        System.out.println(" *** Ataques *** ");
        for (int i = 0; i < teste.numInstances() - 1; i++) {
            Instance testando = teste.instance(i);
            double res1 = selectedClassifier.classifyInstance(testando);
            if (res1 == testando.classValue() && res1 < 1) {
                VN = VN + 1;
            } else if (res1 != testando.classValue() && res1 < 1) {
                FN = FN + 1;
                if (debug) {
                    System.out.println("Errou: " + "[" + i + " = " + testando.classValue() + "] res: " + res1);
                }
            } else if (res1 == testando.classValue() && res1 > 0) {
                VP = VP + 1;
            } else {
                FP = FP + 1;
                if (debug) {
                    System.out.println("Errou: " + "[" + i + " = " + testando.classValue() + "] res: " + res1);
                }
            }

            try {

                acuracia = Float.valueOf(((VP + VN)) * 100) / Float.valueOf((VP + VN + FP + FN));
                txDec = Float.valueOf((VP * 100)) / Float.valueOf((VP + FN));  // Sensitividade ou Taxa de Detecção
                txAFal = Float.valueOf((FP * 100)) / Float.valueOf((VN + FP)); // Especificade ou Taxa de Alarmes Falsos    
            } catch (java.lang.ArithmeticException e) {
                System.out.println("Divisão por zero ((" + VP + " + " + VN + ") * 100) / (" + VP + " + " + VN + "+ " + FP + "+" + FN + "))");
            }
        }
        Resultado r = new Resultado(descricao, VP, FN, VN, FP, acuracia, txDec, txAFal);
        return r;

    }

    public static Resultado testaEssaGalera(String descricao, int begin, int end) throws Exception {
        selectedClassifier.buildClassifier(allInstances[0]);

        // Resultados
        double acuracia = 0;
        double txDec = 0;
        double txAFal = 0;
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;

        // Validação de ataques
//        System.out.println(" *** Ataques *** ");
        for (int i = begin; i < end; i++) {
            Instance testando = allInstances[1].instance(i);
            double res1 = selectedClassifier.classifyInstance(testando);
            if (res1 == testando.classValue()) {
                VP = VP + 1;
            } else {
                FN = FN + 1;
            }
        }

        // Validação de normais
//        System.out.println(" *** Normais *** ");
        for (int i = begin; i < end; i++) {
            Instance testando2 = allInstances[2].instance(i);
            double res2 = selectedClassifier.classifyInstance(testando2);
            if (res2 == testando2.classValue()) {
                VN = VN + 1;
            } else {
                FP = FP + 1;
            }
        }

        try {

            acuracia = Float.valueOf(((VP + VN)) * 100) / Float.valueOf((VP + VN + FP + FN));
            txDec = Float.valueOf((VP * 100)) / Float.valueOf((VP + FN));  // Sensitividade ou Taxa de Detecção
            txAFal = Float.valueOf((FP * 100)) / Float.valueOf((VN + FP)); // Especificade ou Taxa de Alarmes Falsos    
        } catch (java.lang.ArithmeticException e) {
            System.out.println("Divisão por zero ((" + VP + " + " + VN + ") * 100) / (" + VP + " + " + VN + "+ " + FP + "+" + FN + "))");
        }
        Resultado r = new Resultado(descricao, VP, FN, VN, FP, acuracia, txDec, txAFal);
        return r;

    }

    //Instances[]{trainInstances, testAttackInstances, testNormalInstances};
    public static Resultado testaEssaGaleraRetroalimentando(String descricao, int begin, int end) throws Exception {
        selectedClassifier.buildClassifier(allInstances[0]);

        // Resultados
        double acuracia = 0;
        double txDec = 0;
        double txAFal = 0;
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;

        // Validação de ataques
//        System.out.println(" *** Ataques *** ");
        for (int i = begin; i < end; i++) {
            Instance testando = allInstances[1].instance(i);
            double res1 = selectedClassifier.classifyInstance(testando);
            if (res1 == testando.classValue()) {
                VP = VP + 1;
            } else {
                FN = FN + 1;
            }
            testando.setClassValue(res1);
            allInstances[0].add(testando);
        }

        // Validação de normais
//        System.out.println(" *** Normais *** ");
        for (int i = begin; i < end; i++) {
            Instance testando2 = allInstances[2].instance(i);
            double res2 = selectedClassifier.classifyInstance(testando2);
            if (res2 == testando2.classValue()) {
                VN = VN + 1;
            } else {
                FP = FP + 1;
            }

            //retroalimentação
            testando2.setClassValue(res2);
            allInstances[0].add(testando2);

        }

        try {

            acuracia = Float.valueOf(((VP + VN)) * 100) / Float.valueOf((VP + VN + FP + FN));
            txDec = Float.valueOf((VP * 100)) / Float.valueOf((VP + FN));  // Sensitividade ou Taxa de Detecção
            txAFal = Float.valueOf((FP * 100)) / Float.valueOf((VN + FP)); // Especificade ou Taxa de Alarmes Falsos    
        } catch (java.lang.ArithmeticException e) {
            System.out.println("Divisão por zero ((" + VP + " + " + VN + ") * 100) / (" + VP + " + " + VN + "+ " + FP + "+" + FN + "))");
        }
        Resultado r = new Resultado(descricao, VP, FN, VN, FP, acuracia, txDec, txAFal);
        return r;

    }

    //Instances[]{trainInstances, testAttackInstances, testNormalInstances};
    public static Resultado testaEssaGaleraRetroalimentandoPerfeitamente(String descricao, int begin, int end) throws Exception {
        selectedClassifier.buildClassifier(allInstances[0]);

        // Resultados
        double acuracia = 0;
        double txDec = 0;
        double txAFal = 0;
        int VP = 0;
        int VN = 0;
        int FP = 0;
        int FN = 0;

        // Validação de ataques
//        System.out.println(" *** Ataques *** ");
        for (int i = begin; i < end; i++) {
            Instance testando = allInstances[1].instance(i);
            double res1 = selectedClassifier.classifyInstance(testando);
            if (res1 == testando.classValue()) {
                VP = VP + 1;
            } else {
                FN = FN + 1;
            }
//            testando.setClassValue(res1);
            allInstances[0].add(testando);
        }

        // Validação de normais
//        System.out.println(" *** Normais *** ");
        for (int i = begin; i < end; i++) {
            Instance testando2 = allInstances[2].instance(i);
            double res2 = selectedClassifier.classifyInstance(testando2);
            if (res2 == testando2.classValue()) {
                VN = VN + 1;
            } else {
                FP = FP + 1;
            }

            //retroalimentação
//            testando2.setClassValue(res2);
            allInstances[0].add(testando2);

        }

        try {

            acuracia = Float.valueOf(((VP + VN)) * 100) / Float.valueOf((VP + VN + FP + FN));
            txDec = Float.valueOf((VP * 100)) / Float.valueOf((VP + FN));  // Sensitividade ou Taxa de Detecção
            txAFal = Float.valueOf((FP * 100)) / Float.valueOf((VN + FP)); // Especificade ou Taxa de Alarmes Falsos    
        } catch (java.lang.ArithmeticException e) {
            System.out.println("Divisão por zero ((" + VP + " + " + VN + ") * 100) / (" + VP + " + " + VN + "+ " + FP + "+" + FN + "))");
        }
        Resultado r = new Resultado(descricao, VP, FN, VN, FP, acuracia, txDec, txAFal);
        return r;

    }

}
