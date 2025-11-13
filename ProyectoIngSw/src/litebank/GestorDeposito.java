package litebank;

import java.util.concurrent.atomic.AtomicInteger;

public class GestorDeposito {
    private final GestorCuenta gestorCuenta;
    private final DepositoPersistencia repo = new DepositoPersistencia();
    private final AtomicInteger secComp = new AtomicInteger(1); 

    public GestorDeposito(GestorCuenta gestorCuenta) { this.gestorCuenta = gestorCuenta; }

    private String nextId4() {
        int n = secComp.getAndUpdate(x -> (x>=9999?1:x+1));
        return String.format("%04d", n);
    }

    public Deposito generarCodigo(String cuentaDestino, double monto) {
        if (!cuentaDestino.matches("\\d{6}")) throw new IllegalArgumentException("Cuenta inválida");
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        Codigo cod = new Codigo(Codigo.codigo6(), System.currentTimeMillis()+5*60*1000);
        Deposito d = new Deposito(cuentaDestino, monto, cod);
        repo.guardar(d);
        return d;
    }

    public Comprobante aplicar(String codigo) {
        Deposito d = repo.buscarPorCodigo(codigo);
        if (d == null) throw new IllegalArgumentException("Código inválido");
        if (!d.codigo.vigente()) throw new IllegalArgumentException("Código caducado");

        boolean ok = gestorCuenta.depositar(d.cuentaDestino, d.monto);
        if (!ok) throw new IllegalArgumentException("Cuenta destino inválida o inactiva");

        d.codigo.marcarUsado(); d.aplicado = true;
        Comprobante c = new Comprobante(nextId4(), "DEPÓSITO",
                "Depósito a " + d.cuentaDestino + " por " + d.monto);
        d.comprobante = c;
        repo.eliminarPorCodigo(codigo);
        return c;
    }
}
