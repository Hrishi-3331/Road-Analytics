package SimulationObjects;

import Simulation.Animatable;
import Simulation.SimulationSystemConfig;
import res.GraphicsBucket;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashMap;

public class Road implements Animatable {

    private int length;
    private int traffic;
    private HashMap<Integer, Double> roadMap;
    private Cloud cloud;

    public Road(int heading, int length, int traffic){
        roadMap = new HashMap<>();
        this.traffic = traffic;
        this.length = length;
        for (int i = 0; i <= length; i++){
            roadMap.put(i, 0.0);
        }
    }

    public Road(){

    }

    public void setupCloud(Cloud cloud){
        this.cloud = cloud;
    }

    public Cloud getCloudReference(){
        return this.cloud;
    }

    public int getLength() {
        return length;
    }

    public int getTraffic() {
        return traffic;
    }

    public double getRoadCondition(int position){
        return roadMap.get(position);
    }

    @Override
    public void drawOnGUI(Graphics graphics) {
        Graphics2D canvas = (Graphics2D) graphics;
        try {
            Image roadImage = ImageIO.read(getClass().getResourceAsStream(GraphicsBucket.ROAD));
            canvas.drawImage(roadImage, SimulationSystemConfig.ROAD_POS_X, SimulationSystemConfig.ROAD_POS_Y, SimulationSystemConfig.ROAD_LENGTH, SimulationSystemConfig.ROAD_WIDTH, null);
        } catch (Exception e){
            e.printStackTrace();
            canvas.drawRect(SimulationSystemConfig.ROAD_POS_X, SimulationSystemConfig.ROAD_POS_Y, SimulationSystemConfig.ROAD_LENGTH, SimulationSystemConfig.ROAD_WIDTH);
        }
    }
}
