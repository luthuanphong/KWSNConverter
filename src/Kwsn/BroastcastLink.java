package Kwsn;

import Pnml.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fredlu on 13/07/2017.
 */
public class BroastcastLink {
    public String id;
    public String MaxSendingRate;
    public String MinSendingRate;
    public String From;
    public List<String> To;
    public List<String> Program;

    private Variable Buffer;
    private Variable MaxSendingRateVar;
    private Variable MinSendingRateVar;
    private List<Program> programList;
    private Program receiveProgram;
    private Program sendProgram;

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

    public BroastcastLink(Link link,int index){
        this.To = new ArrayList<>();
        this.Program = new ArrayList<>();
        this.id = "link"+(index+1);
        this.MaxSendingRate = link.MaxSendingRate;
        this.MinSendingRate = link.MinSendingRate;
        this.From = link.From;
        this.To.add(link.To);
        this.Program.add(link.Program);
    }

    public void convertToPnml(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace, ArrayList<Program> programs,ArrayList<Variable> variables){
        Place intermediate = new Place();
        intermediate.id = "Intermediate"+this.id;
        intermediate.label = "Intermediate "+this.id;

        Transition receive = new Transition();
        receive.id = "receive"+this.id;
        receive.label = "receive "+this.id;

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
        afterReceive.id = "afterReceive"+this.id;
        afterReceive.weight = 1;
        afterReceive.direction = ArcDirection.TRANSITION_TO_PLACE;
        afterReceive.place = intermediate.id;
        afterReceive.transition = receive.id;

        Arc beforeSend = new Arc();
        beforeSend.id = "beforeSend"+this.id;
        beforeSend.weight = 1;
        beforeSend.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeSend.place = intermediate.id;
        beforeSend.transition = send.id;

        for(int i = 0 ; i < this.To.size() ; i++){
            Arc afterSend = new Arc();
            afterSend.id = "afterSend"+this.id+i;
            afterSend.weight = 1;
            afterSend.direction = ArcDirection.TRANSITION_TO_PLACE;
            afterSend.transition = send.id;
            afterSend.place = InputPlaces.get(this.To.get(i));

            pnml.net.arcs.add(afterSend);
        }

        pnml.net.places.add(intermediate);
        pnml.net.transitions.add(receive);
        pnml.net.transitions.add(send);
        pnml.net.arcs.add(beforeReceive);
        pnml.net.arcs.add(afterReceive);
        pnml.net.arcs.add(beforeSend);

        StringBuilder program = new StringBuilder();
        for(int i = 0 ; i < Program.size() ;i++){
            program.append(this.Program.get(i));
        }

        variables.add(getMaxSendingRateVar());
        variables.add(getBuffer());

        receiveProgram = new Program(receive.id,program.toString());
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
