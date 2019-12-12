package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText te_correo, te_password;
    private MaterialButton button_login;
    private RequestQueue queue, queue_obj;
    private StringRequest request, request_get_objetivo;
    private ProgressBar progressBar;
    private TextView forget_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        forget_pass = findViewById(R.id.forget_pass);
        forget_pass.setMovementMethod(LinkMovementMethod.getInstance());

        te_correo = findViewById(R.id.correo);

        te_password = findViewById(R.id.password);
        button_login = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressbar);

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        queue = Volley.newRequestQueue(getApplicationContext());
        queue_obj = Volley.newRequestQueue(getApplicationContext());


        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);

                LoginActivity.this.finish();
            }
        });

        //al dar click en el botón del login, se ejecuta lo siguiente
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String correo = te_correo.getText().toString();
                final String password = te_password.getText().toString();


                //se validan los campos
                if(TextUtils.isEmpty(correo) || !validarEmail(correo)){
                    te_correo.setError("Ingresa un correo valido. Ej. example@mail.com");
                    te_correo.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(password)){
                    te_password.setError("Por favor introduzca una contraseña válida", null);
                    te_password.requestFocus();
                    return;
                } else{

                    //final ProgressBar progressBar = new ProgressBar(getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);


                    request = new StringRequest(Request.Method.POST, Config.LOGIN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                Log.d("RESPUESTA", jsonObject.toString());

                                String status = jsonObject.getString("status");
                                final String postToken = jsonObject.getString("access_token");
                                final String tokenType = jsonObject.getString("token_type");
                                String tokenExpire = jsonObject.getString("expires_in");
                                JSONObject usuario = jsonObject.getJSONObject("usuario");

                                //Log.d("ESTATUS", status);

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
                                    //String postIdPlan = usuario.getString("id_plan_entrenamiento");
                                    //String postIdRutina = usuario.getString("id_rutina");
                                    //String postIdAlimentacion = usuario.getString("id_plan_alimentacion");

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
                                    //values.put("idPlan", postIdPlan);
                                    //values.put("idRutina", postIdRutina);
                                    //values.put("idPlanAlimentacion", postIdAlimentacion);
                                    values.put("token", postToken);
                                    values.put("tokenType", tokenType);
                                    values.put("tokenExpire", tokenExpire);

                                    db.insert("usuarios",null, values);
                                    db.close();

                                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

                                    if(postObjetivo.equals("Perder peso")){
                                        request_get_objetivo = new StringRequest(Request.Method.GET, Config.GET_OBJETIVO_URL, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {
                                                    JSONArray respuesta = new JSONArray(response);
                                                    Log.d("res_objetivo", respuesta.toString());

                                                    if (respuesta.toString().equals("[]")) {

                                                        //Toast.makeText(getApplicationContext(), "no hay datos", Toast.LENGTH_LONG).show();

                                                        startActivity(new Intent(LoginActivity.this, PrePerderPesoActivity.class));
                                                        LoginActivity.this.finish();

                                                    } else {
                                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(i);
                                                        LoginActivity.this.finish();
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
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
                                                headers.put("Authorization", tokenType + " " + postToken);
                                                return headers;
                                            }
                                        };

                                        queue_obj.add(request_get_objetivo);


                                    }


                                } else if (status.equals("401")) {
                                    String error = jsonObject.getString("message");
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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
                                    Log.e("error_logn", jsonObjectError.toString());

                                    if (jsonObjectError.has("message")){
                                        String err = jsonObjectError.getString("message");

                                        if (err.equals("The given data was invalid.")){
                                            JSONObject errors = jsonObjectError.getJSONObject("errors");
                                            if (errors.has("password")){

                                                JSONArray err_pas = errors.getJSONArray("password");

                                                StringBuilder stringBuilder = new StringBuilder();

                                                for (int i=0; i<err_pas.length(); i++){
                                                    String valor = err_pas.getString(i);
                                                    stringBuilder.append(valor+"\n");
                                                }
                                                te_password.setError(stringBuilder, null);
                                                te_password.requestFocus();
                                            }
                                        } else if(err.equals("Correo o contraseña incorrectos")){

                                            String datos_inc = jsonObjectError.getString("message");
                                            //Toast.makeText(getApplicationContext(), datos_inc, Toast.LENGTH_LONG).show();

                                            new AlertDialog.Builder(LoginActivity.this)
                                                    .setTitle("Error !")
                                                    .setMessage(datos_inc)
                                                    .setIcon(R.mipmap.error_black_icon)
                                                    .setCancelable(false)
                                                    .setPositiveButton("ok", null)
                                                    .show();

                                        } else if (err.equals("Membresia expirada")){

                                            new AlertDialog.Builder(LoginActivity.this)
                                                    .setTitle("Error !")
                                                    .setMessage(err+". Favor de acudir a tu gimnasio y renovarla.")
                                                    .setCancelable(false)
                                                    .setIcon(R.mipmap.error_black_icon)
                                                    .setPositiveButton("ok", null)
                                                    .show();
                                        }
                                    }

                                }catch (JSONException e){

                                }
                            }
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email", correo);
                            hashMap.put("password", password);
                            return hashMap;
                        }
                    };

                    queue.add(request);
                }
            }
        });

    }
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
