package litebank;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GestorCuenta {
    private final CuentaPersistencia repo = new CuentaPersistencia();
    private final GestorCliente gestorCliente;
    private GestorTarjeta gestorTarjeta;
    private final AtomicInteger sec = new AtomicInteger(100001);

    public GestorCuenta(GestorCliente gestorCliente) {
        this.gestorCliente = gestorCliente;
        abrirCuenta("0102030405", "Ahorros", 500.0);
        abrirCuenta("0607080910", "Ahorros", 300.0);
        abrirCuenta("0607080910", "Corriente", 1000.0);
    }

    public void setGestorTarjeta(GestorTarjeta gt) { this.gestorTarjeta = gt; }

    private String next6() {
        String s = String.valueOf(sec.getAndIncrement());
        while (s.length()<6) s="0"+s;
        if (s.length()>6) s=s.substring(s.length()-6);
        return s;
    }

    public Cuenta abrirCuenta(String cedula, String tipo) { return abrirCuenta(cedula, tipo, 0.0); }

    public Cuenta abrirCuenta(String cedula, String tipo, double saldoInicial) {
        if (gestorCliente.buscarPorCedula(cedula)==null) throw new IllegalArgumentException("Cliente no existe");
        Cuenta c = Cuenta.desdeFormulario(next6(), tipo, cedula, saldoInicial);
        repo.guardar(c);
        return c;
    }

    public boolean cerrarCuenta(String numero) {
        Cuenta c = repo.buscarPorNumero(numero);
        if (c==null || !c.isActiva()) return false;
        c.cerrar(); repo.guardar(c);
        if (gestorTarjeta!=null) gestorTarjeta.bloquearTodasDeCuenta(c.getCedulaCliente(), c.getNumero());
        return true;
    }

    public Cuenta buscar(String numero) { return repo.buscarPorNumero(numero); }

    public Cuenta[] cuentasDe(String cedula) {
        ArrayList<Cuenta> l = repo.listarPorCedula(cedula);
        return l.toArray(new Cuenta[0]);
    }

    public boolean depositar(String cuenta, double monto) {
        Cuenta d = repo.buscarPorNumero(cuenta);
        if (d==null || !d.isActiva()) return false;
        d.depositar(monto); repo.guardar(d); return true;
    }

    public boolean retirar(String cuenta, double monto) {
        Cuenta c = repo.buscarPorNumero(cuenta);
        if (c==null || !c.isActiva()) return false;
        boolean ok = c.retirar(monto); repo.guardar(c); return ok;
    }
}
