package SimulationObjects;

import java.util.HashMap;

public class Cloud {

    private Road road;
    private HashMap<Integer, RoadSegment> cloudreport;


    public Cloud(Road road){
        this.road = road;
        cloudreport = new HashMap<>();
        for (int i = 0; i <= road.getLength(); i++){
            cloudreport.put(i, new RoadSegment());
        }
    }

    void sendReport(double condition, int position){
        cloudreport.get(position).updateQuality(condition);
    }

    private class RoadSegment{

        private int reporter;
        private double quality;
        private int goodreports = 0;
        private int badreports = 0;
        private int medreports = 0;

        RoadSegment(){
            this.reporter = 0;
            this.quality = 0;
        }

        void updateQuality(double quality){
            this.reporter++;
        }
    }
}