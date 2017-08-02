import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /***
     * Líst of variable
     */
    private ArrayList<Variable> variables = new ArrayList<>();

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
    public Pnml convert(
            String path,
            HashMap<String,String> Energies,
            HashMap<String,String> EnergiesRule,
            String sensorMinsendingRate,
            String sensorMinprocessingRate,
            String chanelMinsendingRate,
            int chanelMode) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(WSN.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        WSN wsn = (WSN) unmarshaller.unmarshal(new File(path));
        Pnml pnml = new Pnml();
        declaration = new Declaration(wsn.Declaration);
        variables.add(new Variable(BasicType.INT,Constants.SENSOR_MAX_BUFFER_SZIE,wsn.Network.SensorMaxBufferSize));
        variables.add(new Variable(BasicType.INT,Constants.SENSOR_MAX_QUEUE_SIZE,wsn.Network.SensorMaxQueueSize));
        variables.add(new Variable(BasicType.INT,Constants.CHANEL_MAX_BUFFER_SIZE,wsn.Network.ChannelMaxBufferSize));
        variables.add(new Variable(BasicType.BOOL,Constants.CONGESTION,"false"));
        for(Kwsn.Process process : wsn.Network.processes) {
            ////Convert Sensor
            for (Sensor sensor : process.sensors.listSensor) {
                sensor.setEnergy(Energies.getOrDefault(sensor.Id,"50"));
                sensor.setEnergyRule(EnergiesRule);
                sensor.MinSendingRate = sensorMinsendingRate;
                sensor.MinProcessingRate = sensorMinprocessingRate;
                sensor.convertToPnml(pnml, InputPlaces, OutputPlaces , programs,variables);
            }
            ////Convert chanel
            if(chanelMode == UNICAST){
                for (Link link : process.links.listLinks) {
                    link.MinSendingRate = chanelMinsendingRate;
                    link.convertToPnml(pnml, InputPlaces, OutputPlaces , programs,variables);
                    Sensor from = findSensor(process.sensors.listSensor,link.From);
                    link.generateCode(from);
                }
                for (Sensor sensor : process.sensors.listSensor) {
                    Link link = findLink(process.links.listLinks,sensor.Name);
                    sensor.generateCode(link);
                }

            }else if(chanelMode == BROADCAST){
                List<BroastcastLink> links = new ArrayList<>();
                for(int i = 0 ;i < process.links.listLinks.size();i++){
                    process.links.listLinks.get(i).MinSendingRate = chanelMinsendingRate;
                    BroastcastLink link = new BroastcastLink(process.links.listLinks.get(i),i);
                    for(int j = 0 ; j < process.links.listLinks.size();j++){
                        if(i != j && link.From.equals(process.links.listLinks.get(j).From)){
                            link.To.add(process.links.listLinks.get(j).To);
                            link.Program.add(process.links.listLinks.get(j).Program);
                            process.links.listLinks.remove(j);
                        }
                    }
                    Sensor from = findSensor(process.sensors.listSensor,link.From);
                    link.convertToPnml(pnml,InputPlaces,OutputPlaces,programs,variables);
                    link.generateCode(from);
                    links.add(link);
                }
                for(Sensor sensor : process.sensors.listSensor ){
                    BroastcastLink link = findBroastCastLink(links,sensor.Name);
                    sensor.generateCode(link);
                }
            }
        }
        programs.add(new Program("main",""));
        return pnml;
    }

    public void SaveConvertFile(
            String sourcePath,
            String folderPath,
            HashMap<String,String> Energies,
            HashMap<String,String> EnergiesRule,
            String sensorMinsendingRate,
            String sensorMinprocessingRate,
            String chanelMinsendingRate,
            int chanelMode){

        try {
            File file = new File(sourcePath);
            String sourceFileName = file.getName().split("\\.")[0];

            Pnml pnml = convert(sourcePath,Energies,EnergiesRule,sensorMinsendingRate,sensorMinprocessingRate,chanelMinsendingRate,chanelMode);
            JAXBContext context = JAXBContext.newInstance(Pnml.class);
            Marshaller marshaller = context.createMarshaller();
            //Save normal file
            marshaller.marshal(pnml,new File(folderPath+sourceFileName+".pmnl"));

            FunctionFileWriter.Write(folderPath+sourceFileName+".txt",programs,variables,false);
            FunctionFileWriter.Write(folderPath+sourceFileName+"_minimize.txt",programs,variables,true);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private Sensor findSensor(List<Sensor> sensors, String Name){
        for(Sensor sensor : sensors){
            if(sensor.Name.equals(Name)){
                return sensor;
            }
        }
        return null;
    }

    private Link findLink(List<Link> links,String sensorName){
        for(Link link : links){
            if(link.To.equals(sensorName)){
                return link;
            }
        }
        return null;
    }

    private BroastcastLink findBroastCastLink(List<BroastcastLink> links, String sensorName){
        for(BroastcastLink link : links){
            for(String To : link.To){
                if(To.equals(sensorName)){
                    return link;
                }
            }
        }
        return null;
    }
}
