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
    public int MaxSendingRate;
    public String From;
    public List<String> To;
    public List<String> Program;

    public BroastcastLink(Link link,int index){
        this.To = new ArrayList<>();
        this.Program = new ArrayList<>();
        this.id = "link"+(index+1);
        this.MaxSendingRate = Integer.parseInt(link.MaxSendingRate);
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

        variables.add(new Variable(BasicType.INT,"SendingRate_"+this.id,this.MaxSendingRate+""));
        variables.add(new Variable(BasicType.INT,"Buffer_"+this.id,"0"));

        programs.add(new Program(receive.id,program.toString()));
        programs.add(new Program(send.id,""));

    }
}
