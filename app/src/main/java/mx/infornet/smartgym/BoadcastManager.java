package mx.infornet.smartgym;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class BoadcastManager extends BroadcastReceiver {

    private int idNotify = 1;
    private String channelId = "my_channel_01";
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Alarma cada 15 min", Toast.LENGTH_LONG).show();
        Log.d("alarma", "cada 15 min");

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(context, null);

        Intent notificationIntent = new Intent(context, AvancePerderPesoActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent1 = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //nombre visible del canal
            CharSequence name = "Atención !!";

            //descripcion visible del canal
            String descripcion = "Quedan pocos días para que se termine tu mensaualidad";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);

            //se configura el canal de la notificacion
            mChannel.setDescription(descripcion);
            mChannel.enableLights(true);

            //se determina luces, vibracion, etc
            mChannel.setLightColor(Color.BLACK);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[] {100,200,300,400,500,400,300,200,400});
            manager.createNotificationChannel(mChannel);

            mBuilder = new NotificationCompat.Builder(context, channelId);

        }

        mBuilder
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("Atención !!")
                .setContentText("Te quedan pocos días")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Quedan pocos días para que se termine tu mensualidad, acercate a tu gimnasio a realizar tu pago"))
                .setAutoCancel(true)
                .setTicker("Notificacion !")
                .setContentIntent(intent1);

        manager.notify(idNotify, mBuilder.build());

    }

}