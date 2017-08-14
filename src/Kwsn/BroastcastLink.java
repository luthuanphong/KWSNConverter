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
    private Program chanelProgram;

    public List<Program> getPrograms(){
        if(programList == null){
            this.programList = new ArrayList<>();
        }
        return this.programList;
    }

    public Variable getBuffer(){
        if(this.Buffer == null){
            this.Buffer = new Variable(BasicType.FLOAT,"Chanel_Buffer_"+this.id,"0");
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
        this.id = (index+1)+"";
        this.MaxSendingRate = link.MaxSendingRate;
        this.MinSendingRate = link.MinSendingRate;
        this.From = link.From;
        this.To.add(link.To);
        this.Program.add(link.Program);
    }

    public void convertToPnml(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace, ArrayList<Program> programs,ArrayList<Variable> variables){

        Transition chanel = new Transition();
        chanel.id = "chanel_"+this.id;

        Arc inChanel = new Arc();
        inChanel.id = "In_"+this.id;
        inChanel.label = "Input "+this.id;
        inChanel.weight = 1;
        inChanel.place = OutputPlace.get(this.From);
        inChanel.transition = chanel.id;
        inChanel.direction = ArcDirection.PLACE_TO_TRANSITION;

        pnml.net.transitions.add(chanel);
        pnml.net.arcs.add(inChanel);

        for(int i = 0 ; i < this.To.size() ; i++){

            Arc outChanel = new Arc();
            outChanel.id = "Out_"+i;
            outChanel.label = "Output "+i;
            outChanel.weight = 1;
            outChanel.transition = chanel.id;
            outChanel.place = InputPlaces.get(this.To.get(i));
            outChanel.direction = ArcDirection.TRANSITION_TO_PLACE;
            pnml.net.arcs.add(outChanel);
        }


        StringBuilder program = new StringBuilder();
        for(int i = 0 ; i < Program.size() ;i++){
            program.append(this.Program.get(i));
        }

        variables.add(getMaxSendingRateVar());
        variables.add(getBuffer());

        chanelProgram = new Program(chanel.id,program.toString());

        getPrograms().add(chanelProgram);

        programs.add(chanelProgram);

    }
    public void generateCode(Sensor sensor){
        chanelProgram.Code =Code.CreateSensorToChanelChanelPart(getBuffer(),sensor.getMaxSendingRateVar(),sensor.getMinSendingRateVar()) +
                System.lineSeparator() +
                Code.CreateChaneltoSensorChanelPart(getBuffer(), getMaxSendingRateVar(),getMinSendingRateVar());
    }
}
