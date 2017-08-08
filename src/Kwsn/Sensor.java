package Kwsn; /**
 * Created by FredLu on 02/07/2017.
 */
import javax.xml.bind.annotation.*;
import Pnml.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "Sensor")
public class Sensor {
    public String MinSendingRate;
    public String MinProcessingRate;
    @XmlAttribute(name = "MaxSendingRate")
    public String MaxSendingRate;
    @XmlAttribute(name = "MaxProcessingRate")
    public String MaxProcessingRate;
    @XmlAttribute(name = "id")
    public String Id;
    @XmlAttribute(name = "Name")
    public String Name;
    @XmlAttribute(name = "Init")
    public String Init;
    @XmlAttribute(name = "SType")
    public int Type;

    public HashMap<String,String> energyRule = null;

    private Variable Buffer;
    private Variable Queue;
    private Variable MinSendingRateVar;
    private Variable MinProcessingRateVar;
    private Variable MaxProcessRateVar;
    private Variable MaxSendingRateVar;
    private Variable Energy;
    private List<Program> programList;

    private Program generateProgram;
    private Program sendProgram;
    private Program receiveProgram;
    private Program processProgram;

    public void setEnergyRule(HashMap<String,String> rules){
        this.energyRule = rules;
    }

    public List<Program> getPrograms(){
        if(programList == null){
            this.programList = new ArrayList<>();
        }
        return this.programList;
    }


    public Variable getBuffer(){
        if(this.Buffer == null){
            this.Buffer = new Variable(BasicType.FLOAT,"Buffer_"+this.Id,"0");
        }
        return Buffer;
    }

    public Variable getQueue(){
        if(this.Queue == null){
            this.Queue = new Variable(BasicType.FLOAT,"Queue_"+this.Id,"0");
        }
        return Queue;
    }

    public Variable getMaxProcessRateVar(){
        if(this.MaxProcessRateVar == null){
            this.MaxProcessRateVar = new Variable(BasicType.INT,"MaxProcessRate_"+this.Id,this.MaxProcessingRate);
        }
        return this.MaxProcessRateVar;
    }

    public Variable getMaxSendingRateVar(){
        if(this.MaxSendingRateVar == null){
            this.MaxSendingRateVar = new Variable(BasicType.INT,"MaxSendingRate_"+this.Id,this.MaxSendingRate);
        }
        return this.MaxSendingRateVar;
    }

    public Variable getMinSendingRateVar(){
        if(this.MinSendingRateVar == null){
            this.MinSendingRateVar = new Variable(BasicType.INT,"MinSendingRate_"+this.Id,this.MinSendingRate);
        }
        return this.MinSendingRateVar;
    }

    public Variable getMinProcessingRateVar(){
        if(this.MinProcessingRateVar == null){
            this.MinProcessingRateVar = new Variable(BasicType.INT,"MinProcessingRate_"+this.Id,this.MinProcessingRate);
        }
        return this.MinProcessingRateVar;
    }

    /**
     * Convert sensor to pnml structure
     * @param pnml pnml object used to store convert data
     * @param InputPlaces stored id of input place
     * @param OutputPlace stored id of output place
     */
    public void convertToPnml(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> programs , ArrayList<Variable> variables){
        switch (this.Type){
            case 1:
                convertSourceNode(pnml,InputPlaces,OutputPlace,programs,variables);
                break;
            case 2:
                convertSinkNode(pnml,InputPlaces,OutputPlace,programs,variables);
                break;
            case 3:
                convertIntermediateNode(pnml,InputPlaces,OutputPlace,programs,variables);
                break;
        }
        variables.add(getMaxProcessRateVar());
        variables.add(getMaxSendingRateVar());
        variables.add(getMinProcessingRateVar());
        variables.add(getMinSendingRateVar());
        variables.add(getBuffer());
        variables.add(getQueue());
        variables.add(getEnergy());
    }

