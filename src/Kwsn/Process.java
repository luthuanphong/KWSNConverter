package Kwsn;

/**
 * Created by FredLu on 02/07/2017.
 */
import javax.xml.bind.annotation.*;
public class Process {

    @XmlElement(name = "Sensors")
    public Sensors sensors;
    @XmlElement(name = "Links")
    public Links links;
}
