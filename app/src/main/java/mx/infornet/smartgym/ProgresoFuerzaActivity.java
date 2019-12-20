package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProgresoFuerzaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progreso_fuerza);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_prog_fuerza);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Progreso");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //saca el numero de registros para generar el array con el numero de semanas
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM objetivo_fuerza",null);

        if (c.getCount() > 1){

            LineChart lineChart = findViewById(R.id.linechart_fuerza);
            lineChart.getDescription().setText("Objetivo incrementar fuerza");
            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);

            Cursor cp = db.rawQuery("SELECT * FROM objetivo_fuerza WHERE musculo = ?", new String[]{"Pecho"});

            if (cp.getCount()>0){
                LineDataSet dataSet = new LineDataSet(getDataPecho(), "Pecho");
                dataSet.setLineWidth(5f);
                dataSet.setCircleRadius(5f);
                dataSet.setValueTextSize(15f);
                int colorr = ContextCompat.getColor(getApplicationContext(), R.color.rojomaterial);
                dataSet.setColor(colorr);

                final String[] tiempo = new String[cp.getCount()];

                for(cp.moveToFirst(); !cp.isAfterLast(); cp.moveToNext()) {
                    tiempo[cp.getPosition()] = cp.getString(cp.getColumnIndex("fecha"));
                    System.out.println(tiempo[cp.getPosition()]);
                }

                ValueFormatter formatter = new ValueFormatter(){
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return tiempo[(int) value];
                    }
                };

                xAxis.setValueFormatter(formatter);

                dataSets.add(dataSet);
            }

            cp.close();

            Cursor ce = db.rawQuery("SELECT * FROM objetivo_fuerza WHERE musculo = ?", new String[]{"Espalda"});

            if (ce.getCount()>0){

                LineDataSet dataSet = new LineDataSet(getDataEspalda(), "Espalda");
                dataSet.setLineWidth(5f);
                dataSet.setCircleRadius(5f);
                dataSet.setValueTextSize(15f);
                int colorr = ContextCompat.getColor(getApplicationContext(), R.color.azulmaterial);
                dataSet.setColor(colorr);

                final String[] tiempo = new String[ce.getCount()];

                for (int i=0; i<tiempo.length;i++){
                    tiempo[i] = "Registro "+ i;
                    System.out.println(tiempo[i]);
                }

                /*for(ce.moveToFirst(); !ce.isAfterLast(); ce.moveToNext()) {
                    tiempo[ce.getPosition()] = ce.getString(ce.getColumnIndex("fecha"));
                    System.out.println(tiempo[ce.getPosition()] + " " + ce.getPosition());
                }*/

                ValueFormatter formatter = new ValueFormatter(){
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return tiempo[(int)value];
                    }
                };


                //xAxis.setValueFormatter(formatter);

                dataSets.add(dataSet);

            }

            ce.close();

            Cursor cb = db.rawQuery("SELECT * FROM objetivo_fuerza WHERE musculo=?", new String[]{"Biceps"});

            if (cb.getCount()>0){
                LineDataSet dataSet = new LineDataSet(getDataBiceps(), "Biceps");
                dataSet.setLineWidth(5f);
                dataSet.setCircleRadius(5f);
                dataSet.setValueTextSize(15f);
                int colorr = ContextCompat.getColor(getApplicationContext(), R.color.verdematerial);
                dataSet.setColor(colorr);

                final String[] tiempo = new String[cb.getCount()];

                for(cb.moveToFirst(); !cb.isAfterLast(); cb.moveToNext()) {
                    tiempo[cb.getPosition()] = cb.getString(cb.getColumnIndex("fecha"));
                    System.out.println(tiempo[cb.getPosition()]);
                }

                ValueFormatter formatter = new ValueFormatter(){
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return tiempo[(int) value];
                    }
                };

                xAxis.setValueFormatter(formatter);

                dataSets.add(dataSet);

            }
            cb.close();

            Cursor ct = db.rawQuery("SELECT * FROM objetivo_fuerza WHERE musculo=?", new String[]{"Triceps"});

            if (ct.getCount()>0){
                LineDataSet dataSet = new LineDataSet(getDataTriceps(), "Triceps");
                dataSet.setLineWidth(5f);
                dataSet.setCircleRadius(5f);
                dataSet.setValueTextSize(15f);
                int colorr = ContextCompat.getColor(getApplicationContext(), R.color.amarillomaterial);
                dataSet.setColor(colorr);

                final String[] tiempo = new String[ct.getCount()];

                for(ct.moveToFirst(); !ct.isAfterLast(); ct.moveToNext()) {
                    tiempo[ct.getPosition()] = ct.getString(ct.getColumnIndex("fecha"));
                    System.out.println(tiempo[ct.getPosition()]);
                }

                ValueFormatter formatter = new ValueFormatter(){
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return tiempo[(int) value];
                    }
                };

                xAxis.setValueFormatter(formatter);

                dataSets.add(dataSet);
            }
            ct.close();

            Cursor ca = db.rawQuery("SELECT * FROM objetivo_fuerza WHERE musculo=?", new String[]{"Abdomen"});

            if (ca.getCount()>0){
                LineDataSet dataSet = new LineDataSet(getDataAbdomen(), "Abdomen");
                dataSet.setLineWidth(5f);
                dataSet.setCircleRadius(5f);
                dataSet.setValueTextSize(15f);
                int colorr = ContextCompat.getColor(getApplicationContext(), android.R.color.holo_purple);
                dataSet.setColor(colorr);

                final String[] tiempo = new String[ca.getCount()];

                for(ca.moveToFirst(); !ca.isAfterLast(); ca.moveToNext()) {
                    tiempo[ca.getPosition()] = ca.getString(ca.getColumnIndex("fecha"));
                    System.out.println(tiempo[ca.getPosition()]);
                }

                ValueFormatter formatter = new ValueFormatter(){
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return tiempo[(int) value];
                    }
                };

                xAxis.setValueFormatter(formatter);

                dataSets.add(dataSet);
            }

            ca.close();

            Cursor cpi = db.rawQuery("SELECT * FROM objetivo_fuerza WHERE musculo=?", new String[]{"Pierna"});

            if (cpi.getCount()>0){
                LineDataSet dataSet = new LineDataSet(getDataPierna(), "Pierna");
                dataSet.setLineWidth(5f);
                dataSet.setCircleRadius(5f);
                dataSet.setValueTextSize(15f);
                int colorr = ContextCompat.getColor(getApplicationContext(), R.color.naranjamaterial);
                dataSet.setColor(colorr);

                final String[] tiempo = new String[cpi.getCount()];

                for(cpi.moveToFirst(); !cpi.isAfterLast(); cpi.moveToNext()) {
                    tiempo[cpi.getPosition()] = cpi.getString(cpi.getColumnIndex("fecha"));
                    System.out.println(tiempo[cpi.getPosition()]);
                }

                ValueFormatter formatter = new ValueFormatter(){
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return tiempo[(int) value];
                    }
                };

                xAxis.setValueFormatter(formatter);

                dataSets.add(dataSet);
            }
            cpi.close();


            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);

            YAxis yAxisLeft = lineChart.getAxisLeft();
            yAxisLeft.setGranularity(5f);
            yAxisLeft.setAxisMinimum(20f);
            //yAxisLeft.setAxisMaximum(100f);

            LineData data = new LineData(dataSets);
            lineChart.setData(data);
            lineChart.animateX(3500);
            lineChart.invalidate();


        } else {

            final Dialog dialog = new Dialog(ProgresoFuerzaActivity.this);
            dialog.setContentView(R.layout.alert_info_layout);
            dialog.setCancelable(false);
            TextView mensaje = dialog.findViewById(R.id.mensaje_info);
            TextView btnok = dialog.findViewById(R.id.positive_info);
            TextView btncancel = dialog.findViewById(R.id.neutral_btn_info);
            btncancel.setVisibility(View.GONE);
            mensaje.setText(R.string.mensaje_info_nodata);
            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.show();

            //Toast.makeText(getApplicationContext(), "Datos insuficientes", Toast.LENGTH_LONG).show();
        }

        c.close();
        db.close();
    }

    private ArrayList getDataPecho(){
        ArrayList<Entry> entriesPecho = new ArrayList<>();

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_fuerza WHERE musculo = ?";

            Cursor cursor = db.rawQuery(query, new String[]{"Pecho"});

            if (cursor.moveToFirst()){
                float i = 0;
                do {
                    float peso = cursor.getFloat(cursor.getColumnIndex("pesoLevantado"));
                    int repeticiones = cursor.getInt(cursor.getColumnIndex("repeticiones"));
                    float fuerza = (0.033f * repeticiones * peso) + peso;
                    entriesPecho.add(new Entry(i, fuerza));
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();


        } catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entriesPecho;
    }

    private ArrayList getDataEspalda(){
        ArrayList<Entry> entriesEsp = new ArrayList<>();

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_fuerza WHERE musculo = ?";

            Cursor cursor = db.rawQuery(query, new String[]{"Espalda"});

            if (cursor.moveToFirst()){
                float i = 0;
                do {
                    float peso = cursor.getFloat(cursor.getColumnIndex("pesoLevantado"));
                    int repeticiones = cursor.getInt(cursor.getColumnIndex("repeticiones"));
                    float fuerza = (0.033f * repeticiones * peso) + peso;
                    entriesEsp.add(new Entry(i, fuerza));
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();


        } catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entriesEsp;
    }

    private ArrayList getDataBiceps(){
        ArrayList<Entry> entriesBic = new ArrayList<>();

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_fuerza WHERE musculo = ?";

            Cursor cursor = db.rawQuery(query, new String[]{"Biceps"});

            if (cursor.moveToFirst()){
                float i = 0;
                do {
                    float peso = cursor.getFloat(cursor.getColumnIndex("pesoLevantado"));
                    int repeticiones = cursor.getInt(cursor.getColumnIndex("repeticiones"));
                    float fuerza = (0.033f * repeticiones * peso) + peso;
                    entriesBic.add(new Entry(i, fuerza));
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();


        } catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entriesBic;
    }

    private ArrayList getDataTriceps(){
        ArrayList<Entry> entriesTric = new ArrayList<>();

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_fuerza WHERE musculo = ?";

            Cursor cursor = db.rawQuery(query, new String[]{"Triceps"});

            if (cursor.moveToFirst()){
                float i = 0;
                do {
                    float peso = cursor.getFloat(cursor.getColumnIndex("pesoLevantado"));
                    int repeticiones = cursor.getInt(cursor.getColumnIndex("repeticiones"));
                    float fuerza = (0.033f * repeticiones * peso) + peso;
                    entriesTric.add(new Entry(i, fuerza));
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();


        } catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entriesTric;
    }

    private ArrayList getDataAbdomen(){
        ArrayList<Entry> entriesAbd = new ArrayList<>();

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_fuerza WHERE musculo = ?";

            Cursor cursor = db.rawQuery(query, new String[]{"Abdomen"});

            if (cursor.moveToFirst()){
                float i = 0;
                do {
                    float peso = cursor.getFloat(cursor.getColumnIndex("pesoLevantado"));
                    int repeticiones = cursor.getInt(cursor.getColumnIndex("repeticiones"));
                    float fuerza = (0.033f * repeticiones * peso) + peso;
                    entriesAbd.add(new Entry(i, fuerza));
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();


        } catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entriesAbd;
    }

    private ArrayList getDataPierna(){
        ArrayList<Entry> entriesPierna = new ArrayList<>();

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_fuerza WHERE musculo = ?";

            Cursor cursor = db.rawQuery(query, new String[]{"Pierna"});

            if (cursor.moveToFirst()){
                float i = 0;
                do {
                    float peso = cursor.getFloat(cursor.getColumnIndex("pesoLevantado"));
                    int repeticiones = cursor.getInt(cursor.getColumnIndex("repeticiones"));
                    float fuerza = (0.033f * repeticiones * peso) + peso;
                    entriesPierna.add(new Entry(i, fuerza));
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();


        } catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entriesPierna;
    }
}
