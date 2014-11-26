/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Codigo.Conexion;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mario A
 */
public class Ventana extends javax.swing.JFrame {

    private Conexion connection;
    // Modelos Tablas Mantenimiento
    private DefaultTableModel modeloMarca;
    private DefaultTableModel modeloFamilia;
    private DefaultTableModel modeloImpuesto;
    private DefaultTableModel modeloArticulo;
    private DefaultTableModel modeloTipoDeMovimiento;
    private DefaultTableModel modeloActualMantenimiento;
    // Modelos Tablas Procesos
    private DefaultTableModel modeloTomaFisica;
    private DefaultTableModel modeloDetalleTomaFisica;
    private DefaultTableModel modeloMovimientoInventario;
    private DefaultTableModel modeloDetalleMovimientoInventario;

    // ComboBox utilizado para seleccionar los elementos existentes en cierta tabla
    private JComboBox<String> cmbFamilias;
    private JComboBox<String> cmbMarcas;
    private JComboBox<String> cmbImpuestos;
    private JComboBox<String> cmbArticulos;
    private JComboBox<String> cmbTipoDeMovimientos;

    private int selectedRowTomaMovimiento = -1;
    private int selectedRowDetalleTomaMov = -1;

    // Arreglo para almacenar los datos de una fila, antes de modificarse algun compo de esta
    private Object[] datosFilaActual = null;
    private int numFilaAnterior = -1;
    private String pkSelectedRow = null;

