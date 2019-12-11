package mx.infornet.smartgym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RutinasActivity extends AppCompatActivity {

    private ImageView btn_back, btn_add;
    private TextView error;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private StringRequest request;
    private RequestQueue queue;
    private List<Rutinas> rutinasList;
    private String token, token_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);


        error = findViewById(R.id.txt_error_mis_rutinas);
        error.setVisibility(View.GONE);

        progressBar = findViewById(R.id.prog_bar_mis_rutinas);
        progressBar.setVisibility(View.VISIBLE);

        btn_add = findViewById(R.id.btn_add_rutinas);
        btn_back = findViewById(R.id.btn_back_mis_rutinas);

        recyclerView = findViewById(R.id.recycler_view_mis_rutinas);
        recyclerView.setAdapter(null);


        queue = Volley.newRequestQueue(getApplicationContext());

        rutinasList = new ArrayList<>();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getApplicationContext(), ListaRutinasActivity.class);
                startActivity(intent);
                finish();
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


        request = new StringRequest(Request.Method.GET, Config.MIS_RUTINAS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("status")){
                        String status = jsonObject.getString("status");

                        if (status.equals("Token is Expired")){
                            Toast.makeText(getApplicationContext(), status+". Favor de iniciar sesión nuevamente", Toast.LENGTH_LONG).show();

                            ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getApplicationContext(), "usuarios", null, 4);
                            SQLiteDatabase db = conn.getWritableDatabase();
                            db.execSQL("DELETE FROM usuarios");

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

                try {

                    JSONArray array = new JSONArray(response);

                    //Log.d("RESPONSE", array.toString());

                    if (array.toString().equals("[]")){
                        error.setVisibility(View.VISIBLE);
                        error.setText(R.string.errorrutinas);
                    } else{
                        for (int i=0; i<array.length(); i++){
                            JSONObject rutina = array.getJSONObject(i);
                            rutinasList.add(new Rutinas(
                                    rutina.getInt("id"),
                                    rutina.getString("nombre"),
                                    rutina.getString("descripcion"),
                                    rutina.getInt("id_coach")
                            ));
                        }


                        AdapderMisRutinas adaptermisruitnas = new AdapderMisRutinas(getApplicationContext(), rutinasList);
                        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setAdapter(adaptermisruitnas);
                    }

                }catch (JSONException e){
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
            public Map<String, String> getHeaders()throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token_type+" "+token);
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
