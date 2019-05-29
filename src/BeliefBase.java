import java.util.ArrayList;

public class BeliefBase {

    private ArrayList<SentenceInterface> beliefBase = new ArrayList<>();
    private ArrayList<Variable> variables = new ArrayList<>();
    //private ArrayList<String> variableHeader = new ArrayList<>();
    private ArrayList<ArrayList<Boolean>> variableValues = new ArrayList<>();
    private ArrayList<ArrayList<Boolean>> sentenceValues = new ArrayList<>();
    private ArrayList<String> sentenceHeader = new ArrayList<>();

    public void printSentences () {
        System.out.println("The Belief Base consists of the following sentences:");
        for (SentenceInterface si: beliefBase) {
            System.out.println(si.toString());
        }
        generateTruthTable();
    }

    public void addSentence (SentenceInterface sentence) {

        beliefBase.add(sentence);
        generateTruthTable();
        checkEntailment();
        checkConsistency();
    }

    public void checkEntailment () {
        boolean entails = false;
        for (int i = 0; i < sentenceValues.size() - 1; i++) {
            entails = true;
            for (int j = 0; j < sentenceValues.get(i).size(); j++) {
                if (sentenceValues.get(i).get(j) && !sentenceValues.get(sentenceValues.size()-1).get(j)) {
                    entails = false;
                    break;
                }
            }
            if (entails) {
                break;
            }
        }
        if (entails){
            beliefBase.remove(beliefBase.size()-1);
        }else{
            entails = true;
            for(int i = sentenceValues.size() - 2; i >= 0; i--){
                for(int j = 0; j < sentenceValues.get(i).size(); j++){
                    if(!sentenceValues.get(i).get(j) && sentenceValues.get(sentenceValues.size()-1).get(j)){
                        entails = false;
                        break;
                    }
                }
                if(entails){
                    beliefBase.remove(beliefBase.get(i));
                }
            }
        }
    }

    public void checkConsistency () {
        int consistentRows = 0;
        for (int i = 0; i < sentenceValues.get(0).size(); i++) {
            boolean isRowConsistent = true;
            for (int j = 0; j < sentenceValues.size(); j++) {
                if(!sentenceValues.get(j).get(i)){
                    isRowConsistent = false;
                    break;
                }
            }
            if(isRowConsistent){
                consistentRows++;
            }
        }
        if(consistentRows < 1){
            //Belief base inconsistent!
            System.out.println("Belief base is inconsistent. Removing conflicting statements.");
            handleInconsistency();
        }else{
        }
    }

    public void handleInconsistency () {
        ArrayList<Inconsistency> inconList = new ArrayList<>();
        for (int j = 0; j < sentenceValues.get(0).size(); j++) {
            if (sentenceValues.get(sentenceValues.size() - 1).get(j)) {
                inconList.add(new Inconsistency(j));
                for (int i = 0; i < sentenceValues.size(); i++) {
                    if (!sentenceValues.get(i).get(j)) {
                        inconList.get(inconList.size() - 1).getZeroList().add(i);
                    }
                }
            }
        }
        int minInconsintensies = 1000000;
        for(int i = 0; i < inconList.size(); i++) {
            if(inconList.get(i).getZeroList().size() <= minInconsintensies){
                minInconsintensies = inconList.get(i).getZeroList().size();
            }
        }

        for(int i = 0; i < inconList.size(); i++) {
            if(inconList.get(i).getZeroList().size() > minInconsintensies){
                inconList.remove(i);
                i--;
            }
        }

        for (int i = 0; i < inconList.get(0).getZeroList().size(); i++) {
            int minIndex = 1000;

            for (int j = 0; j < inconList.size(); j++) {
                if (inconList.get(j).getZeroList().get(i) < minIndex) {
                    minIndex = inconList.get(j).getZeroList().get(i);
                }
            }
            for (int j = 0; j < inconList.size(); j++) {
                if (inconList.get(j).getZeroList().get(i) != minIndex) {
                    inconList.remove(j);
                    j--;
                }
            }
        }
        int bla = 0;
        for (int i = 0; i < inconList.get(0).getZeroList().size(); i++) {
            beliefBase.remove(inconList.get(0).getZeroList().get(i)-bla);
            bla++;
        }
        generateTruthTable();
    }

    public void removeSentence (String sentence) {
        for (int i = 0; i < beliefBase.size(); i++) {
            if (beliefBase.get(i).toString().equals(sentence)) {
                beliefBase.remove(i);
            }
        }
        generateTruthTable();
    }

