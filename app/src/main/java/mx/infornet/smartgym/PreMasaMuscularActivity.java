package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PreMasaMuscularActivity extends AppCompatActivity {

    private TextInputEditText peso, repeticiones;
    private JsonObjectRequest request;
    private RequestQueue queue;
    private String token, token_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_masa_muscular);

        queue = Volley.newRequestQueue(getApplicationContext());

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

        peso = findViewById(R.id.peso_ini_levantado);
        repeticiones = findViewById(R.id.repeticiones_ini);

        final ProgressBar progressBar = findViewById(R.id.prog_bar_masa_init);
        progressBar.setVisibility(View.GONE);

        MaterialButton guardar = findViewById(R.id.btn_save_masa_musc_init);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String peso_levantado = peso.getText().toString();
                String num_repeticiones = repeticiones.getText().toString();

                if (TextUtils.isEmpty(peso_levantado)){
                    peso.setError("Introduce el peso levantado");
                    peso.requestFocus();
                } else if (TextUtils.isEmpty(num_repeticiones)){
                    repeticiones.setError("Introduce el número de repeticiones");
                    repeticiones.requestFocus();
                } else {

                    double peso_decimal = Double.parseDouble(peso_levantado);
                    int repeticiones_int = Integer.parseInt(num_repeticiones);

                    progressBar.setVisibility(View.VISIBLE);

                    JSONObject params_masa = new JSONObject();

                    try {
                        params_masa.put("peso_levantado", peso_decimal);
                        params_masa.put("repeticiones", repeticiones_int);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    request = new JsonObjectRequest(Request.Method.POST, Config.POST_MASA_MUSC_URL, params_masa, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);

                            Log.d("res_post_masa", response.toString());

                            try {
                                if (response.has("message")){
                                    String ms = response.getString("message");

                                    Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
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
