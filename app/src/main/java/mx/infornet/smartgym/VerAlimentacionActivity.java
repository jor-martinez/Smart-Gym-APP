package mx.infornet.smartgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class VerAlimentacionActivity extends AppCompatActivity {

    private ImageView btn_back;
    private TextView titulo, descripcion_alim;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_alimentacion);

        titulo = findViewById(R.id.title_nombre_alimentacion);
        descripcion_alim = findViewById(R.id.tv_descr_alimentacion);
        btn_back = findViewById(R.id.btn_back_alimentacion_miembro);

        Intent intent = getIntent();

        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        id = intent.getIntExtra("id", 0);

        titulo.setText(nombre);
        descripcion_alim.setText(descripcion);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
