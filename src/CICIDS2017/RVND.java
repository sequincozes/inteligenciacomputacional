/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CICIDS2017;

import static inteligenciacomputacional.Apuracao.readDataFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.LinearForwardSelection;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;

/**
 *
 * @author sequi
 *
 */
public class RVND {

    private static final String DATASET = "Datadet WSN";
    private static final String ATAQUE = "binario";
    private static final String DIRETORIO = "C:\\Users\\sequi\\Google Drive\\2019\\Datasets\\" + DATASET + "\\" + ATAQUE + "\\";
    private static final String TRAIN_FILE = DIRETORIO + "binario_treino_1.csv";

    public static void main(String[] args) throws IOException, Exception {
        //        int solucao = 1;  
        //        weka.attributeSelection.WrapperSubsetEval x = new WrapperSubsetEval();
        //        x.setClassifier(newClassifier);
        //        System.out.println(x.getEvaluationMeasure());

        LinearForwardSelection search = new LinearForwardSelection();
        Classifier classifier = new NaiveBayes();
        int seed = 0;
        int fold = 19;
//        search.setNumUsedAttributes(19);
//        search
//        String resultado = execute(search, classifier, fold, seed, null);
//        System.out.println(resultado);
//        doRVND(solucao);
        Instances instances = new Instances(readDataFile(TRAIN_FILE));
        instances.setClassIndex(instances.numAttributes() - 1);
        classify(classifier, instances);

    }

    public static void classify(Classifier model, Instances trainingSet)
            throws Exception {

        String[] optionsW = new String[10];
        optionsW[0] = "-F";
        optionsW[1] = "5";
        optionsW[2] = "-T";
        optionsW[3] = "0.01";
        optionsW[4] = "-B";
        optionsW[5] = "weka.classifiers.bayes.NaiveBayes";
        optionsW[6] = "-R";
        optionsW[7] = "1";
        optionsW[8] = "-E";
        optionsW[9] = "acc";

        String[] optionsS = new String[9];
        optionsS[0] = "-D";
        optionsS[1] = "1";
        optionsS[2] = "-N";
        optionsS[3] = "5";
        optionsS[4] = "-I";
        optionsS[5] = "-K";
        optionsS[6] = "5";
        optionsS[7] = "-T";
        optionsS[8] = "1";

        AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
        WrapperSubsetEval eval = new WrapperSubsetEval();
        eval.setOptions(optionsW);

        LinearForwardSelection search = new LinearForwardSelection();
        search.setOptions(optionsS);
        search.setStartSet("1,2,3,4");
        classifier.setClassifier(model);
        classifier.setEvaluator(eval);
        classifier.setSearch(search);

        classifier.buildClassifier(trainingSet);

        //Print out the model that is created 
        System.out.println(classifier.toString());
//        System.out.println(eval.get);
    }

    public static void selectFeatures() throws Exception {
        /* Dados */
        Instances instances = new Instances(readDataFile(TRAIN_FILE));
        instances.setClassIndex(instances.numAttributes() - 1);

        /* Validação */
        CfsSubsetEval eval = new CfsSubsetEval();
        eval.buildEvaluator(instances);
        String[] opts = {"-P 1,2,3,4,5"};
        eval.setOptions(opts);
//        J48 j48 = new J48();
//        j48.buildClassifier(instances);

        /* Local Search Structures */
//        BestFirst search = new BestFirst();
        LinearForwardSelection c = new LinearForwardSelection();

        /* Parameters */
//        c.setNumUsedAttributes(5);

        /* Selection */
        AttributeSelection attSelection = new AttributeSelection();
        attSelection.setEvaluator(eval);
        attSelection.setSearch(c);

        try {

            int[] attrSet = {1, 2, 3, 4, 5, 7, 11, 12, 17, 18};
            attSelection.setFolds(1);
            String options[] = {"-E1"};
            attSelection.SelectAttributes(eval, options, instances);
            int[] attIndex = attSelection.selectedAttributes();
            System.out.println(Utils.arrayToString(attIndex));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static String execute(ASSearch search, Classifier classifier, int folds, int seed, Filter... filter) throws Exception {
        Instances instances;
        try {
            instances = new Instances(readDataFile(TRAIN_FILE));
            instances.setClassIndex(instances.numAttributes() - 1);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (filter != null) {
            for (int i = 0; i < filter.length; i++) {
                try {
                    filter[i].setInputFormat(instances);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    instances = Filter.useFilter(instances,
                            filter[i]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }
        AttributeSelection selection = new AttributeSelection();
        WrapperSubsetEval eval = new WrapperSubsetEval();
        eval.setClassifier(classifier);
        eval.setFolds(folds);

        selection.setEvaluator(eval);
        selection.setSearch(search);
        selection.setFolds(folds);
        selection.setSeed(seed);

        try {
            selection.SelectAttributes(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int[] attributes;
        try {
            attributes = selection.selectedAttributes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int featureSize = instances.numAttributes() - 1;

        int deletedFetures = 0;
        for (int i = 0; i < featureSize; i++) {
            boolean featureIsIncluded = false;
            for (int j = 0; j < attributes.length; j++) {
                if (i == attributes[j]) {
                    featureIsIncluded = true;
                    break;
                }
            }
            if (!featureIsIncluded) {
                instances.deleteAttributeAt(i - deletedFetures);
                deletedFetures++;
            }
        }
        instances.setClassIndex(instances.numAttributes() - 1);
        Evaluation evalualtion = null;
        try {
            evalualtion = new Evaluation(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            evalualtion.crossValidateModel(classifier, instances, folds, new Random(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Retorna o valor da classificação
        return Utils.arrayToString(attributes) + " " + evalualtion.pctCorrect();
    }

    public static int doRVND(int solution) {
        String[] ADS = new String[1];           //2 Update ADSs 
        int[] NL = new int[3];                  //3 Initialize the Inter-Route Neighborhood List (NL)
        while (NL.length > 0) {                 //4 while (NL <> 0) do
            Random r = new Random();
            int posN = r.nextInt(3);            //Choose a neighborhood N(η) 5 ∈ NL at random
            int N = NL[posN];
            int melhorSolucaoDeN = melhorSolucaoVizinha(N);            //Find the best neighbor s of s ∈ N(η) 6
            if (melhorSolucaoDeN > solution) { //if (f (s 7 ) < f (s)) then
                solution = melhorSolucaoDeN; //s ← s 8
                solution = buscaIntraRota(solution); //9 s ← IntraRouteSearch(s)         
                //10 Update Fleet // Only for FSM
                //11 Update NL
            } else {
                //Remove N(η) 13 from the NL

            }   //    14 end 
            ADS = new String[1];//15 Update ADSs
        }//16 end
        return solution; //17 return s
    } //18 end

    private static int melhorSolucaoVizinha(int N) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static int buscaIntraRota(int solution) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static int[] IWSS(int[] initial, int RCL) {
        int[] output = new int[5];
        
        return output;
    }

}
