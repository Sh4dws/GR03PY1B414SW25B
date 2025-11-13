package litebank;

import java.util.ArrayList;

public class RetiroPersistencia {
    private final ArrayList<Retiro> pendientes = new ArrayList<>();

    public void guardar(Retiro r) { pendientes.add(r); }

    public Retiro buscarPorCodigo(String codigo) {
        for (Retiro r : pendientes) if (r.codigo.getCodigo().equals(codigo)) return r;
        return null;
    }

    public boolean eliminarPorCodigo(String codigo) {
        for (int i=0;i<pendientes.size();i++) {
            if (pendientes.get(i).codigo.getCodigo().equals(codigo)) { pendientes.remove(i); return true; }
        }
        return false;
    }
}
