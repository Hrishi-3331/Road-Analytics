package SimulationObjects;

import Simulation.Animatable;
import Simulation.Simulatable;
import Simulation.SimulationSystemConfig;
import res.GraphicsBucket;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Random;

public class Vehicle implements Animatable, Simulatable {
    private int xPos;
    private int yPos;
    private Road road;
    private int speed;
    private Cloud cloud;
    private Image graphic;

    public Vehicle(int xPos, int yPos, Road road) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.road = road;
        this.speed = SimulationSystemConfig.DEFAULT_VEHICLE_SPEED;
        this.graphic = getRandomGraphic();
    }

    public Vehicle(int xPos, int yPos, Road road, int speed) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.road = road;
        this.speed = speed;
        this.graphic = getRandomGraphic();
    }

    private Image getRandomGraphic(){
        try {
            Image image1 = ImageIO.read(getClass().getResourceAsStream(GraphicsBucket.CAR1));
            Image image2 = ImageIO.read(getClass().getResourceAsStream(GraphicsBucket.CAR2));
            Image image3 = ImageIO.read(getClass().getResourceAsStream(GraphicsBucket.CAR3));
            Image image4 = ImageIO.read(getClass().getResourceAsStream(GraphicsBucket.CAR4));
            Image[] images = new Image[]{image1, image2, image3, image4};
            Random random = new Random();
            int index = random.nextInt(3);
            return images[index];
        }
        catch (Exception e){
            System.out.println("Error in assigning graphics");
            return null;
        }
    }

    public void connectToCloud(Cloud cloud){
        this.cloud = cloud;
    }

    @Override
    public void drawOnGUI(Graphics graphics) {
        Graphics2D canvas = (Graphics2D) graphics;
        canvas.drawImage(graphic, this.xPos, this.yPos, SimulationSystemConfig.VEHICLE_WIDTH, SimulationSystemConfig.VEHICLE_LENGTH, null);
    }

    @Override
    public void simulate() {
        double condition = road.getRoadCondition(this.xPos);
        Random random = new Random();
        int flag = random.nextInt(100);
        if (condition == 0.0){
            if (flag < SimulationSystemConfig.PROBABILIY_OF_FALSE_REPORT) cloud.sendReport(1.0, xPos);
            else cloud.sendReport(0.0, xPos);
        }
        else {
            if (flag < SimulationSystemConfig.PROBABILITY_OF_MISSED_REPORT) cloud.sendReport(0.0, xPos);
            else cloud.sendReport(1.0, xPos);
        }
        this.xPos += speed;
    }

    public boolean isWithinSystem(){
        return this.xPos <= SimulationSystemConfig.SIMULATION_WIDTH;
    }
}
