package Kwsn;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Created by FredLu on 02/07/2017.
 */
@XmlRootElement(name = "Links")
public class Links {
    @XmlElement(name = "Link")
    public ArrayList<Link> listLinks = new ArrayList<>();
}
