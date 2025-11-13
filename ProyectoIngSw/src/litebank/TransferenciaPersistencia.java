package litebank;

import java.util.ArrayList;

public class TransferenciaPersistencia {
    private final ArrayList<Transferencia> historial = new ArrayList<>();

    public void guardar(Transferencia t) {
        historial.add(t);
    }

    public ArrayList<Transferencia> listarTodas() {
        return new ArrayList<>(historial);
    }

    public ArrayList<Transferencia> listarPorCuentaOrigen(String cuentaOrigen) {
        ArrayList<Transferencia> out = new ArrayList<>();
        for (Transferencia t : historial) {
            if (t.getOrigen().equals(cuentaOrigen)) out.add(t);
        }
        return out;
    }

    public ArrayList<Transferencia> listarPorCuentaDestino(String cuentaDestino) {
        ArrayList<Transferencia> out = new ArrayList<>();
        for (Transferencia t : historial) {
            if (t.getDestino().equals(cuentaDestino)) out.add(t);
        }
        return out;
    }
}
