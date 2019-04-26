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
public class VND {

    public static SolucaoNSL doVND(SolucaoNSL semente) {
        SolucaoNSL melhor = semente.newClone();
        for (int i = 0; i < 3; i++) {
            SolucaoNSL nova = semente.newClone(); // EstruturaDeVizinhanca(i)
            if (nova.isBest(melhor)) {
                melhor = nova.newClone();
            }
        }
        return melhor;
    }

    public static SolucaoNSL doRVND(SolucaoNSL semente) {
        int min = 0;
        int max = 2;

        // Inicializar T
        ArrayList<Integer> T = new ArrayList<>();
        T.add(0);
        T.add(1);
        T.add(2);
        SolucaoNSL melhor = semente.newClone();

        while (T.size() > 0) {
            int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
            SolucaoNSL nova = semente.newClone(); // EstruturaDeVizinhanca(randomNum)
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
}
