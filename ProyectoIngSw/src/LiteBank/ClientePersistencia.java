package litebank;

import java.util.ArrayList;

public class ClientePersistencia {
    private final ArrayList<Cliente> datos = new ArrayList<>();

    public void guardar(Cliente c) {
        int idx = -1;
        for (int i=0;i<datos.size();i++) if (datos.get(i).getCedula().equals(c.getCedula())) { idx=i; break; }
        if (idx>=0) datos.set(idx, c); else datos.add(c);
    }

    public Cliente buscarPorCedula(String cedula) {
        for (Cliente c : datos) if (c.getCedula().equals(cedula)) return c;
        return null;
    }

    public ArrayList<Cliente> listar() { return new ArrayList<>(datos); }
}
