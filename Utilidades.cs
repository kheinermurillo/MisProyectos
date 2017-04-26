using System;
using System.Data;
using System.Text;

    public class Utilidades
    {

        #region Lectura de campos

        public static DateTime ObtenerFecha(DataRow dr, String Campo)
        {
            try
            {
                return (DateTime)dr[Campo];
            }
            catch
            {
                return DateTime.MinValue;
            }
        }

        public static double ObtenerReal(DataRow dr, String Campo)
        {
            try
            {
                return (double)dr[Campo];
            }
            catch
            {
                return 0;
            }
        }

        public static int ObtenerEntero(DataRow dr, String Campo)
        {
            try
            {
                return (int)dr[Campo];
            }
            catch
            {
                try
                {
                    return Convert.ToInt16(dr[Campo]);
                }
                catch
                {
                    return 0;
                }
            }
        }


        public static Byte ObtenerByte(DataRow dr, String Campo)
        {
            try
            {
                return (Byte)dr[Campo];
            }
            catch
            {
                return 0;
            }
        }

        public static String ObtenerTexto(DataRow dr, String Campo)
        {
            try
            {
                return (String)dr[Campo].ToString();
            }
            catch
            {
                return "";
            }
        }

        public static Boolean ObtenerBooleano(DataRow dr, String Campo)
        {
            try
            {
                return (Boolean)dr[Campo];
            }
            catch
            {
                return false;
            }
        }

        #endregion

        #region Encriptar Datos


        // Encripta una cadena de texto
        public static String Encriptar(String Texto)
        {
            byte[] caracteres = Encoding.Unicode.GetBytes(Texto);
            return Convert.ToBase64String(caracteres);
        }

        // Desencripta una cadena de texto
        public static String Desencriptar(String Texto)
        {
            byte[] caracteres = Convert.FromBase64String(Texto);
            //result = System.Text.Encoding.Unicode.GetString(decryted, 0, decryted.ToArray().Length);
            return Encoding.Unicode.GetString(caracteres);
        }

        #endregion

        // Método para redimensionar un vector
        public static Array RedimensionarVector(Array ArregloOriginal, Int32 NuevoTamaño)
        {

            // Determina el tipo de cada elemento 
            Type t = ArregloOriginal.GetType().GetElementType();

            // Construye un nuevo arreglo con el nuevo número de elementos
            // Cada elemento es del mismo tipo del arreglo original
            Array NuevoArreglo = Array.CreateInstance(t, NuevoTamaño);

            // Copia el elemento desde el arreglo original en el nuevo arreglo
            Array.Copy(ArregloOriginal, 0,
               NuevoArreglo, 0, Math.Min(ArregloOriginal.Length, NuevoTamaño));

            // retorna el nuevo arreglo
            return NuevoArreglo;
        }


    }

