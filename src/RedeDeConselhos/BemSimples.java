/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RedeDeConselhos;

import inteligenciacomputacional.ClassifierExtended;
import inteligenciacomputacional.Resultado;
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
    static Classifier selectedClassifier = null;// Parameters.NAIVE_BAYES.getClassifier();
    static boolean rawOutput = false;
    static boolean debug = false;

    public static void main(String[] args) throws Exception {
        allInstances = Util.loadAndFilter(false, true);
//        int K = 5; // Conselheiro (Detector 2)
//        int K = 4; // Detector 
//        System.out.println("K=" + K);
//        SimpleKMeans kmeans = avaliacaoComClustering(K, allInstances[0], allInstances[1], allInstances[3]);
//        Resultado rs = testeComClustering(kmeans, allInstances[2], K, true);;
//        System.out.println(String.valueOf(rs.getCx() + ";" + String.valueOf(rs.getAcuracia()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaAlarmeFalsos()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaDeteccao()).replace(".", ",") + "%" + ";" + rs.getVP() + ";" + rs.getVN() + ";" + rs.getFN() + ";" + rs.getFP()));;

//        semClustering(allInstances[1]);
        BemSimples.anomalyTests(331541);
    }

    public static void anomalyTests(int firstNormals) throws Exception {
        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setPreserveInstancesOrder(true);
        kmeans.setNumClusters(4);
        kmeans.buildClusterer(allInstances[0]);
        int VP = 0, VN = 0, FP = 0, FN = 0;
        long timeTotal = 0;

        // Modelo
        if (true) {
            int[] assignments = kmeans.getAssignments();
            int i = 0;
            int normal = 41434;
            int c0N = 0, c1N = 0, c2N = 0, c3N = 0;
            int c0A = 0, c1A = 0, c2A = 0, c3A = 0;
            for (int clusterNum : assignments) {
//                System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
                if (clusterNum == 0 && i < normal) {
                    c0N++;
                } else if (clusterNum == 1 && i < normal) {
                    c1N++;
                } else if (clusterNum == 2 && i < normal) {
                    c2N++;
                } else if (clusterNum == 3 && i < normal) {
                    c3N++;
                } else if (clusterNum == 0 && i >= normal) {
                    c0A++;
                } else if (clusterNum == 1 && i >= normal) {
                    c1A++;
                } else if (clusterNum == 2 && i >= normal) {
                    c2A++;
                } else if (clusterNum == 3 && i >= normal) {
                    c3A++;
                }
                i++;
            }
            System.out.println("Cluster 0: "+c0N+" normais/"+c0A+" anômalos");
            System.out.println("Cluster 1: "+c1N+" normais/"+c1A+" anômalos");
            System.out.println("Cluster 2: "+c2N+" normais/"+c2A+" anômalos");
            System.out.println("Cluster 3: "+c3N+" normais/"+c3A+" anômalos");
        }

        // Desconhecido
        if (false) {
            for (int i = 0; i < allInstances.length; i++) {
                long timeBegin = System.nanoTime();
                int clusterNum = kmeans.clusterInstance(allInstances[1].instance(i));
                long timeEnd = System.nanoTime();
                timeTotal = timeTotal + (timeEnd - timeBegin);
                if (i < firstNormals) { // é pra ser normal
                    if (clusterNum == 0 || clusterNum == 0 || clusterNum == 0) { // Clusters normais
                        VN = VN + 1;
                    } else { // clusters anômalos
                        FP = FP + 1;
                    }
                } else { // é pra ser anomalia
                    if (clusterNum == 0 || clusterNum == 0 || clusterNum == 0) { // Clusters normais
                        FN = FN + 1;
                    } else { // clusters anômalos
                        VP = VP + 1;
                    }
                }

            }
        }

        System.out.println(
                "VP: " + VP + ", VN: " + VN + ", FP: " + FP + ", FN: " + FN);

    }

    public static Resultado testeComClustering(SimpleKMeans kmeans, Instances instancias, int K, boolean printConflicts) throws Exception {
        Resultado finalResult = new Resultado("FinalResult", 0, 0, 0, 0);

        Resultado[][] resultadosPorCluster = new Resultado[K][Parameters.CLASSIFIERS_FOREACH.length];

        /* Prepare Header Conflicting Results */
        if (printConflicts) {
            System.out.print("Instância,Cluster,Classe,");
        }

        for (ClassifierExtended conflicting : Parameters.CLASSIFIERS_FOREACH) {
            if (conflicting.equals(Parameters.CLASSIFIERS_FOREACH[Parameters.CLASSIFIERS_FOREACH.length - 1])) {
                System.out.print(conflicting.getClassifierName());
            } else {
                System.out.print(conflicting.getClassifierName() + ",");
            }
        }
        System.out.println("");
        for (int i = 0; i < instancias.numInstances(); i++) {
            String outputConflitos = "";
            Instance testando = instancias.instance(i);

            /* Prepare Conflicting Results */
//            Instance tempInstance = new Instance(instancias.instance(i));
//            tempInstance.isMissing(tempInstance.numAttributes() - 1);
//            System.out.println("tempInstance: " + tempInstance);
//            int clusterNum = kmeans.clusterInstance(tempInstance);
            Instance tempInstance = new Instance(instancias.instance(i));
            int deleteClassIndex = tempInstance.numAttributes() - 1;
            tempInstance.deleteAttributeAt(deleteClassIndex);
            int clusterNum = kmeans.clusterInstance(tempInstance);

//            if (clusterNum != 7) {
//                throw new NullPointerException("RAPAIZZ, CLUSTER = "+clusterNum);
//                break;
//            }
//            System.out.println("Cluster:" + clusterNum + "tempInstance: " + tempInstance);  

            /* Prepare Conflicting Results */
            outputConflitos = outputConflitos + i + "," + clusterNum + "," + testando.classValue() + ",";

            ArrayList<ClassifierExtended> errados = new ArrayList();
            ArrayList<ClassifierExtended> certos = new ArrayList();
            for (ClassifierExtended tempClassifier : Parameters.CLASSIFIERS_FOREACH) {
                selectedClassifier = tempClassifier.getClassifier();
                double res1 = selectedClassifier.classifyInstance(testando);
                tempClassifier.setTempDecision(res1);
                if (res1 != testando.classValue()) {
                    errados.add(tempClassifier);
                    /* Preciso incrementar algo falso */
                    if (testando.classValue() == 0) {
                        finalResult.setFP(finalResult.getFP() + 1);
                    } else {
                        finalResult.setFN(finalResult.getFN() + 1);
                    }
                } else {
                    certos.add(tempClassifier);
                    /* Preciso incrementar algo verdadeiro */
                    if (testando.classValue() == 1) {
                        finalResult.setVP(finalResult.getVP() + 1);
                    } else {
                        finalResult.setVN(finalResult.getVN() + 1);
                    }
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
            boolean exibirApenasConflitos = false;
            if (printConflicts) {
                if (exibirApenasConflitos) {
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
                } else {
                    System.out.println(outputConflitos);
                }
            }

        }
        finalResult.recalcular();
        return finalResult;
    }

    public static SimpleKMeans avaliacaoComClustering(int k, Instances treino, Instances avaliacao, Instances avaliacaoLimpa) throws Exception {
        if (avaliacao.numInstances() != avaliacaoLimpa.numInstances()) {
            System.out.println("Bases de avaliação com tamanhos diferentes.");
            return null;
        }
        SimpleKMeans kmeans = Util.clusterData(avaliacaoLimpa, k);
        for (ClassifierExtended CLASSIFIERS_FOREACH : Parameters.CLASSIFIERS_FOREACH) {
            selectedClassifier = CLASSIFIERS_FOREACH.getClassifier();
            selectedClassifier.buildClassifier(treino);
            Resultado[] clustersResults = new Resultado[k];
            int numCluster = 0;

            for (int i = 0; i < k; i++) {
                clustersResults[i] = new Resultado("[cluster:" + String.valueOf(numCluster) + "];" + CLASSIFIERS_FOREACH.getClassifierName(), 0, 0, 0, 0);
                numCluster++;
            }

            int[] assignments = kmeans.getAssignments(); // Avaliação No-Label
            for (int i = 0; i < assignments.length; i++) {
                int cluster = assignments[i];
//                System.out.println("Cluster " + cluster + ": " + i);
                // Classificação
                Instance testando = avaliacao.instance(i); // Avaliação com Label
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

    public static void semClustering(Instances instances) throws Exception {
        /* SEM CLUSTERING */
        if (true) {
            for (ClassifierExtended CLASSIFIERS_FOREACH : Parameters.CLASSIFIERS_FOREACH) {
                selectedClassifier = CLASSIFIERS_FOREACH.getClassifier();
                Resultado rs = avaliaEssaGalera(CLASSIFIERS_FOREACH.getClassifierName(), allInstances[0], instances);
//            System.out.println(String.valueOf(rs.getCx() + " => " + " | Acurácia: " + rs.getAcuracia() + "Alarme Falso: " + rs.getTaxaAlarmeFalsos() + "Detecção: " + rs.getTaxaDeteccao() + "VP: " + rs.getVP() + ", VN: " + rs.getVN() + ", FN: " + rs.getFN() + ", FP: " + rs.getFP()));
//                System.out.println(
                //"[" + rs.getTime() + "ns (" + ((rs.getVN() + rs.getVP() + rs.getFN() + rs.getFP())/rs.getTime()) + "samp/ns)"+
//                        "(" + (rs.getTime()/(rs.getVN() + rs.getVP() + rs.getFN() + rs.getFP())) + "ns/samp)"
                //+ "]"
//                        String.valueOf(rs.getCx() + ";" + String.valueOf(rs.getAcuracia()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaAlarmeFalsos()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaDeteccao()).replace(".", ",") + "%" + ";" + rs.getVP() + ";" + rs.getVN() + ";" + rs.getFN() + ";" + rs.getFP())
//                );
                System.out.println(rs.getCx() + ": " + (rs.getTime() / (rs.getVN() + rs.getVP() + rs.getFN() + rs.getFP()) / 1000) + "ms");

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
        long timeDiff = 0;
        for (int i = 0; i < teste.numInstances() - 1; i++) {
            Instance testando = teste.instance(i);
            long time = System.nanoTime();
            double res1 = selectedClassifier.classifyInstance(testando);
            long timeEnd = System.nanoTime();
            timeDiff = timeDiff + timeEnd - time;
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
        Resultado r = new Resultado(descricao, VP, FN, VN, FP, acuracia, txDec, txAFal, timeDiff);
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
