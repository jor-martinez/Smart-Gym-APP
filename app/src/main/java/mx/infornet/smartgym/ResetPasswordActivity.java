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
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText password, password_confirm, email;
    private MaterialButton bt_reset;
    private ProgressBar progressBar;
    private StringRequest request;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);

        password = findViewById(R.id.pass_reset);
        password_confirm = findViewById(R.id.pass_reset_confirm);
        email = findViewById(R.id.email_reset);
        bt_reset = findViewById(R.id.btn_reset_pass);
        progressBar = findViewById(R.id.prog_bar_reset_pass);

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getData().toString());

        final String token = uri.getQueryParameter("token");

        //Log.d("TOKEN", token);

        queue = Volley.newRequestQueue(this);

        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String contra = password.getText().toString();
                final String contra_confirm = password_confirm.getText().toString();
                final String correo = email.getText().toString();


                //se validan los campos
                if (TextUtils.isEmpty(correo) || !validarEmail(correo)){
                    email.setError("Ingresa un correo valido. Ej. example@mail.com");
                    email.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(contra)){
                    password.setError("Por favor introduzca una contraseña");
                    password.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(contra_confirm)){
                    password_confirm.setError("Por favor introduzca una contraseña");
                    password_confirm.requestFocus();
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    request = new StringRequest(Request.Method.POST, Config.RESET_PASSWORD_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                //Log.d("TIENE_ERROR", String.valueOf(jsonObject.has("error")));
                                //Log.d("TIENE_STAUS", String.valueOf(jsonObject.has("status")));

                                if(jsonObject.has("error")){
                                    String error = jsonObject.getString("error");
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                }else if(jsonObject.has("status")){
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

                                        Intent i = new Intent(ResetPasswordActivity.this, MainActivity.class);
                                        startActivity(i);
                                        ResetPasswordActivity.this.finish();

                                        //email.setText("");
                                        //password.setText("");
                                        //password_confirm.setText("");

                                    } else if (status.equals("401")) {
                                        String error = jsonObject.getString("message");
                                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                    }
                                }

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

                            NetworkResponse networkResponse = error.networkResponse;
                            if(networkResponse != null && networkResponse.data != null){
                                String jsonError = new String(networkResponse.data);
                                try {
                                    JSONObject jsonObjectError = new JSONObject(jsonError);

                                    //Log.e("error_500", jsonObjectError.toString());

                                    if (jsonObjectError.has("password")){
                                        JSONArray err_pas = jsonObjectError.getJSONArray("password");

                                        StringBuilder stringBuilder = new StringBuilder();

                                        for (int i=0; i<err_pas.length(); i++){
                                            String valor = err_pas.getString(i);
                                            stringBuilder.append(valor+"\n");
                                        }

                                        password.setError(stringBuilder, null);
                                        password.requestFocus();
                                    } else if(jsonObjectError.has("error")){
                                        String no_user = jsonObjectError.getString("error");

                                        new AlertDialog.Builder(ResetPasswordActivity.this)
                                                .setTitle("Error !")
                                                .setMessage(no_user)
                                                .setCancelable(false)
                                                .setPositiveButton("ok", null)
                                                .show();
                                    } else if(jsonObjectError.has("status")){

                                        String status = jsonObjectError.getString("status");

                                        if(status.equals("401")){
                                            String err = jsonObjectError.getString("error");
                                            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                                        }else if (status.equals("500")){
                                            String err = jsonObjectError.getString("error");
                                            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(getApplicationContext(), "No se puedo conectar con el servidor. Compruba que tienes acceso a la red.", Toast.LENGTH_LONG).show();
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
                            hashMap.put("token", token);
                            hashMap.put("email", correo);
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

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
