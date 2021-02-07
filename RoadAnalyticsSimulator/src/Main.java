import Simulation.*;
import SimulationObjects.Cloud;
import SimulationObjects.Road;

public class Main {

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.setRoad(new Road(SimulationSystemConfig.DIRECTION_EAST, SimulationSystemConfig.SIMULATION_WIDTH, SimulationSystemConfig.TRAFFIC_MED));
        simulation.getRoad().setupCloud(new Cloud(simulation.getRoad()));
        SimulationController controller = new SimulationController(simulation, 400);
        controller.startSimulation();
    }
}
