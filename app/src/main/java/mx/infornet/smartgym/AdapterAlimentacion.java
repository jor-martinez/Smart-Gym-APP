package mx.infornet.smartgym;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterAlimentacion extends RecyclerView.Adapter<AdapterAlimentacion.AlimentacionViewHolder> {

    private Context mCtx;
    private List<PlanesAlimentacion> alimentacionList;

    public AdapterAlimentacion(Context mCtx, List<PlanesAlimentacion> alimentacionList) {
        this.mCtx = mCtx;
        this.alimentacionList = alimentacionList;
    }


    @NonNull
    @Override
    public AlimentacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_rutinas_layout, null);

        return new AdapterAlimentacion.AlimentacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAlimentacion.AlimentacionViewHolder holder, int position) {
        PlanesAlimentacion planesAlimentacion = alimentacionList.get(position);

        holder.nombre_alim.setText(planesAlimentacion.getNombre());
    }

    @Override
    public int getItemCount() {
        return alimentacionList.size();
    }

    @Override
    public long getItemId(int position) {
        return alimentacionList.get(position).getId();
    }

    class AlimentacionViewHolder extends RecyclerView.ViewHolder {

        TextView nombre_alim;

        public AlimentacionViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre_alim = itemView.findViewById(R.id.nombre_rutina);

            CardView cardView = itemView.findViewById(R.id.card_view_item);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int requestCode = getAdapterPosition();
                    String nombreRutina = alimentacionList.get(requestCode).getNombre();
                    int idRutina = alimentacionList.get(requestCode).getId();
                    String descRutina = alimentacionList.get(requestCode).getDescripcion();

                    Intent intentVerRutina = new Intent(mCtx, VerAlimentacionActivity.class);
                    intentVerRutina.putExtra("id", idRutina);
                    intentVerRutina.putExtra("nombre", nombreRutina);
                    intentVerRutina.putExtra("descripcion", descRutina);

                    mCtx.startActivity(intentVerRutina);
                }
            });

        }
    }
}

