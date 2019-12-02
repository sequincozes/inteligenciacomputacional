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

/*

Treina D1;
Treina D2;

Avalia D1:
 for clusters    
    ClusterN;
        - Classificador 1 = acuracia
        - Classificador 2 = acuracia
        - Classificador 3 = acuracia 
        - Classificador 4 = acuracia
        - Classificador 5 = acuracia
  

Testa D1:
    for instancias:
        cluster = Kmeans.cluster
            clusterX.classificadorY() = acuracia, 0 // Normal
            clusterX.classificadorY() = acuracia, 1 // Ataque
            clusterX.classificadorY() = acuracia, 0 // Normal
            armazena: instanciaID|D1|clusterX|classificadorY|acuracia|resultado


*/


/**
 *
 * @author silvio
 */
public class BemSimplesJournal {

    static Instances[] allInstances;
    static Classifier selectedClassifier = Parameters.CLASSIFIERS_FOREACH[0].getClassifier();// Parameters.NAIVE_BAYES.getClassifier();
    static boolean rawOutput = false;
    static boolean debug = false;
    static SimpleKMeans kmeans;

    public static void main(String[] args) throws Exception {
//        allInstances = Util.loadAndFilter(false, false);
        Instances fullTrainD1 = Util.loadSingleInstances(true, true, Parameters.FILE_TRAIN_D1);
        Instances fullTrainD2 = Util.loadSingleInstances(true, true, Parameters.FILE_TRAIN_D2);

        Instances[] metadeTrain = Util.splitInstance(fullTrainD2, 1); // Proporçao de treinamento
        Instances[] treinoEval = Util.splitInstance(metadeTrain[0], 50); //50% ev e 50% tra
        allInstances = new Instances[3];
        allInstances[0] = treinoEval[0]; // train
        allInstances[1] = treinoEval[1]; // evaluation

        Instances fullTest = Util.loadSingleInstances(true, true, Parameters.FILE_TEST);
//        Instances[] metadesTest = Util.splitInstance(fullTrain, 50); //Porcentagem da base de testes
        allInstances[2] = fullTest;// metadesTest[0]; // teste

        /* AVALIAÇÃO */
        int K = 4;
//        System.out.println("K=" + K);
//        kmeans = avaliacaoComClustering(K, allInstances[0], allInstances[1], allInstances[3]);
        for (ClassifierExtended CLASSIFIERS_FOREACH : Parameters.CLASSIFIERS_FOREACH) {
            selectedClassifier = CLASSIFIERS_FOREACH.getClassifier();
            System.out.println("-------------");
            System.out.println("Accurácia: " + CLASSIFIERS_FOREACH.getClassifierName() + ": " + testaEssaGaleraRetroalimentando("All", 0, 100000));
            System.out.println("Accurácia: " + CLASSIFIERS_FOREACH.getClassifierName() + ": " + testaEssaGaleraRetroalimentando("All", 100000, 200000));
            System.out.println("Accurácia: " + CLASSIFIERS_FOREACH.getClassifierName() + ": " + testaEssaGaleraRetroalimentando("All", 200000, 300000));
            System.out.println("Accurácia: " + CLASSIFIERS_FOREACH.getClassifierName() + ": " + testaEssaGaleraRetroalimentando("All", 300000, 400000));
            System.out.println("Accurácia: " + CLASSIFIERS_FOREACH.getClassifierName() + ": " + testaEssaGaleraRetroalimentando("All", 400000, 500000));
        }
//        int K = 5; // Conselheiro (Detector 2)
//        int K = 4; // Detector 
//        System.out.println("K=" + K);
//        SimpleKMeans kmeans = avaliacaoComClustering(K, allInstances[0], allInstances[1], allInstances[3]);
//        Resultado rs = testeComClustering(kmeans, allInstances[2], K, true);;
//        System.out.println(String.valueOf(rs.getCx() + ";" + String.valueOf(rs.getAcuracia()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaAlarmeFalsos()).replace(".", ",") + "%" + ";" + String.valueOf(rs.getTaxaDeteccao()).replace(".", ",") + "%" + ";" + rs.getVP() + ";" + rs.getVN() + ";" + rs.getFN() + ";" + rs.getFP()));;
//        semClustering(allInstances[1]);
//        BemSimples.anomalyTests(331541);
    }

    //Instances[]{trainInstances, testAttackInstances, testNormalInstances};
    public static double testaEssaGaleraRetroalimentando(String descricao, int begin, int end) throws Exception {
        selectedClassifier.buildClassifier(allInstances[0]);
        System.out.println("Train: " + allInstances[0].numInstances());
        System.out.println("Eval: " + allInstances[1].numInstances());
        System.out.println("Test: " + allInstances[2].numInstances());
        // Resultados
        double acuracia = 0;
        int acertos = 0;

        // Validação de ataques + Normais
        for (int i = begin; i < end; i++) {
            Instance testando = allInstances[2].instance(i);
            double res1 = selectedClassifier.classifyInstance(testando);
            if (res1 == testando.classValue()) {
                acertos = acertos + 1;
            }
            testando.setClassValue(res1);
            allInstances[0].add(testando);
        }

        try {
            acuracia = Float.valueOf((acertos * 100) / Float.valueOf((end - begin)));
        } catch (java.lang.ArithmeticException e) {
            System.out.println("Divisão por zero ((" + acertos + ") * 100) / (" + (end - begin) + "))");
        }
        return acuracia;

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
