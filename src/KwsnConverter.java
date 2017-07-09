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

    /**
     *
     */
    private Declaration declaration = null;
    /**
     * convert kwsn file to pnml
     * @param path
     */
    public Pnml convert(String path) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(WSN.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        WSN wsn = (WSN) unmarshaller.unmarshal(new File(path));
        Pnml pnml = new Pnml();
        declaration = new Declaration(wsn.Declaration);
        for(Kwsn.Process process : wsn.Network.processes) {
            for (Sensor sensor : process.sensors.listSensor) {
                sensor.convertToPnml(pnml, InputPlaces, OutputPlaces , programs);
            }
            for (Link link : process.links.listLinks) {
                link.convertToPnml(pnml, InputPlaces, OutputPlaces , programs);
            }
        }
        programs.add(new Program("main",""));
        return pnml;
    }

    public void SaveConvertFile(String sourcePath, String folderPath){

        try {
            File file = new File(sourcePath);
            String sourceFileName = file.getName().split("\\.")[0];

            Pnml pnml = convert(sourcePath);
            JAXBContext context = JAXBContext.newInstance(Pnml.class);
            Marshaller marshaller = context.createMarshaller();
            //Save normal file
            marshaller.marshal(pnml,new File(folderPath+"\\"+sourceFileName+".pmnl"));
            FunctionFileWriter.Write(folderPath+"\\"+sourceFileName+".txt",programs,declaration,false);
            //Save minimize file
            for(Place p : pnml.net.places){
                if(p.token > 0){
                    p.token = 1;
                }
            }
            for(Arc arc : pnml.net.arcs){
                arc.weight = 1;
            }
            marshaller.marshal(pnml,new File(folderPath+"\\"+sourceFileName+"_minimize.pnml"));
            FunctionFileWriter.Write(folderPath+"\\"+sourceFileName+"_minimize.txt",programs,declaration,true);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


}
