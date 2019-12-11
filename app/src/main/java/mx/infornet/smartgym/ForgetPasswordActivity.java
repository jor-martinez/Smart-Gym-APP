package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextInputEditText correo;
    private ProgressBar progressBar;
    private MaterialButton btn_forget;
    private RequestQueue queue;
    private StringRequest request;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent inte = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(inte);

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_password);

        correo = findViewById(R.id.email_forget);
        progressBar = findViewById(R.id.prog_bar_forget);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        btn_forget = findViewById(R.id.btn_forget_pass);


        queue = Volley.newRequestQueue(this);

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = correo.getText().toString();

                //se validan los campos
                if(TextUtils.isEmpty(email) || !validarEmail(email)){
                    correo.setError("Ingresa un correo valido. Ej. example@mail.com");
                    correo.requestFocus();
                    return;
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    request = new StringRequest(Request.Method.POST, Config.EMAIL_RESET_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.has("error")){
                                    String error = jsonObject.getString("error");
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                } else if(jsonObject.has("status")){
                                    String mensaje = jsonObject.getString("status");

                                    //Log.d("RES_SET_PASS", jsonObject.toString());

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
                                    builder.setTitle("Enviado !")
                                    .setMessage(mensaje)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                                    startActivity(intent);

                                                    ForgetPasswordActivity.this.finish();
                                                }
                                            });

                                    AlertDialog alertDialog = builder.create();

                                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                    alertDialog.show();
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
                                    //Log.e("ERROR_500", jsonObjectError.toString());
                                    //String status = jsonObjectError.getString("status");

                                    String err = jsonObjectError.getString("error");

                                    new AlertDialog.Builder(ForgetPasswordActivity.this)
                                            .setTitle("Error !")
                                            .setMessage(err)
                                            .setIcon(R.mipmap.error_black_icon)
                                            .setCancelable(false)
                                            .setPositiveButton("ok", null)
                                            .show();


                                }catch (JSONException e){

                                }
                            }
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
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
