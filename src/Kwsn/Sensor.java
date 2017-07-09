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

    /**
     * Convert sensor to pnml structure
     * @param pnml pnml object used to store convert data
     * @param InputPlaces stored id of input place
     * @param OutputPlace stored id of output place
     */
    public void convertToPnml(Pnml pnml, HashMap<String,String> InputPlaces, HashMap<String,String> OutputPlace , ArrayList<Program> programs){
        //Create input place
        Place inputPlace = new Place();
        inputPlace.id = "In"+Id;
        if(Init.equals("True")){
            //if sensor init is true set tolen to input place
            inputPlace.token = Integer.parseInt(this.MaxProcessingRate);
        }
        inputPlace.label = "Input "+this.Name;
        //Add input place id to map
        InputPlaces.put(this.Name,inputPlace.id);

        //Create transition
        Transition transition = new Transition();
        transition.id = "Transition"+Id;
        transition.label = "Transition " + this.Name;

        //Create output place
        Place outPlace = new Place();
        outPlace.id = "Out"+Id;
        outPlace.label = "Output " + this.Name;
        //Add output place id to map
        OutputPlace.put(this.Name,outPlace.id);

        //Create arc
        Arc in = new Arc();
        in.id = "inputArc"+Id;
        in.weight = Integer.parseInt(this.MaxSendingRate);
        in.direction = ArcDirection.PLACE_TO_TRANSITION;
        in.place = inputPlace.id;
        in.transition = transition.id;

        //Create arc
        Arc out = new Arc();
        out.id = "outputArc"+Id;
        out.weight = Integer.parseInt(this.MaxSendingRate);
        out.direction = ArcDirection.TRANSITION_TO_PLACE;
        out.place = outPlace.id;
        out.transition = transition.id;

        pnml.net.places.add(inputPlace);
        pnml.net.places.add(outPlace);
        pnml.net.transitions.add(transition);
        pnml.net.arcs.add(in);
        pnml.net.arcs.add(out);

        programs.add(new Program(transition.id,""));
    }
}
