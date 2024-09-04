package org.example;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.List;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static ArbolB arbol;
    public static void main(String[] args) {
        //Escáner de texto
        Scanner scanner = new Scanner(System.in);
        arbol = new ArbolB(5);
        //Menú
        while(true){
            System.out.println("Laboratorio 1 Erick Rivas");
            System.out.println("1. Importar CSV");
            System.out.println("2. Salir");
            System.out.println("Ingrese una opción: ");

            int opcion = 0;
            //Validar opción
            try{
                opcion = scanner.nextInt();
            }catch(Exception e){
            }
            scanner.nextLine();
            //Evaluar opción
            switch(opcion){
                case 1:
                    importarLibros();
                    break;
                case 2:
                    System.out.print("Saliendo del programa...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }
    private static void importarLibros(){
        String ultimaLinea = "";
        //Crear un JFileChooser
        JFileChooser archivo = new JFileChooser();
        //Filtro para archivos CSV
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos CSV", "csv");
        archivo.setFileFilter(filtro);

        //Mostrar diálogo para abrir archivo
        int seleccion = archivo.showOpenDialog(null);
        //Si el usuario selecciona un archivo
        if(seleccion == JFileChooser.APPROVE_OPTION){
            try{
                //Obtener archivo seleccionado
                File archivoSeleccionado = archivo.getSelectedFile();
                //Leer archivo
                try (BufferedReader br = new BufferedReader(new FileReader(archivoSeleccionado))) {
                    //Leer archivo línea por línea
                    int procesados = 0;
                    int insertados = 0;
                    String operacionActual = "";
                    String linea;
                    while((linea = br.readLine()) != null){
                        ultimaLinea = linea;
                        //Si la linea comienza con un INSERT
                       procesados++;
                        if(linea.startsWith("INSERT;")){
                            operacionActual = "INSERT";
                            System.out.println("Procesados: " + procesados + " Operación Actual: " + operacionActual);
                            insertados++;
                            //Extraer datos del JSON
                            String datos = linea.substring(7).trim();

                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            //Crear libro
                            Libro libro = new Libro(
                                    json.getLong("isbn"),
                                    json.getString("name"),
                                    json.getString("author"),
                                    // json.getString("category"),
                                    json.getDouble("price"),
                                    json.getInt("quantity")
                            );
                            //Insertar libro en el árbol
                            arbol.Insert(libro);
                        }
                        //Si la línea comienza con un DELETE
                        if(linea.startsWith("DELETE;")){
                            operacionActual = "DELETE";
                            System.out.println("Procesados: " + procesados + " Operación Actual: " + operacionActual);
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            //Eliminar libro del árbol
                            arbol.eliminar(json.getLong("isbn"));
                        }
                        //Si la línea comienza con un PATCH
                        if(linea.startsWith("PATCH;")){
                            operacionActual = "PATCH";
                            System.out.println("Procesados: " + procesados + " Operación Actual: " + operacionActual);
                            String datos = linea.substring(6).trim();
                            JSONObject json = new JSONObject(datos);
                            //Actualizar libro en el árbol
                            arbol.actualizarLibro(json);
                        }
                        //Si la línea comienza con un SEARCH
                        if(linea.startsWith("SEARCH;")){
                            operacionActual = "SEARCH";
                            System.out.println("Procesados: " + procesados + " Operación Actual: " + operacionActual);
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            //Buscar libro en el árbol
                            List<Libro> libros = arbol.buscarLibro(json.getString("name"));
                            // Definir el nombre del archivo de salida
                            String nombreArchivo = "libros_encontrados.txt";

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
                                // Escribir cada libro en una línea del archivo
                                for (Libro libro : libros) {
                                    writer.write(libro.toString()); // Suponiendo que toString() devuelve el formato deseado
                                    writer.newLine(); // Añadir nueva línea
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                System.out.println("CSV importado correctamente");
            } catch (Exception e){
                System.err.println("Error al importar los libros: " + e.getMessage() + " en la línea: " + ultimaLinea);
            }
        }
    }
}