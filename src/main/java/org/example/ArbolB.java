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
        Nodo _raiz = raiz;
        if(_raiz == null){
            raiz = new Nodo(orden, true, this);
            raiz.libros.add(libro);
        }else {
            if(_raiz.libros.size() == raiz.maxKeys){
                Nodo nuevaRaiz = new Nodo(orden, false, this);
                nuevaRaiz.hijos.add(_raiz);
                dividirHijo(nuevaRaiz, 0);
                raiz = nuevaRaiz;
            }
            insertarNoLleno(_raiz, libro);
        }
    }
    private void insertarNoLleno(Nodo nodo, Libro libro){
        if(nodo.IsLeaf){
            int i = nodo.libros.size() - 1;
            while(i >= 0 && nodo.libros.get(i).getISBN() > libro.getISBN()){
                i--;
            }
            nodo.libros.add(i+1, libro);

            if(nodo.libros.size() > nodo.maxKeys){
                dividirNodo(nodo);
            }
        }
        else{
            int i = nodo.libros.size() - 1;
            while(i >= 0 && nodo.libros.get(i).getISBN() > libro.getISBN()){
                i--;
            }
            i++;
            if(nodo.hijos.get(i).libros.size() == nodo.maxKeys + 1){
                dividirHijo(nodo, i);
                if(nodo.libros.get(i).getISBN() < libro.getISBN()){
                    i++;
                }
            }
            insertarNoLleno(nodo.hijos.get(i), libro);
        }
    }
    private void dividirNodo(Nodo nodo){
        if(nodo == raiz){
            Nodo nuevaRaiz = new Nodo(orden, false, this);
            nuevaRaiz.hijos.add(nodo);
            raiz = nuevaRaiz;
            dividirHijo(nuevaRaiz, 0);
        }else{
            Nodo padre = nodo.encontrarPadre(raiz, nodo);
            int i = nodo.encontrarIndice(padre, nodo);
            dividirHijo(padre, i);
            if(padre.libros.size() > padre.maxKeys){
                dividirNodo(padre);
            }
        }
    }
    private void dividirHijo(Nodo padre, int index){
        int orden = this.orden;
        Nodo hijo = padre.hijos.get(index);
        Nodo nuevoHijo = new Nodo(orden, hijo.IsLeaf, this);

        int mitad = (orden - 1) / 2;
        padre.libros.add(index, hijo.libros.get(mitad));
        padre.hijos.add(index + 1, nuevoHijo);

        nuevoHijo.libros.addAll(hijo.libros.subList(mitad + 1, orden - 1));
        hijo.libros.subList(mitad, orden - 1).clear();

        if(!hijo.IsLeaf){
            nuevoHijo.hijos.addAll(hijo.hijos.subList(mitad + 1, orden));
            hijo.hijos.subList(mitad + 1, orden).clear();
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