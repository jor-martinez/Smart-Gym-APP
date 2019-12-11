package mx.infornet.smartgym;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ParseJson {

    public List<Frases> readJsonStream(InputStream in)throws IOException{
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        try {
            return readFrasesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<Frases> readFrasesArray(JsonReader reader) throws IOException {
        List<Frases> recetas = new ArrayList<Frases>();

        reader.beginArray();
        while (reader.hasNext()) {
            recetas.add(readReceta(reader));
        }
        reader.endArray();
        return recetas;
    }

    public Frases readReceta(JsonReader reader) throws IOException {
        int id = -1;
        String frase = null;
        String autor = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("frase")) {
                frase = reader.nextString();
            } else if (name.equals("autor") && reader.peek() != JsonToken.NULL) {
                autor = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Frases(id, frase, autor);
    }
}
