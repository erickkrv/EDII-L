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
    public void PrintTree(){
        if(raiz != null){
            raiz.imprimirArbol(0);
        }else{
            System.out.println("El arbol esta vacio");
        }
    }
    public void Insert(Libro libro){
        if(raiz == null){
            raiz = new Nodo(orden, true, this);
            raiz.libros.add(libro);
        }
        else{
            raiz.Insertar(libro);
        }
    }
    public void Traverse(){
        if(raiz != null){
            raiz.Traverse();
        }
    }
    public Nodo BuscarNodo(Libro libro){
        if(raiz != null){
            return raiz.BuscarNodo(libro);
        }
        return null;
    }
    public void eliminar(int ISBN) {
        // Buscar el libro en el árbol por ISBN
        Libro libro = buscarLibroPorISBN(ISBN);
        if (libro == null) {
            System.out.println("El libro con ISBN " + ISBN + " no se encuentra en el árbol.");
            return;
        }

        // Llamar al método de eliminación del nodo con el libro encontrado
        this.raiz.Eliminar(libro);

        // Si la raíz está vacía y no es una hoja, hacer que la raíz sea su hijo
        if (this.raiz.libros.isEmpty() && !this.raiz.IsLeaf) {
            this.raiz = this.raiz.hijos.get(0);
        }
    }

    private Libro buscarLibroPorISBN(int ISBN) {
        return buscarLibroPorISBN(this.raiz, ISBN);
    }

    private Libro buscarLibroPorISBN(Nodo nodo, int ISBN) {
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
    public boolean arbolValido(){
        if(raiz == null){
            return true;
        }
        return raiz.nodoValido();
    }
    public void actualizarLibro(JSONObject json) {
        int ISBN = json.getInt("isbn");
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
            System.out.println("No se pudo editar el libro con el ISBN " + ISBN + " porque no fue encontrado.");
        }
    }

    public List<Libro> buscarLibro(String nombre) {
        return raiz.buscarPorNombre(nombre);
    }
}
