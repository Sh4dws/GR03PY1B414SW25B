package litebank;

public class Retiro {
    public final String cuentaOrigen;
    public final double monto;
    public final Codigo codigo;
    public boolean aplicado = false;
    public Comprobante comprobante = null;

    public Retiro(String cuentaOrigen, double monto, Codigo codigo) {
        this.cuentaOrigen = cuentaOrigen; this.monto = monto; this.codigo = codigo;
    }
}
