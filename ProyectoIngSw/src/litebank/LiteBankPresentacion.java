package litebank;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class LiteBankPresentacion extends JFrame {
    private GestorCliente gestorCliente = new GestorCliente();
    private GestorCuenta gestorCuenta = new GestorCuenta(gestorCliente);
    private GestorTarjeta gestorTarjeta = new GestorTarjeta(gestorCliente, gestorCuenta);
    private GestorTransferencia gestorTransferencia = new GestorTransferencia(gestorCuenta);
    private GestorDeposito gestorDeposito = new GestorDeposito(gestorCuenta);
    private GestorRetiro gestorRetiro = new GestorRetiro(gestorCuenta);
    private GestorSolicitudCuenta gestorSolicitud = new GestorSolicitudCuenta(gestorCliente, gestorCuenta);

    private Cliente usuario; 

    public LiteBankPresentacion() {
        super("LiteBank - Banca Móvil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(740,560);
        setLocationRelativeTo(null);

        gestorCuenta.setGestorTarjeta(gestorTarjeta);

        
        try {
            String cedLuis = "0607080910";
            Cliente luis = gestorCliente.buscarPorCedula(cedLuis);
            if (luis != null) {
                boolean yaTiene = (luis.getTarjetas() != null && !luis.getTarjetas().isEmpty());
                if (!yaTiene) {
                    Cuenta[] cuentasLuis = gestorCuenta.cuentasDe(cedLuis);
                    if (cuentasLuis != null && cuentasLuis.length > 0) {
                        String cuentaElegida = null;
                        // Priorizar Ahorros activa
                        for (Cuenta c : cuentasLuis) {
                            if (c.isActiva() && "Ahorros".equalsIgnoreCase(c.getTipo())) {
                                cuentaElegida = c.getNumero();
                                break;
                            }
                        }
                        if (cuentaElegida == null) {
                            for (Cuenta c : cuentasLuis) {
                                if (c.isActiva()) { cuentaElegida = c.getNumero(); break; }
                            }
                        }
                        if (cuentaElegida != null) {
                            try {
                                gestorTarjeta.emitir(cedLuis, cuentaElegida, "Debito");
                            } catch (Exception ex) {
                                System.err.println("No se pudo emitir tarjeta por defecto para Luis: " + ex.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Inicialización de tarjeta por defecto falló: " + e.getMessage());
        }
       

        mostrarLogin();
    }

    // ================= LOGIN =================
    private void mostrarLogin() {
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JTextField tCedula = new JTextField();
        JPasswordField tPass = new JPasswordField();
        JButton bLogin = new JButton("Ingresar");
        p.add(new JLabel("  Cédula (10 dígitos):")); p.add(tCedula);
        p.add(new JLabel("  Contraseña:")); p.add(tPass);
        p.add(new JLabel("")); p.add(bLogin);

        bLogin.addActionListener(e->{
            String ced = tCedula.getText().trim();
            String pass = new String(tPass.getPassword());
            if (!ced.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Cédula debe tener 10 dígitos numéricos.");
                return;
            }
            Cliente c = gestorCliente.login(ced, pass);
            if (c==null) { JOptionPane.showMessageDialog(this, "Credenciales incorrectas"); return; }
            this.usuario = c;
            mostrarMenu();
        });

        setContentPane(p);
        setSize(400,200);          
        setLocationRelativeTo(null); 
        revalidate();
    }

    // ================= MENU (botones que abren ventanas) =================
    private void mostrarMenu() {
        JPanel menu = new JPanel(new GridLayout(0,1,8,8));
        JButton bTransf = new JButton("Transferencia");
        JButton bDep = new JButton("Depósito (código)");
        JButton bRet = new JButton("Retiro (código)");
        JButton bAbrir = new JButton("Abrir Cuenta");
        JButton bCerrar = new JButton("Cerrar Cuenta");
        JButton bEmitir = new JButton("Emitir Tarjeta (Débito)");
        JButton bBloq = new JButton("Bloquear Tarjeta");
        JButton bVerT = new JButton("Ver Tarjetas");
        JButton bVerC = new JButton("Ver Cuentas");

        menu.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        menu.add(bTransf); menu.add(bDep); menu.add(bRet);
        menu.add(bAbrir); menu.add(bCerrar);
        menu.add(bEmitir); menu.add(bBloq); menu.add(bVerT);
        menu.add(bVerC);

        bTransf.addActionListener(e-> ventanaTransferencia());
        bDep.addActionListener(e-> ventanaDeposito());
        bRet.addActionListener(e-> ventanaRetiro());
        bAbrir.addActionListener(e-> ventanaAbrirCuenta());
        bCerrar.addActionListener(e-> ventanaCerrarCuenta());
        bEmitir.addActionListener(e-> ventanaEmitirTarjeta());
        bBloq.addActionListener(e-> ventanaBloquearTarjeta());
        bVerT.addActionListener(e-> ventanaVerTarjetas());
        bVerC.addActionListener(e-> ventanaVerCuentas());

        setContentPane(menu);
        setSize(740,560);
        setLocationRelativeTo(null);
        revalidate();
    }

    // ===== Helpers comunes =====
    private JComboBox<String> comboCuentasUsuario() {
        Cuenta[] cs = gestorCuenta.cuentasDe(usuario.getCedula());
        String[] opts = new String[cs.length];
        for (int i=0;i<cs.length;i++) {
            String estado = cs[i].isActiva() ? "ACTIVA" : "CERRADA";
            opts[i] = cs[i].getNumero() + " ("+cs[i].getTipo()+") saldo=" + cs[i].getSaldo() + " ["+estado+"]";
        }
        return new JComboBox<>(opts);
    }
    private String cuentaSeleccionada(JComboBox<String> cb) {
        String sel = (String) cb.getSelectedItem();
        if (sel==null) return null;
        return sel.split(" ")[0];
    }
    private void refrescarCuentas(JComboBox<String> cb) {
        cb.removeAllItems();
        JComboBox<String> nuevo = comboCuentasUsuario();
        for (int i=0;i<nuevo.getItemCount();i++) cb.addItem(nuevo.getItemAt(i));
    }

    // ================= Ventanas =================

    private void ventanaTransferencia() {
        JDialog d = new JDialog(this, "Transferencia", true);
        d.setSize(520,280); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JComboBox<String> cbOrigen = comboCuentasUsuario();
        JTextField tDestino = new JTextField();
        JButton bVal = new JButton("Validar cuenta destino");
        JTextField tMonto = new JTextField();
        JButton bOK = new JButton("Transferir");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Cuenta origen:")); p.add(cbOrigen);
        p.add(new JLabel("Cuenta destino (6 dígitos):")); p.add(tDestino);
        p.add(bVal); p.add(new JLabel(""));
        p.add(new JLabel("Monto:")); p.add(tMonto);
        p.add(new JLabel("")); p.add(bOK);

        bVal.addActionListener(e-> validarCuentaDialog(d, tDestino.getText().trim()));

        bOK.addActionListener(e->{
            try {
                String origen = cuentaSeleccionada(cbOrigen);
                String destino = tDestino.getText().trim();
                double monto = Double.parseDouble(tMonto.getText());
                Comprobante comp = gestorTransferencia.transferir(origen, destino, monto);
                JOptionPane.showMessageDialog(d, "OK:\n" + comp.toString());
                refrescarCuentas(cbOrigen);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d,"Transferencia fallida: " + ex.getMessage());
            }
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaDeposito() {
        JDialog d = new JDialog(this, "Depósito (código)", true);
        d.setSize(560,300); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JTextField tCuenta = new JTextField();
        JTextField tMonto = new JTextField();
        JButton bVal = new JButton("Validar cuenta destino");
        JButton bGen = new JButton("Generar código");
        JTextField tCodigo = new JTextField();
        JButton bApli = new JButton("Validar código y aplicar");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Cuenta destino (6 dígitos):")); p.add(tCuenta);
        p.add(bVal); p.add(new JLabel(""));
        p.add(new JLabel("Monto:")); p.add(tMonto);
        p.add(new JLabel("Código:")); p.add(tCodigo);
        p.add(bGen); p.add(bApli);

        bVal.addActionListener(e-> validarCuentaDialog(d, tCuenta.getText().trim()));

        bGen.addActionListener(e->{
            try {
                String n = tCuenta.getText().trim();
                if (!n.matches("\\d{6}")) { JOptionPane.showMessageDialog(d,"Cuenta inválida"); return; }
                double monto = Double.parseDouble(tMonto.getText());
                Deposito dep = gestorDeposito.generarCodigo(n, monto);
                JOptionPane.showMessageDialog(d, "Código generado (5 min): " + dep.codigo.getCodigo());
            } catch (Exception ex) { JOptionPane.showMessageDialog(d,"Error: "+ex.getMessage()); }
        });

        bApli.addActionListener(e->{
            try {
                Comprobante comp = gestorDeposito.aplicar(tCodigo.getText().trim());
                JOptionPane.showMessageDialog(d, "OK:\n" + comp.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Depósito fallido: " + ex.getMessage());
            }
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaRetiro() {
        JDialog d = new JDialog(this, "Retiro (código)", true);
        d.setSize(560,300); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JComboBox<String> cbCuenta = comboCuentasUsuario();
        JTextField tMonto = new JTextField();
        JButton bGen = new JButton("Generar código");
        JTextField tCodigo = new JTextField();
        JButton bApli = new JButton("Validar código y entregar");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Cuenta origen:")); p.add(cbCuenta);
        p.add(new JLabel("Monto:")); p.add(tMonto);
        p.add(new JLabel("Código:")); p.add(tCodigo);
        p.add(bGen); p.add(bApli);

        bGen.addActionListener(e->{
            try {
                String cuenta = cuentaSeleccionada(cbCuenta);
                double monto = Double.parseDouble(tMonto.getText());
                Retiro r = gestorRetiro.generarCodigo(cuenta, monto);
                JOptionPane.showMessageDialog(d, "Código generado (5 min): " + r.codigo.getCodigo());
            } catch (Exception ex) { JOptionPane.showMessageDialog(d,"Error: "+ex.getMessage()); }
        });

        bApli.addActionListener(e->{
            try {
                Comprobante comp = gestorRetiro.aplicar(tCodigo.getText().trim());
                JOptionPane.showMessageDialog(d, "OK:\n" + comp.toString());
                refrescarCuentas(cbCuenta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Retiro fallido: " + ex.getMessage());
            }
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaAbrirCuenta() {
        JDialog d = new JDialog(this, "Abrir Cuenta", true);
        d.setSize(420,220); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JRadioButton rAhorros = new JRadioButton("Ahorros", true);
        JRadioButton rCorr = new JRadioButton("Corriente");
        ButtonGroup bg = new ButtonGroup(); bg.add(rAhorros); bg.add(rCorr);
        JButton bAbrir = new JButton("Abrir (sin saldo inicial)");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Tipo:"));
        JPanel tipos = new JPanel(); tipos.add(rAhorros); tipos.add(rCorr);
        p.add(tipos);
        p.add(new JLabel("")); p.add(bAbrir);

        bAbrir.addActionListener(e->{
            String tipo = rAhorros.isSelected()? "Ahorros" : "Corriente";
            Cuenta c = gestorCuenta.abrirCuenta(usuario.getCedula(), tipo);
            JOptionPane.showMessageDialog(d, "Cuenta abierto: " + c.getNumero() + " ("+tipo+"), saldo=" + c.getSaldo());
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaCerrarCuenta() {
        JDialog d = new JDialog(this, "Cerrar Cuenta", true);
        d.setSize(540,220); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JComboBox<String> cbCerrar = comboCuentasUsuario();
        JButton bCerrar = new JButton("Cerrar (verificación)");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Cuenta a cerrar:")); p.add(cbCerrar);
        p.add(new JLabel("")); p.add(bCerrar);

        bCerrar.addActionListener(e->{
            String numero = cuentaSeleccionada(cbCerrar);
            if (numero==null) { JOptionPane.showMessageDialog(d,"Seleccione cuenta"); return; }
            String p1 = JOptionPane.showInputDialog(d, "Nombre del titular:");
            String p2 = JOptionPane.showInputDialog(d, "Teléfono del titular:");
            if (p1==null || p2==null) return;
            if (!usuario.getNombre().equals(p1) || !usuario.getTelefono().equals(p2)) {
                JOptionPane.showMessageDialog(d, "Respuestas incorrectas. No se cerró la cuenta.");
                return;
            }
            boolean ok = gestorCuenta.cerrarCuenta(numero);
            JOptionPane.showMessageDialog(d, ok? "Cuenta cerrada. (Tarjetas asociadas bloqueadas automáticamente)" : "No se pudo cerrar.");
            refrescarCuentas(cbCerrar);
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaEmitirTarjeta() {
        JDialog d = new JDialog(this, "Emitir Tarjeta (Débito)", true);
        d.setSize(540,220); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JComboBox<String> cbCuenta = comboCuentasUsuario();
        JButton bEmitir = new JButton("Emitir Débito (costo $5)");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Cuenta:")); p.add(cbCuenta);
        p.add(new JLabel("")); p.add(bEmitir);

        bEmitir.addActionListener(e->{
            try {
                String cuenta = cuentaSeleccionada(cbCuenta);
                String num = gestorTarjeta.emitir(usuario.getCedula(), cuenta, "Debito");
                JOptionPane.showMessageDialog(d, "Tarjeta emitida (Débito): " + num);
                refrescarCuentas(cbCuenta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "No se pudo emitir: " + ex.getMessage());
            }
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaBloquearTarjeta() {
        JDialog d = new JDialog(this, "Bloquear Tarjeta", true);
        d.setSize(600,260); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        JComboBox<String> cbCuenta = comboCuentasUsuario();
        JComboBox<String> cbTarjeta = new JComboBox<>();
        JButton bCargar = new JButton("Cargar tarjetas");
        JButton bBloq = new JButton("Bloquear (verificación)");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Cuenta:")); p.add(cbCuenta);
        p.add(new JLabel("Tarjeta:")); p.add(cbTarjeta);
        p.add(bCargar); p.add(bBloq);

        bCargar.addActionListener(e->{
            cbTarjeta.removeAllItems();
            String cuenta = cuentaSeleccionada(cbCuenta);
            ArrayList<Tarjeta> ts = gestorTarjeta.tarjetasDeClienteYCuenta(usuario.getCedula(), cuenta);
            if (ts.isEmpty()) { JOptionPane.showMessageDialog(d,"No hay tarjetas para esa cuenta."); return; }
            for (Tarjeta t: ts) cbTarjeta.addItem(t.getNumero() + (t.isBloqueada()? " [BLOQUEADA]":""));
        });

        bBloq.addActionListener(e->{
            String sel = (String) cbTarjeta.getSelectedItem();
            if (sel==null) { JOptionPane.showMessageDialog(d,"Seleccione tarjeta"); return; }
            String numero = sel.split(" ")[0];
            String p1 = JOptionPane.showInputDialog(d, "Nombre del titular:");
            String p2 = JOptionPane.showInputDialog(d, "Teléfono del titular:");
            if (p1==null || p2==null) return;
            if (!usuario.getNombre().equals(p1) || !usuario.getTelefono().equals(p2)) {
                JOptionPane.showMessageDialog(d, "Respuestas incorrectas. No se bloqueó la tarjeta.");
                return;
            }
            try {
                gestorTarjeta.bloquear(usuario.getCedula(), numero);
                JOptionPane.showMessageDialog(d, "Tarjeta bloqueada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "No se pudo bloquear: " + ex.getMessage());
            }
            String cuenta = cuentaSeleccionada(cbCuenta);
            cbTarjeta.removeAllItems();
            ArrayList<Tarjeta> ts = gestorTarjeta.tarjetasDeClienteYCuenta(usuario.getCedula(), cuenta);
            for (Tarjeta t: ts) cbTarjeta.addItem(t.getNumero() + (t.isBloqueada()? " [BLOQUEADA]":""));
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaVerTarjetas() {
        JDialog d = new JDialog(this, "Mis Tarjetas", true);
        d.setSize(560,340); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(12,50);
        area.setEditable(false);
        JButton bRef = new JButton("Refrescar");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        p.add(bRef, BorderLayout.SOUTH);

        bRef.addActionListener(e->{
            area.setText("");
            ArrayList<Tarjeta> ts = usuario.getTarjetas();
            if (ts.isEmpty()) { area.setText("No tienes tarjetas.\n"); return; }
            for (Tarjeta t: ts) {
                area.append(t.getNumero()+" ("+t.getTipo()+") cuenta "+t.getCuentaNumero()+(t.isBloqueada()? " [BLOQUEADA]":"")+"\n");
            }
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void ventanaVerCuentas() {
        JDialog d = new JDialog(this, "Mis Cuentas", true);
        d.setSize(560,340); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(12,50);
        area.setEditable(false);
        JButton bRef = new JButton("Refrescar");

        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        p.add(bRef, BorderLayout.SOUTH);

        bRef.addActionListener(e->{
            area.setText("");
            Cuenta[] cs = gestorCuenta.cuentasDe(usuario.getCedula());
            if (cs.length==0) { area.setText("No tienes cuentas.\n"); return; }
            for (Cuenta c: cs) {
                String estado = c.isActiva() ? "ACTIVA" : "CERRADA";
                area.append(c.getNumero()+" ("+c.getTipo()+") saldo="+c.getSaldo()+" ["+estado+"]\n");
            }
        });

        d.setContentPane(p);
        d.setVisible(true);
    }

    private void validarCuentaDialog(Window owner, String cuenta) {
        if (!cuenta.matches("\\d{6}")) { JOptionPane.showMessageDialog(owner,"Cuenta inválida (debe tener 6 dígitos)"); return; }
        Cuenta c = gestorCuenta.buscar(cuenta);
        if (c==null) { JOptionPane.showMessageDialog(owner,"Cuenta no existe"); return; }
        Cliente du = gestorCliente.buscarPorCedula(c.getCedulaCliente());
        JOptionPane.showMessageDialog(owner,"Titular: " + du.getNombre() + " ("+du.getCedula()+")");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new LiteBankPresentacion().setVisible(true));
    }
}
