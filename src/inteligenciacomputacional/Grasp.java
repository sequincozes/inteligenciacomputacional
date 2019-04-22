/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inteligenciacomputacional;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sequi
 */
public class Grasp {

    private final int maxIterations = 10; // quantidade total de iteracoes
    private final int maxNoImprovement = 1; // iteracoes sem melhorias consecutivas
    private final int featuresDisponiveis = 20;
    private final int gerarFeatures = 10;

    public Solucao grasp() throws Exception {
        Solucao bestSolution = null;
        int iteration = 0;
        int noImprovement = 0;

        while (iteration < this.maxIterations && noImprovement < this.maxNoImprovement) {
            iteration = ++iteration;
            System.out.println("############# ITERATION (" + iteration + ") #############");
            ArrayList<Integer> RCL = buildRCL(featuresDisponiveis);
            ArrayList<Integer> RCL_flip = buildRCL(featuresDisponiveis);;

            // Solução Inicial Factível
            Solucao solution = faseDeConstrucao(RCL);
            solution.featuresSelecionadas(RCL);

            // Avalia Solução
            solution.setAcuracia(Validacao.executar(solution.getExcludedFeatures()).getAcuracia());

            // Busca por Ótimo Local
            System.out.println("#### BUSCA LOCAL (RCL: " + RCL.size() + ") #### ");
            solution = buscaLocal(solution, RCL_flip);

            // Atualiza melhor solução geral
            if (bestSolution == null) {
                bestSolution = solution;
            }

            if (solution.isBest(bestSolution)) {
                bestSolution = solution;
                noImprovement = 0;
            } else {
                noImprovement = ++noImprovement;
            }
            System.out.println();
            System.out.println("######### END (" + iteration + ") - Acc:" + bestSolution.getAcuracia());
        }
        return bestSolution;
    }

    // Entropia
//    private ArrayList<Integer> buildRCL(int size) throws Exception {
//        ArrayList<Integer> candidates = new ArrayList<>();
//        System.out.print("[RCL] Seleção por Entropia: {");
//        Atributo[] candidatos = Validacao.getAttributesToKeep(size);
//        System.out.println("Candidatos: " + candidatos.length);
//        for (int i = candidatos.length - 1; i >= 0; i--) {
//            int featureIndex = candidatos[i].getPos();
//            candidates.add(featureIndex);
//            System.out.print(featureIndex);
//            if (i > 0) {
//                System.out.print(",");
//            } else {
//                System.out.println("}");
//            }
//        }
//        return candidates;
//    }
    private ArrayList<Integer> buildRCL(int size) throws Exception {
        ArrayList<Integer> candidates = new ArrayList<>();
//        Integer[] RCL = {66, 9, 10, 12, 14, 20, 23, 37, 46, 56, 83, 104, 3, 6, 32, 132, 115, 42, 117, 113, 64, 133, 134, 88, 118, 119, 120, 121, 122, 125, 126, 87, 84, 92, 101, 112, 111, 114, 116, 136, 69, 74, 60, 67, 45, 44, 72, 62, 41, 70, 71, 65, 55, 4, 5, 61, 58, 142, 76, 73, 7, 8};
        Integer[] RCL = {107, 38, 7, 4, 9, 8, 79, 82, 142, 154, 64, 67, 6, 5, 61, 71, 77, 76, 47, 68};
        Atributo[] candidatos = new Atributo[RCL.length];
        for (int i = 0; i < RCL.length; i++) {
            candidatos[i] = new Atributo(RCL[i], i);
        }
        System.out.println("Candidatos: " + candidatos.length);
        for (int i = candidatos.length - 1; i >= 0; i--) {
            int featureIndex = candidatos[i].getPos();
            candidates.add(featureIndex);
            System.out.print(featureIndex);
            if (i > 0) {
                System.out.print(",");
            } else {
                System.out.println("}");
            }
        }
        return candidates;
    }

    private Solucao faseDeConstrucao(ArrayList<Integer> RCL) {
        ArrayList<Integer> RCLTemp = RCL;
        Random r = new Random();
        Solucao solution = new Solucao();
        while (RCLTemp.size() > gerarFeatures) {
            int featureIndex = r.nextInt(RCLTemp.size());
            solution.setAcuracia(0);
            solution.addFeature(RCLTemp.remove(featureIndex));
        }
        return solution;
    }
//    
//    private Solucao faseDeConstrucao(ArrayList<Integer> RCL) {
//        ArrayList<Integer> RCLTemp = RCL;
//        Random r = new Random();
//        Solucao solution = new Solucao();
//        for (int i = 0; i <= gerarFeatures; i++) {
//            int featureIndex = r.nextInt(RCLTemp.size());
//            solution.setAcuracia(0);
//            solution.addFeature(RCLTemp.remove(featureIndex));
//        }
//        return solution;
//    }

    // Variable neighborhood descent (VND) 
    private Solucao buscaLocal(Solucao solution, ArrayList<Integer> RCL) throws Exception {
        Solucao solutionMutada = new Solucao(RCL, solution.getKeepFeatures());
        solutionMutada = solutionMutada.bitFlip();
        solutionMutada.setAcuracia(Validacao.executar(solutionMutada.getExcludedFeatures()).getAcuracia());
        if (solution.isBest(solutionMutada)) {
            solution.printSelection();
            return solution;
        } else {
            return buscaLocal(solutionMutada, RCL);
        }

    }

}
