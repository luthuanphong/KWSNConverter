package Kwsn;

/**
 * Created by fredlu on 16/07/2017.
 */
public class Variable {

    /**
     * Datatype that used write in txrt file
     */
    private static final String STRING_TYPE = "string";
    /**
     * Datatype that used write in txrt file
     */
    private static final String INT_TYPE = "int";
    /**
     * Datatype that used write in txrt file
     */
    private static final String BOOL_TYPE = "bool";

    /**
     * Type of variable
     */
    private BasicType Type;

    /**
     * Name of variable
     */
    private String Name;

    /**
     * Value of varible
     */
    private String Value;

    /**
     * Set type to variable
     * @param type
     */
    public Variable setType(BasicType type) {
        Type = type;
        return this;
    }

    /**
     * Set name of variable
     * @param name
     */
    public Variable setName(String name) {
        Name = name;
        return this;
    }

    /**
     * Set value to variable
     * @param value
     */
    public Variable setValue(String value) {
        Value = value;
        return this;
    }

    public String getValue(){
        return this.Value;
    }

    public String getName(){
        return this.Name;
    }

    public Variable(BasicType Type,String Name , String Value){
        this.Type = Type;
        this.Name = Name;
        this.Value = Value;
    }

    public String toString(){
        StringBuilder variableString = new StringBuilder();
        switch (this.Type){
            case INT:
                variableString.append(Variable.INT_TYPE);
                break;
            case BOOL:
                variableString.append(Variable.BOOL_TYPE);
                break;
            case STRING:
                variableString.append(Variable.STRING_TYPE);
                break;
        }
        variableString.append(" ");
        variableString.append(this.Name);
        variableString.append(" = ");
        variableString.append(this.Value);
        variableString.append(";");
        return variableString.toString();
    }
}
