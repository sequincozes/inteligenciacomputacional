/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CICIDS2017;

import inteligenciacomputacional.Resultado;
import inteligenciacomputacional.SolucaoCICIDS2017;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sequi
 */
public class GraspCICIDS2017 {

    private int maxIterations = 10; // quantidade total de iteracoes
    private int maxNoImprovement = 100; // iteracoes sem melhorias consecutivas
//    private final int featuresDisponiveis = 20;
    private static int NUM_FEATURES = 5;
    private SolucaoCICIDS2017 bestGlobal;

    public SolucaoCICIDS2017 runGrasp(int[] rcl, int tamanhoSelecao, String name, int iteracoes) throws Exception {
        NUM_FEATURES = tamanhoSelecao;
        maxIterations = iteracoes;
        int iteration = 0;
        int noImprovement = 0;

        /* RCL Baseada no Critério OneR */
        ArrayList<Integer> RCL = buildCustomRCL(rcl);

        // Solução Inicial Factível
        SolucaoCICIDS2017 initialSolution = buildSolucaoInicial(RCL);
        initialSolution = avaliar(initialSolution);
        bestGlobal = initialSolution.newClone();
        System.out.println("%%%% solucaoInicial: " + bestGlobal.getAcuracia());
        System.out.println("%%%% bestGlobal: " + bestGlobal.getAcuracia());

        /* Gera uma solução vizinha igual ou melhor */
        initialSolution = buscaLocal(initialSolution);
        if (initialSolution.isBest(bestGlobal)) {
            bestGlobal = initialSolution.newClone();
        }
        System.out.println("%%%% primeiraVizinha: " + initialSolution.getAcuracia());
        System.out.println("%%%% bestGlobal: " + bestGlobal.getAcuracia());

        imprimeEGravaCabecalho(name);
        while (iteration < this.maxIterations && noImprovement < this.maxNoImprovement) {
            System.out.println("%%%% bestGlobal: " + bestGlobal.getAcuracia());
            iteration = ++iteration;
            System.out.println("############# ITERATION (" + iteration + ") #############");

            SolucaoCICIDS2017 reconstructedSoluction = initialSolution.reconstruirNewSolucao(NUM_FEATURES);

            // Avalia Solução
            avaliar(reconstructedSoluction);
            System.out.println("%%%% solucaoReconstruida: " + reconstructedSoluction.getAcuracia());
            System.out.println("%%%% bestGlobal: " + bestGlobal.getAcuracia());

            // Busca por Ótimo Local
            reconstructedSoluction = buscaLocal(reconstructedSoluction);
            System.out.println("%%%% melhorVizinha: " + reconstructedSoluction.getAcuracia());
            System.out.println("%%%% bestGlobal: " + bestGlobal.getAcuracia());

            if (reconstructedSoluction.isBest(bestGlobal)) {
                bestGlobal = reconstructedSoluction.newClone();
                System.out.println("%%%% NOVA -> bestGlobal: " + bestGlobal.getAcuracia());

                System.out.print(" > " + String.valueOf(bestGlobal.getAcuracia()).substring(0, 7) + "% - Conjunto = " + bestGlobal.getFeaturesSelecionadas());
                bestGlobal.printSelection(">");
                noImprovement = 0;
            } else {
                noImprovement = ++noImprovement;
            }
            System.out.println("%%%% bestGlobal: " + bestGlobal.getAcuracia());

            System.out.println("######### Fim ITERAÇÂO (" + iteration + ") - Acc:" + String.valueOf(bestGlobal.getAcuracia()).substring(0, 7) + "% - Conjunto = " + (Arrays.toString(bestGlobal.getArrayFeaturesSelecionadas())));// " PROVA: " + ValidacaoCICIDS2017.executar(bestGlobal.getArrayFeaturesSelecionadas()).getAcuracia()));
            imprimeEGravaIteracao(iteration, name, bestGlobal.getAcuracia(), bestGlobal.getTaxa_detecao(), bestGlobal.getTaxa_falsos_positivos(), (Arrays.toString(bestGlobal.getArrayFeaturesSelecionadas())));
        }
        return bestGlobal;
    }

    private ArrayList<Integer> buildCustomRCL(int[] RCL) throws Exception {
        ArrayList<Integer> candidates = new ArrayList<>();
        System.out.print("RCL: {");
        for (int i = 0; i < RCL.length; i++) {
            candidates.add(RCL[i]);
            System.out.print(RCL[i]);
            if (i < RCL.length - 1) {
                System.out.print(",");
            } else {
                System.out.println("}");
            }
        }
        return candidates;
    }

    private SolucaoCICIDS2017 buildSolucaoInicial(ArrayList<Integer> RCL) {
        /* Seleciona as features das primeiras N posicoes como solução inicial*/
        SolucaoCICIDS2017 solution = new SolucaoCICIDS2017();
        while (solution.getFeaturesSelecionadas().size() < NUM_FEATURES) {
            solution.setAcuracia(0);
            solution.addFeature(RCL.remove(0));
        }
//        System.out.println("Solução cial:");
//        solution.printSelection();

        /* As demais features irão compor a RCL_flip */
        while (RCL.size() > 0) {
            solution.addFeatureFlip(RCL.remove(0));
        }
//        System.out.print("RCL:");
//        solution.printRCL();
        return solution;
    }

