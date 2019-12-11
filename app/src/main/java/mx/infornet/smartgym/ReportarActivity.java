package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ReportarActivity extends AppCompatActivity {

    private TextInputEditText mensaje;
    private MaterialButton btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar);

        MaterialToolbar toolbar = findViewById(R.id.toolbarReport);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Reportar un problema");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mensaje = findViewById(R.id.mensaje_report);

        btn_send = findViewById(R.id.btn_enviar_report);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mens = mensaje.getText().toString();

                if (TextUtils.isEmpty(mens)){
                    mensaje.setError("Introduzca el mensaje del reporte");
                    mensaje.requestFocus();
                } else {

                    Intent itSend = new Intent(Intent.ACTION_SEND);

                    itSend.setData(Uri.parse("mailito"));
                    itSend.setType("plain/text");
                    itSend.putExtra(Intent.EXTRA_EMAIL, new String[] {"jor.martinez.salgado@gmail.com"});
                    itSend.putExtra(Intent.EXTRA_SUBJECT, "Reporte App Smart Gym miembro");
                    itSend.putExtra(Intent.EXTRA_TEXT, mens);

                    try {
                        startActivity(Intent.createChooser(itSend, "Enviar email"));
                        Log.i("EMAIL", "Enviando email...");
                    } catch (android.content.ActivityNotFoundException e){
                        Toast.makeText(getApplicationContext(), "NO existe ningún cliente de email instalado!.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
}
