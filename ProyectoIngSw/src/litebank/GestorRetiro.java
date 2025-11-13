package litebank;

import java.util.concurrent.atomic.AtomicInteger;

public class GestorRetiro {
    private final GestorCuenta gestorCuenta;
    private final RetiroPersistencia repo = new RetiroPersistencia();
    private final AtomicInteger secComp = new AtomicInteger(1); // 4 dígitos

    public GestorRetiro(GestorCuenta gestorCuenta) { this.gestorCuenta = gestorCuenta; }

    private String nextId4() {
        int n = secComp.getAndUpdate(x -> (x>=9999?1:x+1));
        return String.format("%04d", n);
    }
    
    public Retiro generarCodigo(String cuentaOrigen, double monto) {
        if (!cuentaOrigen.matches("\\d{6}")) throw new IllegalArgumentException("Cuenta inválida");
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        Codigo cod = new Codigo(Codigo.codigo6(), System.currentTimeMillis()+5*60*1000);
        Retiro r = new Retiro(cuentaOrigen, monto, cod);
        repo.guardar(r);
        return r;
    }
    
    public Comprobante aplicar(String codigo) {
        Retiro r = repo.buscarPorCodigo(codigo);
        if (r == null) throw new IllegalArgumentException("Código inválido");
        if (!r.codigo.vigente()) throw new IllegalArgumentException("Código caducado");

        Cuenta cta = gestorCuenta.buscar(r.cuentaOrigen);
        if (cta == null) throw new IllegalArgumentException("Cuenta origen no existe");
        if (!cta.isActiva()) throw new IllegalArgumentException("Cuenta origen inactiva");
        if (cta.getSaldo() < r.monto) throw new IllegalArgumentException("Saldo insuficiente");

        boolean ok = gestorCuenta.retirar(r.cuentaOrigen, r.monto);
        if (!ok) throw new IllegalArgumentException("No se pudo retirar");

        r.codigo.marcarUsado(); r.aplicado = true;
        Comprobante c = new Comprobante(nextId4(), "RETIRO",
                "Retiro de " + r.cuentaOrigen + " por " + r.monto);
        r.comprobante = c;
        repo.eliminarPorCodigo(codigo);
        return c;
    }
}
