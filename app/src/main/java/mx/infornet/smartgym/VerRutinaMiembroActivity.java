package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerRutinaMiembroActivity extends AppCompatActivity {

    private TextView tx_nombre, tx_desc;
    private ImageView b_back;
    private FloatingActionButton btn_delete;
    private StringRequest request;
    private RequestQueue queue;
    private String token, token_type;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_rutina_miembro);

        Intent intent = getIntent();

        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        id = intent.getIntExtra("id", 0);

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

        queue = Volley.newRequestQueue(getApplicationContext());

        tx_nombre = findViewById(R.id.title_nombre_rutina_miembro);
        tx_nombre.setText(nombre);

        tx_desc = findViewById(R.id.tv_descr_rutina_miembro);
        tx_desc.setText(descripcion);

        b_back = findViewById(R.id.btn_back_rutina_miembro);
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_delete = findViewById(R.id.btn_delete_rutina);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(VerRutinaMiembroActivity.this);
                builder.setTitle("Alerta !")
                .setMessage("¿Estás seguro de eliminar ésta rutina?")
                .setCancelable(false)
                .setIcon(R.mipmap.warning_black_icon)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                request = new StringRequest(Request.Method.DELETE, Config.DELETE_RUTINA + id + "/eliminar-rutina", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Log.e("RES_DELTE", jsonObject.toString());

                                            String msg = jsonObject.getString("message");
                                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                                            startActivity(new Intent(VerRutinaMiembroActivity.this, RutinasActivity.class));
                                            finish();

                                        }catch (JSONException e){
                                            Log.e("ERRORJSON_DELTE", e.toString());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("ERROR_RESP", error.toString());
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
                        })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

            }
        });
    }
}
