package litebank;

public class Deposito {
    public final String cuentaDestino;
    public final double monto;
    public final Codigo codigo;
    public boolean aplicado = false;
    public Comprobante comprobante = null;

    public Deposito(String cuentaDestino, double monto, Codigo codigo) {
        this.cuentaDestino = cuentaDestino; this.monto = monto; this.codigo = codigo;
    }
}
