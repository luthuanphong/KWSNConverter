import Kwsn.Declaration;
import Kwsn.Program;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by FredLu on 08/07/2017.
 */
public class FunctionFileWriter {
    public static void Write(String filePath ,ArrayList<Program> programs,Declaration declaration,boolean isMinimizeMode){
        try {
            FileWriter writer = new FileWriter(filePath);
            if(declaration != null){
                writer.write(declaration.convertToPnmlVariableDeclaration(isMinimizeMode));
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
