package Kwsn;

import java.util.HashMap;

/**
 * Created by fredlu on 19/07/2017.
 */
public class Code {

    public static String CreateSensorProcessingCode(
            Variable buffer, Variable queue, Variable MaxprocessingRate , Variable MinProcessingRate , Variable energy , HashMap<String,String> energyRule){
        StringBuilder code = new StringBuilder();
        code.append("float ").append("temp").append("=").append(buffer.getName()).append(" - ").append("1/").append("randomInt").append("(").append(MinProcessingRate.getName()).append(",").append(MaxprocessingRate.getName()).append(")").append(";");
        code.append("if(").append("0").append("<").append("temp").append(")");
        code.append("{");
        code.append(System.lineSeparator());
        code.append(buffer.getName()).append("=").append("temp");
        code.append(System.lineSeparator());
        code.append("}");
        code.append(System.lineSeparator());
        code.append("else");
        code.append(System.lineSeparator());
        code.append("{");
        code.append(System.lineSeparator());
        code.append(buffer.getName()).append("=").append("0").append(";");
        code.append(System.lineSeparator());
        code.append("}");
        code.append(System.lineSeparator());
        code.append(queue.getName()).append("=").append(queue.getName()).append("+").append("1/").append(MaxprocessingRate.getName()).append(";");
        code.append(System.lineSeparator());
        code.append("if(").append(queue.getName()).append(" > ").append(Constants.SENSOR_MAX_QUEUE_SIZE).append(")").append(System.lineSeparator());
        code.append("{").append(System.lineSeparator()).append(Constants.CONGESTION).append(" = ").append("true");
        code.append(System.lineSeparator());
        code.append("}");
        code.append(System.lineSeparator());
        code.append(energy.getName()).append(" = ").append(energy.getName()).append(" - ").append(energyRule.getOrDefault("process","1")).append(";");
        return code.toString();
    }

    public static String CreateSensorToChanelSensorPart(
            Variable Queue, Variable MaxsendingRate, Variable MinsendingRate, Variable energy, HashMap<String,String> energyRule){
        StringBuilder code = new StringBuilder();
        code.append(System.lineSeparator());
        code.append(Queue.getName()).append("=").append(Queue.getName()).append("-").append("1/").append("randomInt").append("(").append(MinsendingRate.getName()).append(",").append(MaxsendingRate.getName()).append(")").append(";");
        code.append(System.lineSeparator());
        code.append(energy.getName()).append(" = ").append(energy.getName()).append(" - ").append(energyRule.getOrDefault("send","1")).append(";");
        return code.toString();
    }

    public static String CreateSensorToChanelChanelPart(Variable Buffer,Variable MaxsendingRate, Variable MinsendingRate){
                StringBuilder code = new StringBuilder();
                code.append(System.lineSeparator());
                code.append(Buffer.getName())
                        .append("=")
                        .append(Buffer.getName()).append("+").append("1/").append("randomInt(").append(MinsendingRate.getName()).append(",").append(MaxsendingRate.getName()).append(")").append(";");
                code.append(System.lineSeparator());
                code.append("if(").append(Buffer.getName()).append(" > ").append(Constants.CHANEL_MAX_BUFFER_SIZE).append(")").append(System.lineSeparator());
                code.append("{").append(System.lineSeparator());
                code.append(Constants.CONGESTION).append(" = ").append("true");
                code.append(System.lineSeparator());
                code.append("}");
                return code.toString();
    }

    public static String CreateChaneltoSensorChanelPart(Variable buffer,Variable MaxTranmitionRate, Variable MinTramitionRate){
        StringBuilder code = new StringBuilder();
        code.append("float ").append("temp").append(" = ").append(buffer.getName()).append("- 1/").append("randomInt(").append(MinTramitionRate.getName()).append(",").append(MaxTranmitionRate.getName()).append(")").append(";");
        code.append("if(").append("0").append("<").append("temp").append(")");
        code.append("{");
        code.append(System.lineSeparator());
        code.append(buffer.getName()).append(" = ").append("temp").append(";");
        code.append(System.lineSeparator());
        code.append("}");
        code.append(System.lineSeparator());
        code.append("else");
        code.append(System.lineSeparator());
        code.append("{");
        code.append(System.lineSeparator());
        code.append(buffer.getName()).append("=").append("0").append(";");
        code.append(System.lineSeparator());
        code.append("}");
        code.append(System.lineSeparator());
        return code.toString();
    }

    public static String CreateChanelToSensorSensorPart(Variable buffer,Variable MaxTranmitionRate, Variable MinTranmintionRate){
        StringBuilder code = new StringBuilder();
        code.append(System.lineSeparator());
        code.append(buffer.getName())
                .append("=").append(buffer.getName()).append("+").append("1/").append("randomInt(").append(MinTranmintionRate.getName()).append(",").append(MaxTranmitionRate.getName()).append(");");
        code.append(System.lineSeparator());
        code.append("if(").append(buffer.getName()).append(" > ").append(Constants.SENSOR_MAX_BUFFER_SZIE).append(")");
        code.append("{").append(System.lineSeparator());
        code.append(Constants.CONGESTION).append(" = ").append("true");
        code.append("}");
        return code.toString();
    }
}
