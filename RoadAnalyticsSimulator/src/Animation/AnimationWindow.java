package Animation;

import Simulation.SimulationController;
import javax.swing.*;

public class AnimationWindow extends JFrame {

    private AnimationPanel panel;

    public AnimationWindow(SimulationController controller){
        panel = new AnimationPanel(controller);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
    }

    public void start(){
        panel.start();
        this.setVisible(true);
    }
}
