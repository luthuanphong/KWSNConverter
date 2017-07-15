package Kwsn; /**
 * Created by FredLu on 02/07/2017.
 */
import javax.xml.bind.annotation.*;
import Pnml.*;

import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement(name = "Sensor")
public class Sensor {
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
    /**
     * Convert sensor to pnml structure
     * @param pnml pnml object used to store convert data
     * @param InputPlaces stored id of input place
     * @param OutputPlace stored id of output place
     */
    public void convertToPnml(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> programs){
        switch (this.Type){
            case 1:
                convertSourceNode(pnml,InputPlaces,OutputPlace,programs);
                break;
            case 2:
                convertSinkNode(pnml,InputPlaces,OutputPlace,programs);
                break;
            case 3:
                convertIntermediateNode(pnml,InputPlaces,OutputPlace,programs);
                break;
        }
    }

    private void convertSourceNode(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> program){
        Place inputPlace = new Place();
        inputPlace.id = "In"+this.Id;
        if(Init.equals("True")){
            //if sensor init is true set tolen to input place
            inputPlace.token = 1;
        }
        inputPlace.label = "Input "+this.Name;
        //Add input place id to map
        InputPlaces.put(this.Name,inputPlace.id);

        Place intermediatePlace = new Place();
        intermediatePlace.id = "Intermediate"+this.Id;
        intermediatePlace.label = "Intermediate"+this.Name;

        Place outPlace = new Place();
        outPlace.id = "Out"+this.Id;
        outPlace.label = "Output " + this.Name;
        //Add output place id to map
        OutputPlace.put(this.Name,outPlace.id);

        Transition generate = new Transition();
        generate.id = "generate"+this.Id;
        generate.label = "generate "+this.Name;

        Transition send = new Transition();
        send.id = "send"+this.Id;
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

        program.add(new Program(generate.id,""));
        program.add(new Program(send.id,""));
    }
    private void convertSinkNode(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> program){
        Place inputPlace = new Place();
        inputPlace.id = "In"+this.Id;
        inputPlace.label = "Input "+this.Name;
        //Add input place id to map
        InputPlaces.put(this.Name,inputPlace.id);

        Place intermediatePlace = new Place();
        intermediatePlace.id = "Intermediate"+this.Id;
        intermediatePlace.label = "Intermediate"+this.Name;

        Place outPlace = new Place();
        outPlace.id = "Out"+this.Id;
        outPlace.label = "Output " + this.Name;
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
        afterProcess.weight = Integer.parseInt(this.MaxSendingRate);
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

        program.add(new Program(receive.id,""));
        program.add(new Program(process.id,""));
    }
    private void convertIntermediateNode(
            Pnml pnml, HashMap<String,String> InputPlaces,
            HashMap<String,String> OutputPlace , ArrayList<Program> program){
        Place inputPlace = new Place();
        inputPlace.id = "In"+this.Id;
        inputPlace.label = "Input "+this.Name;
        //Add input place id to map
        InputPlaces.put(this.Name,inputPlace.id);

        Place intermediatePlace = new Place();
        intermediatePlace.id = "Intermediate"+this.Id;
        intermediatePlace.label = "Intermediate"+this.Name;

        Place outPlace = new Place();
        outPlace.id = "Out"+this.Id;
        outPlace.label = "Output " + this.Name;
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

        program.add(new Program(receive.id,""));
        program.add(new Program(send.id,""));
    }
}
