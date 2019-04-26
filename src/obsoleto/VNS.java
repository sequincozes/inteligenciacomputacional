/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obsoleto;

import inteligenciacomputacional.SolucaoCICIDS2017_deprecated;

/**
 *
 * @author sequi
 */
public class VNS {

        public SolucaoCICIDS2017_deprecated run(SolucaoCICIDS2017_deprecated x, int kmax, long tmax) {
            long t = System.currentTimeMillis();
            do {
                int k = 1;
                do {
                    x = shake(); //' ← shake(x, k) /* Shaking */;
                    SolucaoCICIDS2017_deprecated x_new = bestImprovement(); // bestImprovement(x' );
                    x = null;//neighbourhoodChange(x, x'', k) /* Change neighbourhood */;
                } while (k < kmax);
            } while (t < tmax);
            return null;
        }

        /* Local search */
        private SolucaoCICIDS2017_deprecated bestImprovement() {
            /**
             * @TODO
             */
            return null;
        }

        /* Local search */
        private SolucaoCICIDS2017_deprecated shake() {
            /**
             * @TODO
             */
            return null;
        }
    }