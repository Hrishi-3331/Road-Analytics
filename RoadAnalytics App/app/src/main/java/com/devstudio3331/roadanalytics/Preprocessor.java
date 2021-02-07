package com.devstudio3331.roadanalytics;

import java.util.LinkedList;
import java.util.Queue;

public class Preprocessor {

    private Queue<Double> xValues;
    private Queue<Double> yValues;
    private Queue<Double> zValues;
    private Queue<Double> nValues;
    private static final int WINDOW_LENGTH = 10;
    private double xSum;
    private double ySum;
    private double zSum;
    private double nSum;
    private double[] lastValues;

    public Preprocessor(){
        xValues = new LinkedList<Double>();
        yValues = new LinkedList<Double>();
        zValues = new LinkedList<Double>();
        nValues = new LinkedList<Double>();

        lastValues = new double[]{0, 0, 0, 0};
        xSum = 0;
        ySum = 0;
        zSum = 0;
        nSum = 0;

        for (int i = 0; i < 5; i++) {
            xValues.add(0.0);
            yValues.add(0.0);
            zValues.add(0.0);
            nValues.add(0.0);
        }
    }

    public void addToPreprocessor(double[] parameters){
        double xPar = parameters[0];
        double yPar = parameters[1];
        double zPar = parameters[2];
        double nPar = Math.sqrt((parameters[0]*parameters[0]) + (parameters[1]*parameters[1]) + (parameters[2]*parameters[2]));

        /* Derivative of parameters*/
        double xDer = (xPar - lastValues[0])/(0.2);
        double yDer = (yPar - lastValues[1])/(0.2);
        double zDer = (zPar - lastValues[2])/(0.2);
        double nDer = (nPar - lastValues[3])/(0.2);

        /* Storing last value for future derivative */
        lastValues[0] = xPar;
        lastValues[1] = yPar;
        lastValues[2] = zPar;
        lastValues[3] = nPar;

        /* Squaring Derivative */
        xDer = (xDer * xDer);
        yDer = (yDer * yDer);
        zDer = (zDer * zDer);
        nDer = (nDer * nDer);

        xSum += xDer;
        ySum += yDer;
        zSum += zDer;
        nSum += nDer;

        if (xValues.size() >= WINDOW_LENGTH){
            xSum -= xValues.remove();
            ySum -= yValues.remove();
            zSum -= zValues.remove();
            nSum -= nValues.remove();
        }

        xValues.add(xDer);
        yValues.add(yDer);
        zValues.add(zDer);
        nValues.add(nDer);

        //System.out.println(xDer + " " + yDer + " " + zDer);
    }

    public double[] getPreProcessedData(){
        double[] res = new double[4];
        if (xValues.size() < WINDOW_LENGTH){
            res[0] = -1;
            res[1] = -1;
            res[2] = -1;
            res[3] = -1;
        }
        else{
            res[0] = xSum/WINDOW_LENGTH;
            res[1] = ySum/WINDOW_LENGTH;
            res[2] = zSum/WINDOW_LENGTH;
            res[3] = nSum/WINDOW_LENGTH;
        }
        return res;
    }
}