    /**
     * Creates new form Ventana
     */
    public Ventana() {
        initComponents();
        this.setLocationRelativeTo(null);
        // Agrego los radio button 'tomaFisica' y 'movimientoInventario' al radioGroup
        Rdg_Procesos.add(Rdb_tomaFisica);
        Rdg_Procesos.add(Rdb_movimientoInventario);
        // Inhabilito el boton que ya esta seleccionado (tomaFisica)
        Rdb_tomaFisica.setEnabled(false);

        // Se crea la conexion con la base de datos
        try {
            connection = new Conexion("dbinventario", "usrUTN", "12345");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Ha ocurrido un error al cargar la base de datos",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        cargarDatosComboBoxs();
        inicializarModelos();

        Lbl_Titulo.setText("Tabla Familia");
        tbl_Tabla.setModel(modeloFamilia);
        modeloActualMantenimiento = modeloFamilia;

        tbl_tomaMovimiento.setModel(modeloTomaFisica);
        tbl_detalleTomaMovimiento.setModel(modeloDetalleTomaFisica);
        tbl_detalleTomaMovimiento.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cmbArticulos));
    }

    /**
     * Metodo utilizado para cargar todos los valores de las FK en su respectivo
     * ComboBox. Este valor es almacenado si la tupla posea el campo activo =
     * 'S'
     */
    private void cargarDatosComboBoxs() {
        ResultSet respuestaSelect;
        try {
            // Se cargan los valores para tipoDeMovimiento
            String[] movimientos = {"Resta", "No aplica", "Suma"};
            cmbTipoDeMovimientos = new JComboBox<>(movimientos);

            // Se cargan los valores para cmbFamilias
            cmbFamilias = new JComboBox<>();
            respuestaSelect = connection.select("cod_familia, descripcion",
                    "\"schinventario\".familia_articulo", "activo = 'S'");
            while (respuestaSelect.next()) {
                cmbFamilias.addItem(respuestaSelect.getString(1) + " = " + respuestaSelect.getString(2));
            }

            // Se cargan los valores para cmbMarcas
            cmbMarcas = new JComboBox<>();
            respuestaSelect = connection.select("cod_marca, descripcion",
                    "\"schinventario\".marca_articulo", "activo = 'S'");
            while (respuestaSelect.next()) {
                cmbMarcas.addItem(respuestaSelect.getString(1) + " = " + respuestaSelect.getString(2));
            }

            // Se cargan los valores para cmbImpuestos
            cmbImpuestos = new JComboBox<>();
            respuestaSelect = connection.select("cod_impuesto, descripcion",
                    "\"schinventario\".impuesto", "activo = 'S'");
            while (respuestaSelect.next()) {
                cmbImpuestos.addItem(respuestaSelect.getString(1) + " = " + respuestaSelect.getString(2));
            }

            // Se cargan los valores para cmbArticulos
            cmbArticulos = new JComboBox<>();
            respuestaSelect = connection.select("cod_articulo, descripcion",
                    "\"schinventario\".articulo", "activo = 'S'");
            while (respuestaSelect.next()) {
                cmbArticulos.addItem(respuestaSelect.getString(1) + " = " + respuestaSelect.getString(2));
            }

        } catch (SQLException ex) {
            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo utilizado para inicializar los modelos de cada una de las tablas.
     */
    private void inicializarModelos() {
        // Creación del modelo para la tabla Familia
        String[] titulosFamilia = {"Código", "Descripción", "Activo"};
        modeloFamilia = new DefaultTableModel(titulosFamilia, 0) {
            Class[] types = new Class[]{
                String.class, String.class, Boolean.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creación del modelo para la tabla Marca               
        String[] titulosMarca = {"Código", "Descripción", "Activo"};
        modeloMarca = new DefaultTableModel(titulosMarca, 0) {
            Class[] types = new Class[]{
                String.class, String.class, Boolean.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creación del modelo para la tabla Articulo
        String[] titulosArticulo = {"Código", "Descripción", "Familia", "Marca",
            "Costo", "Impuesto", "Porcentaje de utilidad", "Precio de Venta", "Activo"};
        modeloArticulo = new DefaultTableModel(titulosArticulo, 0) {
            Class[] types = new Class[]{
                String.class, String.class, String.class, String.class,
                Double.class, String.class, Double.class, Double.class, Boolean.class
            };

            boolean[] canEdit = new boolean[]{
                true, true, true, true, true, true, true, false, true
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creación del modelo para la tabla Impuesto
        String[] titulosImpuesto = {"Código", "Descripción", "Porcentaje", "Activo"};
        modeloImpuesto = new DefaultTableModel(titulosImpuesto, 0) {
            Class[] types = new Class[]{
                String.class, String.class, Double.class, Boolean.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creación del modelo para la tabla Tipo de Movimiento
        String[] titulosTipoMovimiento = {"Código", "Descripción", "Tipo de Operación", "Activo"};
        modeloTipoDeMovimiento = new DefaultTableModel(titulosTipoMovimiento, 0) {
            Class[] types = new Class[]{
                String.class, String.class, String.class, Boolean.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creacion del modelo para la tabla Movimiento de Inventario
        String[] titulosMovimientoInventario = {"Nº Documento", "Fecha", "Justificación", "Aplicado", "Anulado"};
        modeloMovimientoInventario = new DefaultTableModel(titulosMovimientoInventario, 0) {
            Class[] types = new Class[]{
                Integer.class, String.class, String.class, Boolean.class, Boolean.class
            };

            boolean[] canEdit = new boolean[]{
                false, false, true, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creacion del modelo para la tabla Toma Fisica
        String[] titulosTomaFisica = {"Nº Documento", "Fecha", "Justificación", "Aplicado", "Anulado", "Aplicado por"};
        modeloTomaFisica = new DefaultTableModel(titulosTomaFisica, 0) {
            Class[] types = new Class[]{
                Integer.class, String.class, String.class, Boolean.class, Boolean.class, String.class
            };

            boolean[] canEdit = new boolean[]{
                false, false, true, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creacion del modelo para la tabla Detalle Movimiento de Inventario
        String[] titulosDetalleMovimientoInventario = {"Nº Línea", "Artículo", "Existencia Previa",
            "Costo", "Tipo Movimiento", "Cantidad", "Saldo General"};
        modeloDetalleMovimientoInventario = new DefaultTableModel(titulosDetalleMovimientoInventario, 0) {
            Class[] types = new Class[]{
                Integer.class, String.class, Double.class, Double.class, String.class, Double.class, Double.class
            };

            boolean[] canEdit = new boolean[]{
                false, true, false, false, true, true, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        // Creacion del modelo para la tabla Detalle Toma Fisica
        String[] titulosDetalleTomaFisica = {"Nº Línea", "Artículo", "Existencia teórica", "Existencia Física", "Costo unitario"};
        modeloDetalleTomaFisica = new DefaultTableModel(titulosDetalleTomaFisica, 0) {
            Class[] types = new Class[]{
                Integer.class, String.class, Double.class, Double.class, Double.class
            };

            boolean[] canEdit = new boolean[]{
                false, true, false, true, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        // SE ESTABLECE CADA MODELO PARA CARGAR LOS DATOS
        modeloActualMantenimiento = modeloFamilia;
        getDatosForMantenimiento();
        modeloActualMantenimiento = modeloMarca;
        getDatosForMantenimiento();
        modeloActualMantenimiento = modeloArticulo;
        getDatosForMantenimiento();
        modeloActualMantenimiento = modeloImpuesto;
        getDatosForMantenimiento();
        modeloActualMantenimiento = modeloTipoDeMovimiento;
        getDatosForMantenimiento();
        // Se manda a cargar los modelos 'Movimiento de inventario' y 'Toma fisica'
        Rdb_movimientoInventario.setSelected(true);
        getDatosForProceso(false);
        Rdb_tomaFisica.setSelected(true);
        getDatosForProceso(false);
    }

    /**
     * Metodo utilizado para cargar lo Movimiento de Inventario, la Toma Fisica
     * y el detalle de estos, segun sea el caso
     *
     * @param esDetalle true si se desea cargar el detalle o false si es la
     * tabla principal
     */
    private void getDatosForProceso(boolean esDetalle) {
        ResultSet resultSet;
        try {
            // Verifica si se ha solicitado cargar el detalle (de Toma Fisica o Movimiento de inventario)
            if (esDetalle) {
                int numDocumento = (int) tbl_tomaMovimiento.getValueAt(selectedRowTomaMovimiento, 0);
                if (Rdb_tomaFisica.isSelected()) {
                    resultSet = connection.select("*", "\"schinventario\".detalle_toma_fisica",
                            "num_documento = " + numDocumento);
                    while (resultSet.next()) {
                        int numeroLinea = resultSet.getInt(2);
                        // Se hace otro select para obtener la descripcion del articulo
                        ResultSet selectCodigo = connection.select("descripcion", "\"schinventario\".articulo",
                                "cod_articulo = '" + resultSet.getString(3) + "'");
                        selectCodigo.next();
                        String articulo = resultSet.getString(3) + " = " + selectCodigo.getString(1);
                        Double existTeorica = resultSet.getDouble(4);
                        Double existFisica = resultSet.getDouble(5);
                        Double costoUnitario = resultSet.getDouble(6);
                        Object[] data = {numeroLinea, articulo, existTeorica, existFisica, costoUnitario};
                        modeloDetalleTomaFisica.addRow(data);
                    }
                    modeloDetalleTomaFisica.setRowCount(modeloDetalleTomaFisica.getRowCount() + 1);
                } else {
                    resultSet = connection.select("*", "\"schinventario\".detalle_movimiento_inventario",
                            "num_documento = " + numDocumento);
                    while (resultSet.next()) {
                        int numeroLinea = resultSet.getInt(2);
                        // Busco la descripcion del articulo de acuerdo al codigo
                        ResultSet select2 = connection.select("descripcion", "\"schinventario\".articulo",
                                "cod_articulo = '" + resultSet.getString(3) + "'");
                        select2.next();
                        String articulo = resultSet.getString(3) + " = " + select2.getString(1);
                        // Busco la descripcion para el codigo del tipo de movimiento
                        select2 = connection.select("descripcion", "\"schinventario\".tipo_movimiento",
                                "tipo_movimiento = '" + resultSet.getString(4) + "'");
                        select2.next();
                        String tipoMovimiento = resultSet.getString(4) + " = " + select2.getString(1);
                        Double cantidad = resultSet.getDouble(5);
                        Double saldoGeneral = resultSet.getDouble(6);
                        Double existenciaPrevia = resultSet.getDouble(7);
                        Double costo = resultSet.getDouble(8);
                        Object[] data = {numeroLinea, articulo, existenciaPrevia, costo, tipoMovimiento, cantidad, saldoGeneral};
                        modeloDetalleTomaFisica.addRow(data);
                    }
                    modeloDetalleMovimientoInventario.setRowCount(modeloDetalleMovimientoInventario.getRowCount() + 1);
                }
            } else {
                // Verifica si se debe cargar Toma Fisica(true) o Movimiento de inventario(false)
                if (Rdb_tomaFisica.isSelected()) {
                    resultSet = connection.select("*", "\"schinventario\".toma_fisica", "");
                    while (resultSet.next()) {
                        int numeroDocumento = resultSet.getInt(1);
                        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(resultSet.getDate(2));
                        String justificacion = resultSet.getString(3);
                        boolean aplicado = ((String) resultSet.getString(4)).equals("S");
                        boolean anulado = ((String) resultSet.getString(5)).equals("S");
                        String aplicadoPor = resultSet.getString(6);
                        Object[] fila = {numeroDocumento, fecha, justificacion, aplicado, anulado, aplicadoPor};
                        modeloTomaFisica.addRow(fila);
                    }
                    modeloTomaFisica.setRowCount(modeloTomaFisica.getRowCount() + 1);
                } else {
                    resultSet = connection.select("*", "\"schinventario\".movimiento_inventario", "");
                    while (resultSet.next()) {
                        int numeroDocumento = resultSet.getInt(1);
                        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(resultSet.getDate(2));
                        String justificacion = resultSet.getString(3);
                        boolean aplicado = ((String) resultSet.getString(4)).equals("S");
                        boolean anulado = ((String) resultSet.getString(5)).equals("S");
                        Object[] fila = {numeroDocumento, fecha, justificacion, aplicado, anulado};
                        modeloMovimientoInventario.addRow(fila);
                    }
                    modeloMovimientoInventario.setRowCount(modeloMovimientoInventario.getRowCount() + 1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método utilizado para cargar la información de cada tabla en su
     * respectivo modelo
     */
    private void getDatosForMantenimiento() {
        try {
            ResultSet respuestaSelect;
            if (modeloActualMantenimiento == modeloFamilia) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".familia_articulo", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(3).equals("S");
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(2), activo};
                    // Inserta los datos al modelo familia
                    modeloActualMantenimiento.addRow(data);
                }
            } else if (modeloActualMantenimiento == modeloMarca) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".marca_articulo", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(3).equals("S");
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(2), activo};
                    // Inserta los datos al modelo marca
                    modeloActualMantenimiento.addRow(data);
                }
            } else if (modeloActualMantenimiento == modeloImpuesto) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".impuesto", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(4).equals("S");
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(2),
                        respuestaSelect.getDouble(3), activo};
                    // Inserta los datos al modelo impuesto
                    modeloActualMantenimiento.addRow(data);
                }
            } else if (modeloActualMantenimiento == modeloTipoDeMovimiento) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".tipo_movimiento", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(4).equals("S");
                    // Se obtiene el valor del tipo de operacion y se cambia a su String equivalente
                    String tipoOperacion = "";
                    if (respuestaSelect.getString(3).equals("-1")) {
                        tipoOperacion = "Resta";
                    } else if (respuestaSelect.getString(3).equals("0")) {
                        tipoOperacion = "No aplica";
                    } else if (respuestaSelect.getString(3).equals("1")) {
                        tipoOperacion = "Suma";
                    }
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(2), tipoOperacion, activo};
                    // Inserta los datos al modelo Tipo de Movimiento
                    modeloActualMantenimiento.addRow(data);
                }
            } else if (modeloActualMantenimiento == modeloArticulo) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".articulo", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(8).equals("S");
                    String familia = respuestaSelect.getString(2);
                    String marca = respuestaSelect.getString(3);
                    String impuesto = respuestaSelect.getString(6);
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(4),
                        respuestaSelect.getString(2), respuestaSelect.getString(3),
                        respuestaSelect.getDouble(5), respuestaSelect.getString(6),
                        respuestaSelect.getDouble(9), respuestaSelect.getDouble(7), activo};

                    ResultSet descMarcaFamiliaImpuesto;
                    // Se obtiene la descripcion de la familia
                    descMarcaFamiliaImpuesto = connection.select("descripcion", "\"schinventario\".familia_articulo",
                            "cod_familia = '" + familia + "'");
                    descMarcaFamiliaImpuesto.next();
                    familia += " = " + descMarcaFamiliaImpuesto.getString(1);
                    // Se obtiene la descripcion de la marca
                    descMarcaFamiliaImpuesto = connection.select("descripcion", "\"schinventario\".marca_articulo",
                            "cod_marca = '" + marca + "'");
                    descMarcaFamiliaImpuesto.next();
                    marca += " = " + descMarcaFamiliaImpuesto.getString(1);
                    // Se obtiene la descripcion del impuesto
                    descMarcaFamiliaImpuesto = connection.select("descripcion", "\"schinventario\".impuesto",
                            "cod_impuesto = '" + impuesto + "'");
                    descMarcaFamiliaImpuesto.next();
                    impuesto += " = " + descMarcaFamiliaImpuesto.getString(1);

                    // Se cambian los valores del arreglo por la marca, familia e impuesto con descripcion
                    data[2] = familia;
                    data[3] = marca;
                    data[5] = impuesto;
                    // Inserta los datos al modelo artículo
                    modeloActualMantenimiento.addRow(data);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        modeloActualMantenimiento.setRowCount(modeloActualMantenimiento.getRowCount() + 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Rdg_Procesos = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        Pnl_Mantenimiento = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_Tabla = new javax.swing.JTable();
        Btn_Insertar = new javax.swing.JButton();
        Btn_Actualizar = new javax.swing.JButton();
        Btn_Borrar = new javax.swing.JButton();
        Lbl_Titulo = new javax.swing.JLabel();
        Cmb_Tablas = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        Rdb_tomaFisica = new javax.swing.JRadioButton();
        Rdb_movimientoInventario = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_tomaMovimiento = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbl_detalleTomaMovimiento = new javax.swing.JTable();
        btn_CrearTomaMovimiento = new javax.swing.JButton();
        Btn_Anular = new javax.swing.JButton();
        Btn_aplicar = new javax.swing.JButton();
        Btn_Salvar = new javax.swing.JButton();
        Btn_CrearDetalle = new javax.swing.JButton();
        Pnl_Consultas = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inventario");

        tbl_Tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_TablaMousePressed(evt);
            }
        });
        tbl_Tabla.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbl_TablaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_Tabla);

        Btn_Insertar.setText("Insert");
        Btn_Insertar.setFocusable(false);
        Btn_Insertar.setRequestFocusEnabled(false);
        Btn_Insertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_InsertarActionPerformed(evt);
            }
        });

        Btn_Actualizar.setText("Update");
        Btn_Actualizar.setFocusable(false);
        Btn_Actualizar.setRequestFocusEnabled(false);
        Btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_ActualizarActionPerformed(evt);
            }
        });

        Btn_Borrar.setText("Delete");
        Btn_Borrar.setFocusable(false);
        Btn_Borrar.setRequestFocusEnabled(false);
        Btn_Borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_BorrarActionPerformed(evt);
            }
        });

        Lbl_Titulo.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        Lbl_Titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        Cmb_Tablas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Familia", "Marca", "Artículo", "Impuesto", "Tipo de Movimiento de Inventario" }));
        Cmb_Tablas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                Cmb_TablasFocusGained(evt);
            }
        });
        Cmb_Tablas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cmb_TablasActionPerformed(evt);
            }
        });

        jLabel1.setText("Selección de Tabla");
        jLabel1.setFocusable(false);

        javax.swing.GroupLayout Pnl_MantenimientoLayout = new javax.swing.GroupLayout(Pnl_Mantenimiento);
        Pnl_Mantenimiento.setLayout(Pnl_MantenimientoLayout);
        Pnl_MantenimientoLayout.setHorizontalGroup(
            Pnl_MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Pnl_MantenimientoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Pnl_MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Lbl_Titulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(Pnl_MantenimientoLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cmb_Tablas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 760, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(Pnl_MantenimientoLayout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(Btn_Insertar, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(Btn_Actualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Btn_Borrar, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addGap(163, 163, 163))
        );
        Pnl_MantenimientoLayout.setVerticalGroup(
            Pnl_MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Pnl_MantenimientoLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(Pnl_MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Cmb_Tablas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(Lbl_Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addGap(27, 27, 27)
                .addGroup(Pnl_MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Btn_Insertar, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(Btn_Actualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(Btn_Borrar, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Mantenimiento", Pnl_Mantenimiento);

        jLayeredPane1.setOpaque(true);

        Rdb_tomaFisica.setForeground(new java.awt.Color(0, 153, 0));
        Rdb_tomaFisica.setSelected(true);
        Rdb_tomaFisica.setText("Toma física");
        Rdb_tomaFisica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Rdb_tomaFisicaActionPerformed(evt);
            }
        });

        Rdb_movimientoInventario.setText("Movimiento de Inventario");
        Rdb_movimientoInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Rdb_movimientoInventarioActionPerformed(evt);
            }
        });

        jLabel3.setText("Trabajando sobre:");

        tbl_tomaMovimiento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_tomaMovimiento.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_tomaMovimiento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_tomaMovimientoMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tbl_tomaMovimiento);

        tbl_detalleTomaMovimiento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_detalleTomaMovimiento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_detalleTomaMovimientoMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tbl_detalleTomaMovimiento);

        btn_CrearTomaMovimiento.setText("Crear la toma física");
        btn_CrearTomaMovimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CrearTomaMovimientoActionPerformed(evt);
            }
        });

        Btn_Anular.setText("Anular");

        Btn_aplicar.setText("Aplicar");

        Btn_Salvar.setText("Salvar");

        Btn_CrearDetalle.setText("Crear Detalle");
        Btn_CrearDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_CrearDetalleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(Btn_aplicar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Btn_Anular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_CrearTomaMovimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(21, 21, 21))
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Rdb_tomaFisica, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Rdb_movimientoInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3)))
                .addGap(39, 39, 39)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(Btn_Salvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Btn_CrearDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(105, 105, 105))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Rdb_tomaFisica)
                        .addComponent(Rdb_movimientoInventario)))
                .addGap(43, 43, 43)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_CrearTomaMovimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Btn_aplicar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Btn_Salvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Btn_CrearDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Btn_Anular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(87, 87, 87))
        );

        jLayeredPane1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {Rdb_movimientoInventario, Rdb_tomaFisica, jLabel3});

        jLayeredPane1.setLayer(Rdb_tomaFisica, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(Rdb_movimientoInventario, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane5, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(btn_CrearTomaMovimiento, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(Btn_Anular, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(Btn_aplicar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(Btn_Salvar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(Btn_CrearDetalle, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab("Procesos", jLayeredPane1);

        javax.swing.GroupLayout Pnl_ConsultasLayout = new javax.swing.GroupLayout(Pnl_Consultas);
        Pnl_Consultas.setLayout(Pnl_ConsultasLayout);
        Pnl_ConsultasLayout.setHorizontalGroup(
            Pnl_ConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1110, Short.MAX_VALUE)
        );
        Pnl_ConsultasLayout.setVerticalGroup(
            Pnl_ConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Consultas", Pnl_Consultas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Metodo utilizado para insertar articulos, impuestos, marcas, familias,
     * tipos de movimientos en la base de datos
     *
     * @param evt
     */
    private void Btn_InsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_InsertarActionPerformed
        // Restaura los valores de la fila seleccionada en caso de que no fuera la de insercion 
        if (numFilaAnterior != -1) {
            valuesFromArrayToRow();
            numFilaAnterior = -1;
            datosFilaActual = null;
            pkSelectedRow = null;
            tbl_Tabla.clearSelection();
        }

        String campos = "";
        String tabla = "\"schinventario\".";
        int filaInsercion = tbl_Tabla.getRowCount() - 1;

        if (modeloActualMantenimiento == modeloFamilia) {
            tabla += "familia_articulo";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            if (tbl_Tabla.getValueAt(filaInsercion, 2) == null) {
                tbl_Tabla.setValueAt(false, filaInsercion, 2);
            }
            String activo = (boolean) tbl_Tabla.getValueAt(filaInsercion, 2) ? "S" : "N";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (codigo == null || descripcion == null || codigo.equals("") || descripcion.equals("")) {
                JOptionPane.showMessageDialog(this, "No es posible insertar una nueva familia si hay campos sin información");
                return;
            } else {
                campos = "'" + codigo + "', '" + descripcion + "', '" + activo + "'";
//////                System.out.println(campos);
            }

        } else if (modeloActualMantenimiento == modeloMarca) {
            tabla += "marca_articulo";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            if (tbl_Tabla.getValueAt(filaInsercion, 2) == null) {
                tbl_Tabla.setValueAt(false, filaInsercion, 2);
            }
            String activo = (boolean) tbl_Tabla.getValueAt(filaInsercion, 2) ? "S" : "N";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (codigo == null || descripcion == null || codigo.equals("") || descripcion.equals("")) {
                JOptionPane.showMessageDialog(this, "No es posible insertar una nueva marca si hay campos sin información");
                return;
            } else {
                campos = "'" + codigo + "', '" + descripcion + "', '" + activo + "'";
//////                System.out.println(campos);
            }

        } else if (modeloActualMantenimiento == modeloImpuesto) {
            tabla += "impuesto";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            Double porcentaje = (Double) tbl_Tabla.getValueAt(filaInsercion, 2);
            if (tbl_Tabla.getValueAt(filaInsercion, 3) == null) {
                tbl_Tabla.setValueAt(false, filaInsercion, 3);
            }
            String activo = (boolean) tbl_Tabla.getValueAt(filaInsercion, 3) ? "S" : "N";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (codigo == null || descripcion == null || porcentaje == null || codigo.equals("") || descripcion.equals("")) {
                JOptionPane.showMessageDialog(this, "No es posible insertar un nuevo impuesto si hay campos sin información");
                return;
            } else {
                campos = "'" + codigo + "', '" + descripcion + "', " + porcentaje + ", '" + activo + "'";
//////                System.out.println(campos);
            }

        } else if (modeloActualMantenimiento == modeloTipoDeMovimiento) {
            tabla += "tipo_movimiento";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String tipo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            String operacion = (String) tbl_Tabla.getValueAt(filaInsercion, 2);
            if (tbl_Tabla.getValueAt(filaInsercion, 3) == null) {
                tbl_Tabla.setValueAt(false, filaInsercion, 3);
            }
            String activo = (boolean) tbl_Tabla.getValueAt(filaInsercion, 3) ? "S" : "N";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (tipo == null || descripcion == null || operacion == null || tipo.equals("") || descripcion.equals("")) {
                JOptionPane.showMessageDialog(this, "No es posible insertar un nuevo tipo de movimiento, si hay campos sin información");
                return;
            } else {
                // Obtiene el valor correspondiente al tipo de operacion para insertar en la BD
                if (operacion.equals("Resta")) {
                    operacion = "-1";
                } else if (operacion.equals("No aplica")) {
                    operacion = "0";
                } else {
                    operacion = "1";
                }
                campos = "'" + tipo + "', '" + descripcion + "', '" + operacion + "', '" + activo + "'";
//////                System.out.println(campos);
            }

        } else if (modeloActualMantenimiento == modeloArticulo) {
            tabla += "articulo";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            String familia = (String) tbl_Tabla.getValueAt(filaInsercion, 2);
            String marca = (String) tbl_Tabla.getValueAt(filaInsercion, 3);
            Double precioSinImp = (Double) tbl_Tabla.getValueAt(filaInsercion, 4);
            String impuesto = (String) tbl_Tabla.getValueAt(filaInsercion, 5);
            Double utilidad = (Double) tbl_Tabla.getValueAt(filaInsercion, 6);
            if (tbl_Tabla.getValueAt(filaInsercion, 8) == null) {
                tbl_Tabla.setValueAt(false, filaInsercion, 8);
            }
            String activo = (boolean) tbl_Tabla.getValueAt(filaInsercion, 8) ? "S" : "N";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (codigo == null || codigo.equals("") || descripcion == null || descripcion.equals("") || familia == null
                    || marca == null || precioSinImp == null || impuesto == null || utilidad == null) {
                JOptionPane.showMessageDialog(this, "No es posible insertar un nuevo artículo si hay campos sin información");
                return;
            } else {
                // Obtiene los codigos de FK familia, marca e impuesto
                String separador = " = ";
                familia = familia.substring(0, familia.indexOf(separador));
                marca = marca.substring(0, marca.indexOf(separador));
                impuesto = impuesto.substring(0, impuesto.indexOf(separador));
                // Se hace un select para obtener el porcentaje de impuesto
                ResultSet resSelect;
                try {
                    resSelect = connection.select("porcentaje", "\"schinventario\".impuesto", "cod_impuesto = '" + impuesto + "'");
                    resSelect.next();
                    Double valorImp;
                    valorImp = resSelect.getDouble(1);
                    // Se calcula el costo del articulo con impuestos
                    Double costo = precioSinImp * ((valorImp / 100) + 1) * ((utilidad / 100) + 1);
                    // Se coloca el costo del articulo en la tabla
                    tbl_Tabla.setValueAt(costo, filaInsercion, 7);
                    // Se almacenan todos los campos que se van a insertar en un String
                    campos = "'" + codigo + "', '" + familia + "', '" + marca + "', '" + descripcion + "', "
                            + precioSinImp + ", '" + impuesto + "', " + costo + ", '" + activo + "', " + utilidad;
//////                    System.out.println(campos);
                } catch (SQLException ex) {
                    Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error al intentar obtener el impuesto");
                    return;
                }
            }
        }

        if (connection.insert(tabla, campos)) {
//////            System.out.println("Inserción exitosa");
            // Verifica si el campo esta activo y lo agrega en el respectivo comboBox
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);

            if (modeloActualMantenimiento == modeloFamilia && (boolean) tbl_Tabla.getValueAt(filaInsercion, 2)) {
                cmbFamilias.addItem(codigo + " = " + descripcion);
            } else if (modeloActualMantenimiento == modeloMarca && (boolean) tbl_Tabla.getValueAt(filaInsercion, 2)) {
                cmbMarcas.addItem(codigo + " = " + descripcion);
            } else if (modeloActualMantenimiento == modeloImpuesto && (boolean) tbl_Tabla.getValueAt(filaInsercion, 3)) {
                cmbImpuestos.addItem(codigo + " = " + descripcion);
            } else if (modeloActualMantenimiento == modeloArticulo && (boolean) tbl_Tabla.getValueAt(filaInsercion, 8)) {
                cmbArticulos.addItem(codigo + " = " + descripcion);
            } else if (modeloActualMantenimiento == modeloTipoDeMovimiento && (boolean) tbl_Tabla.getValueAt(filaInsercion, 3)) {
                cmbTipoDeMovimientos.addItem(codigo + " = " + descripcion);
            }
            // Agrega una nueva fila a la tabla
            modeloActualMantenimiento.setNumRows(modeloActualMantenimiento.getRowCount() + 1);
        } else {
            JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la inserción");
            // Borra los valores de la ultima fila
            ((DefaultTableModel) tbl_Tabla.getModel()).removeRow(filaInsercion);
            ((DefaultTableModel) tbl_Tabla.getModel()).setRowCount(filaInsercion + 1);
        }
    }//GEN-LAST:event_Btn_InsertarActionPerformed

    /**
     * Metodo utilizado para eliminar la fila seleccionada en la tabla, al
     * presionar el botón "borrar"
     *
     * @param evt
     */
    private void Btn_BorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_BorrarActionPerformed
        if (numFilaAnterior != -1) {
            String tabla = "\"schinventario\".";
            String condicion = "";
            // Valores para comprobar si hay que eliminar algun dato del comboBox
            String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
            boolean activo = false;

            // Se busca cual modelo es el utilizado para eliminar los datos de su respectiva tabla
            if (modeloActualMantenimiento == modeloFamilia) {
                tabla += "familia_articulo";
                condicion = "cod_familia = '" + pkSelectedRow + "'";
                activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2);
            } else if (modeloActualMantenimiento == modeloMarca) {
                tabla += "marca_articulo";
                condicion = "cod_marca = '" + pkSelectedRow + "'";
                activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2);
            } else if (modeloActualMantenimiento == modeloArticulo) {
                tabla += "articulo";
                condicion = "cod_articulo = '" + pkSelectedRow + "'";
                activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 8);
            } else if (modeloActualMantenimiento == modeloImpuesto) {
                tabla += "impuesto";
                condicion = "cod_impuesto = '" + pkSelectedRow + "'";
                activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 3);
            } else if (modeloActualMantenimiento == modeloTipoDeMovimiento) {
                tabla += "tipo_movimiento";
                condicion = "tipo_movimiento = '" + pkSelectedRow + "'";
                activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 3);
            }

            // Se procede a eliminar los datos de la tabla
            if (connection.borrar(tabla, condicion)) {
//////                System.out.println("Borrado exitoso");
                // Se elimina la fila seleccionada
                modeloActualMantenimiento.removeRow(numFilaAnterior);
                // Se verifica si se esta eliminando un campo que haya en un comboBox y lo quita
                if (modeloActualMantenimiento == modeloFamilia && activo) {
                    cmbFamilias.removeItem(codigo + " = " + descripcion);
                } else if (modeloActualMantenimiento == modeloMarca && activo) {
                    cmbMarcas.removeItem(codigo + " = " + descripcion);
                } else if (modeloActualMantenimiento == modeloImpuesto && activo) {
                    cmbImpuestos.removeItem(codigo + " = " + descripcion);
                } else if (modeloActualMantenimiento == modeloArticulo && activo) {
                    cmbArticulos.removeItem(codigo + " = " + descripcion);
                } else if (modeloActualMantenimiento == modeloTipoDeMovimiento && activo) {
                    cmbTipoDeMovimientos.removeItem(codigo + " = " + descripcion);
                }
                // Se eliminan los datos de la ultima seleccion
                numFilaAnterior = -1;
                datosFilaActual = null;
                pkSelectedRow = null;

            } else {
                JOptionPane.showMessageDialog(this, "No es posible eliminar la tupla seleccionada");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado una fila con datos para eliminarla");
        }
    }//GEN-LAST:event_Btn_BorrarActionPerformed

    /**
     * Metodo para verificar si una instancia activa esta siento utilizada en
     * una tabla especifica
     *
     * @param pTabla tabla donde se va a analizar si es utilizado dicha
     * instancia
     * @param pCodigo codigo de la instancia a buscar
     * @param pColumna columna de la tabla donde se encuentra dicho codigo como
     * F.K
     * @return true si existe o false en caso contrario
     */
    private boolean esCodigoActivoUtilizado(String pTabla, String pCodigo, int pColumna) {
        if (pTabla.equals("articulos")) {
            // Verifica todas la filas para ver si se encuentra el pCodigo en la columna pColumna
            for (int i = 0; i < modeloArticulo.getRowCount() - 1; i++) {
                if (modeloArticulo.getValueAt(i, pColumna).equals(pCodigo)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metodo utilizado para modificar la aparicion de un elemento en una
     * columna
     *
     * @param pTable tabla donde se va a buscar
     * @param pValActual valor que se va a buscar para cambiar
     * @param pValNuevo valor nuevo que se desea cambiar
     * @param pColumna numero de columna sobre la cual se va a realizar la
     * modificacion
     */
    private void modificarAparicionesEnCol(String pTable, String pValActual, String pValNuevo, int pColumna) {
        if (pTable.equals("articulos")) {
            for (int i = 0; i < modeloArticulo.getRowCount() - 1; i++) {
                // Cambia el valor viejo por el nuevo si el campo analizado debe modificarse
                if (((String) modeloArticulo.getValueAt(i, pColumna)).equals(pValActual)) {
                    modeloArticulo.setValueAt(pValNuevo, i, pColumna);
                }
            }
        }
    }

    /**
     * Metodo utilizado para modificar el costo del articulo cuando hay un
     * cambio en el porcentaje de impuesto
     *
     * @param pCodigoPorcentaje codigo del nuevo impuesto(el que fue modificado)
     * @param pNuevoImpuesto valor del nuevo impuesto
     */
    private void modificarCostosArticulos(String pCodigoPorcentaje, Double pNuevoImpuesto) {
        String tabla = "\"schinventario\".articulo";
        for (int i = 0; i < modeloArticulo.getRowCount() - 1; i++) {
            // Verifica si el codigo del porcentaje de la fila analizada es iagual al que se va a modificar
            if (((String) modeloArticulo.getValueAt(i, 5)).equals(pCodigoPorcentaje)) {
                // Objetien los valores del precio y la utilidad que desea ganarse
                Double precioSinImpusto = (Double) modeloArticulo.getValueAt(i, 4);
                Double porcentajeUtilidad = (Double) modeloArticulo.getValueAt(i, 6);
                // Se calcula el costo del articulo con impuestos
                Double costo = precioSinImpusto * ((pNuevoImpuesto / 100) + 1) * ((porcentajeUtilidad / 100) + 1);
                // Se establece el nuevo valor del costo en el articulo
                modeloArticulo.setValueAt(costo, i, 7);
                // Se manda a modificar el costo a la base de datos
                String campo = "costo = " + costo;
                String condicion = "cod_articulo = '" + modeloArticulo.getValueAt(i, 0) + "'";
                connection.actualizar(tabla, campo, condicion);
            }
        }
    }

    private void Btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_ActualizarActionPerformed
        if (numFilaAnterior != -1) {
            String campos = "";
            String primaryKey = "";
            String tabla = "\"schinventario\".";

            if (modeloActualMantenimiento == modeloFamilia) {
                tabla += "familia_articulo";
                primaryKey = "cod_familia = '" + pkSelectedRow + "'";
                // Obtiene el valor de los campos
                String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                String activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2) ? "S" : "N";
                // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor
                if (codigo.equals("") || descripcion.equals("")) {
                    JOptionPane.showMessageDialog(this, "No es posible actualizar una familia si hay campos sin información");
                    return;
                } else if ((boolean) datosFilaActual[2] && activo.equals("N")
                        && esCodigoActivoUtilizado("articulos", datosFilaActual[0] + " = " + datosFilaActual[1], 2)) {
                    // El codigo esta siendo utilizado en articulo, no se puede desactivar
                    JOptionPane.showMessageDialog(this, "No es posible desactivar la familia, ya que aún hay artículos que la continen");
                    return;
                } else {
                    // Se almacenan los campos que se van a modificar en la tabla
                    campos = "cod_familia = '" + codigo + "', descripcion = '" + descripcion + "', "
                            + "activo = '" + activo + "'";
                }

            } else if (modeloActualMantenimiento == modeloMarca) {
                tabla += "marca_articulo";
                primaryKey = "cod_marca = '" + pkSelectedRow + "'";
                // Obtiene el valor de los campos
                String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                String activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2) ? "S" : "N";
                // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor
                if (codigo.equals("") || descripcion.equals("")) {
                    JOptionPane.showMessageDialog(this, "No es posible actualizar una marca si hay campos sin información");
                    return;
                } else if ((boolean) datosFilaActual[2] && activo.equals("N")
                        && esCodigoActivoUtilizado("articulos", datosFilaActual[0] + " = " + datosFilaActual[1], 3)) {
                    // El codigo esta siendo utilizado en articulo, no se puede desactivar
                    JOptionPane.showMessageDialog(this, "No es posible desactivar la marca, ya que aún hay artículos que la continen");
                    return;
                } else {
                    campos = "cod_marca = '" + codigo + "', descripcion = '" + descripcion + "', "
                            + "activo = '" + activo + "'";
                }

            } else if (modeloActualMantenimiento == modeloImpuesto) {
                tabla += "impuesto";
                primaryKey = "cod_impuesto = '" + pkSelectedRow + "'";
                // Obtiene el valor de los campos
                String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                Double porcentaje = (Double) tbl_Tabla.getValueAt(numFilaAnterior, 2);
                String activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 3) ? "S" : "N";
                // Verifica que los campos 'codigo' y 'descripcion' y 'porcentaje' contengan algun valor
                if (porcentaje == null || codigo.equals("") || descripcion.equals("")) {
                    JOptionPane.showMessageDialog(this, "No es posible actualizar un impuesto si hay campos sin información");
                    return;
                } else if ((boolean) datosFilaActual[3] && activo.equals("N")
                        && esCodigoActivoUtilizado("articulos", datosFilaActual[0] + " = " + datosFilaActual[1], 5)) {
                    // El codigo esta siendo utilizado en articulo, no se puede desactivar
                    JOptionPane.showMessageDialog(this, "No es posible desactivar el impuesto, ya que aún hay artículos que lo continen");
                    return;
                } else {
                    campos = "cod_impuesto = '" + codigo + "', descripcion = '" + descripcion + "', "
                            + "porcentaje = " + porcentaje + ", activo = '" + activo + "'";
                }

            } else if (modeloActualMantenimiento == modeloTipoDeMovimiento) {
                tabla += "tipo_movimiento";
                primaryKey = "tipo_movimiento = '" + pkSelectedRow + "'";
                // Obtiene el valor de los campos
                String tipo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                String operacion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 2);
                String activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 3) ? "S" : "N";
                // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor
                if (tipo.equals("") || descripcion.equals("")) {
                    JOptionPane.showMessageDialog(this, "No es posible actualizar un tipo de movimiento, si hay campos sin información");
                    return;
                } /*else if ((boolean) datosFilaActual[3] && activo.equals("N") && 
                 esCodigoActivoUtilizado("articulos", codigo + " = " + descripcion, 5)) {
                 // El codigo esta siendo utilizado en articulo, no se puede desactivar
                 JOptionPane.showMessageDialog(this, "No es posible desactivar el impuesto, ya que aún hay artículos que lo continen");
                 return;
                 }*/ else {
                    // Obtiene el valor correspondiente al tipo de operacion
                    if (operacion.equals("Resta")) {
                        operacion = "-1";
                    } else if (operacion.equals("No aplica")) {
                        operacion = "0";
                    } else {
                        operacion = "1";
                    }
                    campos = "tipo_movimiento = '" + tipo + "', descripcion = '" + descripcion + "', "
                            + "tipo_operacion = '" + operacion + "', activo = '" + activo + "'";
                }

            } else if (modeloActualMantenimiento == modeloArticulo) {
                tabla += "articulo";
                primaryKey = "cod_articulo = '" + pkSelectedRow + "'";
                // Obtiene el valor de los campos
                String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                String familia = (String) tbl_Tabla.getValueAt(numFilaAnterior, 2);
                String marca = (String) tbl_Tabla.getValueAt(numFilaAnterior, 3);
                Double precioSinImp = (Double) tbl_Tabla.getValueAt(numFilaAnterior, 4);
                String impuesto = (String) tbl_Tabla.getValueAt(numFilaAnterior, 5);
                Double utilidad = (Double) tbl_Tabla.getValueAt(numFilaAnterior, 6);
                String activo = (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 8) ? "S" : "N";
                // Verifica que todos los campos contengan algun valor
                if (codigo.equals("") || descripcion.equals("") || precioSinImp == null || utilidad == null) {
                    JOptionPane.showMessageDialog(this, "No es posible actualizar un artículo si hay campos sin información");
                    return;
                } /*else if ((boolean) datosFilaActual[3] && activo.equals("N") && 
                 esCodigoActivoUtilizado("articulos", codigo + " = " + descripcion, 5)) {
                 // El codigo esta siendo utilizado en articulo, no se puede desactivar
                 JOptionPane.showMessageDialog(this, "No es posible desactivar el impuesto, ya que aún hay artículos que lo continen");
                 return;
                 }*/ else {
                    // Obtiene los codigos de FK familia, marca e impuesto
                    String separador = " = ";
                    familia = familia.substring(0, familia.indexOf(separador));
                    marca = marca.substring(0, marca.indexOf(separador));
                    impuesto = impuesto.substring(0, impuesto.indexOf(separador));
                    // Se hace un select para obtener el porcentaje de impuesto
                    ResultSet resSelect;
                    try {
                        resSelect = connection.select("porcentaje", "\"schinventario\".impuesto", "cod_impuesto = '" + impuesto + "'");
                        resSelect.next();
                        Double valorImp = resSelect.getDouble(1);
                        // Se calcula el costo del articulo con impuestos
                        Double costo = precioSinImp * ((valorImp / 100) + 1) * ((utilidad / 100) + 1);
                        // Se coloca el costo del articulo en la tabla
                        tbl_Tabla.setValueAt(costo, numFilaAnterior, 7);
                        // Se almacenan todos los campos
                        campos = "cod_articulo = '" + codigo + "', cod_familia = '" + familia + "', "
                                + "cod_marca = '" + marca + "', descripcion = '" + descripcion + "', "
                                + "precio_sin_imp = " + precioSinImp + ", cod_impuesto = '" + impuesto + "', "
                                + "costo = " + costo + ", activo = '" + activo + "', "
                                + "porcentaje_utilidad = " + utilidad;
                        System.out.println(campos);
                    } catch (SQLException ex) {
                        Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, "Ha ocurrido un error al intentar obtener el impuesto");
                        return;
                    }
                }
            }

            // Verifica si la actualizacion de los datos fue exitosa
            if (connection.actualizar(tabla, campos, primaryKey)) {

                if (modeloActualMantenimiento == modeloFamilia) {
                    // Compara el valor que habia con el que hay ahora para ver si hay que hacer cambios en 'articulos'
                    if ((boolean) datosFilaActual[2] && (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2)) {
                        // Obtiene los valores de codigo y desc anterior y posterior para ver si cambiaron.
                        String codigoAnt = (String) datosFilaActual[0];
                        String descripcionAnt = (String) datosFilaActual[1];
                        String codDesAnterior = codigoAnt + " = " + descripcionAnt;
                        String codigoAct = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                        String descripcionAct = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                        String codDesActual = codigoAct + " = " + descripcionAct;
                        // Si cambiaron, hay que hacer la modificacion en el comboBox de familias y la tabla articulos
                        if (!(codDesAnterior).equals(codDesActual)) {
                            cmbFamilias.removeItem(codDesAnterior);
                            cmbFamilias.addItem(codDesActual);
                            modificarAparicionesEnCol("articulos", codDesAnterior, codDesActual, 2);
                        }
                    } else if ((boolean) datosFilaActual[2] && !(boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2)) {
                        // Remueve la familia del comboBox xq esta ya no esta activa
                        String codigo = (String) datosFilaActual[0];
                        String descripcion = (String) datosFilaActual[1];
                        cmbFamilias.removeItem(codigo + " = " + descripcion);
                    } else if (!(boolean) datosFilaActual[2] && (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2)) {
                        // Ahora esta activo. Agrega el codigo al comboBox de familias
                        String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                        String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                        cmbFamilias.addItem(codigo + " = " + descripcion);
                    }
                } else if (modeloActualMantenimiento == modeloMarca) {
                    // Compara el valor que habia con el que hay ahora para ver si hay que hacer cambios en 'articulos'
                    if ((boolean) datosFilaActual[2] && (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2)) {
                        // Obtiene los valores de codigo y desc anterior y posterior para ver si cambiaron.
                        String codigoAnt = (String) datosFilaActual[0];
                        String descripcionAnt = (String) datosFilaActual[1];
                        String codDesAnterior = codigoAnt + " = " + descripcionAnt;
                        String codigoAct = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                        String descripcionAct = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                        String codDesActual = codigoAct + " = " + descripcionAct;
                        // Si cambiaron, hay que hacer la modificacion en el comboBox de marcas y la tabla articulos
                        if (!(codDesAnterior).equals(codDesActual)) {
                            cmbMarcas.removeItem(codDesAnterior);
                            cmbMarcas.addItem(codDesActual);
                            modificarAparicionesEnCol("articulos", codDesAnterior, codDesActual, 3);
                        }
                    } else if ((boolean) datosFilaActual[2] && !(boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2)) {
                        // Remueve la familia del comboBox xq esta ya no esta activa
                        String codigo = (String) datosFilaActual[0];
                        String descripcion = (String) datosFilaActual[1];
                        cmbMarcas.removeItem(codigo + " = " + descripcion);
                    } else if (!(boolean) datosFilaActual[2] && (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 2)) {
                        // Ahora esta activo. Agrega el codigo al comboBox de familias
                        String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                        String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                        cmbMarcas.addItem(codigo + " = " + descripcion);
                    }
                } else if (modeloActualMantenimiento == modeloImpuesto) {
                    // Compara el valor que habia con el que hay ahora para ver si hay que hacer cambios en 'articulos'
                    if ((boolean) datosFilaActual[3] && (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 3)) {
                        // Obtiene los valores de codigo y desc anterior y posterior para ver si cambiaron.
                        String codigoAnt = (String) datosFilaActual[0];
                        String descripcionAnt = (String) datosFilaActual[1];
                        String codDesAnterior = codigoAnt + " = " + descripcionAnt;
                        String codigoAct = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                        String descripcionAct = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                        String codDesActual = codigoAct + " = " + descripcionAct;
                        // Si cambiaron, hay que hacer la modificacion en el comboBox de impuestos y la tabla articulos
                        if (!(codDesAnterior).equals(codDesActual)) {
                            cmbImpuestos.removeItem(codDesAnterior);
                            cmbImpuestos.addItem(codDesActual);
                            modificarAparicionesEnCol("articulos", codDesAnterior, codDesActual, 5);
                        }
                        // Obtiene el valor del porcentaje de impuesto nuevo y el anterior
                        Double porcentajeAnt = (Double) datosFilaActual[2];
                        Double porcentajeAct = (Double) tbl_Tabla.getValueAt(numFilaAnterior, 2);
                        // Verifica si cambio el porcentaje de impuesto, porque implica cambiar el costo de los articulos
                        if (porcentajeAct != porcentajeAnt) {
                            modificarCostosArticulos(codDesActual, porcentajeAct);
                        }

                    } else if ((boolean) datosFilaActual[3] && !(boolean) tbl_Tabla.getValueAt(numFilaAnterior, 3)) {
                        // Remueve la familia del comboBox xq esta ya no esta activa
                        String codigo = (String) datosFilaActual[0];
                        String descripcion = (String) datosFilaActual[1];
                        cmbImpuestos.removeItem(codigo + " = " + descripcion);
                    } else if (!(boolean) datosFilaActual[3] && (boolean) tbl_Tabla.getValueAt(numFilaAnterior, 3)) {
                        // Ahora esta activo. Agrega el codigo al comboBox de familias
                        String codigo = (String) tbl_Tabla.getValueAt(numFilaAnterior, 0);
                        String descripcion = (String) tbl_Tabla.getValueAt(numFilaAnterior, 1);
                        cmbImpuestos.addItem(codigo + " = " + descripcion);
                    }
                } /*else if(modeloActual == modeloTipoDeMovimiento){
                    
                 } else if(modeloActual == modeloArticulo){
                    
                 }*/

                System.out.println("Modificación exitosa");
                // Actualiza los nuevos valores de la fila en el arreglo temporal
                valuesFromRowToArray();
            } else {
                // Carga los datos que habia anteriormente
                valuesFromArrayToRow();
                JOptionPane.showMessageDialog(this, "No es posible realizar la modificación. Se"
                        + " ha restaurado cada uno de los valores modificados para la fila seleccionada");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado una fila con datos para actualizarla");
        }
    }//GEN-LAST:event_Btn_ActualizarActionPerformed

    /**
     * Metodo utilizado para cambiar a la tabla seleccionada
     *
     * @param evt
     */
    private void Cmb_TablasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cmb_TablasActionPerformed
        if (Cmb_Tablas.getSelectedItem() == "Familia") {
            tbl_Tabla.setModel(modeloFamilia);
            modeloActualMantenimiento = modeloFamilia;
            Lbl_Titulo.setText("Tabla Familia");
        } else if (Cmb_Tablas.getSelectedItem() == "Marca") {
            tbl_Tabla.setModel(modeloMarca);
            modeloActualMantenimiento = modeloMarca;
            Lbl_Titulo.setText("Tabla Marca");
        } else if (Cmb_Tablas.getSelectedItem() == "Artículo") {
            tbl_Tabla.setModel(modeloArticulo);
            modeloActualMantenimiento = modeloArticulo;
            // Carga los valores desde un Combobox, para las celdas personalizadas en la tabla articulo
            tbl_Tabla.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cmbFamilias));
            tbl_Tabla.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbMarcas));
            tbl_Tabla.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cmbImpuestos));
            Lbl_Titulo.setText("Tabla Artículo");
        } else if (Cmb_Tablas.getSelectedItem() == "Impuesto") {
            tbl_Tabla.setModel(modeloImpuesto);
            modeloActualMantenimiento = modeloImpuesto;
            Lbl_Titulo.setText("Tabla Impuesto");
        } else if (Cmb_Tablas.getSelectedItem() == "Tipo de Movimiento de Inventario") {
            tbl_Tabla.setModel(modeloTipoDeMovimiento);
            modeloActualMantenimiento = modeloTipoDeMovimiento;
            // Carga el ComboBox con los datos "Suma, Resta, No aplica", para el campo "tipo de operacion" 
            tbl_Tabla.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cmbTipoDeMovimientos));
            Lbl_Titulo.setText("Tabla Tipo de Movimiento de Inventario");
        }
    }//GEN-LAST:event_Cmb_TablasActionPerformed

    private void tbl_TablaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_TablaKeyReleased
        int selectedRow = tbl_Tabla.getSelectedRow();
        if ((selectedRow < tbl_Tabla.getRowCount() - 1 && (evt.getKeyCode() == KeyEvent.VK_UP
                || evt.getKeyCode() == KeyEvent.VK_DOWN))
                || (tbl_Tabla.getSelectedColumn() == 0 && evt.getKeyCode() == KeyEvent.VK_TAB)) {
            pkSelectedRow = "'" + (String) tbl_Tabla.getValueAt(tbl_Tabla.getSelectedRow(), 0) + "'";
            System.out.println(pkSelectedRow);
        }
    }//GEN-LAST:event_tbl_TablaKeyReleased

    /**
     * Metodo utilizado para salvar y restaurar los valores de las filas que no
     * fueron procesados luego de presionar clic sobre otra fila
     *
     * @param evt
     */
    private void tbl_TablaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_TablaMousePressed
        // Obtiene el indice de la fila que se encuentra seleccionada
        int numFilaActual = tbl_Tabla.getSelectedRow();
        if (numFilaActual != numFilaAnterior) {
            /* Verifica que hubiera seleccionado una fila anteriormente. Esto para restaurar los valores que habian ahi*/
            if (numFilaAnterior != -1) {
                valuesFromArrayToRow();
            }
            /* Verifica si la fila seleccionada no es la ultima, con el fin de almacenar los valores
             originales de la fila en un arreglo*/
            if (numFilaActual != tbl_Tabla.getRowCount() - 1) {
                valuesFromRowToArray();
                // Obtiene la Pk de la fila seleccionada
                pkSelectedRow = (String) tbl_Tabla.getValueAt(numFilaActual, 0);
                // Cambia el num de fila anterior por el actual. El proximo clic esta será la fila anterior.
                numFilaAnterior = numFilaActual;
            } else {
                if (modeloActualMantenimiento == modeloFamilia || modeloActualMantenimiento == modeloMarca) {
                    tbl_Tabla.setValueAt(true, tbl_Tabla.getRowCount() - 1, 2);
                } else if (modeloActualMantenimiento == modeloImpuesto || modeloActualMantenimiento == modeloTipoDeMovimiento) {
                    tbl_Tabla.setValueAt(true, tbl_Tabla.getRowCount() - 1, 3);
                } else if (modeloActualMantenimiento == modeloArticulo) {
                    tbl_Tabla.setValueAt(true, tbl_Tabla.getRowCount() - 1, 8);
                }
                pkSelectedRow = null;
                datosFilaActual = null;
                numFilaAnterior = -1;
            }
        }
    }//GEN-LAST:event_tbl_TablaMousePressed

    /**
     * Metodo utilizado para cargar todos los datos de una tupla en un arreglo
     * antes de que se realice alguna modificacion a un campo de esta.
     */
    private void valuesFromRowToArray() {
        int filaActual = tbl_Tabla.getSelectedRow();

        if (modeloActualMantenimiento == modeloFamilia) {
            String codigo = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 2);
            datosFilaActual = new Object[]{codigo, descripcion, activo};

        } else if (modeloActualMantenimiento == modeloMarca) {
            String codigo = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 2);
            datosFilaActual = new Object[]{codigo, descripcion, activo};

        } else if (modeloActualMantenimiento == modeloImpuesto) {
            String codigo = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            Double porcentaje = (Double) tbl_Tabla.getValueAt(filaActual, 2);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 3);
            datosFilaActual = new Object[]{codigo, descripcion, porcentaje, activo};

        } else if (modeloActualMantenimiento == modeloTipoDeMovimiento) {
            String tipoMovimiento = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            String tipoOperacion = (String) tbl_Tabla.getValueAt(filaActual, 2);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 3);
            datosFilaActual = new Object[]{tipoMovimiento, descripcion, tipoOperacion, activo};

        } else if (modeloActualMantenimiento == modeloArticulo) {
            String codigo = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            String familia = (String) tbl_Tabla.getValueAt(filaActual, 2);
            String marca = (String) tbl_Tabla.getValueAt(filaActual, 3);
            Double precioSinImp = (Double) tbl_Tabla.getValueAt(filaActual, 4);
            String impuesto = (String) tbl_Tabla.getValueAt(filaActual, 5);
            Double utilidad = (Double) tbl_Tabla.getValueAt(filaActual, 6);
            Double costo = (Double) tbl_Tabla.getValueAt(filaActual, 7);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 8);
            datosFilaActual = new Object[]{codigo, descripcion, familia, marca,
                precioSinImp, impuesto, utilidad, costo, activo};
        }
    }

    /**
     * Metodo utilizado para cargar los valores originales de una fila, luego de
     * que hayan sido modificados, pero no se realizara alguna accion(insertar,
     * borrar, modificar) sobre ellos.
     */
    private void valuesFromArrayToRow() {
        if (modeloActualMantenimiento == modeloFamilia) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[2], numFilaAnterior, 2);

        } else if (modeloActualMantenimiento == modeloMarca) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[2], numFilaAnterior, 2);

        } else if (modeloActualMantenimiento == modeloImpuesto) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((Double) datosFilaActual[2], numFilaAnterior, 2);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[3], numFilaAnterior, 3);

        } else if (modeloActualMantenimiento == modeloTipoDeMovimiento) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((String) datosFilaActual[2], numFilaAnterior, 2);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[3], numFilaAnterior, 3);

        } else if (modeloActualMantenimiento == modeloArticulo) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((String) datosFilaActual[2], numFilaAnterior, 2);
            tbl_Tabla.setValueAt((String) datosFilaActual[3], numFilaAnterior, 3);
            tbl_Tabla.setValueAt((Double) datosFilaActual[4], numFilaAnterior, 4);
            tbl_Tabla.setValueAt((String) datosFilaActual[5], numFilaAnterior, 5);
            tbl_Tabla.setValueAt((Double) datosFilaActual[6], numFilaAnterior, 6);
            tbl_Tabla.setValueAt((Double) datosFilaActual[7], numFilaAnterior, 7);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[8], numFilaAnterior, 8);
        }
    }

    /**
     * Cambia el modelo a la tabla por el de Toma Fisica
     *
     * @param evt
     */
    private void Rdb_tomaFisicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Rdb_tomaFisicaActionPerformed
        if (Rdb_tomaFisica.isSelected()) {
            // Establece el modelo toma fisica
            tbl_tomaMovimiento.setModel(modeloTomaFisica);
            modeloDetalleMovimientoInventario.setRowCount(0);
            tbl_detalleTomaMovimiento.setModel(modeloDetalleTomaFisica);
            // Establece el comboBox para los articulos
            tbl_detalleTomaMovimiento.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cmbArticulos));
            // Modifica los radioButton de seleccion en el aspecto Enabled
            Rdb_movimientoInventario.setEnabled(true);
            Rdb_tomaFisica.setEnabled(false);
            //  Cambia el texto del boton crear
            btn_CrearTomaMovimiento.setText("Crear la toma física");
            // Elimina los datos de la ultima fila de Movimiento de inventario
            modeloMovimientoInventario.removeRow(modeloMovimientoInventario.getRowCount() - 1);
            modeloMovimientoInventario.setRowCount(modeloMovimientoInventario.getRowCount() + 1);
            selectedRowTomaMovimiento = -1;
            selectedRowTomaMovimiento = -1;
        }
    }//GEN-LAST:event_Rdb_tomaFisicaActionPerformed

    /**
     * Cambia el modelo de la tabla por el de Movimiento de Inventario
     *
     * @param evt
     */
    private void Rdb_movimientoInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Rdb_movimientoInventarioActionPerformed
        if (Rdb_movimientoInventario.isSelected()) {
            // Establece el modelo Movimiento de Inventario
            tbl_tomaMovimiento.setModel(modeloMovimientoInventario);
            modeloDetalleTomaFisica.setRowCount(0);
            tbl_detalleTomaMovimiento.setModel(modeloDetalleMovimientoInventario);
            // Establece el comboBox para los articulos y para el movimiento de inventario
            tbl_detalleTomaMovimiento.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cmbArticulos));
            tbl_detalleTomaMovimiento.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cmbTipoDeMovimientos));
            // Cambia el comportamiento de los radioButtons
            Rdb_movimientoInventario.setEnabled(false);
            Rdb_tomaFisica.setEnabled(true);
            //  Cambia el texto del boton crear
            btn_CrearTomaMovimiento.setText("Crear el Movimiento");
            // Elimina los datos de la ultima fila de Toma Fisica
            modeloTomaFisica.removeRow(modeloTomaFisica.getRowCount() - 1);
            modeloTomaFisica.setRowCount(modeloTomaFisica.getRowCount() + 1);
            selectedRowTomaMovimiento = -1;
            selectedRowDetalleTomaMov = -1;
        }
    }//GEN-LAST:event_Rdb_movimientoInventarioActionPerformed

    /**
     * Metodo que restaura los valores de la ultima fila seleccionada y que no
     * fueron procesados, antes de seleccionar otra tabla
     *
     * @param evt
     */
    private void Cmb_TablasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Cmb_TablasFocusGained
        if (numFilaAnterior != -1) {
            valuesFromArrayToRow();
        }
        // Borra los valores de la ultima fila
        ((DefaultTableModel) tbl_Tabla.getModel()).removeRow(tbl_Tabla.getRowCount() - 1);
        ((DefaultTableModel) tbl_Tabla.getModel()).setRowCount(tbl_Tabla.getRowCount() + 1);
        // Cambia los valores tal y como si ninguna fila estuviera seleccionada
        numFilaAnterior = -1;
        datosFilaActual = null;
        pkSelectedRow = null;
        tbl_Tabla.clearSelection();
    }//GEN-LAST:event_Cmb_TablasFocusGained

    /**
     * Metodo utilizado para cargar la informacion del detalle de una fila
     * seleccionada o crear los datos necesarios para crear uno nuevo
     *
     * @param evt
     */
    private void tbl_tomaMovimientoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_tomaMovimientoMouseClicked
        // Obtiene el numero de fila sobre el que se ejecuto el clic
        int filaSeleccionada = tbl_tomaMovimiento.getSelectedRow();
        // Verifica si se dio clic sobre la misma fila, para no procesar la modificacion
        if (filaSeleccionada == selectedRowTomaMovimiento) {
            return;
        }
        // Cambia el valor de la fila seleccionada
        selectedRowTomaMovimiento = filaSeleccionada;
        selectedRowDetalleTomaMov = -1;
        // Verifica si los cambios se cargan sobre toma fisica o movimiento de inventario
        if (Rdb_tomaFisica.isSelected()) {
            // Borra lo que habia en la tabla detalle
            modeloDetalleTomaFisica.setRowCount(0);
            // Selecciono la ultima, carga los datos para llenar
            if (filaSeleccionada == modeloTomaFisica.getRowCount() - 1) {
                if (modeloTomaFisica.getValueAt(filaSeleccionada, 0) == null) {
                    int numDoc = (filaSeleccionada == 0) ? 1 : (int) modeloTomaFisica.getValueAt(filaSeleccionada - 1, 0) + 1;
                    String fecha = new SimpleDateFormat("dd/MM/YYYY").format(Calendar.getInstance().getTime());
                    Object[] datos = {numDoc, fecha, "", false, false, null};
                    modeloTomaFisica.removeRow(filaSeleccionada);
                    modeloTomaFisica.addRow(datos);
                    tbl_tomaMovimiento.getSelectionModel().setSelectionInterval(2, tbl_tomaMovimiento.getRowCount() - 1);
                }
            } // Carga la informacion del detalle de la toma fisica seleccionada
            else {
                getDatosForProceso(true);
            }
        } else {
            // Borra lo que habia en la tabla detalle
            modeloDetalleMovimientoInventario.setRowCount(0);
            // Selecciono la ultima, carga los datos para llenar
            if (filaSeleccionada == modeloMovimientoInventario.getRowCount() - 1) {
                if (modeloMovimientoInventario.getValueAt(filaSeleccionada, 0) == null) {
                    int numDoc = (filaSeleccionada == 0) ? 1 : (int) modeloMovimientoInventario.getValueAt(filaSeleccionada - 1, 0) + 1;
                    String fecha = new SimpleDateFormat("dd/MM/YYYY").format(Calendar.getInstance().getTime());
                    Object[] datos = {numDoc, fecha, "", false, false};
                    modeloMovimientoInventario.removeRow(filaSeleccionada);
                    modeloMovimientoInventario.addRow(datos);
                    tbl_tomaMovimiento.getSelectionModel().setSelectionInterval(2, tbl_tomaMovimiento.getRowCount() - 1);
                }
            } // Carga la informacion del detalle de la toma fisica seleccionada
            else {
                getDatosForProceso(true);
            }
        }
    }//GEN-LAST:event_tbl_tomaMovimientoMouseClicked

    /**
     * Metodo utilizado para crear un nuevo movimiento de inventario o toma
     * fisica
     *
     * @param evt
     */
    private void btn_CrearTomaMovimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CrearTomaMovimientoActionPerformed
        int numUltimaFila = tbl_tomaMovimiento.getRowCount() - 1;
        // Verifica si hay datos en la ultima fila para crear la toma fisica/movimiento de inventario
        if (tbl_tomaMovimiento.getValueAt(numUltimaFila, 0) == null) {
            JOptionPane.showMessageDialog(this, "No se puede " + btn_CrearTomaMovimiento.getText() + ". No hay"
                    + " datos en la última fila");
        } else {
            // Verifica si se ha creado la 'justificacion' para crear la toma fisica/movimiento de inventario
            if (tbl_tomaMovimiento.getValueAt(numUltimaFila, 2) == "") {
                JOptionPane.showMessageDialog(this, "Debe especificarse una justificacion para " + btn_CrearTomaMovimiento.getText());
            } else {
                String tabla = "\"schinventario\".";
                String campos;
                // Se verifica si se quiere crear una toma fisica o un movimiento de inventario
                if (Rdb_tomaFisica.isSelected()) {
                    int numDocumento = (int) tbl_tomaMovimiento.getValueAt(numUltimaFila, 0);
                    String fecha = (String) tbl_tomaMovimiento.getValueAt(numUltimaFila, 1);
                    String justificacion = (String) tbl_tomaMovimiento.getValueAt(numUltimaFila, 2);
                    String aplicado = (boolean) tbl_tomaMovimiento.getValueAt(numUltimaFila, 3) ? "S" : "N";
                    String anulado = (boolean) tbl_tomaMovimiento.getValueAt(numUltimaFila, 4) ? "S" : "N";
                    String aplicadoPor = (String) tbl_tomaMovimiento.getValueAt(numUltimaFila, 5);
                    // Crea la tabla y los campos donde se va a insertar
                    tabla += "toma_fisica";
                    campos = numDocumento + ", to_date('" + fecha + "', 'dd/mm/yyyy'), '" + justificacion + "', '"
                            + aplicado + "', '" + anulado + "', " + aplicadoPor;
                } else {
                    int numDocumento = (int) tbl_tomaMovimiento.getValueAt(numUltimaFila, 0);
                    String fecha = (String) tbl_tomaMovimiento.getValueAt(numUltimaFila, 1);
                    String justificacion = (String) tbl_tomaMovimiento.getValueAt(numUltimaFila, 2);
                    String aplicado = (boolean) tbl_tomaMovimiento.getValueAt(numUltimaFila, 3) ? "S" : "N";
                    String anulado = (boolean) tbl_tomaMovimiento.getValueAt(numUltimaFila, 4) ? "S" : "N";
                    // Crea la tabla y los campos donde se va a insertar
                    tabla += "movimiento_inventario";
                    campos = numDocumento + ", to_date('" + fecha + "', 'dd/mm/yyyy'), '" + justificacion + "', '"
                            + aplicado + "', '" + anulado + "'";
                }
                // Inserta los datos a la BD
                if (connection.insert(tabla, campos)) {
                    // Agrega una nueva fila
                    ((DefaultTableModel) tbl_tomaMovimiento.getModel()).setRowCount(tbl_tomaMovimiento.getRowCount() + 1);
                } else {
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error al " + btn_CrearTomaMovimiento.getText());
                }
                // Borra la seleccion
                selectedRowTomaMovimiento = -1;
                tbl_tomaMovimiento.clearSelection();
            }
        }
    }//GEN-LAST:event_btn_CrearTomaMovimientoActionPerformed

    /**
     * Metodo utilizado para generar el numero de linea para un nuevo detalle
     *
     * @param evt
     */
    private void tbl_detalleTomaMovimientoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_detalleTomaMovimientoMouseClicked
        // Obtiene el numero de fila sobre el que se ejecuto el clic
        int filaSeleccionada = tbl_detalleTomaMovimiento.getSelectedRow();
        // Verifica si se dio clic sobre la misma fila, para no procesar la modificacion
        if (filaSeleccionada == selectedRowDetalleTomaMov) {
            return;
        }
        // Cambia el valor de la fila seleccionada
        selectedRowDetalleTomaMov = filaSeleccionada;
        // Selecciono la ultima, carga los datos para llenar
        if (filaSeleccionada == tbl_detalleTomaMovimiento.getRowCount() - 1) {
            if (tbl_detalleTomaMovimiento.getValueAt(filaSeleccionada, 0) == null) {
                // Genera el numero de linea
                int numLinea = (filaSeleccionada == 0) ? 1
                        : (int) tbl_detalleTomaMovimiento.getValueAt(filaSeleccionada - 1, 0) + 1;
                tbl_detalleTomaMovimiento.setValueAt(numLinea, filaSeleccionada, 0);
            }
        }
    }//GEN-LAST:event_tbl_detalleTomaMovimientoMouseClicked

    /**
     * Metodo utilizado para crear un nuevo detalle de toma fisica o movimiento de inventario
     * @param evt 
     */
    private void Btn_CrearDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_CrearDetalleActionPerformed
        int ultimaFila = tbl_detalleTomaMovimiento.getRowCount() - 1;
        if (Rdb_movimientoInventario.isSelected()) {
            Object numLinea = tbl_detalleTomaMovimiento.getValueAt(ultimaFila, 0);
            String articulo = (String) tbl_detalleTomaMovimiento.getValueAt(ultimaFila, 1);
            String tipoMovimiento = (String) tbl_detalleTomaMovimiento.getValueAt(ultimaFila, 4);
            Double cantidad = (Double) tbl_detalleTomaMovimiento.getValueAt(ultimaFila, 5);
            // Verifica que no se hayan dejado campos sin valor
            if (numLinea != null && articulo != null && tipoMovimiento != null && cantidad != null) {
                try {
                    // Obtiene el codigo del articulo y hace un select de este para tener la existencia y el costo
                    String codArticulo = articulo.substring(0, articulo.indexOf(" = "));
                    ResultSet select = connection.select("precio_sin_imp, existencia", "\"schinventario\".articulo",
                            "cod_articulo = '" + codArticulo + "'");
                    select.next();
                    Double costo = select.getDouble(1);
                    Double existenciaPrevia = select.getDouble(2);
                    // Establece el valor de existencia y el de costo
                    tbl_detalleTomaMovimiento.setValueAt(costo, ultimaFila, 3);
                    tbl_detalleTomaMovimiento.setValueAt(existenciaPrevia, ultimaFila, 2);
                    // Se calcula el saldo general
                    Double saldoGeneral = costo*cantidad;
                    if(tipoMovimiento.equals("Resta")){
                        saldoGeneral *= -1;
                    }else if(tipoMovimiento.equals("No aplica")){
                        saldoGeneral = 0.0;
                    }
                    tbl_detalleTomaMovimiento.setValueAt(saldoGeneral, ultimaFila, 6);
                    // se agrega una nueva fila
                    modeloDetalleMovimientoInventario.setRowCount(tbl_detalleTomaMovimiento.getRowCount() + 1);
                    tbl_detalleTomaMovimiento.clearSelection();                    
                } catch (SQLException ex) {
                    Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No es posible crear un nuevo detalle. Información faltante");
            }
        } else {
            Object numLinea = tbl_detalleTomaMovimiento.getValueAt(ultimaFila, 0);
            String articulo = (String) tbl_detalleTomaMovimiento.getValueAt(ultimaFila, 1);
            Double existenciaFisica = (Double) tbl_detalleTomaMovimiento.getValueAt(ultimaFila, 3);
            // Verifica que no se hayan dejado campos sin valor
            if (numLinea != null && articulo != null && existenciaFisica != null) {
                try {
                    // Obtiene el codigo del articulo y hace un select de este para tener la existencia y el costo
                    String codArticulo = articulo.substring(0, articulo.indexOf(" = "));
                    ResultSet select = connection.select("precio_sin_imp, existencia", "\"schinventario\".articulo",
                            "cod_articulo = '" + codArticulo + "'");
                    select.next();
                    Double costo = select.getDouble(1);
                    Double existenciaPrevia = select.getDouble(2);
                    // Establece el valor de existencia y el de costo
                    tbl_detalleTomaMovimiento.setValueAt(costo, ultimaFila, 4);
                    tbl_detalleTomaMovimiento.setValueAt(existenciaPrevia, ultimaFila, 2);
                    // se agrega una nueva fila
                    modeloDetalleTomaFisica.setRowCount(tbl_detalleTomaMovimiento.getRowCount() + 1);
                    tbl_detalleTomaMovimiento.clearSelection();                    
                } catch (SQLException ex) {
                    Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No es posible crear un nuevo detalle. Información faltante");
            }
        }
    }//GEN-LAST:event_Btn_CrearDetalleActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Btn_Actualizar;
    private javax.swing.JButton Btn_Anular;
    private javax.swing.JButton Btn_Borrar;
    private javax.swing.JButton Btn_CrearDetalle;
    private javax.swing.JButton Btn_Insertar;
    private javax.swing.JButton Btn_Salvar;
    private javax.swing.JButton Btn_aplicar;
    private javax.swing.JComboBox Cmb_Tablas;
    private javax.swing.JLabel Lbl_Titulo;
    private javax.swing.JPanel Pnl_Consultas;
    private javax.swing.JPanel Pnl_Mantenimiento;
    private javax.swing.JRadioButton Rdb_movimientoInventario;
    private javax.swing.JRadioButton Rdb_tomaFisica;
    private javax.swing.ButtonGroup Rdg_Procesos;
    private javax.swing.JButton btn_CrearTomaMovimiento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tbl_Tabla;
    private javax.swing.JTable tbl_detalleTomaMovimiento;
    private javax.swing.JTable tbl_tomaMovimiento;
    // End of variables declaration//GEN-END:variables
}
