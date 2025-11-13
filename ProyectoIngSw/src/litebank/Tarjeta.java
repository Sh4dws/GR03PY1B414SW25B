package litebank;

public class Tarjeta {
    private String numero;       
    private String cuentaNumero; 
    private String tipo;         
    private boolean bloqueada;

    private Tarjeta(String numero, String cuentaNumero, String tipo) {
        this.numero = numero; this.cuentaNumero = cuentaNumero; this.tipo = tipo; this.bloqueada = false;
    }

    public static Tarjeta desdeFormulario(String numeroAsignado, String cuentaNumero) {
        if (numeroAsignado == null || !numeroAsignado.matches("\\d{6}")) throw new IllegalArgumentException("Número de tarjeta inválido.");
        if (cuentaNumero == null || !cuentaNumero.matches("\\d{6}")) throw new IllegalArgumentException("Cuenta inválida.");
        return new Tarjeta(numeroAsignado, cuentaNumero, "Debito");
    }

    public String getNumero() { return numero; }
    public String getCuentaNumero() { return cuentaNumero; }
    public String getTipo() { return tipo; }
    public boolean isBloqueada() { return bloqueada; }
    public void bloquear() { bloqueada = true; }
}
