package mx.infornet.smartgym;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionSQLiteHelperPeso extends SQLiteOpenHelper {

    private static final String TABLE_PESO = "objetivo_perder_peso";
    private static final String KEY_ID = "_ID";
    private static final String KEY_PESO = "peso";
    private static final String KEY_FECHA = "fecha";
    private static final String KEY_ESTATURA = "estatura";
    private static final String KEY_META = "meta";
    private static final String KEY_TIEMPO = "tiempo";
    private static final String KEY_FECHA_FINAL = "fechaFinal";

    public ConexionSQLiteHelperPeso(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_OBJETIVO_PERDER_PESO_TABLE =
                "CREATE TABLE " + TABLE_PESO + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ESTATURA + " REAL, "
                + KEY_META + " INTEGER, "
                + KEY_TIEMPO + " INTEGER, "
                + KEY_PESO + " REAL NOT NULL, "
                + KEY_FECHA + " TEXT NOT NULL, "
                + KEY_FECHA_FINAL + " TEXT)";

        db.execSQL(CREATE_OBJETIVO_PERDER_PESO_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PESO);


        onCreate(db);
    }
}
