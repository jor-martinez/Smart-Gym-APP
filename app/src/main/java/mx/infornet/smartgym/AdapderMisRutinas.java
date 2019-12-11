package mx.infornet.smartgym;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AdapderMisRutinas extends RecyclerView.Adapter<AdapderMisRutinas.RutinaViewHolder> {

    private Context mCtx;
    private List<Rutinas> rutinasList;

    public AdapderMisRutinas(Context mCtx, List<Rutinas> rutinasList){
        this.mCtx = mCtx;
        this.rutinasList = rutinasList;
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_rutinas_layout, null);

        return new AdapderMisRutinas.RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        Rutinas rutina = rutinasList.get(position);

        holder.nombre.setText(rutina.getNombre());
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

        TextView nombre;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre_rutina);

            CardView cardView = itemView.findViewById(R.id.card_view_item);

            cardView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {

                    int requestCode = getAdapterPosition();
                    String nombreRutina = rutinasList.get(requestCode).getNombre();
                    int idRutina = rutinasList.get(requestCode).getId();
                    String descRutina = rutinasList.get(requestCode).getDescripcion();

                    Intent intentVerRutina = new Intent(mCtx, VerRutinaMiembroActivity.class);
                    intentVerRutina.putExtra("id", idRutina);
                    intentVerRutina.putExtra("nombre", nombreRutina);
                    intentVerRutina.putExtra("descripcion", descRutina);

                    mCtx.startActivity(intentVerRutina);
                }
            });

        }
    }
}
