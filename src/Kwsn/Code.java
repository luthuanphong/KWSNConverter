package Kwsn;

/**
 * Created by fredlu on 19/07/2017.
 */
public class Code {

    public static String CreateSensorProcessingCode(
            Variable buffer, Variable queue, Variable processingRate){
        StringBuilder code = new StringBuilder();
        code.append("if(").append("0").append("<").append(buffer.getName()).append("-").append("1/").append(processingRate.getName()).append(")");
        code.append("{");
        code.append(System.lineSeparator());
        code.append(buffer.getName()).append("=").append(buffer.getName()).append("-").append("1/").append(processingRate.getName()).append(";");
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
        code.append(queue.getName()).append("=").append(queue.getName()).append("+").append("1/").append(processingRate.getName()).append(";");
        code.append(System.lineSeparator());
        code.append("if(").append(queue.getName()).append(" > ").append(Constants.SENSOR_MAX_QUEUE_SIZE).append(")").append(System.lineSeparator());
        code.append("{").append(System.lineSeparator()).append(Constants.CONGESTION).append(" = ").append("true");
        code.append(System.lineSeparator());
        code.append("}");
        return code.toString();
    }

    public static String CreateSensorToChanelSensorPart(
            Variable Queue,Variable sendingRate){
        StringBuilder code = new StringBuilder();
        code.append(System.lineSeparator());
        code.append(Queue.getName()).append("=").append(Queue.getName()).append("-").append("1/").append(sendingRate.getName()).append(";");
        code.append(System.lineSeparator());
        return code.toString();
    }

    public static String CreateSensorToChanelChanelPart(Variable Buffer,Variable sendingRate){
                StringBuilder code = new StringBuilder();
                code.append(System.lineSeparator());
                code.append(Buffer.getName())
                        .append("=")
                        .append(Buffer.getName()).append("+").append("1/").append(sendingRate.getName()).append(";");
                code.append(System.lineSeparator());
                code.append("if(").append(Buffer.getName()).append(" > ").append(Constants.CHANEL_MAX_BUFFER_SIZE).append(")").append(System.lineSeparator());
                code.append("{").append(System.lineSeparator());
                code.append(Constants.CONGESTION).append(" = ").append("true");
                code.append(System.lineSeparator());
                code.append("}");
                return code.toString();
    }

    public static String CreateChaneltoSensorChanelPart(Variable buffer,Variable tranmition){
        StringBuilder code = new StringBuilder();
        code.append("if(").append("0").append("<").append(buffer.getName()).append("-").append("1/").append(tranmition.getName()).append(")");
        code.append("{");
        code.append(System.lineSeparator());
        code.append(buffer.getName()).append("=").append(buffer.getName()).append("-").append("1/").append(tranmition.getName()).append(";");
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

    public static String CreateChanelToSensorSensorPart(Variable buffer,Variable tranmition){
        StringBuilder code = new StringBuilder();
        code.append(System.lineSeparator());
        code.append(buffer.getName())
                .append("=").append(buffer.getName()).append("+").append("1/").append(tranmition.getName()).append(";");
        code.append(System.lineSeparator());
        code.append("if(").append(buffer.getName()).append(" > ").append(Constants.SENSOR_MAX_BUFFER_SZIE).append(")");
        code.append("{").append(System.lineSeparator());
        code.append(Constants.CONGESTION).append(" = ").append("true");
        code.append("}");
        return code.toString();
    }
}
