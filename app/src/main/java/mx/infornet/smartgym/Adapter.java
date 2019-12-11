package mx.infornet.smartgym;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.RutinaViewHolder>{

    private Context mCtx;
    private List<Rutinas> rutinasList;

    public Adapter(Context mCtx, List<Rutinas> rutinasList){
        this.mCtx = mCtx;
        this.rutinasList = rutinasList;
    }


    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_rutinas_layout, null);

        return new RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        Rutinas rutina = rutinasList.get(position);

        holder.nombre.setText(rutina.getNombre());
        //holder.autor.setText(rutina.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return rutinasList.size();
    }

    @Override
    public long getItemId(int position) {
        return rutinasList.get(position).getId();
    }


    class RutinaViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, autor;


        public RutinaViewHolder(View itemView){
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre_rutina);
            //autor = itemView.findViewById(R.id.autor_rutina);
            CardView cardView = itemView.findViewById(R.id.card_view_item);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int requestCode = getAdapterPosition();
                    String nombreRutina = rutinasList.get(requestCode).getNombre();
                    int idRutina = rutinasList.get(requestCode).getId();
                    String descRutina = rutinasList.get(requestCode).getDescripcion();
                    //int autorRutina = rutinasList.get(requestCode).getIdCoach();

                    Intent intentVerRutina = new Intent(mCtx, VerRutinaListaActivity.class);
                    intentVerRutina.putExtra("id", idRutina);
                    intentVerRutina.putExtra("nombre", nombreRutina);
                    intentVerRutina.putExtra("descripcion", descRutina);
                    //intentVerRutina.putExtra("autor", autorRutina);

                    mCtx.startActivity(intentVerRutina);
                }
            });

        }
    }

    public void EnviarRutinas(){

    }
}
