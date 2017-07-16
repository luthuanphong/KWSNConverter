package Kwsn;

import java.util.ArrayList;

/**
 * Created by FredLu on 02/07/2017.
 */
import javax.xml.bind.annotation.*;
public class Network {
    @XmlAttribute(name = "SensorMaxBufferSize")
    public String SensorMaxBufferSize;
    @XmlAttribute(name = "SensorMaxQueueSize")
    public String SensorMaxQueueSize;
    @XmlAttribute(name = "ChannelMaxBufferSize")
    public String ChannelMaxBufferSize;
    @XmlElement(name = "Process")
    public ArrayList<Process> processes = new ArrayList<>();
}
