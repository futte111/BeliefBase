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
    }

    public void addSentence (SentenceInterface sentence) {
        beliefBase.add(sentence);

    }

    public void printTruthTable() {

      /*  for (int i = 0; i < Math.pow(2, variables.size()); i++) {
            sentenceValues.add(new ArrayList<>());

            for (int j = 0; j < beliefBase.size(); j++) {
                sentenceValues.get(i).add(true);
            }
        } */
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

        for (int i = 0; i < sentenceValues.size(); i++) {

            for (int j = 0; j < variables.size(); j++) {
                if (variableValues.get(j).get(i)) {
                    System.out.print("1 | ");
                }
                else {
                    System.out.print("0 | ");
                }
            }
            for (int q = 0; q < variables.size(); q++) {

            }

            for (int j = 0; j < sentenceValues.get(i).size(); j++) {

                for (int k = 0; k < (headerSize - 1) - (headerSize - 1) / 2 ; k++) {
                    System.out.print(" ");

                }
                if (sentenceValues.get(i).get(j)) {

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

        for (int i = 0; i < Math.pow(2, variables.size()); i++) {
            sentenceValues.add(new ArrayList<>());

            for (int j = 0; j < beliefBase.size(); j++) {

                sentenceValues.get(i).add(generateSentenceValue(beliefBase.get(j), i));
            }
        }
    }

    public boolean generateSentenceValue(SentenceInterface si, int variableRow) {

        if (si instanceof Variable) {
            for (int i = 0; i < variables.size(); i++) {
                if (variables.get(i).getName().equals(((Variable) si).getName())) {
                    return variableValues.get(i).get(variableRow);
                }
            }
        }

        else {

            switch (((Expression)si).getConnective()) {
                case AND:
                    if (generateSentenceValue(((Expression) si).getSentence1(), variableRow) &&
                        generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        return true;
                    }
                    else {
                        return false;
                    }

                case OR:
                    if (generateSentenceValue(((Expression) si).getSentence1(), variableRow) ||
                        generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        return true;
                    }
                    else {
                        return false;
                    }

                case IMPLICATION:
                    if (!generateSentenceValue(((Expression) si).getSentence1(), variableRow) ||
                         generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        return true;
                    }
                    else {
                        return false;
                    }

                case BIIMPLICATION:
                    if (generateSentenceValue(((Expression) si).getSentence1(), variableRow) ==
                        generateSentenceValue(((Expression) si).getSentence2(), variableRow)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                    default:
                        break;
            }
        }
        return false;
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



    public ArrayList<Variable> getVariables() {
        return variables;
    }
    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }
    public ArrayList<SentenceInterface> getBeliefBase() {
        return beliefBase;
    }
    public void setBeliefBase(ArrayList<SentenceInterface> beliefBase) {
        this.beliefBase = beliefBase;
    }
}