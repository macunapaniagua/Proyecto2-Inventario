/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Codigo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Mario A
 */
public class Conexion {

    // Se crea variable conexion con la base de datos
    private Connection conn;

    public Conexion(String pNombreDB, String pUser, String pPassword) throws ClassNotFoundException {
        try {
            String driver = "org.postgresql.Driver"; // se asigna el driver de conexion con base de datos
            Class.forName(driver);
            String connString = "jdbc:postgresql://localhost:5432/" + pNombreDB; //
            String user = pUser;
            String password = pPassword;

            this.conn = DriverManager.getConnection(connString, user, password);
            System.out.println("Conexion realizada con exito");

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Metodo utilizado para seleccionar datos de una tabla
     * @param pCampos campos que deseo visualizar
     * @param pTabla tabla donde voy a obtener la informacion
     * @param pCondicion condicion para realizar una busqueda especifica
     * @return ResulSet con la informacion de todas las tuplas que cumplen el 
     * criterio de busqueda dado
     */
    public ResultSet select(String pCampos, String pTabla, String pCondicion) {
        ResultSet rs = null; //obtener los datos del select
        Statement s = null; // se utiliza para inicializar la conexión
        String sentencia = "";
        try {
            s = this.conn.createStatement();
            sentencia = " SELECT " + pCampos + " FROM " + pTabla; // se crea el select
            if (!pCondicion.isEmpty()) {
                sentencia += " WHERE " + pCondicion;
            }
            rs = s.executeQuery(sentencia); // 
        } catch (Exception e) {
            System.out.printf("Error: " + e.toString());
        }
        return rs;
    }

    /**
     * Metodos utilizados para insertar una tupla a una tabla
     * @param pTabla tabla en la que se va a insertar los datos
     * @param pDatos valores de la tupla
     * @return true si la insercion es exitosa o false si ocurre un error
     */
    public boolean insert(String pTabla, String pDatos) {
        Statement s = null; // se utiliza para inicializar la conexión
        String sentencia = "";
        try {
            s = this.conn.createStatement();
            //String campos = "(cod_articulo, descripcion, precio_sin_impuesto, costo_proveedor, activo, existencia)";
            sentencia = "INSERT INTO " + pTabla /*+ campos */+ " VALUES (" + pDatos + ");"; // se crea el insert
            s.execute(sentencia); //
            return true;
        } catch (Exception e) {
            System.out.printf("Error: " + e.toString());
            return false;
        }
    }

    public boolean actualizar(String pTabla, String pCambio, String pCondicion) {
        Statement s = null; // se utiliza para inicializar la conexión
        String sentencia = "";
        try {
            s = this.conn.createStatement();
            sentencia = "UPDATE " + pTabla + " SET " + pCambio + " WHERE " + pCondicion; // se crea el insert
            s.execute(sentencia); //
            return true;
        } catch (Exception e) {
            System.out.printf("Error: " + e.toString());
            return false;
        }
    }

    /**
     * Metodo utilizado para eliminar tuplas de la base de datos
     * @param pTabla Tabla donde se encuentra la tupla a eliminar
     * @param pCondicion Condicion que identifica la tupla a eliminar (PK).
     * @return true si se elimino una tupla o false si ocurrio un error.
     */
    public boolean borrar(String pTabla, String pCondicion) {
        Statement s; // se utiliza para inicializar la conexión
        String sentencia;
        try {
            s = this.conn.createStatement();
            sentencia = "DELETE FROM " + pTabla + " WHERE " + pCondicion; // se crea el BORRADO
            s.execute(sentencia); //
            return true;
        } catch (Exception e) {
            System.out.printf("Error: " + e.toString());
            return false;
        }
    }
}
