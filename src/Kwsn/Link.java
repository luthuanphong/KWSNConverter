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
    @XmlElement(name = "From")
    public String From;
    @XmlElement(name = "To")
    public String To;
    @XmlElement(name = "Program")
    public String Program;

    public void convertToPnml(Pnml pnml, HashMap<String,String> InputPlaces, HashMap<String,String> OutputPlace, ArrayList<Program> programs){

        //Create transiton
        Transition transition = new Transition();
        transition.id="Transition"+this.id;
        transition.label=id;

        //Create Arc
        Arc in = new Arc();
        in.id = "In"+this.id;
        in.weight = Integer.parseInt(this.MaxSendingRate);
        in.direction = ArcDirection.PLACE_TO_TRANSITION;
        in.place = OutputPlace.get(this.From);
        in.transition = transition.id;

        Arc out = new Arc();
        out.id = "Out"+this.id;
        out.weight = Integer.parseInt(this.MaxSendingRate);
        out.direction = ArcDirection.TRANSITION_TO_PLACE;
        out.transition = transition.id;
        out.place = InputPlaces.get(this.To);

        pnml.net.transitions.add(transition);
        pnml.net.arcs.add(in);
        pnml.net.arcs.add(out);

        programs.add(new Program(transition.id,this.Program));
    }
}
