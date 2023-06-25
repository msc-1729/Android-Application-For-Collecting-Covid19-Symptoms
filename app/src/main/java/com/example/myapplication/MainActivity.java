package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    float respiration_value = 0f;
    float heart_rate_value = 0f;
    TextView rr_value;
//    private final Activity activity;
//    MainActivity(Activity _activity) {
//        activity = _activity;
//    }
    public static final String TABLE_NAME = "Mahamkali";
    public static final String ID = "ID";
    public static final String RESPIRATORY_RATE = "RESPIRATION_RATE";
    public static final String HEART_RATE = "HEART_RATE";
    public static final String NAUSEA = "NAUSEA";
    public static final String HEAD_ACHE = "HEAD_ACHE";
    public static final String DIARRHEA = "DIARRHEA";
    public static final String SOAR_THROAT = "SOAR_THROAT";
    public static final String FEVER = "FEVER";
    public static final String MUSCLE_ACHE = "MUSCLE_ACHE";
    public static final String LOSS_OF_SMELL_TASTE = "LOSS_OF_SMELL_TASTE";
    public static final String COUGH = "COUGH";
    public static final String SHORT_BREATH = "SHORT_BREATH";
    public static final String FEEL_TIRED = "FEEL_TIRED";
    private Map<String, Float> createMap() {
        Map<String,Float> Symptoms_Map = new HashMap<String,Float>();
//        Symptoms_Map.put("HeartRate", (float) 0.0);
//        Symptoms_Map.put("BreathRate", (float) 0.0);
        Symptoms_Map.put("Nausea", (float) 0.0);
        Symptoms_Map.put("Headache", (float) 0.0);
        Symptoms_Map.put("Diarrhea",(float) 0.0);
        Symptoms_Map.put("Soar Throat",(float) 0.0);
        Symptoms_Map.put("Fever",(float) 0.0);
        Symptoms_Map.put("Muscle Ache", (float) 0.0);
        Symptoms_Map.put("Loss of smell or taste", (float) 0.0);
        Symptoms_Map.put("Cough", (float) 0.0);
        Symptoms_Map.put("Shortness of Breath", (float) 0.0);
        Symptoms_Map.put("Feeling Tired", (float) 0.0);
        return Symptoms_Map;
    }

    Map<String, Float> Symptoms_Map = createMap();
    private List<String> Symptoms_List = new ArrayList<>(Symptoms_Map.keySet());
