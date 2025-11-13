package litebank;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comprobante {
    private final String id;      
    private final String tipo;    
    private final String detalle;
    private final long fechaMs;

    public Comprobante(String id, String tipo, String detalle) {
        this.id = id; this.tipo = tipo; this.detalle = detalle; this.fechaMs = System.currentTimeMillis();
    }

    @Override public String toString() {
        String f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(fechaMs));
        return "COMPROBANTE - " + tipo + "\n"
             + "ID COMPROBANTE: " + id + "\n"
             + detalle + "\n"
             + "Fecha: " + f;
    }
}

