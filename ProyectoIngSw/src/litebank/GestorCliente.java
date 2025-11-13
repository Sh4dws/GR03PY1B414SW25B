package litebank;

public class GestorCliente {
    private final ClientePersistencia repo = new ClientePersistencia();

    public GestorCliente() {
        repo.guardar(Cliente.desdeFormulario("0102030405","Ana Molina","099111111","ana123"));
        repo.guardar(Cliente.desdeFormulario("0607080910","Luis Pérez","098222222","luis123"));
    }

    public Cliente login(String cedula, String password) {
        Cliente c = repo.buscarPorCedula(cedula);
        if (c!=null && c.getPassword().equals(password)) return c;
        return null;
    }

    public void registrar(Cliente c) { repo.guardar(c); }

    public Cliente buscarPorCedula(String cedula) { return repo.buscarPorCedula(cedula); }
}
