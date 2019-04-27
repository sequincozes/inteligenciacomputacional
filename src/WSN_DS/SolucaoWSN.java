/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WSN_DS;

import inteligenciacomputacional.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sequi
 */
public class SolucaoWSN {

    public final ArrayList<Integer> featuresSelecionadas;
    public final ArrayList<Integer> featuresRCL;
    private double acuracia = 0;
    private double taxa_detecao = 0;
    private double taxa_falsos_positivos = 0;
    public boolean VERBOSE = false;

    public static void main(String[] args) {
        ArrayList<Integer> featuresSelecionadas = new ArrayList<>();
        featuresSelecionadas.add(4);
        featuresSelecionadas.add(5);
        featuresSelecionadas.add(6);
        featuresSelecionadas.add(7);
        featuresSelecionadas.add(8);

        ArrayList<Integer> featuresRCL = new ArrayList<>();
        featuresRCL.add(1);
        featuresRCL.add(2);
        featuresRCL.add(3);
        featuresRCL.add(9);
        featuresRCL.add(10);
        featuresRCL.add(11);
        featuresRCL.add(12);
        featuresRCL.add(13);
        featuresRCL.add(14);
        featuresRCL.add(15);
        featuresRCL.add(16);
        featuresRCL.add(17);
        featuresRCL.add(18);
        featuresRCL.add(19);

        System.out.print("-- Semente: ");
        SolucaoWSN s = new SolucaoWSN(featuresSelecionadas, featuresRCL);
        SolucaoWSN s1 = new SolucaoWSN(featuresSelecionadas, featuresRCL);
        System.out.println("Acurácia: " + s.getAcuracia());

        long tempoi = System.currentTimeMillis();
        s = s.IWSSNewSolution();
        long tempo1 = System.currentTimeMillis();
        s1 = s1.IWSSrNewSolution();
        System.out.println("Acurácia: " + s.getAcuracia() + " | tempo: " + (tempo1 - tempoi));
        System.out.println("Acurácia: " + s1.getAcuracia() + " | tempo: " + (System.currentTimeMillis() - tempo1));

    }

    public SolucaoWSN IWSSrNewSolution() {
        int size = this.featuresSelecionadas.size();

        /* União de features na RCL */
        ArrayList<Integer> rclCompleta = new ArrayList<>();
        rclCompleta.addAll(featuresSelecionadas);
        rclCompleta.addAll(featuresRCL);

        ArrayList<Integer> novaSelecao = new ArrayList<>();

        /* Listas Auxiliares */
        ArrayList<Integer> selecaoExclusao;
        ArrayList<Integer> rclExclusao;

        // Construindo feature a feature
        int iterationCont = 0;
        SolucaoWSN melhorUnExc;
        SolucaoWSN melhorGeral = null;

        do {
            iterationCont = iterationCont + 1;

            /* União */
            System.out.print("-- União: ");
            novaSelecao.add(rclCompleta.remove(0));
            SolucaoWSN novaSolucao = new SolucaoWSN(novaSelecao, rclCompleta);

            /* Tentativa de Exclusão */
//            boolean uniaoIsBest = true;
            melhorUnExc = novaSolucao.newClone(); // baseline

            for (int i = 0; i < novaSelecao.size(); i++) {
                // Parte da União Completa, para cada exclusão (reset)
                selecaoExclusao = new ArrayList<>(novaSelecao);
                rclExclusao = new ArrayList<>(rclCompleta);

                // Executa Exclusão (se size > 1)
                if (selecaoExclusao.size() > 1) {
                    int f_exc = selecaoExclusao.remove(i);
                    rclExclusao.add(f_exc);
//                    System.out.print("---> Exlusão (" + f_exc + "): ");
                    SolucaoWSN exclusao = new SolucaoWSN(selecaoExclusao, rclExclusao);
                    if (exclusao.isReallyBest(melhorUnExc)) {
                        melhorUnExc = exclusao.newClone();
//                        System.out.println("---[" + f_exc + "]---- [NOVA] Melhor Exclusão: " + "[" + melhorUnExc.getFeaturesSelecionadas().size() + "] = {" + melhorUnExc + "}" + melhorUnExc.getAcuracia());
                    }
                } else {
                    melhorGeral = melhorUnExc.newClone();
                }
            }
            if (VERBOSE) {
                System.out.println("Melhor Exclusão: {" + melhorUnExc.getFeaturesSelecionadas() + "}" + melhorUnExc.getAcuracia());
            }
            // Se nenhuma exclusão superou as exclusões anteriores, restaura para a melhor histórica
            if (!melhorUnExc.isBest(melhorGeral)) {
                melhorUnExc = melhorGeral.newClone();
                if (VERBOSE) {
                    System.out.println("Restaurou");
                }
            } else {
                novaSelecao = melhorUnExc.getFeaturesSelecionadas();
                rclCompleta = melhorUnExc.getFeaturesBitFlip(); // RCL
            }
        } while ((melhorUnExc.getFeaturesSelecionadas().size() < size) && (iterationCont < (rclCompleta.size() + 1)));
        if (VERBOSE) {
            System.out.println("Melhor geral: " + melhorGeral.getFeaturesSelecionadas() + "|" + melhorGeral.getAcuracia());
        }
        while (melhorUnExc.getFeaturesSelecionadas().size() < size) {
            melhorUnExc.addFeature(melhorUnExc.featuresRCL.remove(0));
            if (VERBOSE) {
                System.out.println("Melhor complementada: " + melhorGeral.getFeaturesSelecionadas() + "|" + melhorGeral.getAcuracia());
            }
        }
        if (melhorUnExc.isBest(this)) {
            return melhorUnExc;
        }
        if (VERBOSE) {
            System.out.println("Sem melhorias com IWSS.");
        }
        return this;
    }

