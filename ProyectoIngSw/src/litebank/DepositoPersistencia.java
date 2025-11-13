package litebank;

import java.util.ArrayList;

public class DepositoPersistencia {
    private final ArrayList<Deposito> pendientes = new ArrayList<>();

    public void guardar(Deposito d) { pendientes.add(d); }

    public Deposito buscarPorCodigo(String codigo) {
        for (Deposito d : pendientes) if (d.codigo.getCodigo().equals(codigo)) return d;
        return null;
    }

    public boolean eliminarPorCodigo(String codigo) {
        for (int i=0;i<pendientes.size();i++) {
            if (pendientes.get(i).codigo.getCodigo().equals(codigo)) { pendientes.remove(i); return true; }
        }
        return false;
    }
}
