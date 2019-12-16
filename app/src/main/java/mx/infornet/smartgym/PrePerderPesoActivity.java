package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PrePerderPesoActivity extends AppCompatActivity {

    private TextInputEditText peso_inicial, estatura, tiempo, meta;
    private JsonObjectRequest request_save_perder_peso;
    private RequestQueue queue_save_perder_peso;
    private Dialog dialog_ok_perder_peso;
    private ProgressBar progressBar;
    private String token, token_type;

    private int ID_PENDING_AVANCE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_perder_peso);

        peso_inicial = findViewById(R.id.peso_inicial);
        estatura = findViewById(R.id.estatura);
        tiempo = findViewById(R.id.tiempo);
        meta = findViewById(R.id.metaKG);
        MaterialButton btn_guardar = findViewById(R.id.btn_save_bajar_peso);
        progressBar = findViewById(R.id.prog_bar_peso_init);
        progressBar.setVisibility(View.GONE);

        queue_save_perder_peso = Volley.newRequestQueue(getApplicationContext());

        dialog_ok_perder_peso = new Dialog(this);

        ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getApplicationContext(), "usuarios", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM usuarios";

            Cursor cursor = db.rawQuery(query, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                token = cursor.getString(cursor.getColumnIndex("token"));
                token_type = cursor.getString(cursor.getColumnIndex("tokenType"));
                //objetivo = cursor.getString(cursor.getColumnIndex("objetivo"));
            }

        }catch (Exception e){

            Toast toast = Toast.makeText(getApplicationContext(), "Error: "+  e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String peso = peso_inicial.getText().toString();
                String estat = estatura.getText().toString();
                final String time = tiempo.getText().toString();
                String goal = meta.getText().toString();


                if (TextUtils.isEmpty(peso)){
                    peso_inicial.setError("Introduce tu peso");
                    peso_inicial.requestFocus();
                } else if (TextUtils.isEmpty(estat)){
                    estatura.setError("Introduce tu estatura");
                    estatura.requestFocus();
                } else if (TextUtils.isEmpty(time)){
                    tiempo.setError("Introduce el tiempo de la meta");
                    tiempo.requestFocus();
                } else if (TextUtils.isEmpty(goal)){
                    meta.setError("Introduce tu meta en Kg");
                    meta.requestFocus();
                } else {

                    progressBar.setVisibility(View.VISIBLE);

                    final double peso_decimal = Double.parseDouble(peso);
                    final double estatura_decimal = Double.parseDouble(estat);
                    final int tiempo_int = Integer.parseInt(time);
                    final int meta_int = Integer.parseInt(goal);

                    JSONObject json_save = new JSONObject();

                    try {
                        json_save.put("estatura", estatura_decimal);
                        json_save.put("peso_inicial", peso_decimal);
                        json_save.put("meta", meta_int);
                        json_save.put("tiempo", tiempo_int);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), "Chido !", Toast.LENGTH_LONG).show();

                    request_save_perder_peso = new JsonObjectRequest(Request.Method.POST, Config.POST_PERDER_PESO_URL, json_save, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            progressBar.setVisibility(View.GONE);

                            Log.d("res_objetivo_post", response.toString());

                            if (response.has("message")){
                                String mensj = null;
                                try {
                                    mensj = response.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(getApplicationContext(), mensj, Toast.LENGTH_LONG).show();

                                //fecha actual
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String fecha_actual = df.format(calendar.getTime());

                                //fecha final
                                //se hace el calculo dependiendo del tiempo que puso el miembro
                                Calendar caf = Calendar.getInstance();
                                caf.add(Calendar.MONTH, tiempo_int);
                                SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String fecha_final = sf.format(caf.getTime());

                                //se guardan los datos del usuario para bajar de peso en una base de datos
                                ConexionSQLiteHelperPeso  conn = new ConexionSQLiteHelperPeso(getApplicationContext(), "objetivo_perder_peso", null, 2);
                                SQLiteDatabase db = conn.getWritableDatabase();

                                ContentValues datos = new ContentValues();

                                datos.put("estatura", estatura_decimal);
                                datos.put("meta", meta_int);
                                datos.put("tiempo", tiempo_int);
                                datos.put("peso", peso_decimal);
                                datos.put("fecha", fecha_actual);
                                datos.put("fechaFinal", fecha_final);

                                db.insert("objetivo_perder_peso", null, datos);
                                db.close();

                                Log.d("datos peso save", "se guardaron los datos en la base de datos perder peso");


                                switch (tiempo_int){
                                    case 1:
                                        boolean alarmUP = (PendingIntent.getBroadcast(getApplicationContext(), ID_PENDING_AVANCE, new Intent(getApplicationContext(), BroadcastAvancePerderPeso.class), PendingIntent.FLAG_NO_CREATE) != null);

                                        if (alarmUP){
                                            Log.d("alarma_avance", "Alarma YA activada");
                                        } else {

                                            Calendar c = Calendar.getInstance();
                                            c.add(Calendar.WEEK_OF_MONTH, 1);
                                            c.set(Calendar.HOUR, 12);

                                            Log.d("sig semana", c.getTime().toString());

                                            Intent in = new Intent(getApplicationContext(), BroadcastAvancePerderPeso.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ID_PENDING_AVANCE, in, PendingIntent.FLAG_UPDATE_CURRENT);

                                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                                            Log.d("alarma_avance", "alarma iniciada");
                                            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 7*1440*60000, pendingIntent);
                                        }
                                        break;
                                    case 2:
                                        boolean alarmUP1 = (PendingIntent.getBroadcast(getApplicationContext(), ID_PENDING_AVANCE, new Intent(getApplicationContext(), BroadcastAvancePerderPeso.class), PendingIntent.FLAG_NO_CREATE) != null);

                                        if (alarmUP1){
                                            Log.d("alarma_avance", "Alarma YA activada");
                                        } else {

                                            Calendar c = Calendar.getInstance();
                                            c.add(Calendar.WEEK_OF_MONTH, 2);
                                            c.set(Calendar.HOUR, 12);

                                            Log.d("sig semana", c.getTime().toString());

                                            Intent in = new Intent(getApplicationContext(), BroadcastAvancePerderPeso.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ID_PENDING_AVANCE, in, PendingIntent.FLAG_UPDATE_CURRENT);

                                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                                            Log.d("alarma_avance", "alarma iniciada");
                                            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 7*1440*60000, pendingIntent);
                                        }

                                        break;

                                    case 3:

                                        boolean alarmUP2 = (PendingIntent.getBroadcast(getApplicationContext(), ID_PENDING_AVANCE, new Intent(getApplicationContext(), BroadcastAvancePerderPeso.class), PendingIntent.FLAG_NO_CREATE) != null);

                                        if (alarmUP2){
                                            Log.d("alarma_avance", "Alarma YA activada");
                                        } else {

                                            Calendar c = Calendar.getInstance();
                                            c.add(Calendar.WEEK_OF_MONTH, 2);
                                            c.set(Calendar.HOUR, 12);

                                            Log.d("sig semana", c.getTime().toString());

                                            Intent in = new Intent(getApplicationContext(), BroadcastAvancePerderPeso.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ID_PENDING_AVANCE, in, PendingIntent.FLAG_UPDATE_CURRENT);

                                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                                            Log.d("alarma_avance", "alarma iniciada");
                                            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 7*1440*60000, pendingIntent);
                                        }

                                        break;
                                }

                                ShowOkPerderPeso(peso_decimal, meta_int, tiempo_int);

                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressBar.setVisibility(View.GONE);

                            Log.d("err_res_objetivo_post", error.toString());

                            if (error instanceof TimeoutError) {
                                Toast.makeText(getApplicationContext(),
                                        "Oops. Timeout error!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders()throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", token_type+" "+token);
                            return headers;
                        }
                    };

                    queue_save_perder_peso.add(request_save_perder_peso);

                }
            }
        });

    }

    private void ShowOkPerderPeso(double peso, double meta, int tiempo){
        ImageView close;
        TextView tv_meta, tv_semana, tv_termino, tv_inicio, tv_pedir_datos;
        MaterialButton btn;

        dialog_ok_perder_peso.setContentView(R.layout.info_perder_peso_layout);

        tv_meta = dialog_ok_perder_peso.findViewById(R.id.tv_meta_info_perder_peso);
        tv_semana = dialog_ok_perder_peso.findViewById(R.id.tv_semana_info_perder_peso);
        tv_termino = dialog_ok_perder_peso.findViewById(R.id.tv_termino_info_perder_peso);
        tv_inicio = dialog_ok_perder_peso.findViewById(R.id.tv_inicio_info_perder_peso);
        tv_pedir_datos = dialog_ok_perder_peso.findViewById(R.id.tv_pedir_datos_info_perder_peso);
        close = dialog_ok_perder_peso.findViewById(R.id.btn_close_info_perder_peso);
        btn = dialog_ok_perder_peso.findViewById(R.id.btn_ok_info_peso);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_ok_perder_peso.dismiss();

                startActivity(new Intent(PrePerderPesoActivity.this, MainActivity.class));
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_ok_perder_peso.dismiss();

                startActivity(new Intent(PrePerderPesoActivity.this, MainActivity.class));
                finish();

            }
        });

        //se obtiene la fecha de inicio que es la actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fecha_actual = df.format(calendar.getTime());



        DecimalFormat format = new DecimalFormat("#.00");
        double peso_perder_semana;
        String perderkgsem = "";
        String perderkg = meta + " Kg";


        String ultimo = "";

        switch (tiempo){
            case 1:
                peso_perder_semana = meta / 4;
                Log.d("semana", String.valueOf(peso_perder_semana));
                perderkgsem = format.format(peso_perder_semana)+" Kg";
                ultimo = "Cada semana te estaremos pidiendo tu avance para medir tu progreso. Acercate con tu coach para que te asigne las rutinas y alimentación adecuadas para cumplir tu objetivo.";

                //se obtiene la fecha de un mes despues;
                Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.MONTH, 1);
                //calendar1.add(Calendar.WEEK_OF_MONTH,1);
                SimpleDateFormat dff = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fecha_final = dff.format(calendar1.getTime());
                tv_termino.setText(fecha_final);
                break;
            case 2:
                peso_perder_semana = meta / 8;
                Log.d("semana", String.valueOf(peso_perder_semana));
                perderkgsem = format.format(peso_perder_semana)+" Kg";
                ultimo = "Cada 2 semanas te estaremos pidiendo tu avance para medir tu progreso. Acercate con tu coach para que te asigne las rutinas y alimentación adecuadas para cumplir tu objetivo.";

                //se obtiene la fecha de un mes despues;
                calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.MONTH, 2);
                //calendar1.add(Calendar.WEEK_OF_MONTH,1);
                SimpleDateFormat dff2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fecha_final2 = dff2.format(calendar1.getTime());

                tv_termino.setText(fecha_final2);

                break;
            case 3:
                peso_perder_semana = meta / 12;
                Log.d("semana", String.valueOf(peso_perder_semana));
                perderkgsem = format.format(peso_perder_semana)+" Kg";
                ultimo = "Cada 2 semanas te estaremos pidiendo tu avance para medir tu progreso. Acercate con tu coach para que te asigne las rutinas y alimentación adecuadas para cumplir tu objetivo.";

                //se obtiene la fecha de un mes despues;
                calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.MONTH, 3);
                //calendar1.add(Calendar.WEEK_OF_MONTH,1);
                SimpleDateFormat dff3 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fecha_final3 = dff3.format(calendar1.getTime());
                tv_termino.setText(fecha_final3);
                break;
        }

        tv_pedir_datos.setText(ultimo);
        tv_inicio.setText(fecha_actual);

        tv_meta.setText(perderkg);
        tv_semana.setText(perderkgsem);


        dialog_ok_perder_peso.setCancelable(false);
        dialog_ok_perder_peso.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog_ok_perder_peso.show();

    }
}
