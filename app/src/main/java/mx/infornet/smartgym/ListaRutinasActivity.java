package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaRutinasActivity extends AppCompatActivity {

    private ImageView btn_back;
    private TextView error, tvcurrPag;
    private ProgressBar progressBar;
    private Button anterior, siguiente, primer, ultimo;
    private RequestQueue queue, queuePag;
    private RecyclerView recyclerView;
    private String token, token_type;
    private List<Rutinas> rutinasList;
    private int currentPag;
    private int numPaginas;
    private int totalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rutinas);

        btn_back = findViewById(R.id.btn_back_lista_rutinas);

        error = findViewById(R.id.txt_error_lista_rutinas);
        error.setVisibility(View.GONE);
        anterior = findViewById(R.id.anterior_lista_rutinas);
        anterior.setVisibility(View.GONE);
        siguiente = findViewById(R.id.siguiente_lista_rutinas);
        siguiente.setVisibility(View.GONE);
        tvcurrPag = findViewById(R.id.tv_current_pag_lista_rutinas);
        tvcurrPag.setVisibility(View.GONE);
        primer = findViewById(R.id.primero_lista_rutinas);
        primer.setVisibility(View.GONE);
        ultimo = findViewById(R.id.ultimo_lista_rutinas);
        ultimo.setVisibility(View.GONE);

        progressBar = findViewById(R.id.prog_bar_lista_rutinas);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recycler_view_lista_rutinas);
        recyclerView.setAdapter(null);


        queue = Volley.newRequestQueue(getApplicationContext());
        queuePag = Volley.newRequestQueue(getApplicationContext());

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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



        //JSONArray array = new JSONArray(response);
        //se toman los datos de la paginacion para su posterior uso
        StringRequest request = new StringRequest(Request.Method.GET, Config.RUTINAS_PAGINATION_GYM_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject pagination = jsonObject.getJSONObject("pagination");
                    JSONArray array = jsonObject.getJSONArray("data");

                    //se toman los datos de la paginacion para su posterior uso
                    currentPag = pagination.getInt("current_page");
                    numPaginas = pagination.getInt("last_page");
                    totalData = pagination.getInt("total");

                    //Log.d("paginacion", pagination.toString());

                    if (totalData <= 0){
                        error.setVisibility(View.VISIBLE);
                        error.setText(R.string.errorrutinasgym);
                        anterior.setVisibility(View.GONE);
                        siguiente.setVisibility(View.GONE);
                        tvcurrPag.setVisibility(View.GONE);
                        primer.setVisibility(View.GONE);
                        ultimo.setVisibility(View.GONE);
                    } else {
                        anterior.setVisibility(View.VISIBLE);
                        siguiente.setVisibility(View.VISIBLE);
                        tvcurrPag.setVisibility(View.VISIBLE);
                        primer.setVisibility(View.VISIBLE);
                        ultimo.setVisibility(View.VISIBLE);
                        GetRutinas(currentPag);
                    }

                    //GetRutinas(currentPag);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Log.d("ERROR", error.toString());

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(),
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




        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentPag < numPaginas){
                    currentPag++;
                    GetRutinas(currentPag);
                } else {
                    Toast.makeText(getApplicationContext(), "No hay más páginas", Toast.LENGTH_LONG).show();
                }


            }
        });

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPag > 1){
                    currentPag--;
                    GetRutinas(currentPag);
                } else {
                    Toast.makeText(getApplicationContext(), "No hay más páginas", Toast.LENGTH_LONG).show();
                }
            }
        });

        primer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPag=1;
                GetRutinas(currentPag);
            }
        });

        ultimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPag=numPaginas;
                GetRutinas(currentPag);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ListaRutinasActivity.this, RutinasActivity.class));
        finish();
    }

    public void GetRutinas(int pagina){

        progressBar.setVisibility(View.VISIBLE);

        String pag = "Página: " + pagina;
        tvcurrPag.setText(pag);

        rutinasList = new ArrayList<>();

        StringRequest requestPag = new StringRequest(Request.Method.GET, Config.RUTINAS_GYM_URL + pagina, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray array = jsonObject.getJSONArray("data");

                    Log.d("datos", array.toString());

                    if (array.toString().equals("[]")) {
                        error.setVisibility(View.VISIBLE);
                        error.setText(R.string.errorrutinasgym);
                    } else {

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject rutina = array.getJSONObject(i);
                            rutinasList.add(i, new Rutinas(
                                    rutina.getInt("id"),
                                    rutina.getString("nombre"),
                                    rutina.getString("descripcion"),
                                    rutina.getInt("id_coach")
                            ));

                        }

                        Adapter adapterlista = new Adapter(getApplicationContext(), rutinasList);
                        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setAdapter(adapterlista);
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Log.d("ERROR", error.toString());

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(),
                            "Oops. Timeout error!",
                            Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token_type + " " + token);
                return headers;
            }
        };

        queuePag.add(requestPag);

    }
}
