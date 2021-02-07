package com.devstudio3331.roadanalytics;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import org.tensorflow.lite.Interpreter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private Interpreter interpreter;
    private Sensor gyroscope_sensor;
    private SensorManager manager;
    private TextView gyX;
    private TextView gyY;
    private TextView gyZ;
    private TextView qualityIndex;
    private Preprocessor preprocessor;
    private LocationManager locationManager;
    private boolean locationAvailable;
    private boolean locationTracking;
    private boolean recording;
    private FileOutputStream stream;
    double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView image = (ImageView) findViewById(R.id.driving_gif);
        Glide.with(this).load(R.drawable.driving).into(image);

        gyX = (TextView) findViewById(R.id.xValue2);
        gyY = (TextView) findViewById(R.id.yValue2);
        gyZ = (TextView) findViewById(R.id.zValue2);
        qualityIndex = (TextView) findViewById(R.id.rqi);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope_sensor = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        preprocessor = new Preprocessor();

        locationAvailable = false;
        locationTracking = false;
        longitude = 0.0;
        latitude = 0.0;
        recording = false;
        stream = null;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET}, 10 );
        }

        try {
            interpreter = new Interpreter(loadModelFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.registerListener(MainActivity.this, gyroscope_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /** Permission Result Handling */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10){
            for (int i = 0; i < grantResults.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need to grant permissions to use this app.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    /** Name of file for recording data */

    private String getFileName() {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        return s.format(new Date());
    }

    public void toggleRecording(View view){
        ImageButton button = (ImageButton) view;
        switch (button.getContentDescription().toString()) {
            case "on":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_on));
                button.setContentDescription("off");
                stopRecording();
                break;

            case "off":
                if (startRecording()) {
                    button.setImageDrawable(getResources().getDrawable(R.drawable.button_off));
                    button.setContentDescription("on");
                }
                break;
        }
    }

    private boolean startRecording(){
        if (!locationTracking){
            Toast.makeText(this, "Please start Road Analytics to record data", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!locationAvailable){
            Toast.makeText(this, "Please wait for GPS to sync with satellite", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "Road Analytics Database");
            if (!directory.exists()){
                directory.mkdir();
            }
            File file = new File(directory, getFileName() + ".txt");
            try {
                file.createNewFile();
                stream = new FileOutputStream(file);
                recording = true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to start recording because " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(this, "Recording Started !", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private boolean stopRecording(){
        if (recording){
            recording = false;
            try {
                stream.flush();
                stream.close();
            }catch (Exception e){
                Toast.makeText(this, "File Saving Failed!", Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(this, "File Saved Successfully!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void record(String data){
        try {
            stream.write(data.getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error while writing", Toast.LENGTH_SHORT).show();
        }
    }

    /** Machine Learning Code */

    private MappedByteBuffer loadModelFile() throws IOException{
        AssetFileDescriptor descriptor = MainActivity.this.getAssets().openFd("model2.tflite");
        FileInputStream inputStream = new FileInputStream(descriptor.getFileDescriptor());
        FileChannel channel = inputStream.getChannel();
        long startOffset = descriptor.getStartOffset();
        long length = descriptor.getLength();
        return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }

    private String getInferences(double[] in) {
        float[] input = new float[4];
        input[0] = (float) in[0];
        input[1] = (float) in[1];
        input[2] = (float) in[2];
        input[3] = (float) in[3];
        float[][] output = new float[1][3];
        interpreter.run(input, output);
        double res = 0;
        double pX = Double.valueOf(String.valueOf(output[0][0]));
        double pY = Double.valueOf(String.valueOf(output[0][1]));
        double pZ = Double.valueOf(String.valueOf(output[0][2]));
        if (pX > pY && pX > pZ){
            res = 0;
        }
        else if (pY > pX && pY > pZ){
            res = 1;
        }
        else res = 2;
        return "" + pX + "," + pY + "," + pZ + "," + res;
    }

    private void runModel(double[] parameters) {
        setVisualData(parameters);
        preprocessor.addToPreprocessor(parameters);
        parameters = preprocessor.getPreProcessedData();
        if (parameters[0] > 0) {
            String res = getInferences(parameters);
            qualityIndex.setText(res.split(",")[3]);
            if (recording){
                record(getFileName() + "," + res + "," + latitude + "," + longitude + "\n");
            }
        } else {
            qualityIndex.setText("Estimating..");
        }
    }

    private void setVisualData(double[] parameters) {
        TextView[] views = new TextView[]{gyX, gyY, gyZ};
        for (int i = 0; i < 3; i++) {
            views[i].setText(String.format("%.4f", parameters[i]));
        }
    }

    private double[] testManually(int par) {
        double[] test_a_1 = new double[]{0.0015455, 0.0030355, 0.004189};
        double[] test_a_2 = new double[]{0.0016334, 0.003036, 0.0041945};
        double[] test_a_3 = new double[]{0.0016379, 0.003084, 0.0042368};
        double[] test_a_4 = new double[]{0.0017275, 0.0029981, 0.0041087};
        double[] test_a_5 = new double[]{0.0016954, 0.0030284, 0.0033091};

        double[] test_b_1 = new double[]{0.00073334, 0.0035346, 0.000070908};
        double[] test_b_2 = new double[]{0.00079992, 0.0035071, 0.00010585};
        double[] test_b_3 = new double[]{0.00069838, 0.0034833, 0.000084112};
        double[] test_b_4 = new double[]{0.00067157, 0.0038325, 0.00012217};
        double[] test_b_5 = new double[]{0.00082823, 0.0035217, 0.00012489};

        int random = new Random().nextInt(4);
        double[][] good_data = new double[][]{test_a_1, test_a_2, test_a_3, test_a_4, test_a_5};
        double[][] bad_data = new double[][]{test_b_1, test_b_2, test_b_3, test_b_4, test_b_5};
        if (par == 0) return good_data[random];
        else return bad_data[random];
    }

    /** Sensor Listener functions */

    @Override
    public void onSensorChanged(SensorEvent event) {
        double[] params = new double[3];
        params[0] = (double) event.values[0];
        params[1] = (double) event.values[1];
        params[2] = (double) event.values[2];
        runModel(params);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /** Location listener functions */

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(!locationAvailable){
            Toast.makeText(this, "Location is now available. You may start recording.", Toast.LENGTH_SHORT).show();
        }
        locationAvailable = true;
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);
    }

    /** Location Tracking Button */

    @SuppressLint("MissingPermission")
    public void startLocationTracking(View view) {
        ImageButton button = (ImageButton) view;
        switch (button.getContentDescription().toString()) {
            case "on":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_on));
                button.setContentDescription("off");
                locationTracking = false;
                locationManager.removeUpdates(MainActivity.this);
                break;

            case "off":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_off));
                button.setContentDescription("on");
                locationTracking = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                break;
        }
    }
}