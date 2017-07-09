package Kwsn;

import java.util.ArrayList;

/**
 * Created by FredLu on 02/07/2017.
 */
import javax.xml.bind.annotation.*;
public class Network {
    @XmlElement(name = "Process")
    public ArrayList<Process> processes = new ArrayList<>();
}
