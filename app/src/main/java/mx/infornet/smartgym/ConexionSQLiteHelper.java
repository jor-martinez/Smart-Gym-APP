package mx.infornet.smartgym;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Puchita on 15/03/2018.
 */

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    private static final String TABLE_USER = "usuarios";
    private static final String KEY_ID = "_id";
    private static final String KEY_ID_USER = "idUsuarios";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDOS = "apat";
    private static final String KEY_FECHA_NACIMIENTO = "fechaDeNacimiento";
    private static final String KEY_TELEFONO  = "telefono";
    private static final String KEY_TEL_EMERG = "telefonoEmergencia";
    private static final String KEY_CONDICION = "condicionFisica";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKEN_TYPE = "tokenType";
    private static final String KEY_TOKEN_EXPIRE = "tokenExpire";
    private static final String KEY_ID_GYM = "idGimnasio";
    private static final String KEY_ID_PLAN = "idPlan";
    private static final String KEY_ID_RUTINA = "idRutina";
    private static final String KEY_ID_ALIMEN = "idPlanAlimentacion";
    private static final String KEY_SEXO = "sexo";
    private static final String KEY_PESO = "peso";
    private static final String KEY_ESTATURA = "estatura";
    private static final String KEY_OBJETIVO = "objetivo";

    public ConexionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE =
                "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_ID_USER + " INTEGER, "
                + KEY_EMAIL + " TEXT, "
                + KEY_NOMBRE + " TEXT, "
                + KEY_APELLIDOS + " TEXT, "
                + KEY_ESTATURA + " DECIMAL, "
                + KEY_PESO + " DECIMAL, "
                + KEY_SEXO + " TEXT, "
                + KEY_OBJETIVO + " TEXT, "
                + KEY_FECHA_NACIMIENTO + " TEXT,"
                + KEY_TELEFONO + " TEXT,"
                + KEY_TEL_EMERG + " TEXT,"
                + KEY_CONDICION + " TEXT,"
                + KEY_ID_GYM + " INTEGER,"
                + KEY_ID_PLAN + " INTEGER,"
                + KEY_ID_RUTINA + " INTEGER,"
                + KEY_ID_ALIMEN + " INTEGER,"
                + KEY_TOKEN + " TEXT,"
                + KEY_TOKEN_TYPE + " TEXT,"
                + KEY_TOKEN_EXPIRE + " TEXT)";

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
