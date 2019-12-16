package mx.infornet.smartgym;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class EditarPasswordFragment extends Fragment {

    private View myView;
    private String token, token_type;
    private ProgressBar progressBar;
    private TextInputEditText current, nueva, nuevaConfirm;
    private MaterialButton btn_edit;
    private RequestQueue queue;
    private StringRequest request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_editar_password, container, false);

        ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getContext(), "usuarios", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM usuarios";

            Cursor cursor = db.rawQuery(query, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                token = cursor.getString(cursor.getColumnIndex("token"));
                token_type = cursor.getString(cursor.getColumnIndex("tokenType"));
            }

        }catch (Exception e){

            Toast toast = Toast.makeText(getContext(), "Error: "+  e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        //Log.d("TOKEN", token);

        current = myView.findViewById(R.id.current_pass);
        nueva = myView.findViewById(R.id.pass_edit);
        nuevaConfirm = myView.findViewById(R.id.pass_edit_confirm);
        btn_edit = myView.findViewById(R.id.btn_edit_pass);

        progressBar = myView.findViewById(R.id.prog_bar_edit_pass);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        queue = Volley.newRequestQueue(getContext());

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarPassword();
            }
        });

        return myView;
    }


    private void EditarPassword(){

        final String contra_actual = current.getText().toString();
        final String contra_nueva = nueva.getText().toString();
        final String contra_nueva_confirm = nuevaConfirm.getText().toString();

        if (TextUtils.isEmpty(contra_actual) || contra_actual.length() <= 4){
            current.setError("Ingresa una contraseña mayor a 4 caracteres", null);
            current.requestFocus();
        } else if (TextUtils.isEmpty(contra_nueva)){
            nueva.setError("Ingresa una contraseña", null);
            nueva.requestFocus();
        } else if(TextUtils.isEmpty(contra_nueva_confirm)){
            nuevaConfirm.setError("Ingresa una contraseña", null);
            nuevaConfirm.requestFocus();

        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);

            request = new StringRequest(Request.Method.POST, Config.EDIT_PASSWORD_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Log.d("RESPUESTA", jsonObject.toString());

                        if (jsonObject.has("status")){
                            String status = jsonObject.getString("status");
                            if (status.equals("Token is Expired")){
                                Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } else if(jsonObject.has("message")){
                            String messageok = jsonObject.getString("message");
                                Toast.makeText(getContext(), messageok, Toast.LENGTH_LONG).show();

                                startActivity(new Intent(getContext(), MainActivity.class));
                                getActivity().finish();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        String err = e.toString();
                        Toast.makeText(getContext(), "Error " + err, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);

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
                            Log.d("ERROR", jsonObjectError.toString());
                            //String status = jsonObjectError.getString("status");

                            if (jsonObjectError.has("message")){
                                String message = jsonObjectError.getString("message");
                                //Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                if (message.equals("The given data was invalid.")){

                                    JSONObject errors = jsonObjectError.getJSONObject("errors");

                                    if (errors.has("current_password")){

                                        String sb_curr_pas = errors.getString("current_password");

                                        current.setError(sb_curr_pas, null);
                                        current.requestFocus();

                                    } else if (errors.has("password")){

                                        JSONArray err_pas = errors.getJSONArray("password");

                                        StringBuilder sb_pas = new StringBuilder();

                                        for (int i=0; i<err_pas.length(); i++){
                                            String valor = err_pas.getString(i);
                                            sb_pas.append(valor+"\n");
                                        }

                                        nueva.setError(sb_pas, null);
                                        nueva.requestFocus();

                                    } else if (errors.has("password_confirmation")){

                                        JSONArray err_pas_con = errors.getJSONArray("password_confirmation");

                                        StringBuilder sb_pas_con = new StringBuilder();

                                        for (int i=0; i<err_pas_con.length(); i++){
                                            String valor = err_pas_con.getString(i);
                                            sb_pas_con.append(valor+"\n");
                                        }

                                        nuevaConfirm.setError(sb_pas_con, null);
                                        nuevaConfirm.requestFocus();
                                    }
                                }
                            }

                        }catch (JSONException e){
                            //Log.d("ERROR_EXCEPTION", e.toString());
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("current_password", contra_actual);
                    params.put("password", contra_nueva);
                    params.put("password_confirmation", contra_nueva_confirm);
                    return params;
                }

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

}