//    public DataBase db;

    List<Double> accelValuesX = new ArrayList<Double>();
    List<Double>  accelValuesY = new ArrayList<Double>();
    List<Double>  accelValuesZ = new ArrayList<Double>();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    SensorManager sensorManager;
    Sensor accelerometer;
    CountDownTimer timer;
    ProgressBar mProgressBar;
    int per = 30;
    calculation_average ca;
    private final Camera camera = new Camera(this);
    private final int RCC = 0;
    public static final int MTV = 3;
    public static final int HRF = 1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @SuppressLint("HandlerLeak")
    private final Handler mainHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == HRF) {
                ((TextView) findViewById(R.id.hr_value)).setText(msg.obj.toString());
//                Symptoms_Map.put("HeartRate", Float.valueOf(msg.obj.toString()));
                heart_rate_value = Float.valueOf(msg.obj.toString());
                findViewById(R.id.Camera_View).setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int i = 0;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                RCC);
        FindValue finder = new FindValue(this, mainHandler);

        SensorEventListener accelListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int acc) { }


            public void onSensorChanged(SensorEvent event) {
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];

                accelValuesX.add(x);
                accelValuesY.add(y);
                accelValuesZ.add(z);
                ca.addData((float) z);
                System.out.println(accelValuesZ.size());

            }
        };


        timer = new CountDownTimer(45000, 1000) {
            int i = 0;

            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                i++;
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                i++;
                respiration_value =  ca.getPeakCount();
                System.out.println(respiration_value);
                respiration_value = respiration_value * 4f/3f;
                rr_value.setText(String.valueOf(respiration_value));
//                Symptoms_Map.put("BreathRate", respiration_value);
            }
        };


        rr_value = findViewById(R.id.rr_value);
        final Button respiratory_button = (Button) findViewById(R.id.resRateButton);
        final Button heart_rate_button = (Button) findViewById(R.id.heartRateButton);
        final Button upload_sign_button = (Button) findViewById(R.id.upload_signs);
        respiratory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//            Intent intent_to_res = new Intent(MainActivity.this,Respiratory.class);
            timer.start();
            ca = new calculation_average(per);
            sensorManager.registerListener(accelListener, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);

            }
        });
        upload_sign_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.println(Log.ERROR, "data", "permission denied to write data");
                    }
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.println(Log.ERROR, "data", "permission denied to read data");
                    }

                    db = openOrCreateDatabase("myDB.db", Context.MODE_PRIVATE,null);
                    db.beginTransaction();
                    try {
                        String createTableStatement = "CREATE TABLE " + TABLE_NAME + "( "
                                + HEART_RATE + " FLOAT, "
                                + RESPIRATORY_RATE + " FLOAT, "
                                + NAUSEA + " FLOAT, "
                                + HEAD_ACHE + " FLOAT, "
                                + DIARRHEA + " FLOAT, "
                                + SOAR_THROAT + " FLOAT, "
                                + FEVER + " FLOAT, "
                                + MUSCLE_ACHE + " FLOAT, "
                                + LOSS_OF_SMELL_TASTE + " FLOAT, "
                                + COUGH + " FLOAT, "
                                + SHORT_BREATH + " FLOAT, "
                                + FEEL_TIRED + " FLOAT" + " )";
                        db.execSQL("DROP TABLE IF EXISTS Mahamkali");
                        db.execSQL(createTableStatement );

                        db.setTransactionSuccessful();
                    }
                    catch (SQLiteException e) {

                    }
                    finally {
                        db.endTransaction();
                    }
                }catch (SQLException e){

                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }


                try {

                    db.execSQL( "insert or replace into Mahamkali(RESPIRATION_RATE,HEART_RATE) values ('"+respiration_value+"', '"+heart_rate_value+"' );" );

                }
                catch (SQLiteException e) {
                    //report problem
                }
                finally{
//                    db.endTransaction();
                }


            }
        });
        heart_rate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                TextureView camview = findViewById(R.id.Camera_View);
                SurfaceTexture previewSurfaceTexture = camview.getSurfaceTexture();
                Surface previewSurface = new Surface(previewSurfaceTexture);
                camera.start(previewSurface);
                finder.measurehr(camview, camera);
            }
        });
        final Button  symptoms_button = (Button) findViewById(R.id.symptoms_upload);

        symptoms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.symptoms_main);
                Spinner dropdown_button = findViewById(R.id.symptoms_dropdown);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_item, Symptoms_List
                );
                dropdown_button.setAdapter(adapter);
                final Button save_symptoms = findViewById(R.id.symptoms_save);
                Spinner symptom_drop = findViewById(R.id.symptoms_dropdown);
                RatingBar rating_bar = (RatingBar) findViewById(R.id.symptoms_ratings);
                rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        Symptoms_Map.put(symptom_drop.getSelectedItem().toString(), rating);
                    }
                });
                final Button symptoms_save_button = (Button) findViewById(R.id.symptoms_save);
                symptoms_save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ContentValues content = new ContentValues();
                            content.put(HEART_RATE, heart_rate_value);
                            content.put(RESPIRATORY_RATE, respiration_value);

                            content.put(MUSCLE_ACHE, Symptoms_Map.get("Muscle Ache"));
                            content.put(LOSS_OF_SMELL_TASTE, Symptoms_Map.get("Loss of smell or taste"));
                            content.put(COUGH, Symptoms_Map.get("Cough"));
                            content.put(SHORT_BREATH, Symptoms_Map.get("Shortness of Breath"));
                            content.put(NAUSEA, Symptoms_Map.get("Nausea"));
                            content.put(HEAD_ACHE, Symptoms_Map.get("Headache"));
                            content.put(SOAR_THROAT, Symptoms_Map.get("Soar Throat"));
                            content.put(FEVER, Symptoms_Map.get("Fever"));
                            content.put(DIARRHEA, Symptoms_Map.get("Diarrhea"));

                            content.put(FEEL_TIRED, Symptoms_Map.get("Feeling Tired"));
                            //perform your database operations here ...
                            db.update(TABLE_NAME, content, HEART_RATE + "=?", new String[]{String.valueOf(heart_rate_value)});
//                            db.execSQL( "insert or replace into Mahamkali(MUSCLE_ACHE,LOSS_OF_SMELL_TASTE,COUGH,SHORT_BREATH,NAUSEA,HEAD_ACHE,SOAR_THROAT,FEVER,DIARRHEA,FEEL_TIRED) " +
//                                    "values ( '"+Symptoms_Map.get("Muscle Ache")+"','"+Symptoms_Map.get("Loss of smell or taste")+"'," +
//                                    "'"+Symptoms_Map.get("Cough")+"','"+Symptoms_Map.get("Shortness of Breath")+"'," +
//                                    "'"+Symptoms_Map.get("Nausea")+"','"+Symptoms_Map.get("Headache")+"'," +
//                                    "'"+Symptoms_Map.get("Soar Throat")+"','"+Symptoms_Map.get("Fever")+"'," +
//                                    "'"+Symptoms_Map.get("Diarrhea")+"','"+Symptoms_Map.get("Feeling Tired")+"' );" );
//                            db.setTransactionSuccessful(); //commit your changes
                        }
                        catch (SQLiteException e) {
                            //report problem
                        }
                        finally{
//                            db.endTransaction();
                    }

                    }
                });
                symptom_drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((TextView) findViewById(R.id.symptom_name)).setText(Symptoms_List.get(i));
                        rating_bar.setRating(Symptoms_Map.get(Symptoms_List.get(i)));
                    }

                    public void onNothingSelected(AdapterView<?> adapterView) {
                        return;
                    }
                });

            }
        });

    }
}