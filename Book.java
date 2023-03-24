// -----------------------------------------------------
// Assignment 3
// Written by: Breno Carneiro de Faria (40232960)
// -----------------------------------------------------

import java.io.Serializable;

public class Book implements Serializable {
    String title;
    String authors;
    double price;
    String isbn;
    String genre;
    int year;
    
    public Book(String title, String authors, double price, String isbn, String genre, int year) {
        this.title = title;
        this.authors = authors;
        this.price = price;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
    }

    public Book(String title, String[] fields) {
        this.title = title;
        this.authors = fields[0];
        this.price = Double.parseDouble(fields[1]);
        this.isbn = fields[2];
        this.genre = fields[3];
        this.year = Integer.parseInt(fields[4]);
    }

    public Book(Book b2) {
        this.title = b2.title;
        this.authors = b2.authors;
        this.price = b2.price;
        this.isbn = b2.isbn;
        this.genre = b2.genre;
        this.year = b2.year;
    }

    public String toString() {
        return '"'+title+'"'+','+authors+','+price+','+isbn+','+genre+','+year;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Book) {
            if (this.authors.equals(((Book) obj).authors) && this.title.equals(((Book) obj).title) && this.price == ((Book) obj).price
            && this.isbn.equals(((Book) obj).isbn) && this.genre.equals(((Book) obj).genre) && this.year == ((Book) obj).year) {
                return true;
            }
        }
        return false;
    }
}
