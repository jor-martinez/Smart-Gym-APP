package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VerRutinaListaActivity extends AppCompatActivity {

    private TextView txt_nombre, txt_descripcion;
    private ImageView btnback;
    private FloatingActionButton btn_add;
    private RequestQueue queue;
    private JsonObjectRequest jsonObjectRequest;
    private String token, token_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_rutina_lista);

        Intent intent = getIntent();

        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        final int id = intent.getIntExtra("id", 0);

        ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getApplicationContext(), "usuarios", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM usuarios";

            Cursor cursor = db.rawQuery(query, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                token = cursor.getString(cursor.getColumnIndex("token"));
                token_type = cursor.getString(cursor.getColumnIndex("tokenType"));
            }

        }catch (Exception e){

            Toast toast = Toast.makeText(getApplicationContext(), "Error: "+  e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        txt_nombre = findViewById(R.id.title_nombre_rutina);
        txt_descripcion = findViewById(R.id.tv_descr_rutina);
        btnback = findViewById(R.id.btn_back_rutina);
        btn_add = findViewById(R.id.btn_add_rutina);

        queue = Volley.newRequestQueue(getApplicationContext());

        //Log.d("token", token);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txt_nombre.setText(nombre);
        txt_descripcion.setText(descripcion);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] arr = {id};

                Map<String, int[]> params = new HashMap<String, int[]>();
                params.put("rutinas", arr);

                //JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject(params);
                Log.i("jsonString", jsonObject.toString());

                /*try {
                    jsonObject.put("rutinas", arr[0]);
                    jsonArray.put(jsonObject);
                    Log.i("jsonString", jsonObject.toString());
                } catch (Exception e){

                }*/

                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.MIS_RUTINAS_URL, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RESPONSE", response.toString());
                        try {

                            if (response.has("status")) {

                                String status = response.getString("status");
                                if (status.equals("Token is expired")) {
                                    Toast.makeText(getApplicationContext(), "Token invalido. Favor de iniciar sesión nuevamente", Toast.LENGTH_LONG).show();
                                }
                            } else if(response.has("message")){
                                String mensaje = response.getString("message");
                                if (mensaje.equals("Rutinas actualizadas")) {
                                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(VerRutinaListaActivity.this, ListaRutinasActivity.class));
                                    finish();
                                }
                            }
                        } catch (JSONException e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Error.Response", error.toString());
                        String json = null;
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            switch(response.statusCode){
                                case 500:
                                    json = new String(response.data);
                                    System.out.println(json);
                                    break;
                            }
                            //Additional cases
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
                queue.add(jsonObjectRequest);
            }
        });
    }



}
