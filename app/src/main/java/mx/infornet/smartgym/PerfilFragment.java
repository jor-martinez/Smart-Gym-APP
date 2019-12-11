package mx.infornet.smartgym;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilFragment extends Fragment {

    private View myView;
    private String nombreUsuario, apellidosUsuario, fechaUsuario, telefonoUsuario, telEmerUsuario, correoUsuario, condicionUsuario;
    private TextView txt_nombre, txt_apellidos, txt_fecha, txt_telefono, txt_telEmer, txt_condicion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.perfil_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_edit){

            Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_perfil, container, false);

        ConexionSQLiteHelper  conn = new ConexionSQLiteHelper(getActivity(), "usuarios", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        try {

            String query = "SELECT * FROM usuarios";
            String imagenUsuario = null;

            Cursor cursor = db.rawQuery(query, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                nombreUsuario = cursor.getString(cursor.getColumnIndex("nombre"));
                apellidosUsuario = cursor.getString(cursor.getColumnIndex("apat"));
                fechaUsuario = cursor.getString(cursor.getColumnIndex("fechaDeNacimiento"));
                telefonoUsuario = cursor.getString(cursor.getColumnIndex("telefono"));
                telEmerUsuario = cursor.getString(cursor.getColumnIndex("telefonoEmergencia"));
                correoUsuario = cursor.getString(cursor.getColumnIndex("email"));
                condicionUsuario = cursor.getString(cursor.getColumnIndex("condicionFisica"));
            }

        }catch (Exception e){

            Toast toast = Toast.makeText(getActivity(), "Error: "+  e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

        db.close();

        txt_nombre = myView.findViewById(R.id.txt_profile_nombre);
        txt_apellidos = myView.findViewById(R.id.txt_profile_apat);
        txt_fecha = myView.findViewById(R.id.txt_profile_fecha);
        txt_telefono = myView.findViewById(R.id.txt_profile_telefono);
        txt_telEmer = myView.findViewById(R.id.txt_profile_telEmer);
        txt_condicion = myView.findViewById(R.id.txt_profile_condicion);


        txt_nombre.setText(nombreUsuario);
        txt_apellidos.setText(apellidosUsuario);
        txt_fecha.setText(fechaUsuario);
        txt_telefono.setText(telefonoUsuario);
        txt_telEmer.setText(telEmerUsuario);
        txt_condicion.setText(condicionUsuario);

        return myView;
    }

}
