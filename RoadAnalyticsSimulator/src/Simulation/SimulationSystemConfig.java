package Simulation;

import java.awt.*;

public class SimulationSystemConfig {

    private static final Dimension SIMULATION_DIMENSIONS = Toolkit.getDefaultToolkit().getScreenSize();

    public static final int SIMULATION_WIDTH = SIMULATION_DIMENSIONS.width;
    public static final int SIMULATION_HEIGHT = SIMULATION_DIMENSIONS.height;

    public static final int DIRECTION_NORTH = 0;
    public static final int DIRECTION_SOUTH = 1;
    public static final int DIRECTION_EAST = 2;
    public static final int DIRECTION_WEST = 3;

    public static final int TRAFFIC_HIGH = 0;
    public static final int TRAFFIC_MED = 1;
    public static final int TRAFFIC_LOW = 2;

    public static final int ROAD_LENGTH = SIMULATION_WIDTH;
    public static final int ROAD_WIDTH = 120;

    public static final int VEHICLE_LENGTH = 50;
    public static final int VEHICLE_WIDTH = 100;
    public static final int VEHICLE_Y_POS = SIMULATION_HEIGHT/2 - VEHICLE_WIDTH/2 + ROAD_WIDTH/4;

    public static final int ROAD_POS_X = 0;
    public static final int ROAD_POS_Y = SIMULATION_HEIGHT/2 - ROAD_WIDTH/2;

    public static final int DEFAULT_VEHICLE_SPEED = 10;

    public static final int PROBABILIY_OF_FALSE_REPORT = 15;
    public static final int PROBABILITY_OF_MISSED_REPORT = 5;
}
