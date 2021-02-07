package Animation;

import Simulation.SimulationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationPanel extends JPanel implements ActionListener {
    private Timer timer;
    private SimulationController controller;

    AnimationPanel(SimulationController controller){
        this.controller = controller;
        timer = new Timer(50, this);
    }

    void start(){
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setBackground(Color.green);
        controller.animateSystem(g);

        /**
        Graphics2D graphics2D = (Graphics2D) g;
        Font myFont = new Font ("Courier New", Font.BOLD, 17);
        graphics2D.setFont(myFont);
        graphics2D.drawString("Instance " + handler.getCurrentInstance(), 30 , 30);
         */
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.simulateSystem();
        if (!controller.isSimulationRunning()){
            timer.stop();
            System.out.println("Animation Ended");
            //System.out.println("Average waiting time is " + handler.calculateAverageTime());
            //handler.plotGraph();
        }
        this.repaint();
    }
}
