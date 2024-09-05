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
    private static void importarLibros() {
        String ultimaLinea = "";
        JFileChooser archivo = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos CSV", "csv");
        archivo.setFileFilter(filtro);

        int seleccion = archivo.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            try {
                File archivoSeleccionado = archivo.getSelectedFile();
                // Leer archivo
                try (BufferedReader br = new BufferedReader(new FileReader(archivoSeleccionado));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("libros_encontrados.txt", true)))) {

                    int procesados = 0;
                    int insertados = 0;
                    int busquedadHechas = 0;
                    String operacionActual = "";
                    String linea;

                    StringBuilder sb = new StringBuilder();
                    while ((linea = br.readLine()) != null) {
                        ultimaLinea = linea;
                        procesados++;

                        if (linea.startsWith("INSERT;")) {
                            operacionActual = "INSERT";
                            insertados++;
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            // Crear libro
                            Libro libro = new Libro(
                                    json.getLong("isbn"),
                                    json.getString("name"),
                                    json.getString("author"),
                                    json.getDouble("price"),
                                    json.getInt("quantity")
                            );
                            // Insertar libro en el árbol
                            arbol.Insert(libro);
                        }

                        if (linea.startsWith("DELETE;")) {
                            operacionActual = "DELETE";
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            arbol.eliminar(json.getLong("isbn"));
                        }

                        if (linea.startsWith("PATCH;")) {
                            operacionActual = "PATCH";
                            String datos = linea.substring(6).trim();
                            JSONObject json = new JSONObject(datos);
                            arbol.actualizarLibro(json);
                        }

                        if (linea.startsWith("SEARCH;")) {
                            operacionActual = "SEARCH";
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);

                            // Buscar libro en el árbol
                            List<Libro> libros = arbol.buscarLibro(json.getString("name"));
                            busquedadHechas++;
                            System.out.println(busquedadHechas + " busquedas hechas");

                            if (!libros.isEmpty()) {
                                for (Libro libro : libros) {
                                    sb.append(libro.toString()).append("\n");
                                }
                            }
                        }
                    }
                    writer.write(sb.toString());
                }
                System.out.println("CSV importado correctamente");
            } catch (Exception e) {
                System.err.println("Error al importar los libros: " + e.getMessage() + " en la línea: " + ultimaLinea);
            }
        }
    }

}