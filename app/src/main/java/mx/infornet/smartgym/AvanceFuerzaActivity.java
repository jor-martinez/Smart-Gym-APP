package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AvanceFuerzaActivity extends AppCompatActivity {

    private JsonObjectRequest request;
    private RequestQueue queue;
    private String token, token_type, fecha_final, tiempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avance_fuerza);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_avance_fuerza);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Incrementar fuerza");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getApplicationContext(), "usuarios", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM usuarios";

            Cursor cursor = db.rawQuery(query, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                token = cursor.getString(cursor.getColumnIndex("token"));
                token_type = cursor.getString(cursor.getColumnIndex("tokenType"));
            }

            cursor.close();

        }catch (Exception e){

            Toast toast = Toast.makeText(getApplicationContext(), "Error: "+  e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
        db.close();

        ConexionSQLiteHelper  con = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
        SQLiteDatabase db1 = con.getWritableDatabase();

        try {

            String query = "SELECT * FROM objetivo_fuerza where _ID=1";

            Cursor cursor = db1.rawQuery(query, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                tiempo = cursor.getString(cursor.getColumnIndex("tiempo"));
                fecha_final = cursor.getString(cursor.getColumnIndex("fechaFinal"));
            }

            cursor.close();

        }catch (Exception e){

            Toast toast = Toast.makeText(getApplicationContext(), "Error: "+  e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db1.close();

        final ProgressBar progressBar = findViewById(R.id.prog_bar_fuerza_avance);
        progressBar.setVisibility(View.GONE);
        final TextInputEditText peso = findViewById(R.id.peso_levantado_avance);
        final TextInputEditText repeticiones = findViewById(R.id.repeticiones_avance);
        MaterialButton guardar = findViewById(R.id.btn_save_fuerza_avance);
        final Spinner musculos = findViewById(R.id.sp_musculos_avance);
        FloatingActionButton fab = findViewById(R.id.btn_prog_fuerza);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProgresoFuerzaActivity.class));
            }
        });

        queue = Volley.newRequestQueue(this);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String peso_levantado = peso.getText().toString();
                String repet = repeticiones.getText().toString();
                final String musculo = musculos.getSelectedItem().toString();

                if (TextUtils.isEmpty(peso_levantado)){
                    peso.setError("Introduce el peso levantado");
                    peso.requestFocus();
                } else if (TextUtils.isEmpty(repet)){
                    repeticiones.setError("Introduce el número de repeticiones");
                    repeticiones.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    final double peso_decimal = Double.parseDouble(peso_levantado);
                    final int repeticiones_int = Integer.parseInt(repet);

                    JSONObject params_fuerza = new JSONObject();

                    try {
                        params_fuerza.put("peso_levantado", peso_decimal);
                        params_fuerza.put("repeticiones", repeticiones_int);
                        params_fuerza.put("musculo", musculo);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    request = new JsonObjectRequest(Request.Method.POST, Config.POST_FUERZA_URL, params_fuerza, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            progressBar.setVisibility(View.GONE);

                            Log.d("res_post_fuerza", response.toString());

                            if (response.has("message")){

                                String mensj = null;
                                try {
                                    mensj = response.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //fecha actual
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String fecha_actual = df.format(calendar.getTime());


                                ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "objetivo_fuerza", null, 4);
                                SQLiteDatabase db = conn.getWritableDatabase();

                                ContentValues datos = new ContentValues();

                                datos.put("pesoLevantado", peso_decimal);
                                datos.put("repeticiones", repeticiones_int);
                                datos.put("musculo", musculo);
                                datos.put("tiempo", tiempo);
                                datos.put("fecha", fecha_actual);
                                datos.put("fechaFinal", fecha_final);

                                db.insert("objetivo_fuerza", null, datos);
                                db.close();

                                Toast.makeText(getApplicationContext(), mensj, Toast.LENGTH_LONG).show();

                                startActivity(new Intent(AvanceFuerzaActivity.this, MainActivity.class));
                                finish();

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

                            NetworkResponse networkResponse = error.networkResponse;

                            if(networkResponse != null && networkResponse.data != null){
                                String jsonError = new String(networkResponse.data);
                                try {
                                    JSONObject jsonObjectError = new JSONObject(jsonError);
                                    Log.e("error_logn", jsonObjectError.toString());

                                    if (jsonObjectError.has("message")){
                                        String err = jsonObjectError.getString("message");
                                        if (err.equals("The given data was invalid.")){
                                            JSONObject errors = jsonObjectError.getJSONObject("errors");

                                            if (errors.has("peso_levantado")){

                                                JSONArray err_peso = errors.getJSONArray("peso_levantado");

                                                StringBuilder stringBuilder = new StringBuilder();

                                                for (int i=0; i<err_peso.length(); i++){
                                                    String valor = err_peso.getString(i);
                                                    stringBuilder.append(valor+"\n");
                                                }
                                                peso.setError(stringBuilder, null);
                                                peso.requestFocus();
                                            } else if (errors.has("repeticiones")){
                                                JSONArray err_repet = errors.getJSONArray("repeticiones");
                                                StringBuilder stringBuilder = new StringBuilder();

                                                for (int i=0; i<err_repet.length(); i++){
                                                    String valor = err_repet.getString(i);
                                                    stringBuilder.append(valor+"\n");
                                                }
                                                repeticiones.setError(stringBuilder, null);
                                                repeticiones.requestFocus();
                                            }
                                        }
                                    }

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

                    queue.add(request);

                }

            }
        });

    }
}
