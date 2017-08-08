package Kwsn; /**
 * Created by FredLu on 02/07/2017.
 */
import Pnml.*;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "Link")
public class Link{
    @XmlAttribute(name = "id")
    public String id;
    @XmlAttribute(name = "MaxSendingRate")
    public String MaxSendingRate;
    @XmlAttribute(name = "MinSendingRate")
    public String MinSendingRate;
    @XmlElement(name = "From")
    public String From;
    @XmlElement(name = "To")
    public String To;
    @XmlElement(name = "Program")
    public String Program;

    private Variable Buffer;
    private Variable MinSendingRateVar;
    private Variable MaxSendingRateVar;
    private List<Program> programList;
    private Program chanelProgram;

    public List<Program> getPrograms(){
        if(programList == null){
            this.programList = new ArrayList<>();
        }
        return this.programList;
    }

    public Variable getBuffer(){
        if(this.Buffer == null){
            this.Buffer = new Variable(BasicType.FLOAT,"Buffer_"+this.id,"0");
        }
        return this.Buffer;
    }

    public Variable getMaxSendingRateVar(){
        if(this.MaxSendingRateVar == null){
            this.MaxSendingRateVar = new Variable(BasicType.INT,"MaxSendingRate_"+this.id,this.MaxSendingRate);
        }
        return this.MaxSendingRateVar;
    }

    public Variable getMinSendingRateVar(){
        if(this.MinSendingRateVar == null){
            this.MinSendingRateVar = new Variable(BasicType.INT,"MinSendingRate_"+this.id,this.MinSendingRate);
        }
        return this.MinSendingRateVar;
    }

    /**
     * Convert to pnml in unicast mode
     * @param pnml
     * @param InputPlaces
     * @param OutputPlace
     * @param programs
     */
    public void convertToPnml(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace,
            ArrayList<Program> programs,ArrayList<Variable> variables){

        Transition chanel = new Transition();
        chanel.id = "chanel_"+this.id;

        Arc inChanel = new Arc();
        inChanel.id = "In_"+this.id;
        inChanel.label = "Input "+this.id;
        inChanel.weight = 1;
        inChanel.place = OutputPlace.get(this.From);
        inChanel.transition = chanel.id;
        inChanel.direction = ArcDirection.PLACE_TO_TRANSITION;

        Arc outChanel = new Arc();
        outChanel.id = "Out_"+this.id;
        outChanel.label = "Output "+this.id;
        outChanel.weight = 1;
        outChanel.transition = chanel.id;
        outChanel.place = InputPlaces.get(this.To);
        outChanel.direction = ArcDirection.TRANSITION_TO_PLACE;

        pnml.net.transitions.add(chanel);
        pnml.net.arcs.add(inChanel);
        pnml.net.arcs.add(outChanel);

        variables.add(getMaxSendingRateVar());
        variables.add(getMinSendingRateVar());
        variables.add(getBuffer());

        chanelProgram = new Program(chanel.id,this.Program);

        getPrograms().add(chanelProgram);

        programs.add(chanelProgram);

    }

    public void generateCode(Sensor sensor){
        chanelProgram.Code =Code.CreateSensorToChanelChanelPart(getBuffer(),sensor.getMaxSendingRateVar(),sensor.getMinSendingRateVar()) +
                System.lineSeparator() +
                Code.CreateChaneltoSensorChanelPart(getBuffer(), getMaxSendingRateVar(),getMinSendingRateVar());
    }
}
