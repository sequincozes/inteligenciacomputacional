/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WSN_DS;

import inteligenciacomputacional.SolucaoNSL;
import static java.lang.Integer.max;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author silvio
 */
public class WSNVND {

    public static SolucaoWSN doVND(SolucaoWSN semente) throws Exception {
        SolucaoWSN melhor = semente.newClone();
        for (int i = 0; i < 3; i++) {
            System.out.println("-> BEGIN| Estrutura de Vizinhança: " + i);
            long tempoInicial = System.currentTimeMillis();
            SolucaoWSN nova = buscaLocal(semente, i);
            long tempoFinal = System.currentTimeMillis() - tempoInicial;
            System.out.println("-> END| Estrutura de Vizinhança: " + i + "["+tempoFinal+"]");
            if (nova.isBest(melhor)) {
                melhor = nova.newClone();
            }
        }
        return melhor;
    }

    public static SolucaoWSN doRVND(SolucaoWSN semente) throws Exception {
        int min = 0;
        int max = 2;

        // Inicializar T
        ArrayList<Integer> T = new ArrayList<>();
        T.add(0);
        T.add(1);
        T.add(2);
        SolucaoWSN melhor = semente.newClone();

        while (T.size() > 0) {
            int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
            SolucaoWSN nova = buscaLocal(semente, randomNum);
            if (nova.isBest(melhor)) {
                melhor = nova.newClone();
                T = new ArrayList<>();
                T.add(0);
                T.add(1);
                T.add(2);
            } else {
                T.remove(randomNum);
            }
        }

        return melhor;
    }

    public static SolucaoWSN buscaLocal(SolucaoWSN solution, int neighborhoodStructure) throws Exception {
        SolucaoWSN solutionMutada = null;
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
