package Kwsn; /**
 * Created by FredLu on 02/07/2017.
 */
import javax.xml.bind.annotation.*;
@XmlRootElement(name = "WSN")
public class WSN {
    @XmlElement(name = "Declaration")
    public String Declaration;
    @XmlElement(name = "Network")
    public Network Network;
}
