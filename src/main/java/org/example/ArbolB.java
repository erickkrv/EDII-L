package org.example;

import org.json.JSONObject;

import java.util.List;

public class ArbolB {
    public Nodo raiz;
    public int orden;

    public ArbolB(int orden){
        raiz = null;
        this.orden = orden;
    }
    public void Insert(Libro libro){
        if(raiz == null){
            raiz = new Nodo(orden, true, this);
            raiz.libros.add(libro);
        }
        else{
            if(raiz.libros.size() == raiz.maxKeys){
                Nodo s = new Nodo(orden, false, raiz.arbol);
                s.hijos.add(raiz);
                s.dividirNodo(0, raiz);
                int i = 0;
                if(s.libros.get(0).getISBN() < libro.getISBN()){
                    i++;
                }
                s.hijos.get(i).InsertarNoLleno(libro);
                raiz = s;
            }else{
                raiz.InsertarNoLleno(libro);
            }
        }
    }
    public void eliminar(long ISBN) {
        // Buscar el libro en el árbol por ISBN
        Libro libro = buscarLibroPorISBN(ISBN);
        if(raiz == null){
            return;
        }
        if(libro != null){
            raiz.Eliminar(libro);
        }else{
            return;
        }
        if(raiz.libros.size() == 0){
            if(raiz.IsLeaf){
                raiz = null;
            }else{
                raiz = raiz.hijos.get(0);
            }
        }
    }

    private Libro buscarLibroPorISBN(long ISBN) {
        if(raiz != null){
            return buscarLibroPorISBN(this.raiz, ISBN);
        }else{
            return null;
        }
    }

    private Libro buscarLibroPorISBN(Nodo nodo, long ISBN) {
        int i = 0;
        while (i < nodo.libros.size() && ISBN > nodo.libros.get(i).getISBN()) {
            i++;
        }

        if (i < nodo.libros.size() && ISBN == nodo.libros.get(i).getISBN()) {
            return nodo.libros.get(i);
        }

        if (nodo.IsLeaf) {
            return null;
        }

        return buscarLibroPorISBN(nodo.hijos.get(i), ISBN);
    }
    public void actualizarLibro(JSONObject json) {
        long ISBN = json.getLong("isbn");
        if(raiz != null){
            Nodo nodo = raiz.BuscarNodo(new Libro(ISBN, "", "", 0, 0));
            if (nodo != null) {
                for (int i = 0; i < nodo.libros.size(); i++) {
                    Libro libro = nodo.libros.get(i);
                    if (libro.getISBN() == ISBN) {
                        if (json.has("name")) {
                            libro.setTitulo(json.getString("name"));
                        }
                        if (json.has("author")) {
                            libro.setAutor(json.getString("author"));
                        }
                        if (json.has("price")) {
                            libro.setPrecio(json.getDouble("price"));
                        }
                        if (json.has("quantity")) {
                            libro.setStock(json.getInt("quantity"));
                        }
                        break;
                    }
                }
            }else{
                // System.out.println("No se pudo editar el libro con el ISBN " + ISBN + " porque no fue encontrado.");
            }
        }else{
            // System.out.println("No se pudo editar el libro con el ISBN " + ISBN + " porque el árbol está vacío.");
        }
    }

    public List<Libro> buscarLibro(String nombre) {
        return raiz.buscarPorNombre(nombre);
    }
}