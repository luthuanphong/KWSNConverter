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
    private Program sendProgram;
    private Program receiveProgram;

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

        Place intermediatePlace = new Place();
        intermediatePlace.id="Intermediate"+this.id;
        intermediatePlace.label = "Intermediate "+this.id;

        Transition receive = new Transition();
        receive.id = "receive"+this.id;
        receive.label = "recive "+this.id;

        Transition send = new Transition();
        send.id = "send"+this.id;
        send.label = "send "+this.id;

        Arc beforeReceive = new Arc();
        beforeReceive.id = "beforeReceive"+this.id;
        beforeReceive.weight = 1;
        beforeReceive.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeReceive.place = OutputPlace.get(this.From);
        beforeReceive.transition = receive.id;

        Arc afterReceive = new Arc();
        afterReceive.id="afterReceive"+this.id;
        afterReceive.weight = 1;
        afterReceive.direction = ArcDirection.TRANSITION_TO_PLACE;
        afterReceive.place = intermediatePlace.id;
        afterReceive.transition = receive.id;

        Arc beforeSend = new Arc();
        beforeSend.id = "beforeSend"+this.id;
        beforeSend.weight = 1;
        beforeSend.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeSend.place = intermediatePlace.id;
        beforeSend.transition = send.id;

        Arc afterSend = new Arc();
        afterSend.id="afterSend"+this.id;
        afterSend.weight = 1;
        afterSend.direction = ArcDirection.TRANSITION_TO_PLACE;
        afterSend.place = InputPlaces.get(this.To);
        afterSend.transition = send.id;

        pnml.net.places.add(intermediatePlace);
        pnml.net.transitions.add(receive);
        pnml.net.transitions.add(send);
        pnml.net.arcs.add(beforeReceive);
        pnml.net.arcs.add(afterReceive);
        pnml.net.arcs.add(beforeSend);
        pnml.net.arcs.add(afterSend);

        variables.add(getMaxSendingRateVar());
        variables.add(getMinSendingRateVar());
        variables.add(getBuffer());

        receiveProgram = new Program(receive.id,this.Program);
        sendProgram = new Program(send.id,"");

        getPrograms().add(receiveProgram);
        getPrograms().add(sendProgram);

        programs.add(receiveProgram);
        programs.add(sendProgram);
    }

    public void generateCode(Sensor sensor){
        receiveProgram.Code =Code.CreateSensorToChanelChanelPart(getBuffer(),sensor.getMaxSendingRateVar(),sensor.getMinSendingRateVar());
        sendProgram.Code = Code.CreateChaneltoSensorChanelPart(getBuffer(), getMaxSendingRateVar(),getMinSendingRateVar());
    }
}
