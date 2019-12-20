package mx.infornet.smartgym;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    private View myView;
    private String nombreUsuario, token, token_type, nombre_gym, apellidos, objetivo, fecha_fin="01/01/1999";
    private TextView txt_nombre, txt_gym_valor, txt_dias_restantes, txt_status, status_color, txt_fecha_expira;
    private RelativeLayout btn_rutinas, btn_alim, btn_progreso;
    private StringRequest request_pago;
    private RequestQueue queue_pago;
    private int channel_id = 100;
    private NotificationCompat.Builder mBuilder;

    private Context context;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_home, container, false);

        txt_nombre = myView.findViewById(R.id.txt_nombre);
        txt_gym_valor = myView.findViewById(R.id.txt_gym_value);
        txt_dias_restantes = myView.findViewById(R.id.txt_dias);
        txt_fecha_expira = myView.findViewById(R.id.txt_until_value);
        txt_status = myView.findViewById(R.id.txt_status_value);
        status_color = myView.findViewById(R.id.status_color);
        btn_rutinas = myView.findViewById(R.id.btn_to_rutinas);
        btn_alim = myView.findViewById(R.id.btn_to_alimentacion);
        btn_progreso = myView.findViewById(R.id.btn_to_progreso);



        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "usuarios", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM usuarios";
            //String imagenUsuario = null;

            Cursor cursor = db.rawQuery(query, null);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                nombreUsuario = cursor.getString(cursor.getColumnIndex("nombre"));
                apellidos = cursor.getString(cursor.getColumnIndex("apat"));
                token = cursor.getString(cursor.getColumnIndex("token"));
                token_type = cursor.getString(cursor.getColumnIndex("tokenType"));
                objetivo = cursor.getString(cursor.getColumnIndex("objetivo"));
            }

        } catch (Exception e) {

            Toast toast = Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();


        btn_rutinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentrutinas = new Intent(getContext(), RutinasActivity.class);
                startActivity(intentrutinas);
            }
        });
        btn_alim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAlim = new Intent(getContext(), AlimentacionActivity.class);
                startActivity(intentAlim);
            }
        });
        btn_progreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (objetivo.equals("Perder peso")){
                    startActivity(new Intent(getContext(), ProgresoPerderPesoActivity.class));
                } else  if (objetivo.equals("Incrementar fuerza")){
                    startActivity(new Intent(getContext(), AvanceFuerzaActivity.class));
                }

            }
        });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        //Log.e("RESPONSE_GYM", jsonObject.toString());
        StringRequest request = new StringRequest(Request.Method.GET, Config.GET_INFO_GYM_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //Log.e("RESPONSE_GYM", jsonObject.toString());

                    if (jsonObject.has("status")) {

                        String status = jsonObject.getString("status");

                        if (status.equals("Token is Expired")) {

                            Toast.makeText(getContext(), "Token expirado. Favor de iniciar sesión nuevamente", Toast.LENGTH_LONG).show();
                            ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getContext(), "usuarios", null, 4);
                            SQLiteDatabase db = conn.getWritableDatabase();
                            db.execSQL("DELETE FROM usuarios");

                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().finish();

                        } else {


                        }

                    } else {
                        nombre_gym = jsonObject.getString("nombre");

                        txt_gym_valor.setText(nombre_gym);
                    }


                } catch (JSONException e) {
                    Log.e("ERROR_JSON", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR_RESPONSE", error.toString());

                if (error instanceof TimeoutError) {
                    Toast.makeText(getContext(),
                            "Oops. Timeout error!",
                            Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token_type + " " + token);
                return headers;
            }
        };

        queue.add(request);

        try {
            RequestQueue queue_pago = Volley.newRequestQueue(getContext());
            StringRequest request_pago = new StringRequest(Request.Method.GET, Config.GET_PAGO_CURRENT_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fecha_fin = jsonObject.getString("fecha_fin");

                        //Log.d("fecha_fin", fecha_fin);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date fecha_final = new Date();
                        try {
                            fecha_final = sdf.parse(fecha_fin);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat output = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
                        String formatted = output.format(fecha_final);

                        //Log.d("fecha final pago", fecha_final.toString());

                        long dias = getDiasRestantes(new Date(), fecha_final ) + 1;
                        String dias_res = dias+" días";

                        //        Intent notifyIntent = new Intent(getContext(), BroadcastReceiver.class);
                        //        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        if (dias > 0){
                            txt_status.setText("Activo");
                            status_color.setBackgroundColor(getResources().getColor(R.color.usuario_activo));

                        } else{
                            txt_status.setText("Inactivo");
                            status_color.setBackgroundColor(getResources().getColor(R.color.design_default_color_error));
                        }

                        txt_dias_restantes.setText(dias_res);
                        txt_fecha_expira.setText(formatted);

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error instanceof TimeoutError) {
                        Toast.makeText(getContext(),
                                "Oops. Timeout error!",
                                Toast.LENGTH_LONG).show();
                    }

                    NetworkResponse networkResponse = error.networkResponse;

                    if(networkResponse != null && networkResponse.data != null){
                        String jsonError = new String(networkResponse.data);
                        try {
                            JSONObject jsonObjectError = new JSONObject(jsonError);
                            Log.e("error_pago", jsonObjectError.toString());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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

            queue_pago.add(request_pago);
        } catch (Exception e){
            e.printStackTrace();
        }


        String nomCompleto = nombreUsuario + " " + apellidos;


        //Log.e("DIAS_REST", dias);
        txt_nombre.setText(nomCompleto);



        return myView;
    }

    private long getDiasRestantes(Date fecha_inicial, Date fecha_final){
        long diferencia = fecha_final.getTime() - fecha_inicial.getTime();

        //Log.i("MainActivity", "fechaInicial : " + fecha_inicial);
        //Log.i("MainActivity", "fechaFinal : " + fecha_final);

        long segsMilli = 1000;
        long minsMilli = segsMilli * 60;
        long horasMilli = minsMilli * 60;
        long diasMilli = horasMilli * 24;

        long diasTranscurridos = diferencia / diasMilli;
        diferencia = diferencia % diasMilli;

        /*long horasTranscurridos = diferencia / horasMilli;
        diferencia = diferencia % horasMilli;

        long minutosTranscurridos = diferencia / minsMilli;
        diferencia = diferencia % minsMilli;

        long segsTranscurridos = diferencia / segsMilli;*/

        return diasTranscurridos;
    }

}
