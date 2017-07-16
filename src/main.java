/**
 * Created by FredLu on 02/07/2017.
 */
import Kwsn.WSN;

import javax.xml.bind.*;
import java.io.File;
import Pnml.*;
public class main {

    public static void main(String []args) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Pnml.class);
        //Unmarshaller unmarshaller = context.createUnmarshaller();
        //WSN wsn = (WSN) unmarshaller.unmarshal(new File("C:\\Data\\luanvan\\GraduationThesis\\5-sensors.kwsn"));

        KwsnConverter converter = new KwsnConverter();
        converter.SaveConvertFile("/home/fredlu/Downloads/5-sensors.kwsn",
                "/home/fredlu/Downloads/",KwsnConverter.BROADCAST);

    }
}
