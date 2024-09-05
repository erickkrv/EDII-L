package org.example;

import java.util.ArrayList;
import java.util.List;

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
        libros = new ArrayList<>();
        hijos = new ArrayList<>();
        IsLeaf = isLeaf;
        arbol = Arbol;
        minKeys = (int) Math.ceil(orden / 2.0) - 1;
        maxKeys = orden - 1;
    }

    public Nodo BuscarNodo(Libro libro) {
        int i = 0;
        long ISBN = libro.getISBN();
        while (i < libros.size() && ISBN > libros.get(i).getISBN()) {
            i++;
        }
        if (i < libros.size() && libros.get(i).getISBN() == ISBN) {
            return this;
        }
        if (IsLeaf) {
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


    public Nodo encontrarPadre(Nodo nodo, Nodo hijo) {
        if (nodo == null || nodo.IsLeaf) {
            return null;
        }
        for (Nodo n : nodo.hijos) {
            if (n == hijo) {
                return nodo;
            }
            Nodo temp = encontrarPadre(n, hijo);
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }

    public int encontrarIndice(Nodo nodo, Nodo hijo) {
        for (int i = 0; i < nodo.hijos.size(); i++) {
            if (nodo.hijos.get(i) == hijo) {
                return i;
            }
        }
        return -1;
    }


    public void Eliminar(Libro libro) {
        int idx = encontrarLibro(libro);

        if (idx < libros.size() && libros.get(idx).getISBN() == libro.getISBN()) {
            if (IsLeaf) {
                EliminarHoja(idx);
            } else {
                EliminarNoHoja(idx);
            }
        } else {
            if (IsLeaf) {
                return;
            }

            boolean esUltimo = idx == libros.size();
            if (hijos.get(idx).libros.size() < minKeys) {
                llenar(idx);
            }

            if (esUltimo && idx > libros.size()) {
                hijos.get(idx - 1).Eliminar(libro);
            } else {
                hijos.get(idx).Eliminar(libro);
            }
        }
    }

    private int encontrarLibro(Libro libro) {
        int idx = 0;
        while (idx < libros.size() && libros.get(idx).getISBN() < libro.getISBN()) {
            idx++;
        }
        return idx;
    }

    private void EliminarHoja(int idx) {
        libros.remove(idx);

        if (libros.size() < minKeys && this != arbol.raiz) {
            Nodo padre = encontrarPadre(arbol.raiz, this);
            int indicePadre = encontrarIndice(padre, this);
            padre.llenar(indicePadre);
        }
    }

    private void EliminarNoHoja(int idx) {
        Libro libro = libros.get(idx);

        if (hijos.get(idx).libros.size() >= minKeys) {
            Libro anterior = getAnterior(idx);
            libros.set(idx, anterior);
            hijos.get(idx).Eliminar(anterior);
        }
        else if (hijos.get(idx + 1).libros.size() >= minKeys) {
            Libro siguiente = getSiguiente(idx);
            libros.set(idx, siguiente);
            hijos.get(idx + 1).Eliminar(siguiente);
        } else {
            fusionar(idx);
            hijos.get(idx).Eliminar(libro);
        }
    }


    private Libro getAnterior(int idx) {
        Nodo actual = hijos.get(idx);
        while (!actual.IsLeaf) {
            actual = actual.hijos.get(actual.libros.size());
        }
        return actual.libros.get(actual.libros.size() - 1);
    }

    private Libro getSiguiente(int idx) {
        Nodo actual = hijos.get(idx + 1);
        while (!actual.IsLeaf) {
            actual= actual.hijos.get(0);
        }
        return actual.libros.get(0);
    }

    private void llenar(int idx) {
        if (idx != 0 && hijos.get(idx - 1).libros.size() > minKeys) {
            prestarDeAnterior(idx);
        } else if (idx != libros.size() && hijos.get(idx + 1).libros.size() > minKeys) {
            prestarDeSiguiente(idx);
        } else {
            if (idx > 0) {
                fusionar(idx - 1);
            } else {
                fusionar(idx);
            }
        }

        if (libros.size() < minKeys && this != arbol.raiz) {
            Nodo padre = encontrarPadre(arbol.raiz, this);
            int indicePadre = encontrarIndice(padre, this);
            padre.llenar(indicePadre);
        }
    }


    private void prestarDeAnterior(int idx) {
        Nodo hijoActual = hijos.get(idx);
        Nodo hermanoAnterior = hijos.get(idx - 1);

        hijoActual.libros.add(0, libros.get(idx - 1));

        if (!hijoActual.IsLeaf) {
            hijoActual.hijos.add(0, hermanoAnterior.hijos.remove(hermanoAnterior.hijos.size() - 1));
        }

        libros.set(idx - 1, hermanoAnterior.libros.remove(hermanoAnterior.libros.size() - 1));
        hermanoAnterior.libros.remove(hermanoAnterior.libros.size() - 1);

        if(!hermanoAnterior.IsLeaf){
            hermanoAnterior.hijos.remove(hermanoAnterior.hijos.size() - 1);
        }
    }

    private void prestarDeSiguiente(int idx) {
        Nodo hijoActual = hijos.get(idx);
        Nodo hermanoSiguiente = hijos.get(idx + 1);

        hijoActual.libros.add(libros.get(idx));

        if (!hijoActual.IsLeaf) {
            hijoActual.hijos.add(hermanoSiguiente.hijos.get(0));
        }
        libros.set(idx, hermanoSiguiente.libros.get(0));
        hermanoSiguiente.libros.remove(0);

        if (!hermanoSiguiente.IsLeaf) {
            hermanoSiguiente.hijos.remove(0);
        }
    }

    private void fusionar(int idx) {
        Nodo hijoActual = hijos.get(idx);
        Nodo hermanoSiguiente = hijos.get(idx + 1);

        // Mover la clave del padre hacia el hijo actual
        hijoActual.libros.add(libros.get(idx));
        hijoActual.libros.addAll(hermanoSiguiente.libros);

        if (!hijoActual.IsLeaf) {
            hijoActual.hijos.addAll(hermanoSiguiente.hijos);
        }
        libros.remove(idx);
        hijos.remove(idx + 1);
    }

}