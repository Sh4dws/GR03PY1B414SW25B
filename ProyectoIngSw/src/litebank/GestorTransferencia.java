package litebank;

import java.util.concurrent.atomic.AtomicInteger;

public class GestorTransferencia {
    private final GestorCuenta gestorCuenta;
    private final TransferenciaPersistencia repo = new TransferenciaPersistencia();
    private final AtomicInteger secComp = new AtomicInteger(1); // ID comprobante 4 dígitos

    public GestorTransferencia(GestorCuenta gestorCuenta) {
        this.gestorCuenta = gestorCuenta;
    }

    private String nextId4() {
        int n = secComp.getAndUpdate(x -> (x >= 9999 ? 1 : x + 1));
        return String.format("%04d", n);
    }

    public Comprobante transferir(Transferencia t) {

        Cuenta o = gestorCuenta.buscar(t.getOrigen());
        if (o == null) throw new IllegalArgumentException("Cuenta origen no existe");
        if (!o.isActiva()) throw new IllegalArgumentException("Cuenta origen inactiva");

        Cuenta d = gestorCuenta.buscar(t.getDestino());
        if (d == null) throw new IllegalArgumentException("Cuenta destino no existe");
        if (!d.isActiva()) throw new IllegalArgumentException("Cuenta destino inactiva");

        if (o.getSaldo() < t.getMonto()) throw new IllegalArgumentException("Saldo insuficiente");

        if (!gestorCuenta.retirar(o.getNumero(), t.getMonto()))
            throw new IllegalArgumentException("No se pudo debitar");
        if (!gestorCuenta.depositar(d.getNumero(), t.getMonto()))
            throw new IllegalArgumentException("No se pudo acreditar");

        repo.guardar(t);

        return new Comprobante(
                nextId4(),
                "TRANSFERENCIA",
                "Transferencia de " + o.getNumero() + " a " + d.getNumero() + " por " + t.getMonto()
        );
    }

    public Comprobante transferir(String origen, String destino, double monto) {
        Transferencia t = Transferencia.desdeFormulario(origen, destino, monto);
        return transferir(t);
    }
}
