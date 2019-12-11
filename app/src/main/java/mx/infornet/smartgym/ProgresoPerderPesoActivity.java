package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ProgresoPerderPesoActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progreso_perder_peso);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_prog_peso);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Progreso");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lineChart = findViewById(R.id.chart_peso);

        LineDataSet dataSet = new LineDataSet(getData(), "Peso perdido");

        dataSet.setColor(R.color.colorPrimary);
        dataSet.setValueTextColor(R.color.colorPrimaryDark);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //saca el numero de registros para generar el array con el numero de semanas
        ConexionSQLiteHelperPeso  conn = new ConexionSQLiteHelperPeso(getApplicationContext(), "objetivo_perder_peso", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM objetivo_perder_peso",null);

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

    }

    private ArrayList getData(){
        ArrayList<Entry> entries = new ArrayList<>();

        ConexionSQLiteHelperPeso  conn = new ConexionSQLiteHelperPeso(getApplicationContext(), "objetivo_perder_peso", null, 1);
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
