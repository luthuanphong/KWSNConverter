package Kwsn;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by FredLu on 08/07/2017.
 */
public class Declaration {

    private String declareVariable;

    public Declaration(String declareVariable){
        this.declareVariable = declareVariable;
    }

    /**
     * convert variable declared in kwsn to pnml
     * @return string data
     */
    public String convertToPnmlVariableDeclaration(boolean isMinimize){
        List<String> variables = convert(isMinimize);
        StringBuilder returnData = new StringBuilder();
        for(String variable : variables){
            returnData.append(variable);
            returnData.append(";");
            returnData.append(System.lineSeparator());
        }
        return returnData.toString();
    }

    private List<String> convert(boolean isMinimizeMode){
        String[] splitData = declareVariable.split(";");
        List<String> variables = new LinkedList<>(Arrays.asList(splitData));
        for( int i = 0 ; i < variables.size() ; i++){
            String variable = variables.get(i);
            if(variable.contains("var")){
                String variableData[] =variable.split("=");
                try{
                    Integer.parseInt(variableData[1].trim());
                    if(isMinimizeMode){
                        variableData[1] = "1";
                        variable = variableData[0] +"="+ variableData[1];
                    }
                    variables.set(i,variable.replace("var","int"));
                }catch (Exception ex){
                    if(ex instanceof NumberFormatException){
                        if(variableData[1].contains("\"")){
                            variables.set(i,variable.replace("var","String"));
                        }else if(variableData[1].contains("true")||variableData[1].contains("false")){
                            variables.set(i,variable.replace("var","bool"));
                        }
                    }
                }
            }else{
                variables.remove(i);
            }
        }
        return variables;
    }
}
