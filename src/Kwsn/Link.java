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

    /**
     * Convert to pnml in unicast mode
     * @param pnml
     * @param InputPlaces
     * @param OutputPlace
     * @param programs
     */
    public void convertToPnml(Pnml pnml, HashMap<String,String> InputPlaces, HashMap<String,String> OutputPlace, ArrayList<Program> programs){

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

        programs.add(new Program(receive.id,this.Program));
        programs.add(new Program(send.id,""));
    }
}
