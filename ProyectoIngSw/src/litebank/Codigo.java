package litebank;

import java.util.Random;

public class Codigo {
    private String codigo;   
    private long venceEpoch; 
    private boolean usado = false;

    private static final Random R = new Random();
    public static String codigo6() {
        return String.format("%06d", R.nextInt(1_000_000));
    }

    public Codigo(String codigo, long venceEpoch) {
        this.codigo = codigo; this.venceEpoch = venceEpoch;
    }
    public String getCodigo() { return codigo; }
    public boolean vigente() { return System.currentTimeMillis() <= venceEpoch && !usado; }
    public void marcarUsado() { usado = true; }
}