    private void convertSourceNode(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> programs, ArrayList<Variable> variables){
        Place inputPlace = new Place();
        inputPlace.id = "Src_"+"In_"+this.Id;
        if(Init.equals("True")){
            //if sensor init is true set tolen to input place
            inputPlace.token = 1;
        }
        inputPlace.label = "Src "+"Input "+this.Name;
        //Add input place id to map
        InputPlaces.put(this.Name,inputPlace.id);

        Place intermediatePlace = new Place();
        intermediatePlace.id = "Src_"+"Int_"+this.Id;
        intermediatePlace.label = "Src "+"Intermediate "+this.Name;

        Place outPlace = new Place();
        outPlace.id = "Src_"+"Out_"+this.Id;
        outPlace.label = "Src "+"Output " + this.Name;
        //Add output place id to map
        OutputPlace.put(this.Name,outPlace.id);

        Transition generate = new Transition();
        generate.id = "Src_"+"gen_"+this.Id;
        generate.label = "generate "+this.Name;

        Transition send = new Transition();
        send.id = "send_"+this.Id;
        send.label = "send"+this.Name;

        Arc beforeGen = new Arc();
        beforeGen.id = "beforeGen"+this.Id;
        beforeGen.weight = 1;
        beforeGen.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeGen.place = inputPlace.id;
        beforeGen.transition = generate.id;

        Arc afterGen = new Arc();
        afterGen.id = "afterGen"+this.Id;
        afterGen.weight = 1;
        afterGen.direction = ArcDirection.TRANSITION_TO_PLACE;
        afterGen.place = intermediatePlace.id;
        afterGen.transition = generate.id;

        Arc beforeSend = new Arc();
        beforeSend.id = "beforeSend"+this.Id;
        beforeSend.weight = 1;
        beforeSend.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeSend.place = intermediatePlace.id;
        beforeSend.transition = send.id;

        Arc afterSend = new Arc();
        afterSend.id="afterSend"+this.Id;
        afterSend.weight = 1;
        afterSend.direction=ArcDirection.TRANSITION_TO_PLACE;
        afterSend.place = outPlace.id;
        afterSend.transition = send.id;

        pnml.net.places.add(inputPlace);
        pnml.net.places.add(intermediatePlace);
        pnml.net.places.add(outPlace);
        pnml.net.transitions.add(generate);
        pnml.net.transitions.add(send);
        pnml.net.arcs.add(beforeGen);
        pnml.net.arcs.add(afterGen);
        pnml.net.arcs.add(beforeSend);
        pnml.net.arcs.add(afterSend);

        generateProgram =
                new Program(generate.id,Code.CreateSensorProcessingCode(getBuffer(),getQueue(), getMaxProcessRateVar(),getMinProcessingRateVar(),getEnergy(),this.energyRule));
        sendProgram =
                new Program(send.id,Code.CreateSensorToChanelSensorPart(getQueue(), getMaxSendingRateVar(),getMinSendingRateVar(),getEnergy(),this.energyRule));

        getPrograms().add(generateProgram);
        getPrograms().add(sendProgram);
        programs.add(generateProgram);
        programs.add(sendProgram);
    }
    private void convertSinkNode(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> programs, ArrayList<Variable> variables){
        Place inputPlace = new Place();
        inputPlace.id = "SiK_"+"In_"+this.Id;
        inputPlace.label = "Sik " + "Input "+this.Name;
        //Add input place id to map
        InputPlaces.put(this.Name,inputPlace.id);

        Place intermediatePlace = new Place();
        intermediatePlace.id = "Sik_"+"Int_"+this.Id;
        intermediatePlace.label = "Sik " + "Intermediate "+this.Name;

        Place outPlace = new Place();
        outPlace.id = "Sik_"+"Out_"+this.Id;
        outPlace.label = "Sik " + "Output " + this.Name;
        //Add output place id to map
        OutputPlace.put(this.Name,outPlace.id);

        Transition receive = new Transition();
        receive.id = "receive"+this.Id;
        receive.label = "receive "+this.Name;

        Transition process = new Transition();
        process.id = "process"+this.Id;
        process.label = "process"+this.Name;

        Arc beforeReceive = new Arc();
        beforeReceive.id = "beforeReceive"+this.Id;
        beforeReceive.weight = 1;
        beforeReceive.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeReceive.place = inputPlace.id;
        beforeReceive.transition = receive.id;

        Arc afterReceive = new Arc();
        afterReceive.id = "afterReceive"+this.Id;
        afterReceive.weight = 1;
        afterReceive.direction = ArcDirection.TRANSITION_TO_PLACE;
        afterReceive.place = intermediatePlace.id;
        afterReceive.transition = receive.id;

        Arc beforeProcess = new Arc();
        beforeProcess.id = "beforeProcess"+this.Id;
        beforeProcess.weight = 1;
        beforeProcess.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeProcess.place = intermediatePlace.id;
        beforeProcess.transition = process.id;

        Arc afterProcess = new Arc();
        afterProcess.id="afterProcess"+this.Id;
        afterProcess.weight = 1;
        afterProcess.direction=ArcDirection.TRANSITION_TO_PLACE;
        afterProcess.place = outPlace.id;
        afterProcess.transition = process.id;

        pnml.net.places.add(inputPlace);
        pnml.net.places.add(intermediatePlace);
        pnml.net.places.add(outPlace);
        pnml.net.transitions.add(receive);
        pnml.net.transitions.add(process);
        pnml.net.arcs.add(beforeReceive);
        pnml.net.arcs.add(afterReceive);
        pnml.net.arcs.add(beforeProcess);
        pnml.net.arcs.add(afterProcess);

        receiveProgram = new Program(receive.id,"");
        processProgram = new Program(process.id,"");

        getPrograms().add(receiveProgram);
        getPrograms().add(processProgram);
        programs.add(receiveProgram);
        programs.add(processProgram);
    }
    private void convertIntermediateNode(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> programs, ArrayList<Variable> variables){
        Place inputPlace = new Place();
        inputPlace.id = "Int_"+"In_"+this.Id;
        inputPlace.label = "Intermediate "+"Input "+this.Name;
        //Add input place id to map
        InputPlaces.put(this.Name,inputPlace.id);

        Place intermediatePlace = new Place();
        intermediatePlace.id = "Int_"+"Int_"+this.Id;
        intermediatePlace.label = "Int "+"Intermediate "+this.Name;

        Place outPlace = new Place();
        outPlace.id = "Int_"+"Out_"+this.Id;
        outPlace.label = "Int "+"Output " + this.Name;
        //Add output place id to map
        OutputPlace.put(this.Name,outPlace.id);

        Transition receive = new Transition();
        receive.id = "receive"+this.Id;
        receive.label = "receive "+this.Name;

        Transition send = new Transition();
        send.id = "send"+this.Id;
        send.label = "send"+this.Name;

        Arc beforeReceive = new Arc();
        beforeReceive.id = "beforeReceive"+this.Id;
        beforeReceive.weight = 1;
        beforeReceive.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeReceive.place = inputPlace.id;
        beforeReceive.transition = receive.id;

        Arc afterReceive = new Arc();
        afterReceive.id = "afterReceive"+this.Id;
        afterReceive.weight = 1;
        afterReceive.direction = ArcDirection.TRANSITION_TO_PLACE;
        afterReceive.place = intermediatePlace.id;
        afterReceive.transition = receive.id;

        Arc beforeSend = new Arc();
        beforeSend.id = "beforeSend"+this.Id;
        beforeSend.weight = 1;
        beforeSend.direction = ArcDirection.PLACE_TO_TRANSITION;
        beforeSend.place = intermediatePlace.id;
        beforeSend.transition = send.id;

        Arc afterSend = new Arc();
        afterSend.id="afterSend"+this.Id;
        afterSend.weight = 1;
        afterSend.direction=ArcDirection.TRANSITION_TO_PLACE;
        afterSend.place = outPlace.id;
        afterSend.transition = send.id;

        pnml.net.places.add(inputPlace);
        pnml.net.places.add(intermediatePlace);
        pnml.net.places.add(outPlace);
        pnml.net.transitions.add(receive);
        pnml.net.transitions.add(send);
        pnml.net.arcs.add(beforeReceive);
        pnml.net.arcs.add(afterReceive);
        pnml.net.arcs.add(beforeSend);
        pnml.net.arcs.add(afterSend);

        receiveProgram = new Program(receive.id,"");
        sendProgram = new Program(send.id,"");

        getPrograms().add(receiveProgram);
        getPrograms().add(sendProgram);

        programs.add(receiveProgram);
        programs.add(sendProgram);
    }