    public SolucaoWSN IWSSNewSolution() {
        int size = this.featuresSelecionadas.size();
        /* União de features na RCL */
        ArrayList<Integer> rclCompleta = new ArrayList<>();
        rclCompleta.addAll(featuresSelecionadas);
        rclCompleta.addAll(featuresRCL);

        ArrayList<Integer> novaSelecao = new ArrayList<>();

        // Construindo feature a feature
        int iterationCont = 0;
        SolucaoWSN melhorGeral = null;

        do {
            iterationCont = iterationCont + 1;
            /* União */
            if (VERBOSE) {
                System.out.print("-- União: ");
            }
            if (novaSelecao.size() < 1) {
                novaSelecao.add(rclCompleta.remove(0)); // Raiz
                melhorGeral = new SolucaoWSN(novaSelecao, rclCompleta);
            } else {
                novaSelecao.add(rclCompleta.remove(0)); // Raiz
                SolucaoWSN tentativaUniao = new SolucaoWSN(novaSelecao, rclCompleta);
                if (tentativaUniao.isBest(melhorGeral)) {
                    melhorGeral = tentativaUniao.newClone();
                } else {
                    rclCompleta.add(novaSelecao.remove(novaSelecao.size() - 1)); // Devolve para o final da RCL
                }
            }

        } while ((melhorGeral.getFeaturesSelecionadas().size() < size) && (iterationCont < (rclCompleta.size() + 1)));
        if (VERBOSE) {
            System.out.println("Melhor geral: " + melhorGeral.getFeaturesSelecionadas() + "|" + melhorGeral.getAcuracia());
        }
        if (melhorGeral.isBest(this)) {
            return melhorGeral;
        }
        if (VERBOSE) {
            System.out.println("Sem melhorias com IWSS.");
        }
        return this;
    }

    
    public SolucaoWSN bitFlipNewSolution(int maxSemMelhoria) {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> getBitFlipSolution = new ArrayList<>(featuresRCL);
        SolucaoWSN bestLocal = this;
        int itSemMelhoria = 0;
        while (itSemMelhoria <= maxSemMelhoria) {
            Random r = new Random();
            // Movendo feature para RCL
            int saiPos = r.nextInt(featuresSelecionadasAux.size() - 1);
            int saiu = featuresSelecionadasAux.remove(saiPos);

            getBitFlipSolution.add(saiu);

            // Movendo feature para Seleção
            int entraPos = r.nextInt(getBitFlipSolution.size() - 1);
            int entrou = getBitFlipSolution.remove(entraPos);

            featuresSelecionadasAux.add(entrou);
            SolucaoWSN newer = new SolucaoWSN(featuresSelecionadasAux, getBitFlipSolution);
            if (newer.isReallyBest(bestLocal)) {
                maxSemMelhoria = 0;
                bestLocal = newer.newClone();
            } else {
                itSemMelhoria = itSemMelhoria + 1;
            }
        }
        return bestLocal;
    }
    
