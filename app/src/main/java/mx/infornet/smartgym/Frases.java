package mx.infornet.smartgym;

public class Frases {
    private int id;
    private String frase;
    private String autor;

    public Frases(int id, String frase, String autor) {
        this.id = id;
        this.frase = frase;
        this.autor = autor;
    }

    public int getId() {
        return id;
    }

    public String getFrase() {
        return frase;
    }

    public String getAutor() {
        return autor;
    }
}
