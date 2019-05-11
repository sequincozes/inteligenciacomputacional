/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RedeDeConselhos;

import CICIDS2017.*;
import inteligenciacomputacional.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sequi
 */
public class Solucao_RC_CICIDS2017 {

    public final ArrayList<Integer> featuresSelecionadas;
    public final ArrayList<Integer> featuresRCL;
    private double acuracia = 0;
    private double taxa_detecao = 0;
    private double taxa_falsos_positivos = 0;
    public boolean VERBOSE = false;

    public Solucao_RC_CICIDS2017(ArrayList<Integer> featuresSelecionadas, ArrayList<Integer> featuresRCL) {
        this.featuresSelecionadas = featuresSelecionadas;
        this.featuresRCL = featuresRCL;
        try {
            Resultado res = Validacao_RC_CICIDS2017.executar(getArrayFeaturesSelecionadas());
            this.acuracia = res.getAcuracia();
            this.taxa_detecao = res.getTaxaDeteccao();
            this.taxa_falsos_positivos = res.getTaxaAlarmeFalsos();

        } catch (Exception ex) {
            Logger.getLogger(Solucao_RC_CICIDS2017.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Solucao_RC_CICIDS2017() {
        this.featuresSelecionadas = new ArrayList<>();
        this.featuresRCL = new ArrayList<>();
    }

    private Solucao_RC_CICIDS2017(ArrayList<Integer> featuresSelecionadasAux, ArrayList<Integer> featuresRCL, double acuracia, double taxaDR, double taxaFR) {
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

    public boolean isBest(Solucao_RC_CICIDS2017 solucao) {
        return this.getAcuracia() >= solucao.getAcuracia();
    }

    public boolean isReallyBest(Solucao_RC_CICIDS2017 solucao) {
        return this.getAcuracia() > solucao.getAcuracia();
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

    public Solucao_RC_CICIDS2017 reconstruirNewSolucao(int num_features) {
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
        Solucao_RC_CICIDS2017 newer = new Solucao_RC_CICIDS2017(featuresSelecionadasAux, featuresRCLAux);
        return newer;
    }

    public Solucao_RC_CICIDS2017 newClone() {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> getBitFlipSolution = new ArrayList<>(featuresRCL);
        double acuracia = getAcuracia();
        double taxaDR = getTaxa_detecao();
        double taxaFR = getTaxa_falsos_positivos();
        return new Solucao_RC_CICIDS2017(featuresSelecionadasAux, getBitFlipSolution, acuracia, taxaDR, taxaFR);
    }

    public Solucao_RC_CICIDS2017 bitFlipNewSolution() {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> getBitFlipSolution = new ArrayList<>(featuresRCL);

        Random r = new Random();
        // Movendo feature para RCL
        int saiPos = r.nextInt(featuresSelecionadasAux.size() - 1);
        int saiu = featuresSelecionadasAux.remove(saiPos);

        getBitFlipSolution.add(saiu);

        // Movendo feature para Seleção
        int entraPos = r.nextInt(getBitFlipSolution.size() - 1);
        int entrou = getBitFlipSolution.remove(entraPos);

        featuresSelecionadasAux.add(entrou);
        Solucao_RC_CICIDS2017 newer = new Solucao_RC_CICIDS2017(featuresSelecionadasAux, getBitFlipSolution);
        return newer;
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

    public Solucao_RC_CICIDS2017 IWSSrNewSolution() {
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
        Solucao_RC_CICIDS2017 melhorUnExc;
        Solucao_RC_CICIDS2017 melhorGeral = null;

        do {
            iterationCont = iterationCont + 1;

            /* União */
            System.out.print("-- União: ");
            novaSelecao.add(rclCompleta.remove(0));
            Solucao_RC_CICIDS2017 novaSolucao = new Solucao_RC_CICIDS2017(novaSelecao, rclCompleta);

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
                    Solucao_RC_CICIDS2017 exclusao = new Solucao_RC_CICIDS2017(selecaoExclusao, rclExclusao);
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

    public Solucao_RC_CICIDS2017 IWSSNewSolution() {
        int size = this.featuresSelecionadas.size();
        /* União de features na RCL */
        ArrayList<Integer> rclCompleta = new ArrayList<>();
        rclCompleta.addAll(featuresSelecionadas);
        rclCompleta.addAll(featuresRCL);

        ArrayList<Integer> novaSelecao = new ArrayList<>();

        // Construindo feature a feature
        int iterationCont = 0;
        Solucao_RC_CICIDS2017 melhorGeral = null;

        do {
            iterationCont = iterationCont + 1;
            /* União */
            if (VERBOSE) {
                System.out.print("-- União: ");
            }
            if (novaSelecao.size() < 1) {
                novaSelecao.add(rclCompleta.remove(0)); // Raiz
                melhorGeral = new Solucao_RC_CICIDS2017(novaSelecao, rclCompleta);
            } else {
                novaSelecao.add(rclCompleta.remove(0)); // Raiz
                Solucao_RC_CICIDS2017 tentativaUniao = new Solucao_RC_CICIDS2017(novaSelecao, rclCompleta);
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

    public Solucao_RC_CICIDS2017 bitFlipNewSolution(int maxSemMelhoria) {
        ArrayList<Integer> featuresSelecionadasAux = new ArrayList<>(featuresSelecionadas);
        ArrayList<Integer> getBitFlipSolution = new ArrayList<>(featuresRCL);
        Solucao_RC_CICIDS2017 bestLocal = this;
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
            Solucao_RC_CICIDS2017 newer = new Solucao_RC_CICIDS2017(featuresSelecionadasAux, getBitFlipSolution);
            if (newer.isReallyBest(bestLocal)) {
                maxSemMelhoria = 0;
                bestLocal = newer.newClone();
            } else {
                itSemMelhoria = itSemMelhoria + 1;
            }
        }
        return bestLocal;
    }

}
