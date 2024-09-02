package org.example;

public class Libro implements Comparable<Libro>{
    private String ISBN;
    private String titulo;
    private String autor;
    private String categoria;
    private double precio;
    private int stock;

    public Libro(String ISBN, String titulo, String autor, double precio, int stock) {
        this.ISBN = ISBN;
        this.titulo = titulo;
        this.autor = autor;
        //this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }
    //Gets y sets
    public String getISBN() {
        return ISBN;
    }
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    //Método toString
    @Override
    public String toString() {
        return "{\"isbn\":"  + "\"" + ISBN + "\"" + ",\"name\":" + "\"" + titulo +"\"" + ",\"author\":" + "\"" + autor + "\"" +
                ",\"category\":" + "\"" + categoria + "\"" + ",\"price\":" + "\"" + precio + "\"" + ",\"quantity\":" + "\"" + stock + "\"}";
    }
    @Override
    public int compareTo(Libro o) {
        return this.ISBN.compareTo(o.ISBN);
    }
}