    private SolucaoCICIDS2017 buscaLocal(SolucaoCICIDS2017 solution) throws Exception {
        SolucaoCICIDS2017 solutionMutada = solution.bitFlipNewSolution();
//        System.out.println("Solucao: " + solution.getFeaturesSelecionadas() + " (" + solution.getAcuracia() + ")");
//        System.out.println("solucaoMutada" + solutionMutada.getFeaturesSelecionadas() + " (" + solutionMutada.getAcuracia() + ")");
        System.out.println("%%%% bestGlobal: " + bestGlobal.getAcuracia());
        System.out.println("%%%% bestLocal: " + solution.getAcuracia());
        System.out.println("%%%% garota: " + solutionMutada.getAcuracia());

        if (solutionMutada.isBest(solution)) {
            System.out.println("%%%% GEROU MELHOR ");
            return buscaLocal(solutionMutada);
        } else {
//            System.out.println("Melhor da iteração: " + solution.getFeaturesSelecionadas() + " (" + solution.getAcuracia() + ")");
            System.out.println("%%%% PAROU POR AQUI " + solution.getAcuracia());
            return solution;
        }

    }

    /*
 Function VNS (x, kmax, tmax );
 1: repeat
 2:    k ← 1;
 3:    repeat
 4:       x' ← Shake(x, k) /* Shaking 
 5:       x'' ← BestImprovement(x' ) /* Local search 
 6:       x ← NeighbourhoodChange(x, x'', k) /* Change neighbourhood 
 7:    until k = k_max ;
 8:    t ← CpuTime()
 9: until t > t_max ;
     */
    private static void imprimeEGravaCabecalho(String nome) throws IOException {
        System.out.print("CLASSIFICADOR" + "	");
        System.out.print("ACURÁCIA" + "	");
        System.out.print("DETECÇÃO" + "	");
        System.out.print("ALARMES FALSOS" + "	");
        System.out.print("SELECAO" + "	");

        FileWriter arq = new FileWriter("C:\\Users\\sequi\\Google Drive\\2019\\Datasets\\CICIDS2017\\sexta_full\\GRASP_RESULTS\\" + nome + ".txt", true);
        PrintWriter gravarArq = new PrintWriter(arq);
        gravarArq.append(nome + "	" + "ACURÁCIA" + "	" + "DETECÇÃO" + "	" + "ALARME-FALSO" + "	" + "SELECAO" + "\r\n");
        arq.close();
    }

    private static void imprimeEGravaIteracao(int iteracao, String nome, double acuracia, double deteccao, double alarmes_falsos, String selecao) throws IOException {
        FileWriter arq = new FileWriter("C:\\Users\\sequi\\Google Drive\\2019\\Datasets\\CICIDS2017\\sexta_full\\GRASP_RESULTS\\" + nome + ".txt", true);
        PrintWriter gravarArq = new PrintWriter(arq);
        gravarArq.append(nome + "(" + iteracao + ")"
                + "	" + acuracia
                + "	" + deteccao
                + "	" + alarmes_falsos
                + "	" + selecao
                + "	" + "\r\n");
        arq.close();
    }

    private static SolucaoCICIDS2017 avaliar(SolucaoCICIDS2017 solution) throws Exception {
        Resultado desempenho = ValidacaoCICIDS2017.executar(solution.getArrayFeaturesSelecionadas());
        solution.setAcuracia(desempenho.getAcuracia());
        try {
            float setTaxa_detecao = (float) desempenho.getTaxaDeteccao();
            float setTaxa_falsos_positivos = (float) desempenho.getTaxaAlarmeFalsos();
            solution.setTaxa_detecao(setTaxa_detecao);
            solution.setTaxa_falsos_positivos(setTaxa_falsos_positivos);
        } catch (java.lang.ArithmeticException e) {
            e.printStackTrace();
        }
        return solution;
    }

    private class VNS {

        public SolucaoCICIDS2017 run(SolucaoCICIDS2017 x, int kmax, long tmax) {
            long t = System.currentTimeMillis();
            do {
                int k = 1;
                do {
                    x = shake(); //' ← shake(x, k) /* Shaking */;
                    SolucaoCICIDS2017 x_new = bestImprovement(); // bestImprovement(x' );
                    x = null;//neighbourhoodChange(x, x'', k) /* Change neighbourhood */;
                } while (k < kmax);
            } while (t < tmax);
            return null;
        }

        /* Local search */
        private SolucaoCICIDS2017 bestImprovement() {
            /**
             * @TODO
             */
            return null;
        }

        /* Local search */
        private SolucaoCICIDS2017 shake() {
            /**
             * @TODO
             */
            return null;
        }
    }

}
