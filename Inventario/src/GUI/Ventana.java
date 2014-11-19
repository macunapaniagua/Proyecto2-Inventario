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
    // Modelos de cada una de las tablas de apartado Modificacion
    private DefaultTableModel modeloMarca;
    private DefaultTableModel modeloFamilia;
    private DefaultTableModel modeloImpuesto;
    private DefaultTableModel modeloArticulo;
    private DefaultTableModel modeloTipoDeMovimiento;
    private DefaultTableModel modeloActual;

    // ComboBox utilizado para seleccionar los elementos existentes en cierta tabla
    private JComboBox<String> cmbFamilias;
    private JComboBox<String> cmbMarcas;
    private JComboBox<String> cmbImpuestos;
    private JComboBox<String> cmbTipoDeMovimientos;

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
        modeloActual = modeloFamilia;
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
            "Precio sin Impuesto", "Impuesto", "Porcentaje de utilidad", "Costo", "Activo"};
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

        // SE ESTABLECE CADA UNO DE LOS MODELOS A MODELO ACTUAL PARA ENVIAR A CARGAR LOS DATOS
        modeloActual = modeloFamilia;
        getDatosFromDataBase();
        modeloActual = modeloMarca;
        getDatosFromDataBase();
        modeloActual = modeloArticulo;
        getDatosFromDataBase();
        modeloActual = modeloImpuesto;
        getDatosFromDataBase();
        modeloActual = modeloTipoDeMovimiento;
        getDatosFromDataBase();
    }

    /**
     * Método utilizado para cargar la información de cada tabla en su
     * respectivo modelo
     */
    private void getDatosFromDataBase() {
        try {
            ResultSet respuestaSelect;
            if (modeloActual == modeloFamilia) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".familia_articulo", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(3).equals("S");
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(2), activo};
                    // Inserta los datos al modelo familia
                    modeloActual.addRow(data);
                }
            } else if (modeloActual == modeloMarca) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".marca_articulo", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(3).equals("S");
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(2), activo};
                    // Inserta los datos al modelo marca
                    modeloActual.addRow(data);
                }
            } else if (modeloActual == modeloImpuesto) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".impuesto", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(4).equals("S");
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(2),
                        respuestaSelect.getDouble(3), activo};
                    // Inserta los datos al modelo impuesto
                    modeloActual.addRow(data);
                }
            } else if (modeloActual == modeloTipoDeMovimiento) {
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
                    modeloActual.addRow(data);
                }
            } else if (modeloActual == modeloArticulo) {
                respuestaSelect = this.connection.select("*", "\"schinventario\".articulo", "");
                while (respuestaSelect.next()) {
                    boolean activo = respuestaSelect.getString(8).equals("S");
                    // Crea un arreglo con los datos de la tupla
                    Object[] data = {respuestaSelect.getString(1), respuestaSelect.getString(4),
                        respuestaSelect.getString(2), respuestaSelect.getString(3),
                        respuestaSelect.getDouble(5), respuestaSelect.getString(6),
                        respuestaSelect.getDouble(9), respuestaSelect.getDouble(7), activo};
                    // Inserta los datos al modelo artículo
                    modeloActual.addRow(data);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        modeloActual.setRowCount(modeloActual.getRowCount() + 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_Tabla = new javax.swing.JTable();
        Btn_Insertar = new javax.swing.JButton();
        Btn_Actualizar = new javax.swing.JButton();
        Btn_Borrar = new javax.swing.JButton();
        Lbl_Titulo = new javax.swing.JLabel();
        Cmb_Tablas = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

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
        Btn_Insertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_InsertarActionPerformed(evt);
            }
        });

        Btn_Actualizar.setText("Update");
        Btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_ActualizarActionPerformed(evt);
            }
        });

        Btn_Borrar.setText("Delete");
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Lbl_Titulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cmb_Tablas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 652, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(Btn_Insertar, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(Btn_Actualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Btn_Borrar, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addGap(163, 163, 163))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Cmb_Tablas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(Lbl_Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Btn_Insertar, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(Btn_Actualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(Btn_Borrar, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Mantenimiento", jPanel1);

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

    private void Btn_InsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_InsertarActionPerformed
        String campos = "";
        String tabla = "\"schinventario\".";
        int filaInsercion = tbl_Tabla.getRowCount() - 1;
        /*
        
         Aqui deberia de restaurarse la informacion de la fila anterior. Pudo no ser la de insercion la
         que se estaba modificando
        
         */
        tbl_Tabla.getSelectionModel().setSelectionInterval(filaInsercion, filaInsercion);

        if (modeloActual == modeloFamilia) {
            tabla += "familia_articulo";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            Object valCheckActivo = tbl_Tabla.getValueAt(filaInsercion, 2);
            String activo = (valCheckActivo == null || !(boolean) valCheckActivo) ? "N" : "S";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (codigo == null || descripcion == null || codigo.equals("") || descripcion.equals("")) {
                JOptionPane.showMessageDialog(this, "No es posible insertar una nueva familia si hay campos sin información");
                return;
            } else {
                campos = "'" + codigo + "', '" + descripcion + "', '" + activo + "'";
//////                System.out.println(campos);
            }

        } else if (modeloActual == modeloMarca) {
            tabla += "marca_articulo";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            Object valCheckActivo = tbl_Tabla.getValueAt(filaInsercion, 2);
            String activo = (valCheckActivo == null || !(boolean) valCheckActivo) ? "N" : "S";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (codigo == null || descripcion == null || codigo.equals("") || descripcion.equals("")) {
                JOptionPane.showMessageDialog(this, "No es posible insertar una nueva marca si hay campos sin información");
                return;
            } else {
                campos = "'" + codigo + "', '" + descripcion + "', '" + activo + "'";
//////                System.out.println(campos);
            }

        } else if (modeloActual == modeloImpuesto) {
            tabla += "impuesto";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            Double porcentaje = (Double) tbl_Tabla.getValueAt(filaInsercion, 2);
            Object valCheckActivo = tbl_Tabla.getValueAt(filaInsercion, 3);
            String activo = (valCheckActivo == null || !(boolean) valCheckActivo) ? "N" : "S";

            // Verifica que los campos 'codigo' y 'descripcion' contengan algun valor, ya que son NOT NULL
            if (codigo == null || descripcion == null || porcentaje == null || codigo.equals("") || descripcion.equals("")) {
                JOptionPane.showMessageDialog(this, "No es posible insertar un nuevo impuesto si hay campos sin información");
                return;
            } else {
                campos = "'" + codigo + "', '" + descripcion + "', " + porcentaje + ", '" + activo + "'";
//////                System.out.println(campos);
            }

        } else if (modeloActual == modeloTipoDeMovimiento) {
            tabla += "tipo_movimiento";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String tipo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            String operacion = (String) tbl_Tabla.getValueAt(filaInsercion, 2);
            Object valCheckActivo = tbl_Tabla.getValueAt(filaInsercion, 3);
            String activo = (valCheckActivo == null || !(boolean) valCheckActivo) ? "N" : "S";

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

        } else if (modeloActual == modeloArticulo) {
            tabla += "articulo";
            // Obtiene el valor de los campos que se van a insertar en la BD
            String codigo = (String) tbl_Tabla.getValueAt(filaInsercion, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaInsercion, 1);
            String familia = (String) tbl_Tabla.getValueAt(filaInsercion, 2);
            String marca = (String) tbl_Tabla.getValueAt(filaInsercion, 3);
            Double precioSinImp = (Double) tbl_Tabla.getValueAt(filaInsercion, 4);
            String impuesto = (String) tbl_Tabla.getValueAt(filaInsercion, 5);
            Double utilidad = (Double) tbl_Tabla.getValueAt(filaInsercion, 6);
            Object valCheckActivo = tbl_Tabla.getValueAt(filaInsercion, 8);
            String activo = (valCheckActivo == null || !(boolean) valCheckActivo) ? "N" : "S";

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
            System.out.println("Inserción exitosa");

//////            numFilaAnterior = -1;
//////            datosFilaActual = null;
//////            pkSelectedRow = null;
//////            tbl_Tabla.clearSelection();
            modeloActual.setNumRows(modeloActual.getRowCount() + 1);
        } else {
            JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la inserción");
            modeloActual.setRowCount(0);
            getDatosFromDataBase();
        }
    }//GEN-LAST:event_Btn_InsertarActionPerformed

    /**
     * Metodo utilizado para eliminar la fila seleccionada en la tabla, al
     * presionar el botón "borrar"
     *
     * @param evt
     */
    private void Btn_BorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_BorrarActionPerformed
        int filaSeleccionada = tbl_Tabla.getSelectedRow();
        if (filaSeleccionada != -1 && filaSeleccionada < modeloActual.getRowCount() - 1) {
            String tabla = "\"schinventario\".";
            String condicion = "";

            /* ESTOS IF-ELSE IF SON PARA BUSCAR LA CONDICION (PK) DE LA TABLA 
             QUE SE VA A BORRAR */
            if (modeloActual == modeloFamilia) {
                tabla += "familia_articulo";
                condicion = "cod_familia = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
            } else if (modeloActual == modeloMarca) {
                tabla += "marca_articulo";
                condicion = "cod_marca = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
            } else if (modeloActual == modeloArticulo) {
                tabla += "articulo";
                condicion = "cod_articulo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
            } else if (modeloActual == modeloImpuesto) {
                tabla += "impuesto";
                condicion = "cod_impuesto = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
            } else if (modeloActual == modeloTipoDeMovimiento) {
                tabla += "tipo_movimiento";
                condicion = "tipo_movimiento = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
            }

            if (connection.borrar(tabla, condicion)) {
                System.out.println("Borrado exitoso");
                modeloActual.removeRow(filaSeleccionada);
            } else {
                JOptionPane.showMessageDialog(this, "No es posible eliminar la tupla seleccionada");
            }
        } else if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado una fila a borrar");
        } else {
            JOptionPane.showMessageDialog(this, "Solo puede eliminar una tupla existente");
        }
    }//GEN-LAST:event_Btn_BorrarActionPerformed

    private void Btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_ActualizarActionPerformed
        int filaSeleccionada = tbl_Tabla.getSelectedRow();
        if (filaSeleccionada != -1 && filaSeleccionada < modeloActual.getRowCount() - 1) {
            String tabla = "\"schinventario\".";
            String datos = "";
            String primaryKey = "";

            if (modeloActual == modeloFamilia) {
                tabla += "familia_articulo";
                primaryKey = "cod_familia =" + pkSelectedRow;
                //primaryKey += "cod_familia = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
                datos = "cod_familia = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "', "
                        + "descripcion = '" + tbl_Tabla.getValueAt(filaSeleccionada, 1) + "', "
                        + "activo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 2) + "'";

            } else if (modeloActual == modeloMarca) {
                tabla += "marca_articulo";
                primaryKey = "cod_marca =" + pkSelectedRow;
                //primaryKey += "cod_marca = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
                datos = "cod_marca = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "', "
                        + "descripcion = '" + tbl_Tabla.getValueAt(filaSeleccionada, 1) + "', "
                        + "activo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 2) + "'";

            } else if (modeloActual == modeloArticulo) {
                tabla += "articulo";
                primaryKey = "cod_articulo =" + pkSelectedRow;
                //primaryKey += "cod_articulo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
                datos = "cod_articulo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "', "
                        + "cod_familia = '" + tbl_Tabla.getValueAt(filaSeleccionada, 1) + "', "
                        + "cod_marca = '" + tbl_Tabla.getValueAt(filaSeleccionada, 2) + "', "
                        + "descripcion = '" + tbl_Tabla.getValueAt(filaSeleccionada, 3) + "', "
                        + "precio_sin_imp = " + tbl_Tabla.getValueAt(filaSeleccionada, 4) + ", "
                        + "cod_impuesto = '" + tbl_Tabla.getValueAt(filaSeleccionada, 5) + "', "
                        + "costo = " + tbl_Tabla.getValueAt(filaSeleccionada, 6) + ", "
                        + "activo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 7) + "'";

            } else if (modeloActual == modeloImpuesto) {
                tabla += "impuesto";
                primaryKey = "cod_impuesto =" + pkSelectedRow;
//                primaryKey += "cod_impuesto = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
                datos = "cod_impuesto = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "', "
                        + "descripcion = '" + tbl_Tabla.getValueAt(filaSeleccionada, 1) + "', "
                        + "porcentaje = " + tbl_Tabla.getValueAt(filaSeleccionada, 2) + ", "
                        + "activo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 3) + "'";

            } else if (modeloActual == modeloTipoDeMovimiento) {
                tabla += "tipo_movimiento";
                primaryKey = "tipo_movimiento =" + pkSelectedRow;
//                primaryKey += "tipo_movimiento = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "'";
                datos = "tipo_movimiento = '" + tbl_Tabla.getValueAt(filaSeleccionada, 0) + "', "
                        + "descripcion = '" + tbl_Tabla.getValueAt(filaSeleccionada, 1) + "', "
                        + "tipo_operacion = '" + tbl_Tabla.getValueAt(filaSeleccionada, 2) + "', "
                        + "activo = '" + tbl_Tabla.getValueAt(filaSeleccionada, 3) + "'";
            }

            if (connection.actualizar(tabla, datos, primaryKey)) {
                System.out.println("Modificación exitosa");
            } else {
                modeloActual.setNumRows(0);
                /**
                 * AQUI NO ES NECESARIO MODIFICAR TODA LA TABLA, SOLO LOS CAMPOS
                 * DE LA PK
                 */
                this.getDatosFromDataBase();
                JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la modificación");
            }
        } else if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado la fila para modificarle los datos");
        } else {
            JOptionPane.showMessageDialog(this, "Solo puede modificar los datos de una tupla existente");
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
            modeloActual = modeloFamilia;
            Lbl_Titulo.setText("Tabla Familia");
        } else if (Cmb_Tablas.getSelectedItem() == "Marca") {
            tbl_Tabla.setModel(modeloMarca);
            modeloActual = modeloMarca;
            Lbl_Titulo.setText("Tabla Marca");
        } else if (Cmb_Tablas.getSelectedItem() == "Artículo") {
            tbl_Tabla.setModel(modeloArticulo);
            modeloActual = modeloArticulo;
            // Carga los valores desde un Combobox, para las celdas personalizadas en la tabla articulo
            tbl_Tabla.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cmbFamilias));
            tbl_Tabla.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbMarcas));
            tbl_Tabla.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cmbImpuestos));
            Lbl_Titulo.setText("Tabla Artículo");
        } else if (Cmb_Tablas.getSelectedItem() == "Impuesto") {
            tbl_Tabla.setModel(modeloImpuesto);
            modeloActual = modeloImpuesto;
            Lbl_Titulo.setText("Tabla Impuesto");
        } else if (Cmb_Tablas.getSelectedItem() == "Tipo de Movimiento de Inventario") {
            tbl_Tabla.setModel(modeloTipoDeMovimiento);
            modeloActual = modeloTipoDeMovimiento;
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
            /* Verifica que la fila seleaccionada anteriormente no fuera la ultima y ademas, que
             que hubiera seleccionado una fila antes. Esto para restaurar los valores que habian ahi*/
            if (numFilaAnterior != -1 && numFilaAnterior != tbl_Tabla.getRowCount() - 1) {
                valuesFromArrayToRow();
            }
            /* Verifica si la fila seleccionada no es la ultima, con el fin de almacenar los valores
             originales de la fila en un arreglo*/
            if (numFilaActual != tbl_Tabla.getRowCount() - 1) {
                valuesFromRowToArray();
            }

            // Obtiene la Pk de la fila seleccionada
            pkSelectedRow = (String) tbl_Tabla.getValueAt(numFilaActual, 0);
            // Cambia el num de fila anterior por el actual. El proximo clic esta será la fila anterior.
            numFilaAnterior = numFilaActual;
        }
    }//GEN-LAST:event_tbl_TablaMousePressed

    /**
     * Metodo utilizado para cargar todos los datos de una tupla en un arreglo
     * antes de que se realice alguna modificacion a un campo de esta.
     */
    private void valuesFromRowToArray() {
        int filaActual = tbl_Tabla.getSelectedRow();

        if (modeloActual == modeloFamilia) {
            String codigo = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 2);
            datosFilaActual = new Object[]{codigo, descripcion, activo};

        } else if (modeloActual == modeloMarca) {
            String codigo = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 2);
            datosFilaActual = new Object[]{codigo, descripcion, activo};

        } else if (modeloActual == modeloImpuesto) {
            String codigo = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            Double porcentaje = (Double) tbl_Tabla.getValueAt(filaActual, 2);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 3);
            datosFilaActual = new Object[]{codigo, descripcion, porcentaje, activo};

        } else if (modeloActual == modeloTipoDeMovimiento) {
            String tipoMovimiento = (String) tbl_Tabla.getValueAt(filaActual, 0);
            String descripcion = (String) tbl_Tabla.getValueAt(filaActual, 1);
            String tipoOperacion = (String) tbl_Tabla.getValueAt(filaActual, 2);
            boolean activo = (boolean) tbl_Tabla.getValueAt(filaActual, 3);
            datosFilaActual = new Object[]{tipoMovimiento, descripcion, tipoOperacion, activo};

        } else if (modeloActual == modeloArticulo) {
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
        if (modeloActual == modeloFamilia) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[2], numFilaAnterior, 2);

        } else if (modeloActual == modeloMarca) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[2], numFilaAnterior, 2);

        } else if (modeloActual == modeloImpuesto) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((Double) datosFilaActual[2], numFilaAnterior, 2);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[3], numFilaAnterior, 3);

        } else if (modeloActual == modeloTipoDeMovimiento) {
            tbl_Tabla.setValueAt((String) datosFilaActual[0], numFilaAnterior, 0);
            tbl_Tabla.setValueAt((String) datosFilaActual[1], numFilaAnterior, 1);
            tbl_Tabla.setValueAt((String) datosFilaActual[2], numFilaAnterior, 2);
            tbl_Tabla.setValueAt((boolean) datosFilaActual[3], numFilaAnterior, 3);

        } else if (modeloActual == modeloArticulo) {
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
     * Metodo que restaura los valores de la ultima fila seleccionada y que no
     * fueron procesados, antes de seleccionar otra tabla
     *
     * @param evt
     */
    private void Cmb_TablasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Cmb_TablasFocusGained
        if (numFilaAnterior != -1 && numFilaAnterior != tbl_Tabla.getRowCount() - 1) {
            valuesFromArrayToRow();
        }
        // Cambia los valores tal y como si ninguna fila estuviera seleccionada
        numFilaAnterior = -1;
        datosFilaActual = null;
        pkSelectedRow = null;
        tbl_Tabla.clearSelection();
    }//GEN-LAST:event_Cmb_TablasFocusGained

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
    private javax.swing.JButton Btn_Borrar;
    private javax.swing.JButton Btn_Insertar;
    private javax.swing.JComboBox Cmb_Tablas;
    private javax.swing.JLabel Lbl_Titulo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tbl_Tabla;
    // End of variables declaration//GEN-END:variables
}