    public SolucaoWSN(ArrayList<Integer> featuresSelecionadas, ArrayList<Integer> featuresRCL) {
        this.featuresSelecionadas = featuresSelecionadas;
        this.featuresRCL = featuresRCL;
        try {
            Resultado res = ValidacaoWSN.executar(getArrayFeaturesSelecionadas());
            this.acuracia = res.getAcuracia();
            this.taxa_detecao = res.getTaxaDeteccao();
            this.taxa_falsos_positivos = res.getTaxaAlarmeFalsos();

        } catch (Exception ex) {
            Logger.getLogger(SolucaoWSN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SolucaoWSN() {
        this.featuresSelecionadas = new ArrayList<>();
        this.featuresRCL = new ArrayList<>();
    }

    private SolucaoWSN(ArrayList<Integer> featuresSelecionadasAux, ArrayList<Integer> featuresRCL, double acuracia, double taxaDR, double taxaFR) {
        this.featuresSelecionadas = featuresSelecionadasAux;
        this.featuresRCL = featuresRCL;
        this.acuracia = acuracia;
        this.taxa_detecao = taxaDR;
        this.taxa_falsos_positivos = taxaFR;
    }

    public double getAcuracia() {
        return acuracia;
    }

    public void setAcuracia(double acuracia) {
        this.acuracia = acuracia;
    }

    public ArrayList<Integer> getFeaturesSelecionadas() {
        return featuresSelecionadas;
    }

    public int[] getArrayFeaturesSelecionadas() {
        int[] arrayFS = new int[featuresSelecionadas.size()];
        for (int i = 0; i < arrayFS.length; i++) {
            arrayFS[i] = featuresSelecionadas.get(i);
        }
        return arrayFS;
    }

    public ArrayList<Integer> getFeaturesBitFlip() {
        return featuresRCL;
    }

    public boolean isReallyBest(SolucaoWSN solucao) {
        return this.getAcuracia() > solucao.getAcuracia();
    }

    public boolean isBest(SolucaoWSN solucao) {
        return this.getAcuracia() >= solucao.getAcuracia();
    }

    public void printSelection(String selecaoName) {
        System.out.print("Seleção [" + selecaoName + "]: {");
        boolean printComma = false;
        for (int feature : featuresSelecionadas) {
            if (!printComma) {
                printComma = true;
                System.out.print(feature);
            } else {
                System.out.print(",");
                System.out.print(feature);
            }
        }
        System.out.print("} - ");
    }

    public SolucaoWSN reconstruirNewSolucao(int num_features) {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> featuresRCLAux = new ArrayList<>(featuresRCL);;

        Random r = new Random();
        while (featuresSelecionadasAux.size() > 0) {
            int toRemove = featuresSelecionadasAux.remove(0);
            featuresRCLAux.add(toRemove); //devolve a RCL
        }

        while (featuresSelecionadasAux.size() < num_features) {
            int sorteio = r.nextInt(featuresRCLAux.size());
            int featureSorteada = featuresRCLAux.remove(sorteio);
//            System.out.println("Sorteio[" + sorteio + "]: " + featureSorteada);
            if (!featuresSelecionadasAux.contains(featureSorteada)) {
//                System.out.println("A feature " + featureSorteada + " não está em " + s.featuresSelecionadas);
                featuresSelecionadasAux.add(featureSorteada);
            } else {
//                System.out.println("A feature " + featureSorteada + " JÁ está em " + s.featuresSelecionadas);
            }
        }
        SolucaoWSN newer = new SolucaoWSN(featuresSelecionadasAux, featuresRCLAux);
        return newer;
    }

    public SolucaoWSN newClone() {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> getBitFlipSolution = new ArrayList<>(featuresRCL);
        double acuracia = getAcuracia();
        double taxaDR = getTaxa_detecao();
        double taxaFR = getTaxa_falsos_positivos();
        return new SolucaoWSN(featuresSelecionadasAux, getBitFlipSolution, acuracia, taxaDR, taxaFR);
    }

    

    public void addFeature(Integer fature) {
        featuresSelecionadas.add(fature);
    }

    public void addFeatureFlip(Integer fature) {
        featuresRCL.add(fature);
    }

    public void printRCL() {
        System.out.print("RCL: {");
        boolean printComma = false;
        for (int feature : featuresRCL) {
            if (!printComma) {
                printComma = true;
                System.out.print(feature);
            } else {
                System.out.print(",");
                System.out.print(feature);
            }
        }
        System.out.print("} - ");
    }

    public double getTaxa_detecao() {
        return taxa_detecao;
    }

    public void setTaxa_detecao(double taxa_detecao) {
        this.taxa_detecao = taxa_detecao;
    }

    public double getTaxa_falsos_positivos() {
        return taxa_falsos_positivos;
    }

    public void setTaxa_falsos_positivos(double taxa_falsos_positivos) {
        this.taxa_falsos_positivos = taxa_falsos_positivos;
    }

}
