package litebank;

public class Cuenta {
    private String numero;       
    private String tipo;         
    private double saldo;
    private String cedulaCliente;
    private boolean activa = true;

    private Cuenta(String numero, String tipo, double saldo, String cedulaCliente) {
        this.numero = numero; this.tipo = tipo; this.saldo = saldo; this.cedulaCliente = cedulaCliente;
    }

    public static Cuenta desdeFormulario(String numeroAsignado, String tipo, String cedulaCliente, Double saldoInicial) {
        if (numeroAsignado == null || !numeroAsignado.matches("\\d{6}")) throw new IllegalArgumentException("Número de cuenta inválido.");
        if (!"Ahorros".equals(tipo) && !"Corriente".equals(tipo)) throw new IllegalArgumentException("Tipo inválido.");
        if (cedulaCliente == null || !cedulaCliente.matches("\\d{10}")) throw new IllegalArgumentException("Cédula inválida.");
        double si = (saldoInicial == null)? 0.0 : saldoInicial;
        return new Cuenta(numeroAsignado, tipo, si, cedulaCliente);
    }

    public String getNumero() { return numero; }
    public String getTipo() { return tipo; }
    public double getSaldo() { return saldo; }
    public String getCedulaCliente() { return cedulaCliente; }
    public boolean isActiva() { return activa; }

    void depositar(double m) { saldo += m; }
    boolean retirar(double m) { if (!activa) return false; if (m <= saldo) { saldo -= m; return true; } return false; }
    void cerrar() { activa = false; }
}
