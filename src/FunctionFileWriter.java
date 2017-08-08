import Kwsn.BasicType;
import Kwsn.Declaration;
import Kwsn.Program;
import Kwsn.Variable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by FredLu on 08/07/2017.
 */
public class FunctionFileWriter {
    public static void Write(String filePath , ArrayList<Program> programs, ArrayList<Variable> variables, boolean isMinimizeMode){
        try {
            FileWriter writer = new FileWriter(filePath);
            if(isMinimizeMode){
                for(Variable variable : variables){
                    if(variable.getValue().equals("0")) {
                        writer.write(variable.toString());
                    }else{
                        if(variable.getType() == BasicType.FLOAT || variable.getType() == BasicType.INT) {
                            writer.write(variable.setValue("1").toString());
                        }else {
                            writer.write(variable.toString());
                        }
                    }
                    writer.write(System.lineSeparator());
                }
            }else{
                for(Variable variable : variables){
                    writer.write(variable.toString());
                    writer.write(System.lineSeparator());
                }
            }
            for(Program program : programs){
                writer.write(program.toString());
                writer.write(System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