    public void generateCode(Link link){
        switch (this.Type){
            case 1:
                generateProgram.Code = Code.CreateSensorProcessingCode(getBuffer(),getQueue(), getMaxProcessRateVar(),getMinProcessingRateVar(),getEnergy(),this.energyRule);
                sendProgram.Code = Code.CreateSensorToChanelSensorPart(getQueue(), getMaxSendingRateVar(),getMinSendingRateVar(),getEnergy(),this.energyRule);
                break;
            case 2:
                receiveProgram.Code = Code.CreateChanelToSensorSensorPart(getBuffer(),link.getMaxSendingRateVar(),link.getMinSendingRateVar());
                processProgram.Code = Code.CreateSensorProcessingCode(getBuffer(),getQueue(), getMaxProcessRateVar(),getMinProcessingRateVar(),getEnergy(),this.energyRule);
                break;
            case 3:
                receiveProgram.Code = Code.CreateChanelToSensorSensorPart(getBuffer(),link.getMaxSendingRateVar(),link.getMinSendingRateVar());
                sendProgram.Code = Code.CreateSensorToChanelSensorPart(getQueue(), getMaxSendingRateVar(),getMinSendingRateVar(),getEnergy(),this.energyRule);
                break;
        }
    }

    public void generateCode(BroastcastLink link){
        switch (this.Type){
            case 1:
                generateProgram.Code = Code.CreateSensorGenerateCode(getBuffer(),getQueue(), getMaxProcessRateVar(),getMinProcessingRateVar(),getEnergy(),this.energyRule);
                sendProgram.Code = Code.CreateSensorToChanelSensorPart(getQueue(), getMaxSendingRateVar(),getMinSendingRateVar(),getEnergy(),this.energyRule);
                break;
            case 2:
                receiveProgram.Code = Code.CreateChanelToSensorSensorPart(getBuffer(),link.getMaxSendingRateVar(),link.getMinSendingRateVar());
                processProgram.Code = Code.CreateSensorProcessingCode(getBuffer(),getQueue(), getMaxProcessRateVar(),getMinProcessingRateVar(),getEnergy(),this.energyRule);
                break;
            case 3:
                receiveProgram.Code = Code.CreateChanelToSensorSensorPart(getBuffer(),link.getMaxSendingRateVar(),link.getMinSendingRateVar());
                sendProgram.Code = Code.CreateSensorToChanelSensorPart(getQueue(), getMaxSendingRateVar(),getMinSendingRateVar(),getEnergy(),this.energyRule);
                break;
        }
    }

    public Variable getEnergy() {
        return Energy;
    }

    public void setEnergy(String energy) {
       if(this.Energy == null){
           this.Energy = new Variable(BasicType.INT,"Energy"+this.Id,energy);
       }else{
           this.Energy.setValue(energy);
       }
    }
}
