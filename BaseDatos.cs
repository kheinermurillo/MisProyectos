using System;
using System.Data;
using System.Data.SqlClient;


public class BaseDatos
{

    private SqlConnection cn;
    private String strConexion;

    #region Constructores

    //Metodo Constructor
    public BaseDatos()
    {
        strConexion = "";
    }

    //Metodo Constructor
    public BaseDatos(String strConexion)
    {
        this.strConexion = strConexion;
    }

    #endregion

    #region Propiedades

    //Propiedad para hacer accesible la cadena de conexion
    public String CadenaConexion
    {
        get
        {
            return strConexion;
        }
        set
        {
            strConexion = value;
            cn = null;
        }
    }

    //Propiedad que devuelve el objeto de conexión
    public SqlConnection Conexion
    {
        get { return cn; }
    }

    #endregion

    #region Métodos

    //Establece conexión con la base de datos de acuerdo a la cadena de conexión
    public bool Conectar()
    {
        if (cn == null)
        {
            if (strConexion != null && strConexion.Length > 0)
            {
                try
                {
                    cn = new SqlConnection(strConexion);
                    cn.Open();
                    return true;
                }
                catch (Exception e)
                {
                    throw new ArgumentException("Error al conectarse a la base de datos:\n" + e.Message);
                }
            }
            else return false;
        }
        else return true;
    }

    //Cierra la conexión abierta
    public bool Cerrar()
    {
        try { cn.Close(); return true; }
        catch (Exception e)
        {
            throw new ArgumentException("Error al cerrar la conexión a la base de datos:\n" + e.Message);
        }
    }


    public SqlDataReader ConsultarDR(String strConsulta)
    {
        if (Conectar())
            try
            {
                SqlCommand cmd = new SqlCommand(strConsulta, cn);
                return cmd.ExecuteReader();
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error al consultar la base de datos:\n" + e.Message);
            }
        else return null;
    }

    //Método para ejecutar instrucción SELECT
    public DataTable Consultar(String strConsulta)
    {
        if (Conectar())
            try
            {
                DataTable dt = new DataTable();
                SqlDataAdapter da = new SqlDataAdapter(strConsulta, cn);
                da.Fill(dt);
                return dt;
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error al consultar la base de datos:\n" + e.Message);
            }
        else return null;
    }

    public DataTable Consultar(String strConsulta, SqlTransaction t)
    {
        if (Conectar())
            try
            {
                DataTable dt = new DataTable();
                SqlDataAdapter da = new SqlDataAdapter(strConsulta, cn);
                da.SelectCommand.Transaction = t;
                da.Fill(dt);
                return dt;
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error al consultar la base de datos:\n" + e.Message);
            }
        else return null;
    }

    //Método para ejecutar instrucciones INSERT, UPDATE, DELETE
    public bool Actualizar(String strConsulta)
    {
        if (Conectar())
            try
            {
                SqlCommand cmd = new SqlCommand(strConsulta, cn);
                cmd.CommandTimeout = 1200;
                cmd.ExecuteNonQuery();
                cmd = null;
                return true;
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error al actualizar la base de datos:\n" + e.Message);
            }
        else return false;
    }

    public bool Actualizar(String strConsulta, SqlTransaction t)
    {
        if (Conectar())
            try
            {
                SqlCommand cmd = new SqlCommand(strConsulta, cn);
                cmd.CommandTimeout = 1200;
                cmd.Transaction = t;
                cmd.ExecuteNonQuery();
                cmd = null;
                return true;
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error al actualizar la base de datos:\n" + e.Message);
            }
        else return false;
    }

    //Metodo para actualizar un campo imagen desde un archivo
    //public bool ActualizarImagen(String Tabla, String Campo, String Condicion, String ArchivoImagen)
    //{
    //    try
    //    {
    //        //Leer la imagen desde el archivo
    //        Byte[] Imagen = Archivo.CargarImagen(ArchivoImagen);
    //        return ActualizarImagen(Tabla, Campo, Condicion, Imagen);
    //    }
    //    catch (Exception e)
    //    {
    //        throw new ArgumentException("Error al actualizar imagen en la base de datos:\n" + e.Message);
    //    }
    //}


    //Metodo para actualizar un campo imagen desde un vector de octetos
    public bool ActualizarImagen(String Tabla, String Campo, String Condicion, Byte[] Imagen)
    {
        if (Imagen != null && Conectar())
            try
            {
                //Definir el parámetro de tipo IMAGE para la consulta
                SqlParameter prmImagen = new SqlParameter();
                prmImagen.SqlDbType = SqlDbType.Image;
                prmImagen.ParameterName = "Imagen";
                //Asignar los octetos cargados desde la imagen
                prmImagen.Value = Imagen;
                //Definir la cadena de consulta
                String strConsulta = "UPDATE " + Tabla +
                    " SET " + Campo + "=@Imagen";
                if (!Condicion.Equals(String.Empty))
                    strConsulta += " WHERE " + Condicion;
                SqlCommand cmd = new SqlCommand(strConsulta, cn);
                //Agregar el parámetro
                cmd.Parameters.Add(prmImagen);
                //Ejecutar la consulta
                cmd.ExecuteNonQuery();
                cmd = null;
                return true;
            }
            catch (Exception e)
            {
                throw new ArgumentException("Error al actualizar imagen en la base de datos:\n" + e.Message);
            }
        else return false;
    }

    //Metodo para Obtener la imagen binaria de un campo tipo imagen
    public byte[] ObtenerImagen(String Tabla, String Campo, String Condicion)
    {
        //Construir consulta
        String strConsulta = "SELECT " + Campo + " FROM " + Tabla + " WHERE " + Condicion;

        //Ejecutar la consulta
        DataTable tbl = Consultar(strConsulta);

        //verificar que la consulta devuelve registros
        if (tbl != null && tbl.Rows.Count > 0 &&
            !Convert.IsDBNull(tbl.Rows[0][0]))
        {

            return (byte[])tbl.Rows[0][0];
        }
        else
            return null;
    }


    //Metodo que devuelve la clave primaria basado en una consulta de una clave alterna
    public int ObtenerId(String strConsulta)
    {
        try
        {
            //Ejecutar la consulta
            DataTable tbl = Consultar(strConsulta);

            //verificar que la consulta devuelve registros
            if (tbl != null && tbl.Rows.Count > 0)
            {
                return (int)tbl.Rows[0]["Id"];
            }
            else
            {
                return -1;
            }
        }
        catch (Exception ex)
        {
            throw new ArgumentException("Error al obtener clave primaria:\n" + ex.Message);
        }
    }

    //Metodo que devuelve la clave primaria basado en una consulta de una clave alterna
    public int ObtenerId(String strConsulta, SqlTransaction t)
    {
        try
        {
            //Ejecutar la consulta
            DataTable tbl = Consultar(strConsulta, t);

            //verificar que la consulta devuelve registros
            if (tbl != null && tbl.Rows.Count > 0)
            {
                return (int)tbl.Rows[0]["Id"];
            }
            else
            {
                return -1;
            }
        }
        catch (Exception ex)
        {
            throw new ArgumentException("Error al obtener clave primaria:\n" + ex.Message);
        }
    }

    #endregion
}

