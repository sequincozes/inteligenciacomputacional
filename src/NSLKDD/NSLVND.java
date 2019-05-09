/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NSLKDD;

import inteligenciacomputacional.SolucaoNSL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author silvio
 */
public class NSLVND {

    public static SolucaoNSL doVND(SolucaoNSL semente) throws Exception {
        SolucaoNSL melhor = semente.newClone();
        for (int i = 0; i < 3; i++) {
            System.out.println("-> BEGIN| Estrutura de Vizinhança: " + i);
            long tempoInicial = System.currentTimeMillis();
            SolucaoNSL nova = buscaLocal(semente, i);
            long tempoFinal = System.currentTimeMillis() - tempoInicial;
            System.out.println("-> END| Estrutura de Vizinhança: " + i + "[" + tempoFinal + "]");
            if (nova.isBest(melhor)) {
                melhor = nova.newClone();
            }
        }
        return melhor;
    }

    public static SolucaoNSL doRVND(SolucaoNSL semente) throws Exception {

        // Inicializar T
        ArrayList<Integer> T = new ArrayList<>();
        T.add(0);
        T.add(1);
        T.add(2);
        int min = 0;
        int max = T.size();

        SolucaoNSL melhor = semente.newClone();

        while (T.size() > 0) {
            try {

                int randomNum = ThreadLocalRandom.current().nextInt(min, max);
                SolucaoNSL nova = buscaLocal(semente, randomNum);
                if (nova.isBest(melhor)) {
                    melhor = nova.newClone();
                    T = new ArrayList<>();
                    System.out.println("T: " + T.size());
                    T.add(0);
                    System.out.println("ADD(0) - T: " + T.size());
                    T.add(1);
                    System.out.println("ADD(1) - T: " + T.size());
                    T.add(2);
                    System.out.println("ADD(2) - T: " + T.size());
                } else {
                    T.remove(randomNum);
                    System.out.println("REMOVE(" + randomNum + ") - T: " + T.size());
                    max = T.size();
                }
            } catch (Exception e) {
                System.out.println("***********Errinho bobo: " + e.getLocalizedMessage());
            }
        }

        return melhor;
    }

    public static SolucaoNSL buscaLocal(SolucaoNSL solution, int neighborhoodStructure) throws Exception {
        SolucaoNSL solutionMutada = null;
        switch (neighborhoodStructure) {
            case 0:
                solutionMutada = solution.bitFlipNewSolution(10);
                break;
            case 1:
                solutionMutada = solution.IWSSNewSolution();
                break;
            case 2:
                solutionMutada = solution.IWSSrNewSolution();
                break;
        };
        if (solutionMutada.isReallyBest(solution)) {
            return solutionMutada;
        } else {
            return solution;
        }

    }
}
