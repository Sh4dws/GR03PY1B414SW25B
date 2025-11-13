package litebank;

import java.util.ArrayList;

public class Cliente {
    private String cedula;   // 10 dígitos
    private String nombre;
    private String telefono;
    private String password;
    private ArrayList<Tarjeta> tarjetas = new ArrayList<>();

    private Cliente(String cedula, String nombre, String telefono, String password) {
        this.cedula = cedula; this.nombre = nombre; this.telefono = telefono; this.password = password;
    }

    // La ventana pasa datos aquí (conversión/validación)
    public static Cliente desdeFormulario(String cedula, String nombre, String telefono, String password) {
        if (cedula == null || !cedula.matches("\\d{10}")) throw new IllegalArgumentException("Cédula inválida (10 dígitos).");
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("Nombre requerido.");
        if (telefono == null || telefono.trim().isEmpty()) throw new IllegalArgumentException("Teléfono requerido.");
        if (password == null || password.trim().isEmpty()) throw new IllegalArgumentException("Contraseña requerida.");
        return new Cliente(cedula.trim(), nombre.trim(), telefono.trim(), password);
    }

    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getPassword() { return password; }

    // Para mantener compatibilidad con tu GUI (verTarjetas)
    public ArrayList<Tarjeta> getTarjetas() { return tarjetas; }
    public void addTarjeta(Tarjeta t) { tarjetas.add(t); }
}
