package Simulation;

import SimulationObjects.Road;
import SimulationObjects.Vehicle;
import java.util.LinkedList;
import java.util.Queue;

public class Simulation {
    private Road road;
    private Queue<Vehicle> vehicles;

    public Simulation() {
        vehicles = new LinkedList<>();
    }

    public void setRoad(Road road){
        this.road = road;
    }

    public Road getRoad() {
        return road;
    }

    public Queue<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Queue<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
