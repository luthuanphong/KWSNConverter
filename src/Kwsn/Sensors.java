package Kwsn;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Created by FredLu on 02/07/2017.
 */
@XmlRootElement(name = "Sensors")
public class Sensors {
    @XmlElement(name = "Sensor")
    public ArrayList<Sensor> listSensor = new ArrayList<>();
}
