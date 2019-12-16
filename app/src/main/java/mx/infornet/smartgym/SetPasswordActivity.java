package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class SetPasswordActivity extends AppCompatActivity {

    private TextInputEditText password, password_confirm;
    private MaterialButton bt_set;
    private ProgressBar progressBar;
    private StringRequest request;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_password);

        password = findViewById(R.id.set_password);
        password_confirm = findViewById(R.id.set_password_confirm);
        bt_set = findViewById(R.id.btn_set_pass);
        progressBar = findViewById(R.id.prog_bar_set_pass);

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getData().toString());

        final String email = uri.getQueryParameter("email");
        final String gimnasio = uri.getQueryParameter("gimnasio");

        //Log.d("email", email);
        //Log.d("gym", gimnasio);

        queue = Volley.newRequestQueue(this);


        bt_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String contra = password.getText().toString();
                final String contra_confirm = password_confirm.getText().toString();

                //se validan los campos
                if(TextUtils.isEmpty(contra)){
                    password.setError("Por favor introduzca una contraseña");
                    password.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(contra_confirm)){
                    password_confirm.setError("Por favor introduzca una contraseña");
                    password_confirm.requestFocus();
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    request = new StringRequest(Request.Method.POST, Config.SET_PASSWORD_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                Log.d("RES_SET_PASS", jsonObject.toString());

                                if (jsonObject.has("error")){

                                    String error = jsonObject.getString("error");
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

                                } else if(jsonObject.has("status")){

                                    String status = jsonObject.getString("status");
                                    String postToken = jsonObject.getString("access_token");
                                    String tokenType = jsonObject.getString("token_type");
                                    String tokenExpire = jsonObject.getString("expires_in");
                                    JSONObject usuario = jsonObject.getJSONObject("usuario");

                                    if (status.equals("200")) {
                                        //Log.d("JSONUSUARIO", usuario.toString());
                                        String mensaje = jsonObject.getString("message");

                                        String postId = usuario.getString("id");
                                        String postNombre = usuario.getString("nombre");
                                        String postApellidos = usuario.getString("apellidos");
                                        String postEstatura = usuario.getString("estatura");
                                        String postPeso = usuario.getString("peso");
                                        String postSexo = usuario.getString("sexo");
                                        String postObjetivo = usuario.getString("objetivo");
                                        String postFechaNacimiento = usuario.getString("fecha_nacimiento");
                                        String postTelefono = usuario.getString("telefono");
                                        String postTelEmergencia = usuario.getString("telefono_emergencia");
                                        String postCondicionFisica = usuario.getString("condicion_fisica");
                                        String postEmail = usuario.getString("email");
                                        String postIdGym = usuario.getString("id_gimnasio");
                                        String postIdPlan = usuario.getString("id_plan_entrenamiento");
                                        //String postIdRutina = usuario.getString("id_rutina");
                                        String postIdAlimentacion = usuario.getString("id_plan_alimentacion");

                                        ConexionSQLiteHelper con = new ConexionSQLiteHelper(getApplicationContext(), "usuarios", null, 4);
                                        SQLiteDatabase db = con.getWritableDatabase();
                                        ContentValues values = new ContentValues();

                                        values.put("idUsuarios", postId);
                                        values.put("email", postEmail);
                                        values.put("nombre", postNombre);
                                        values.put("apat", postApellidos);
                                        values.put("estatura", postEstatura);
                                        values.put("peso", postPeso);
                                        values.put("sexo", postSexo);
                                        values.put("objetivo", postObjetivo);
                                        values.put("fechaDeNacimiento", postFechaNacimiento);
                                        values.put("telefono", postTelefono);
                                        values.put("telefonoEmergencia", postTelEmergencia);
                                        values.put("condicionFisica", postCondicionFisica);
                                        values.put("idGimnasio", postIdGym);
                                        values.put("idPlan", postIdPlan);
                                        //values.put("idRutina", postIdRutina);
                                        values.put("idPlanAlimentacion", postIdAlimentacion);
                                        values.put("token", postToken);
                                        values.put("tokenType", tokenType);
                                        values.put("tokenExpire", tokenExpire);

                                        db.insert("usuarios",null, values);
                                        db.close();

                                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(SetPasswordActivity.this, MainActivity.class);
                                        startActivity(i);
                                        SetPasswordActivity.this.finish();

                                        //email.setText("");
                                        //password.setText("");
                                        //password_confirm.setText("");

                                    } else if (status.equals("401")) {
                                        String error = jsonObject.getString("message");
                                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                    }
                                }

                                Intent i = new Intent(SetPasswordActivity.this, LoginActivity.class);
                                startActivity(i);

                                finish();

                            } catch (JSONException e){
                                e.printStackTrace();
                                String err = e.toString();
                                Toast.makeText(getApplicationContext(), "Error " + err, Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);

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

                                    Log.e("error_500", jsonObjectError.toString());

                                    if (jsonObjectError.has("message")){

                                        String msg_error = jsonObjectError.getString("message");

                                        if (msg_error.equals("The given data was invalid.")){

                                            JSONObject errors = jsonObjectError.getJSONObject("errors");

                                            if (errors.has("password")){
                                                JSONArray err_pas = errors.getJSONArray("password");

                                                StringBuilder stringBuilder = new StringBuilder();

                                                for (int i=0; i<err_pas.length(); i++){
                                                    String valor = err_pas.getString(i);
                                                    stringBuilder.append(valor+"\n");
                                                }
                                                password.setError(stringBuilder, null);
                                                password.requestFocus();
                                            }

                                        } else if (msg_error.equals("Ya se ha establecido una contraseña")){

                                            new AlertDialog.Builder(SetPasswordActivity.this)
                                                    .setTitle("Error !")
                                                    .setMessage(msg_error)
                                                    .setIcon(R.mipmap.error_black_icon)
                                                    .setCancelable(false)
                                                    .setPositiveButton("ok", null)
                                                    .show();
                                        }
                                    }
                                    /*String status = jsonObjectError.getString("status");

                                    if(status.equals("500")){
                                        String err = jsonObjectError.getString("message");
                                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(getApplicationContext(), "No se puedo conectar con el servidor. Compruba que tienes acceso a la red.", Toast.LENGTH_LONG).show();
                                    }*/

                                }catch (JSONException e){

                                }
                            }
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("gimnasio", gimnasio);
                            hashMap.put("password", contra);
                            hashMap.put("password_confirmation", contra_confirm);
                            return hashMap;
                        }
                    };
                    queue.add(request);
                }
            }
        });

    }
}
