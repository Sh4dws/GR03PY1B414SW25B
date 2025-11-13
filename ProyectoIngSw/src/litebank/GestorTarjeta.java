package litebank;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GestorTarjeta {
    private final GestorCliente gestorCliente;
    private final GestorCuenta gestorCuenta;
    private final TarjetaPersistencia repo = new TarjetaPersistencia();
    private final AtomicInteger sec = new AtomicInteger(400001);

    public GestorTarjeta(GestorCliente gc, GestorCuenta gcu) {
        this.gestorCliente = gc; this.gestorCuenta = gcu;
    }

    private String next6() {
        String s = String.valueOf(sec.getAndIncrement());
        while (s.length()<6) s="0"+s;
        if (s.length()>6) s=s.substring(s.length()-6);
        return s;
    }

    public String emitir(String cedula, String cuentaNumero, String tipoSolicitado) {
        Cuenta cta = gestorCuenta.buscar(cuentaNumero);
        if (cta == null) throw new IllegalArgumentException("Cuenta inexistente");
        if (!cta.isActiva()) throw new IllegalArgumentException("Cuenta inactiva");
        if (!cta.getCedulaCliente().equals(cedula)) throw new IllegalArgumentException("La cuenta no pertenece al cliente");
        if (cta.getSaldo() < 5.0) throw new IllegalArgumentException("Saldo insuficiente (se requieren $5)");
        if (!gestorCuenta.retirar(cta.getNumero(), 5.0)) throw new IllegalArgumentException("No se pudo debitar el costo de emisión");

        String numero = next6();
        Tarjeta t = Tarjeta.desdeFormulario(numero, cuentaNumero);
        Cliente cli = gestorCliente.buscarPorCedula(cedula);
        if (cli != null) cli.addTarjeta(t);
        repo.guardar(t);
        return numero;
    }

    public void bloquear(String cedula, String numeroTarjeta) {
        Tarjeta t = repo.buscarPorNumero(numeroTarjeta);
        if (t == null) throw new IllegalArgumentException("Tarjeta no encontrada");
        t.bloquear(); repo.guardar(t);
    }

    public int bloquearTodasDeCuenta(String cedula, String cuentaNumero) {
        ArrayList<Tarjeta> ts = repo.listarPorCuenta(cuentaNumero);
        int n=0; for (Tarjeta t: ts) if (!t.isBloqueada()) { t.bloquear(); repo.guardar(t); n++; }
        // reflejar estado en cliente si está cargado
        Cliente cli = gestorCliente.buscarPorCedula(cedula);
        if (cli != null) {
            for (Tarjeta tCli : cli.getTarjetas()) {
                if (tCli.getCuentaNumero().equals(cuentaNumero) && !tCli.isBloqueada()) tCli.bloquear();
            }
        }
        return n;
    }

    public ArrayList<Tarjeta> tarjetasDeClienteYCuenta(String cedula, String cuentaNumero) {
        ArrayList<Tarjeta> base = repo.listarPorCuenta(cuentaNumero);
        return base;
    }
}
