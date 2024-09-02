package org.example;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

public class Nodo {
    public int orden;
    public ArrayList<Libro> libros;
    public ArrayList<Nodo> hijos;

    public boolean IsLeaf;
    public ArbolB arbol;

    public int minKeys;

    public int maxKeys;

    public Nodo(int orden, boolean isLeaf, ArbolB Arbol) {
        this.orden = orden;
        libros = new ArrayList<Libro>();
        hijos = new ArrayList<Nodo>();
        IsLeaf = isLeaf;
        arbol = Arbol;
        minKeys = (int) Math.ceil(orden / 2.0) - 1;
        maxKeys = orden - 1;
    }

    public void imprimirArbol(int nivel) {
        // Imprimir el contenido del nodo actual
        for (int i = 0; i < libros.size(); i++) {
            for (int j = 0; j < nivel; j++) {
                System.out.print("   "); // Espacio para la indentación
            }
            System.out.print(libros.get(i).getISBN() + " ");
        }
        System.out.println();

        // Si el nodo no es una hoja, imprimir los hijos
        if (!IsLeaf) {
            for (int i = 0; i < hijos.size(); i++) {
                hijos.get(i).imprimirArbol(nivel + 1);
            }
        }
    }
    public Nodo BuscarNodo(Libro libro){
        int i = 0;
        String ISBN = libro.getISBN();
        while(i < libros.size() && ISBN.compareTo(libros.get(i).getISBN()) > 0){
            i++;
        }
        if(i < libros.size() && ISBN.compareTo(libros.get(i).getISBN()) == 0){
            return this;
        }
        if(IsLeaf){
            return null;
        }

        return hijos.get(i).BuscarNodo(libro);
    }
    public List<Libro> buscarPorNombre(String nombre) {
        List<Libro> resultados = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro.getTitulo().equalsIgnoreCase(nombre)) {
                resultados.add(libro);
            }
        }
        if (!IsLeaf) {
            for (Nodo hijo : hijos) {
                resultados.addAll(hijo.buscarPorNombre(nombre));
            }
        }
        return resultados;
    }
    public void Traverse(){
        int i= 0;
        for (i = 0; i < libros.size(); i++) {
            if (!IsLeaf) {
                hijos.get(i).Traverse();
            }
            System.out.println(" " + libros.get(i).getISBN());
        }

        if(!IsLeaf){
            hijos.get(i).Traverse();
        }
    }
    public void Insertar(Libro libro){
        if(libros.size() == maxKeys){
            InsertarLleno(libro);
        }
        else{
            InsertarNoLleno(libro);
        }
    }
    private void InsertarLleno(Libro libro){
        int i = libros.size() - 1;
        if(IsLeaf){
            while(i >= 0 && libros.get(i).getISBN().compareTo(libro.getISBN()) > 0){
                i--;
            }
            libros.add(i + 1, libro);
        }
        else{
            while(i >= 0 && libros.get(i).getISBN().compareTo(libro.getISBN()) > 0){
                i--;
            }
            i++;
            hijos.get(i).Insertar(libro);
        }
        if(libros.size() >= maxKeys){
            if(arbol.raiz == this){
                Nodo nuevaRaiz = new Nodo(orden, false, this.arbol);
                nuevaRaiz.hijos.add(this);
                nuevaRaiz.dividirNodo(0, arbol.raiz);
                arbol.raiz = nuevaRaiz;
            }
            else{
                Nodo padre = encontrarPadre(arbol.raiz, this);
                padre.dividirNodo(encontrarIndice(padre, this), this);
            }
        }
    }
    private void dividirNodo(int indiceHijo, Nodo nodoSeparado){
        int indiceMedio = orden / 2;
        Nodo nuevoNodo = new Nodo(nodoSeparado.orden, nodoSeparado.IsLeaf, arbol);
        nuevoNodo.libros.addAll(nodoSeparado.libros.subList(indiceMedio+1, nodoSeparado.libros.size()));
        nodoSeparado.libros.subList(indiceMedio + 1, nodoSeparado.libros.size()).clear();

        if(!nodoSeparado.IsLeaf){
            nuevoNodo.hijos.addAll(nodoSeparado.hijos.subList(indiceMedio + 1, nodoSeparado.hijos.size()));
            nodoSeparado.hijos.subList(indiceMedio + 1, nodoSeparado.hijos.size()).clear();
        }

        hijos.add(indiceHijo + 1, nuevoNodo);
        libros.add(indiceHijo, nodoSeparado.libros.get(indiceMedio));
        nodoSeparado.libros.remove(indiceMedio);
    }
    private Nodo encontrarPadre(Nodo nodo, Nodo hijo){
        if(nodo == null || nodo.IsLeaf){
            return null;
        }
        for (Nodo n : nodo.hijos) {
            if(n == hijo){
                return nodo;
            }
            Nodo temp = encontrarPadre(n, hijo);
            if(temp != null){
                return temp;
            }
        }
        return null;
    }
    private int encontrarIndice(Nodo nodo, Nodo hijo){
        for (int i = 0; i < nodo.hijos.size(); i++) {
            if(nodo.hijos.get(i) == hijo){
                return i;
            }
        }
        return -1;
    }
    private void InsertarNoLleno(Libro libro){
        int i =libros.size() - 1;
        if(IsLeaf){
            while(i >= 0 && libros.get(i).getISBN().compareTo(libro.getISBN()) > 0){
                i--;
            }
            libros.add(i + 1, libro);
        }
        else{
            while(i >= 0 && libros.get(i).getISBN().compareTo(libro.getISBN()) > 0){
                i--;
            }
            i++;
            if(hijos.get(i).libros.size() == maxKeys){
                hijos.get(i).InsertarLleno(libro);
            }
            else{
                hijos.get(i).InsertarNoLleno(libro);
            }
        }
    }
    public void Eliminar(Libro libro) {
        int idx = encontrarLibro(libro);

        if (idx < libros.size() && libros.get(idx).getISBN().compareTo(libro.getISBN()) == 0) {
            if (IsLeaf) {
                EliminarHoja(idx);
            } else {
                EliminarNoHoja(idx);
            }
        } else {
            if (IsLeaf) {
                System.out.println("El libro no se encuentra en el árbol");
                return;
            }

            boolean esUltimo = idx == libros.size();
            if (hijos.get(idx).libros.size() < minKeys + 1) {
                llenar(idx);
            }

            if (esUltimo && idx > libros.size()) {
                hijos.get(idx - 1).Eliminar(libro);
            } else {
                hijos.get(idx).Eliminar(libro);
            }
        }
    }


    private int encontrarLibro(Libro libro){
        int idx = 0;
        while(idx < libros.size() && libros.get(idx).getISBN().compareTo(libro.getISBN()) < 0){
            idx++;
        }
        return idx;
    }
    private void EliminarHoja(int idx){
        libros.remove(idx);
        if(libros.size() < minKeys && this != arbol.raiz){
            Nodo padre = encontrarPadre(arbol.raiz, this);
            int indicePadre = encontrarIndice(padre, this);
            padre.llenar(indicePadre);
        }
    }
    private void EliminarNoHoja(int idx){
        Libro libro = libros.get(idx);
        if(hijos.get(idx).libros.size() >= minKeys){
            Libro anterior = getAnterior(idx);
            libros.set(idx, anterior);
            hijos.get(idx).Eliminar(anterior);
        }
        else if(hijos.get(idx + 1).libros.size() >= minKeys){
            Libro siguiente = getSiguiente(idx);
            libros.set(idx, siguiente);
            hijos.get(idx + 1).Eliminar(siguiente);
        }
        else{
            fusionar(idx);
            hijos.get(idx).Eliminar(libro);
        }
    }
    private Libro getAnterior(int idx){
        Nodo actual = hijos.get(idx);
        while(!actual.IsLeaf){
            actual = actual.hijos.get(actual.libros.size());
        }
        return actual.libros.get(actual.libros.size() - 1);
    }
    private Libro getSiguiente(int idx){
        Nodo actual = hijos.get(idx + 1);
        while(!actual.IsLeaf){
            actual = actual.hijos.get(0);
        }
        return actual.libros.get(0);
    }
    private void llenar(int idx){
        if(idx != 0 && hijos.get(idx - 1).libros.size() > minKeys){
            prestarDeAnterior(idx);
        }else if(idx != libros.size() && hijos.get(idx + 1).libros.size() > minKeys){
            prestarDeSiguiente(idx);
        }else{
            if(idx > 0){
                fusionar(idx - 1);
            }else{
                fusionar(idx);
            }
        }
        if(libros.size() < minKeys && this != arbol.raiz){
            Nodo padre = encontrarPadre(arbol.raiz, this);
            int indicePadre = encontrarIndice(padre, this);
            padre.llenar(indicePadre);
        }
    }
    private void prestarDeAnterior(int idx){
        Nodo hijo = hijos.get(idx);
        Nodo hermano = hijos.get(idx - 1);

        hijo.libros.add(0, libros.get(idx - 1));
        if(!hijo.IsLeaf){
            hijo.hijos.add(0, hermano.hijos.get(hermano.hijos.size() - 1));
        }
        libros.set(idx - 1, hermano.libros.get(hermano.libros.size() - 1));
        hermano.hijos.remove(hermano.hijos.size() - 1);

        if(!hermano.IsLeaf){
            hermano.libros.remove(hermano.libros.size() - 1);
        }
    }
    private void prestarDeSiguiente(int idx){
        Nodo hijo = hijos.get(idx);
        Nodo hermano = hijos.get(idx + 1);

        hijo.libros.add(libros.get(idx));
        if(!hijo.IsLeaf){
            hijo.hijos.add(hermano.hijos.get(0));
        }
        libros.set(idx, hermano.libros.get(0));
        hermano.hijos.remove(0);
        if(!hermano.IsLeaf){
            hermano.libros.remove(0);
        }
    }
    private void fusionar(int idx){
        Nodo hijo = hijos.get(idx);
        Nodo hermano = hijos.get(idx + 1);

        hijo.libros.add(libros.get(idx));
        hijo.libros.addAll(hermano.libros);
        if(!hijo.IsLeaf){
            hijo.hijos.addAll(hermano.hijos);
        }
        libros.remove(idx);
        hijos.remove(idx + 1);
    }

    public boolean nodoValido(){
        if(libros.size() < (this == arbol.raiz ? 1 : minKeys) || libros.size() > maxKeys){
            return false;
        }
        for(int i = 1; i < libros.size(); i++){
            if(libros.get(i-1).getISBN().compareTo(libros.get(i).getISBN()) > 0){
                return false;
            }
        }
        if(!IsLeaf){
            if(hijos.size() > orden){
                return false;
            }
            if(hijos.size() != libros.size() + 1){
                return false;
            }
            for(int i = 0; i < hijos.size(); i++){
                if(i > 0 && hijos.get(i).libros.get(0).getISBN().compareTo(libros.get(i - 1).getISBN()) > 0){
                    return false;
                }
                if(i < hijos.size() - 1 && hijos.get(i).libros.get(hijos.get(i).libros.size() - 1).getISBN().compareTo(libros.get(i).getISBN()) < 0){
                    return false;
                }
            }
            for(int i = 0; i < hijos.size(); i++){
                if(!hijos.get(i).nodoValido()){
                    return false;
                }
            }
        }
        return true;
    }
}
