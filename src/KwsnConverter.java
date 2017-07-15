import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by FredLu on 02/07/2017.
 */
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import Kwsn.*;
import Pnml.*;

public class KwsnConverter {
    /**
     * Map used to store id of input place
     */
    private HashMap<String,String> InputPlaces = new HashMap<>();

    /**
     * Map used to store id of output place
     */
    private HashMap<String,String> OutputPlaces = new HashMap<>();

    /**
     * Map used to store code at transition
     */
    private ArrayList<Program> programs = new ArrayList<>();

    public static final int UNICAST = 1;

    public static final int BROADCAST = 2;

    /**
     *
     */
    private Declaration declaration = null;
    /**
     * convert kwsn file to pnml
     * @param path
     */
    public Pnml convert(String path,int chanelMode) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(WSN.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        WSN wsn = (WSN) unmarshaller.unmarshal(new File(path));
        Pnml pnml = new Pnml();
        declaration = new Declaration(wsn.Declaration);
        for(Kwsn.Process process : wsn.Network.processes) {
            ////Convert Sensor
            for (Sensor sensor : process.sensors.listSensor) {
                sensor.convertToPnml(pnml, InputPlaces, OutputPlaces , programs);
            }
            ////Convert chanel
            if(chanelMode == UNICAST){
                for (Link link : process.links.listLinks) {
                    link.convertToPnml(pnml, InputPlaces, OutputPlaces , programs);
                }
            }else if(chanelMode == BROADCAST){
                for(int i = 0 ;i < process.links.listLinks.size();i++){
                    BroastcastLink link = new BroastcastLink(process.links.listLinks.get(i),i);
                    for(int j = 0 ; j < process.links.listLinks.size();j++){
                        if(i != j && link.From.equals(process.links.listLinks.get(j).From)){
                            link.To.add(process.links.listLinks.get(j).To);
                            link.Program.add(process.links.listLinks.get(j).Program);
                            process.links.listLinks.remove(j);
                        }
                    }
                    link.convertToPnml(pnml,InputPlaces,OutputPlaces,programs);
                }
            }
        }
        programs.add(new Program("main",""));
        return pnml;
    }

    public void SaveConvertFile(String sourcePath, String folderPath,int chanelMode){

        try {
            File file = new File(sourcePath);
            String sourceFileName = file.getName().split("\\.")[0];

            Pnml pnml = convert(sourcePath,chanelMode);
            JAXBContext context = JAXBContext.newInstance(Pnml.class);
            Marshaller marshaller = context.createMarshaller();
            //Save normal file
            marshaller.marshal(pnml,new File(folderPath+sourceFileName+".pmnl"));

            FunctionFileWriter.Write(folderPath+sourceFileName+".txt",programs,declaration,false);
            FunctionFileWriter.Write(folderPath+sourceFileName+"_minimize.txt",programs,declaration,true);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


}
