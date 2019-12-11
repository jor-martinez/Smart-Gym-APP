package mx.infornet.smartgym;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class EditarDatosFragment extends Fragment implements View.OnClickListener{

    private View myView;
    private String new_nombre, new_apellidos, new_fecha, new_tel, new_telEmer, new_condicion, token, token_type, new_correo, idUser;
    private ProgressBar progressBar;
    private TextInputEditText nombre, apellidos, fecha, telefono, telEmer, condicion, correo;
    private MaterialButton btn_edit_datos;
    private StringRequest request;
    private RequestQueue queue;
    private Calendar calendar = Calendar.getInstance();

    private final int mes = calendar.get(Calendar.MONTH);
    private final int dia = calendar.get(Calendar.DAY_OF_MONTH);
    private final int anio = calendar.get(Calendar.YEAR);

    private static final String CERO = "0";
    private static final String BARRA = "/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_editar_datos, container, false);

        ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getContext(), "usuarios", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM usuarios";

            Cursor cursor = db.rawQuery(query, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                idUser = cursor.getString(cursor.getColumnIndex("idUsuarios"));
                new_nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                new_apellidos = cursor.getString(cursor.getColumnIndex("apat"));
                new_fecha = cursor.getString(cursor.getColumnIndex("fechaDeNacimiento"));
                new_tel = cursor.getString(cursor.getColumnIndex("telefono"));
                new_telEmer = cursor.getString(cursor.getColumnIndex("telefonoEmergencia"));
                new_condicion = cursor.getString(cursor.getColumnIndex("condicionFisica"));
                new_correo = cursor.getString(cursor.getColumnIndex("email"));
                token = cursor.getString(cursor.getColumnIndex("token"));
                token_type = cursor.getString(cursor.getColumnIndex("tokenType"));
            }

        }catch (Exception e){

            Toast toast = Toast.makeText(getContext(), "Error: "+  e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        //Log.d("TOKEN", token);

        nombre = myView.findViewById(R.id.nombre_new);

        apellidos = myView.findViewById(R.id.apellidos_new);
        fecha = myView.findViewById(R.id.fecha_new);
        telefono = myView.findViewById(R.id.telefono_new);
        telEmer = myView.findViewById(R.id.telEmer_new);
        condicion = myView.findViewById(R.id.condicion_new);
        correo = myView.findViewById(R.id.correo_new);
        btn_edit_datos = myView.findViewById(R.id.btn_edit_datos);


        progressBar = myView.findViewById(R.id.prog_bar_edit_perfil);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        fecha.setOnClickListener(this);

        nombre.setText(new_nombre);
        apellidos.setText(new_apellidos);
        fecha.setText(new_fecha);
        telefono.setText(new_tel);
        telEmer.setText(new_telEmer);
        condicion.setText(new_condicion);
        correo.setText(new_correo);

        btn_edit_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarDatos();
            }
        });


        return myView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fecha_new:
                obtenerFecha();
                break;
        }
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                fecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }

    public void ActualizarDatos(){
        final String nuevoNombre = nombre.getText().toString();
        final String nuevoApe = apellidos.getText().toString();
        final String nuevoFecha = fecha.getText().toString();
        final String nuevoTel = telefono.getText().toString();
        final String nuevoTelEmer = telEmer.getText().toString();
        final String nuevoCondicion = condicion.getText().toString();
        final String nuevoEmail = correo.getText().toString();

        queue = Volley.newRequestQueue(getContext());

        if(TextUtils.isEmpty(nuevoNombre)){
            nombre.setError("Ingresa un nombre");
            nombre.requestFocus();
        } else if(TextUtils.isEmpty(nuevoApe)){
            apellidos.setError("Ingresa los apellidos");
            apellidos.requestFocus();
        } else if(TextUtils.isEmpty(nuevoFecha)){
            fecha.setError("Ingresa tu fecha de nacimiento");
            fecha.requestFocus();
        } else if(TextUtils.isEmpty(nuevoTel)){
            telefono.setError("Ingresa tu número de teléfono");
            telefono.requestFocus();
        } else if(TextUtils.isEmpty(nuevoTelEmer)){
            telEmer.setError("Ingresa un número de emergencia");
            telEmer.requestFocus();
        } else if(TextUtils.isEmpty(nuevoEmail) || !validarEmail(nuevoEmail)){
            correo.setError("Ingresa un correo valido. Ej. example@mail.com");
            correo.requestFocus();
        } else {

            progressBar.setVisibility(View.VISIBLE);

            request = new StringRequest(Request.Method.POST, Config.EDIT_PERFIL_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        //Log.d("RESPUESTA", jsonObject.toString());

                        if(jsonObject.has("status")){

                            String mensaje = jsonObject.getString("status");
                            Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();

                        } else if(jsonObject.has("message")){

                            ConexionSQLiteHelper con = new ConexionSQLiteHelper(getContext(), "usuarios", null, 4);
                            SQLiteDatabase dbUp = con.getWritableDatabase();

                            ContentValues values = new ContentValues();

                            values.put("email", nuevoEmail);
                            values.put("nombre", nuevoNombre);
                            values.put("apat", nuevoApe);
                            values.put("fechaDeNacimiento", nuevoFecha);
                            values.put("telefono", nuevoTel);
                            values.put("telefonoEmergencia", nuevoTelEmer);
                            values.put("condicionFisica", nuevoCondicion);

                            dbUp.update("usuarios", values, "idUsuarios="+idUser, null);
                            dbUp.close();

                            String mensajeOK = jsonObject.getString("message");
                            Toast.makeText(getContext(), mensajeOK, Toast.LENGTH_LONG).show();

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

                    NetworkResponse networkResponse = error.networkResponse;

                    if(networkResponse != null && networkResponse.data != null){
                        String jsonError = new String(networkResponse.data);
                        try {
                            JSONObject jsonObjectError = new JSONObject(jsonError);
                            //Log.d("ERROR500", jsonObjectError.toString());
                            //String status = jsonObjectError.getString("status");

                            if (jsonObjectError.has("message")){
                                String message = jsonObjectError.getString("message");
                                //Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                if (message.equals("The given data was invalid.")){
                                    JSONObject errors = jsonObjectError.getJSONObject("errors");
                                    if (errors.has("nombre")){
                                        String errorNombre = errors.getString("nombre");
                                        nombre.setError(errorNombre);
                                        nombre.requestFocus();
                                    } else if (errors.has("apellidos")){
                                        String errorApe = errors.getString("apellidos");
                                        apellidos.setError(errorApe);
                                        apellidos.requestFocus();
                                    } else if (errors.has("fecha_nacimiento")){
                                        String errorFecha = errors.getString("fecha_nacimiento");
                                        fecha.setError(errorFecha);
                                        fecha.requestFocus();
                                    } else if (errors.has("telefono")){
                                        String errorTel = errors.getString("telefono");
                                        telefono.setError(errorTel);
                                        telefono.requestFocus();
                                    } else if (errors.has("telefono_emergencia")){
                                        String errorTelE = errors.getString("telefono_emergencia");
                                        telEmer.setError(errorTelE);
                                        telEmer.requestFocus();
                                    } else if (errors.has("email")){
                                        String errorEmail = errors.getString("email");
                                        correo.setError(errorEmail);
                                        correo.requestFocus();
                                    }
                                }
                            }

                        }catch (JSONException e){
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("nombre", nuevoNombre);
                    params.put("apellidos", nuevoApe);
                    params.put("fecha_nacimiento", nuevoFecha);
                    params.put("telefono", nuevoTel);
                    params.put("telefono_emergencia", nuevoTelEmer);
                    params.put("email", nuevoEmail);
                    params.put("condicion_fisica", nuevoCondicion);
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

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
