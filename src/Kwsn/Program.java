package Kwsn;

/**
 * Created by FredLu on 02/07/2017.
 */
public class Program {
    /**
     * id of transition
     */
    public String id;
    /**
     * code of transition
     */
    public String Code;

    public Program(String id, String code){
        this.id = id;
        this.Code = code;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id);
        stringBuilder.append("{");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(Code);
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
