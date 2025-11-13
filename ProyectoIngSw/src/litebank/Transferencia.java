package litebank;

public class Transferencia {
    private final String origen;    
    private final String destino;   
    private final double monto;

    private Transferencia(String origen, String destino, double monto) {
        this.origen = origen;
        this.destino = destino;
        this.monto = monto;
    }
    
    public static Transferencia desdeFormulario(String origen, String destino, Double monto) {
        if (origen == null || !origen.matches("\\d{6}")) {
            throw new IllegalArgumentException("Cuenta origen inválida (6 dígitos).");
        }
        if (destino == null || !destino.matches("\\d{6}")) {
            throw new IllegalArgumentException("Cuenta destino inválida (6 dígitos).");
        }
        double m = (monto == null) ? 0.0 : monto.doubleValue();
        if (m <= 0) throw new IllegalArgumentException("Monto inválido.");
        return new Transferencia(origen, destino, m);
    }

    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public double getMonto() { return monto; }
}
