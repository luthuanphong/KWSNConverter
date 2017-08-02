/**
 * Created by FredLu on 02/07/2017.
 */
import Kwsn.WSN;

import javax.xml.bind.*;
import java.io.File;
import java.util.HashMap;

import Pnml.*;
public class main {

    public static void main(String []args) throws JAXBException {

        HashMap<String,String> SensorEnergy = new HashMap<>();
        SensorEnergy.put("1","100");
        SensorEnergy.put("2","80");
        SensorEnergy.put("3","70");
        SensorEnergy.put("4","67");
        SensorEnergy.put("5","78");

        HashMap<String,String> energyRule = new HashMap<>();
        energyRule.put("process","5");
        energyRule.put("send","2");

        JAXBContext context = JAXBContext.newInstance(Pnml.class);
        //Unmarshaller unmarshaller = context.createUnmarshaller();
        //WSN wsn = (WSN) unmarshaller.unmarshal(new File("C:\\Data\\luanvan\\GraduationThesis\\5-sensors.kwsn"));

        KwsnConverter converter = new KwsnConverter();
        converter.SaveConvertFile("/home/fredlu/Downloads/5-sensors.kwsn",
                "/home/fredlu/Downloads/",SensorEnergy,energyRule,"1","1","1",KwsnConverter.BROADCAST);

    }
}
