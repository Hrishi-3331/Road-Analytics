package Simulation;

import Animation.AnimationWindow;
import SimulationObjects.Road;
import SimulationObjects.Vehicle;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SimulationController {

    private Simulation simulation;
    private int tick = 0;
    private int gThreshold = 0;
    private Queue<Vehicle> animationQueue;
    private int time;
    private int interval;

    public SimulationController(Simulation simulation, int interval){
        this.simulation = simulation;
        Road road = simulation.getRoad();
        switch (road.getTraffic()){
            case SimulationSystemConfig.TRAFFIC_LOW:
                gThreshold = 65;
                break;

            case SimulationSystemConfig.TRAFFIC_MED:
                gThreshold = 50;
                break;

            case SimulationSystemConfig.TRAFFIC_HIGH:
                gThreshold = 30;
                break;
        }
        this.interval = interval;
        this.time = 0;
    }

    public void startSimulation(){
        AnimationWindow window = new AnimationWindow(this);
        window.start();
    }

    public void simulateSystem(){
        Queue<Vehicle> newQueue = new LinkedList<>();
        Queue<Vehicle> oldQueue = simulation.getVehicles();
        animationQueue = new LinkedList<>();

        while (!oldQueue.isEmpty()){
            Vehicle vehicle = oldQueue.remove();
            vehicle.simulate();
            if (vehicle.isWithinSystem()){
                newQueue.add(vehicle);
                animationQueue.add(vehicle);
            }
        }

        tick++;
        if (tick >= gThreshold){
            Random random = new Random();
            if (random.nextInt(100) > 90){
                tick = 0;
                Vehicle vehicle = new Vehicle(0, SimulationSystemConfig.VEHICLE_Y_POS + (new Random().nextInt(10) - 10), simulation.getRoad(), SimulationSystemConfig.DEFAULT_VEHICLE_SPEED + new Random().nextInt(3));
                vehicle.connectToCloud(simulation.getRoad().getCloudReference());
                newQueue.add(vehicle);
                animationQueue.add(vehicle);
            }
        }
        simulation.setVehicles(newQueue);
        this.time ++;
    }

    public void animateSystem(Graphics graphics){
        simulation.getRoad().drawOnGUI(graphics);
        while (!animationQueue.isEmpty()){
            Vehicle vehicle = animationQueue.remove();
            vehicle.drawOnGUI(graphics);
        }
    }

    public boolean isSimulationRunning(){
        return time <= interval;
    }
}
