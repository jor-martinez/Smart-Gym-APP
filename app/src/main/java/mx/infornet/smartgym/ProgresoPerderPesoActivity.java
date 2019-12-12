package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProgresoPerderPesoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progreso_perder_peso);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_prog_peso);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Progreso");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //grafica de barras IMC

        BarChart barChart = findViewById(R.id.barchart_peso);
        BarDataSet barDataSet = new BarDataSet(getDataBar(), "Indice de masa corporal");
        barDataSet.setBarBorderWidth(0.9f);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);

        LimitLine ll1 = new LimitLine(18.5f, "normal");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        LimitLine ll2 = new LimitLine(25.0f, "Peso superior al normal");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll2.setTextSize(10f);
        LimitLine ll3 = new LimitLine(30.0f, "Obesidad");
        ll3.setLineWidth(4f);
        ll3.enableDashedLine(10f, 10f, 0f);
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll3.setTextSize(10f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(10f);
        leftAxis.setAxisMaximum(45f);
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.addLimitLine(ll3);

        YAxis yAxisRightb = barChart.getAxisRight();
        yAxisRightb.setEnabled(false);

        XAxis xAxisb = barChart.getXAxis();
        xAxisb.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] prog = new String[]{"Inicio", "Fin"};
        IndexAxisValueFormatter formatterb = new IndexAxisValueFormatter(prog);
        xAxisb.setGranularity(1f);
        xAxisb.setValueFormatter(formatterb);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.animateXY(5000, 5000);
        barChart.invalidate();

        //saca el numero de registros para generar el array con el numero de semanas
        ConexionSQLiteHelperPeso  conn = new ConexionSQLiteHelperPeso(getApplicationContext(), "objetivo_perder_peso", null, 2);
        SQLiteDatabase db = conn.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM objetivo_perder_peso",null);

        if (c.getCount() > 1){

            //grafica de puntos, progreso peso
            LineChart lineChart = findViewById(R.id.chart_peso);

            LineDataSet dataSet = new LineDataSet(getData(), "Peso perdido");

            dataSet.setColor(R.color.colorPrimary);
            dataSet.setValueTextColor(R.color.colorPrimaryDark);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);




            final String[] tiempo = new String[c.getCount()];

            for (int i=0; i<tiempo.length;i++){
                tiempo[i] = "Semana "+ i;
                System.out.println(tiempo[i]);
            }

            ValueFormatter formatter = new ValueFormatter(){
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return tiempo[(int) value];
                }
            };

            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(formatter);

            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);

            YAxis yAxisLeft = lineChart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            LineData data = new LineData(dataSet);
            lineChart.setData(data);
            lineChart.animateX(3500);
            lineChart.invalidate();


        } else {
            Toast.makeText(getApplicationContext(), "Datos insuficientes", Toast.LENGTH_LONG).show();
        }


    }

    private ArrayList getDataBar(){
        ArrayList<BarEntry> entries = new ArrayList<>();

        ConexionSQLiteHelperPeso  conn = new ConexionSQLiteHelperPeso(getApplicationContext(), "objetivo_perder_peso", null, 2);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_perder_peso";

            Cursor c = db.rawQuery(query, null);

            c.moveToFirst();
            float peso_inicial = c.getFloat(c.getColumnIndex("peso"));
            float estatura_inicial = c.getFloat(c.getColumnIndex("estatura"));
            float imc_ini = peso_inicial / (estatura_inicial*estatura_inicial);

            entries.add(new BarEntry(0,imc_ini));

            c.moveToLast();
            float peso_final = c.getFloat(c.getColumnIndex("peso"));
            float estatura_final = c.getFloat(c.getColumnIndex("estatura"));
            double imc_fin = peso_final / (estatura_final*estatura_final);

            entries.add(new BarEntry(1, Float.valueOf(imc_ini)));

            System.out.println("imc inicial" + imc_ini);
            System.out.println("imc final" + imc_fin);

            c.close();

        }catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entries;
    }

    private ArrayList getData(){
        ArrayList<Entry> entries = new ArrayList<>();

        ConexionSQLiteHelperPeso  conn = new ConexionSQLiteHelperPeso(getApplicationContext(), "objetivo_perder_peso", null, 2);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_perder_peso";
            //String imagenUsuario = null;

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()){
                float i = 0;
                do {
                    float peso = cursor.getFloat(cursor.getColumnIndex("peso"));
                    entries.add(new Entry(i, peso));
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();


        } catch (Exception e) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        return entries;
    }
}