    public void printTruthTable() {
        for (SentenceInterface si: beliefBase) {
            sentenceHeader.add(si.toString());
        }

        for (Variable s: variables) {
            System.out.print(s.getName() + " | ");
        }

        int headerSize = 0;
        for(String s: sentenceHeader){
            if(s.length() > headerSize){
                headerSize = s.length();
            }
        }
        for (String s: sentenceHeader) {
            int space = headerSize - s.length();
            for (int i = 0; i < space - (space / 2); i++) {
                System.out.print(" ");
            }
            System.out.print(s);
            for (int i = 0; i <  (space / 2); i++) {
                System.out.print(" ");
            }
            System.out.print("|");
        }
        System.out.println();

        for (int i = 0; i < Math.pow(2, variables.size()); i++) {

            for (int j = 0; j < variables.size(); j++) {
                if (variableValues.get(j).get(i)) {
                    System.out.print("1 | ");
                }
                else {
                    System.out.print("0 | ");
                }
            }
            for (int j = 0; j < sentenceValues.size(); j++) {

                for (int k = 0; k < (headerSize - 1) - (headerSize - 1) / 2 ; k++) {
                    System.out.print(" ");

                }
                if (sentenceValues.get(j).get(i)) {

                    System.out.print("1");
                }
                else {
                    System.out.print("0");
                }
                for (int h = 0; h <  (headerSize - 1) / 2 ; h++ ) {
                    System.out.print(" ");
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    public void generateTruthTable () {
        variables.clear();
        variableValues.clear();
        sentenceValues.clear();
        sentenceHeader.clear();

        for (SentenceInterface si: beliefBase) {
            generateVariables(si);
        }
        for (int j = 0; j < beliefBase.size(); j++) {
            sentenceValues.add(new ArrayList<>());

            for (int i = 0; i < Math.pow(2, variables.size()); i++) {

                sentenceValues.get(j).add(generateSentenceValue(beliefBase.get(j), i));
            }
        }
    }

    public boolean generateSentenceValue(SentenceInterface si, int variableRow) {
        boolean returnValue = false;
        if (si instanceof Variable) {
            for (int i = 0; i < variables.size(); i++) {
                if (variables.get(i).getName().equals(((Variable) si).getName())) {
                    returnValue = variableValues.get(i).get(variableRow);
                }
            }
        }

        else {

            switch (((Expression)si).getConnective()) {
                case AND:
                    if (generateSentenceValue(((Expression) si).getSentence1(), variableRow) &&
                        generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        returnValue = true;
                    }
                    else {
                        returnValue = false;
                    }
                    break;
                case OR:
                    if (generateSentenceValue(((Expression) si).getSentence1(), variableRow) ||
                        generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        returnValue = true;
                    }
                    else {
                        returnValue = false;
                    }
                    break;
                case IMPLICATION:
                    if (!generateSentenceValue(((Expression) si).getSentence1(), variableRow) ||
                         generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        returnValue = true;
                    }
                    else {
                        returnValue = false;
                    }
                    break;
                case BIIMPLICATION:
                    if (generateSentenceValue(((Expression) si).getSentence1(), variableRow) ==
                        generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        returnValue = true;
                    }
                    else {
                        returnValue = false;
                    }
                    break;
                    default:
                        break;
            }
        }
        if (si.isTrue()) {
            return returnValue;
        }
        else {
            return !returnValue;
        }
    }


    public void generateVariables (SentenceInterface sentence) {

        if (sentence instanceof Variable) {
            boolean contains = false;
            for (Variable v: variables) {
                if (v.getName().equals(((Variable) sentence).getName())) {
                    contains = true;
                    break;
                }
            }
            if (contains == false) {
                variables.add(new Variable(true, ((Variable) sentence).getName()));
            }
        }
        else if (sentence instanceof Expression) {
            generateVariables(((Expression) sentence).getSentence1());
            generateVariables(((Expression) sentence).getSentence2());
        }

        boolean countValue = false;

        for (int i = 0; i < variables.size(); i++) {
            int count = (int) Math.pow(2, i);
            variableValues.add(new ArrayList<>());
            for (int j = 0; j < Math.pow(2, variables.size()); j++) {

                variableValues.get(i).add(countValue);
                count--;
                if (count == 0){
                    countValue = !countValue;
                    count = (int) Math.pow(2, i);
                }
            }
        }
    }
}