package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PreLoaderActivity extends AppCompatActivity {

    private StringRequest request_get_objetivo1;
    private RequestQueue queue_obj1;
    private Integer res;
    private String token, token_type, objetivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_loader);

        queue_obj1 = Volley.newRequestQueue(getApplicationContext());

        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(getApplicationContext(), "usuarios", null, 4);
        SQLiteDatabase db = conexion.getWritableDatabase();

        //Primero consulta si existe algun usuario
        try {
            String query = "SELECT * FROM usuarios";
            Cursor cursor = db.rawQuery(query, null);

            res = cursor.getCount();

            System.out.println("res: " + res);

            boolean b = res > 0;

            System.out.println("bool res "+ b);

            if(res > 0){

                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    token = cursor.getString(cursor.getColumnIndex("token"));
                    token_type = cursor.getString(cursor.getColumnIndex("tokenType"));
                    objetivo = cursor.getString(cursor.getColumnIndex("objetivo"));
                }

                cursor.close();

                db.close();

                if(objetivo.equals("Perder peso")){
                    request_get_objetivo1 = new StringRequest(Request.Method.GET, Config.GET_OBJETIVO_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONArray respuesta = new JSONArray(response);

                                Log.d("res_objetivo", respuesta.toString());

                                if (respuesta.toString().equals("[]")) {

                                    Toast.makeText(getApplicationContext(), "no hay datos", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(PreLoaderActivity.this, PrePerderPesoActivity.class));
                                    PreLoaderActivity.this.finish();

                                } else {
                                    Intent i = new Intent(PreLoaderActivity.this, MainActivity.class);
                                    startActivity(i);
                                    PreLoaderActivity.this.finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                //Log.e("RESPONSE_GYM", jsonObject.toString());

                                if (jsonObject.has("status")){

                                    String status = jsonObject.getString("status");

                                    if (status.equals("Token is Expired")){

                                        Toast.makeText(getApplicationContext(), "Token expirado. Favor de iniciar sesión nuevamente", Toast.LENGTH_LONG).show();
                                        ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getApplicationContext(), "usuarios", null, 4);
                                        SQLiteDatabase db = conn.getWritableDatabase();
                                        db.execSQL("DELETE FROM usuarios");

                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();

                                    } else {


                                    }

                                }
                            }catch (JSONException e) {
                                Log.e("ERROR_JSON", e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {


                            Log.e("err_res_objetivo", error.toString());
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", token_type + " " + token);
                            return headers;
                        }
                    };

                    queue_obj1.add(request_get_objetivo1);


                }

                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                //Toast.makeText(getApplicationContext(), "hola", Toast.LENGTH_LONG).show();

                Intent i = new Intent(PreLoaderActivity.this, LoginActivity.class);
                startActivity(i);
                PreLoaderActivity.this.finish();
            }



        }catch (Exception e){
            e.getStackTrace();
        }



    }
}
