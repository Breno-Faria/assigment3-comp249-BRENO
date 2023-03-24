// -----------------------------------------------------
// Assignment 3
// Written by: Breno Carneiro de Faria (40232960)
// -----------------------------------------------------

/* 
    The program is divided in three methods. The first one reads from files separated by year, checks for any syntax errors (such as
    too many fields in a book, too few fields, missing fields, etc), then appends them to the array corresponding to their genre. After
    that, a PrintWriter iterates over the arrays writing all the books to files corresponding to their genre. The books
    that contains any errors are written to a text file, with all the invalid records.

    In method two, the program reads from the files created in the 1st method, parsing the books, checking for semantic errors (such as
    negative price, invalid year, etc), and then appending it to the correct array corresponding to their genre. After that, an
    ObjectOutputStream iterates over the arrays writing all the books to binary files, corresponding to their genre. The books
    that contains any errors are written to a text file, with all the invalid records.

    In the third method, the program reads the binary files, appending the books to their corresponding genre arrays. After that, it allows the
    user to interact with the program, in order to display the records in the arrays with user input.
 */

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {

        // Setting err stream to a log file to not clutter the terminal with exceptions
        try {
            PrintStream p = new PrintStream(new FileOutputStream("log"));
            System.setErr(p);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }

        do_part1();
        do_part2();
        do_part3();
    }

    public static void do_part1() {

        // Creating book arrays, so when the program goes over the files it appends the books there, in order to write the books 
        // all at once after all the files have been parsed, to prevent opening and closing the writer several times
        // (ERR array is String[] since not every object in it can be a book, due to being an invalid record)

        Book[] CCB = new Book[10];
        Book[] HCB = new Book[10];
        Book[] MTV = new Book[10];
        Book[] MRB = new Book[10];
        Book[] NEB = new Book[10];
        Book[] OTR = new Book[10];
        Book[] SSM = new Book[10];
        Book[] TPA = new Book[10];
        String[] ERR = new String[10];

        try {
            Scanner origin = new Scanner(new FileInputStream("./originalFiles/part1_input_file_names.txt"));
            try {
                // utilizing the number of files given in the first line of the file
                int numOfFiles = Integer.parseInt(origin.nextLine());

                // looping over the original files, reading every book and appending it to the corresponding genre
                for (int l = 1; l < numOfFiles; l++) {
                    String currFile = "./originalFiles/" + origin.nextLine();
                    try {
                        Scanner reader = new Scanner(new FileInputStream(currFile));
                        while (reader.hasNextLine()) {
                            try {
                                // In order to parse the title, since some books contain double quotes, a pointer was used, that looks for the
                                // second quotes. Then, it extracts the title, and parses the rest of the string normally using commas as the delimiter
                                String strBook = reader.nextLine();
                                String title = "";
                                if (strBook.charAt(0) == '"') {
                                    int pointer = 1;
                                    while (strBook.charAt(pointer) != '"') {
                                        pointer += 1;
                                    }
                                    title = strBook.substring(1, pointer);
                                    strBook = strBook.substring(pointer + 2);
                                } else {
                                    int pointer = 0;
                                    while (strBook.charAt(pointer) != ',') {
                                        pointer += 1;
                                    }
                                    title = strBook.substring(0, pointer);
                                    strBook = strBook.substring(pointer + 1);
                                }
                                String[] fields = strBook.split(",");

                                // checking for abnormal conditions, and if any is found, the book is appended to the ERR array
                                // using the custom method appendInvalidBook()

                                // fields size bigger than 5 (not 6, since the title is not included)
                                if (fields.length > 5) {
                                    ERR = appendInvalidBook(title, fields, currFile, ERR, -1);
                                    throw new TooManyFieldsException();
                                // fields size smaller than 5 (again not 6, since the title is not included)
                                } else if (fields.length < 5) {
                                    ERR = appendInvalidBook(title, fields, currFile, ERR, -1);
                                    throw new TooFewFieldsException();
                                } else {
                                    // checks if any of the fields are empty. If so, calls the function describing which of the fields are missing
                                    int error = -1;
                                    for (int i = 0; i < fields.length; i++) {
                                        if (fields[i].equals("")) {
                                            error = ++i;
                                            break;
                                        }
                                    }
                                    // separate check for the title, since it is not included in the fields[] array
                                    if (title.equals(""))
                                        error = 1;
                                    if (error > 0) {
                                        ERR = appendInvalidBook(title, fields, currFile, ERR, error);
                                        throw new MissingFieldException();
                                    } else {

                                        // if any error has been found, appends the book to their corresponding genre
                                        Book book = new Book(title, fields);
                                        if (book.genre.equals("CCB")) {
                                            CCB = appendBook(CCB, book);
                                        } else if (book.genre.equals("HCB")) {
                                            HCB = appendBook(HCB, book);
                                        } else if (book.genre.equals("MTV")) {
                                            MTV = appendBook(MTV, book);
                                        } else if (book.genre.equals("MRB")) {
                                            MRB = appendBook(MRB, book);
                                        } else if (book.genre.equals("NEB")) {
                                            NEB = appendBook(NEB, book);
                                        } else if (book.genre.equals("OTR")) {
                                            OTR = appendBook(OTR, book);
                                        } else if (book.genre.equals("SSM")) {
                                            SSM = appendBook(SSM, book);
                                        } else if (book.genre.equals("TPA")) {
                                            TPA = appendBook(TPA, book);
                                        } else {
                                            ERR = appendInvalidBook(title, fields, currFile, ERR, 6);
                                            throw new BadGenreException();
                                        }
                                    }
                                }
                            } catch (TooManyFieldsException e) {
                                System.err.println(e);
                            } catch (TooFewFieldsException e) {
                                System.err.println(e);
                            } catch (MissingFieldException e) {
                                System.err.println(e);
                            } catch (BadGenreException e) {
                                System.err.println(e);
                            } catch (NoSuchElementException e) {
                                System.err.println("Scanner 'reader' tried to read a non-existent line");
                                System.err.println(e);
                            } catch (StringIndexOutOfBoundsException e) {
                                System.err.println("Tried to read beyond String length");
                                System.err.println(e);
                            }
                        }

                    } catch (FileNotFoundException e) {
                        System.err.println("Something went wrong with the creation of the 'reader' Scanner.");
                        System.err.println(e);
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Something went wrong with parsing the number of files");
                System.err.println(e);
            } catch (NoSuchElementException e) {
                System.err.println("Scanner 'origin' tried to read a non-existent line");
                System.err.println(e);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Something went wrong with the creation of the 'origin' Scanner.");
            System.err.println(e);
        }

        // array with the name of the files for simplicity
        String[] newFiles = { "Cartoons_Comics_Books.csv", "Hobbies_Collectibles_Books.csv", "Movies_TV.csv",
                "Music_Radio_Books.csv", "Nostalgia_Eclectic_Books.csv", "Old_Time_Radio.csv",
                "Sports_Sports_Memorabilia.csv", "Trains_Planes_Automobiles.csv" };
        Book[][] genres = new Book[][] { CCB, HCB, MTV, MRB, NEB, OTR, SSM, TPA };

        // looping over the files of each genre, writing the books from their corresponding arrays
        for (int i = 0; i < newFiles.length; i++) {
            try {
                PrintWriter writer = new PrintWriter(new FileOutputStream("./newFiles_1/" + newFiles[i]));
                for (int j = 0; j < genres[i].length; j++) {
                    if (genres[i][j] != null) {
                        writer.println(genres[i][j]);
                    }
                }
                writer.close();
            } catch (FileNotFoundException e) {
                System.err.println("Something went wrong with the creation of the 'writer' PrintWriter");
                System.err.println(e);
            }

        }

        // Now writing to the syntax error file, which requires a separate loop since it is not a book array
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream("./newFiles_1/syntax_error_file.txt"));
            for (int i = 0; i < ERR.length; i++) {
                if (ERR[i] != null) {
                    writer.write(ERR[i]);
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("Something went wrong with the creation of the 'writer' PrintWriter");
            System.err.println(e);
        }
        System.out.println(ERR.length);

    }

    public static void do_part2() {

        // Creating array with the name of the files for simplicity
        String[] newFiles_1 = { "Cartoons_Comics_Books.csv", "Hobbies_Collectibles_Books.csv", "Movies_TV.csv",
                "Music_Radio_Books.csv", "Nostalgia_Eclectic_Books.csv", "Old_Time_Radio.csv",
                "Sports_Sports_Memorabilia.csv", "Trains_Planes_Automobiles.csv" };

        // Same as in method one, creating the arrays corresponding to each genre so the writer is only called once
        Book[] CCB = new Book[10];
        Book[] HCB = new Book[10];
        Book[] MTV = new Book[10];
        Book[] MRB = new Book[10];
        Book[] NEB = new Book[10];
        Book[] OTR = new Book[10];
        Book[] SSM = new Book[10];
        Book[] TPA = new Book[10];
        String[] ERR = new String[10];
        Book[][] books = new Book[][] { CCB, HCB, MTV, MRB, NEB, OTR, SSM, TPA };

        // looping over the files created in method 1, using the same strategy: parsing the title, and then the fields. After that, once the
        // book object is created, it checks for a different set of errors
        for (int l = 0; l < newFiles_1.length; l++) {
            try {
                Scanner reader = new Scanner(new FileInputStream("./newFiles_1/" + newFiles_1[l]));
                while (reader.hasNextLine()) {
                    try {
                        String strBook = reader.nextLine();
                        String title = "";
                        int pointer = 1;
                        while (strBook.charAt(pointer) != '"') {
                            pointer += 1;
                        }
                        title = strBook.substring(1, pointer);
                        strBook = strBook.substring(pointer + 2);
                        String[] fields = strBook.split(",");
                        Book book = new Book(title, fields);
                        // checks for any invalid field according to the restrictions given in the instructions. if any is found,
                        // the book is appended to the ERR[] array using the same custom method (appendInvalidBook), with the corresponding error code
                        if (book.year < 1995 || book.year > 2010) {
                            ERR = appendInvalidBook(title, fields, newFiles_1[l], ERR, 7);
                            throw new BadYearException();
                        } else if (book.price < 0) {
                            ERR = appendInvalidBook(title, fields, newFiles_1[l], ERR, 8);
                            throw new BadPriceException();
                        } else if (book.isbn.length() == 10) {
                            if (!((((10 * Character.getNumericValue(book.isbn.charAt(0)))
                                    + (9 * Character.getNumericValue(book.isbn.charAt(1))) +
                                    (8 * Character.getNumericValue(book.isbn.charAt(2)))
                                    + (7 * Character.getNumericValue(book.isbn.charAt(3))) +
                                    (6 * Character.getNumericValue(book.isbn.charAt(4)))
                                    + (5 * Character.getNumericValue(book.isbn.charAt(5))) +
                                    (4 * Character.getNumericValue(book.isbn.charAt(6)))
                                    + (3 * Character.getNumericValue(book.isbn.charAt(7))) +
                                    (2 * Character.getNumericValue(book.isbn.charAt(8)))
                                    + (Character.getNumericValue(book.isbn.charAt(9)))) % 11) == 0)) {
                                ERR = appendInvalidBook(title, fields, newFiles_1[l], ERR, 9);
                                throw new BadIsbn10Exception();
                            }
                        } else if (book.isbn.length() == 13) {
                            if (!((((Character.getNumericValue(book.isbn.charAt(0)))
                                    + (3 * Character.getNumericValue(book.isbn.charAt(1))) +
                                    (Character.getNumericValue(book.isbn.charAt(2)))
                                    + (3 * Character.getNumericValue(book.isbn.charAt(3))) +
                                    (Character.getNumericValue(book.isbn.charAt(4)))
                                    + (3 * Character.getNumericValue(book.isbn.charAt(5))) +
                                    (Character.getNumericValue(book.isbn.charAt(6)))
                                    + (3 * Character.getNumericValue(book.isbn.charAt(7))) +
                                    (Character.getNumericValue(book.isbn.charAt(8)))
                                    + (3 * Character.getNumericValue(book.isbn.charAt(9))) +
                                    (Character.getNumericValue(book.isbn.charAt(10)))
                                    + (3 * Character.getNumericValue(book.isbn.charAt(11))) +
                                    (Character.getNumericValue(book.isbn.charAt(12)))) % 10) == 0)) {
                                ERR = appendInvalidBook(title, fields, newFiles_1[l], ERR, 10);
                                throw new BadIsbn13Exception();
                            }
                        } else if ((book.isbn.length() != 10 && book.isbn.length() != 13)
                                || book.isbn.indexOf("X") != -1 || book.isbn.indexOf("x") != -1) {
                            ERR = appendInvalidBook(title, fields, newFiles_1[l], ERR, 11);
                            throw new BadIsbnException();
                        }

                        // if no exceptions are thrown, the book is appended to the array of semantically valid records corresponding to their genre
                        books[l] = appendBook(books[l], book);

                    } catch (NoSuchElementException e) {
                        System.err.println("Tried to read a non-existent line");
                        System.err.println(e);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.err.println("Tried to read beyond String");
                        System.err.println(e);
                    } catch (BadPriceException e) {
                        System.err.println(e);
                    } catch (BadYearException e) {
                        System.err.println(e);
                    } catch (BadIsbn10Exception e) {
                        System.err.println(e);
                    } catch (BadIsbn13Exception e) {
                        System.err.println(e);
                    } catch (BadIsbnException e) {
                        System.err.println(e);
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Something went wrong with the creation of the 'reader' Scanner");
                System.err.println(e);
            }

            // after the valid books have been appended to the arrays, calls the ObjectOutputStream to serialize the objects
            try {
                ObjectOutputStream writer = new ObjectOutputStream(
                        new FileOutputStream("./newFiles_2/" + newFiles_1[l] + ".ser"));
                for (int i = 0; i < books[l].length; i++) {
                    if (books[l][i] != null)
                        writer.writeObject(books[l][i]);
                }
                writer.close();
            } catch (IOException e) {
                System.err.println("Something went wrong with the creation of the 'writer' ObjectOutputStream");
                System.err.println(e);
            }

        }

        // separate loop for the ERR[] array, since it is a String array and not a Book array
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream("./newFiles_2/semantic_error_file.txt"));
            for (int i = 0; i < ERR.length; i++) {
                if (ERR[i] != null)
                    writer.println(ERR[i].toString());
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Something went wrong with the creation of the 'writer' PrintWriter");
            System.err.println(e);
        }

    }

    public static void do_part3() {

            // array of files for simplicity, and creation of the Book[] arrays
            String[] newFiles_2 = { "Cartoons_Comics_Books.csv.ser", "Hobbies_Collectibles_Books.csv.ser",
                    "Movies_TV.csv.ser",
                    "Music_Radio_Books.csv.ser", "Nostalgia_Eclectic_Books.csv.ser", "Old_Time_Radio.csv.ser",
                    "Sports_Sports_Memorabilia.csv.ser", "Trains_Planes_Automobiles.csv.ser" };

            Book[] CCB = new Book[10];
            Book[] HCB = new Book[10];
            Book[] MTV = new Book[10];
            Book[] MRB = new Book[10];
            Book[] NEB = new Book[10];
            Book[] OTR = new Book[10];
            Book[] SSM = new Book[10];
            Book[] TPA = new Book[10];
            Book[][] books = new Book[][] { CCB, HCB, MTV, MRB, NEB, OTR, SSM, TPA };

            // loops over the binary file created in the method 2, using a boolean (reachedTheEnd) to recognize when the file is over.
            // If there are no null files and the file reached the end, an exception (EOF) is thrown, and the code catches it, and moves on
            // to the next iteration of the loop.
            boolean reachedTheEnd;
            for (int l = 0; l < newFiles_2.length; l++) {
                try {
                    ObjectInputStream reader = new ObjectInputStream(
                            new FileInputStream("./newFiles_2/" + newFiles_2[l]));
                            reachedTheEnd = false;
                    try {
                        while (!(reachedTheEnd)) {
                            Book book = (Book) reader.readObject();
                            if (book != null) {
                                switch (l) {
                                    case 0:
                                        books[0] = appendBook(books[0], book);
                                        break;
                                    case 1:
                                        books[1] = appendBook(books[1], book);
                                        break;
                                    case 2:
                                        books[2] = appendBook(books[2], book);
                                        break;
                                    case 3:
                                        books[3] = appendBook(books[3], book);
                                        break;
                                    case 4:
                                        books[4] = appendBook(books[4], book);
                                        break;
                                    case 5:
                                        books[5] = appendBook(books[5], book);
                                        break;
                                    case 6:
                                        books[6] = appendBook(books[6], book);
                                        break;
                                    case 7:
                                        books[7] = appendBook(books[7], book);
                                        break;
                                }
                            } else {
                                reachedTheEnd = true;
                            }
                        }

                    } catch (EOFException e) {
                        System.err.println("Tried to read beyond end of the file");
                        System.err.println(e);
                    } catch (ClassNotFoundException e) {
                        System.err.println(e);
                    }

                    reader.close();

                } catch (IOException e) {
                    System.err.println("Something went wrong with the creation of the 'reader' ObjectInputStream");
                    System.err.println(e);
                }
            }

            System.out.println("Welcome to the program! Written by Breno Faria");
            // after the program reads through every binary file and appends it to the corresponding array, the interactive program starts
            // an infinite loop starts, printing the interface and available commands, with X being the one to exit the program.
            // utilizes an integer to represent the current file (checking the index from the array of files)
            int currFileInt = 0;
                    while (true) {
                        String choice;
                        Scanner input = new Scanner(System.in);
                        System.out.print("\n--------------------------------------" +
                                "\n             MAIN MENU                " +
                                "\n--------------------------------------" +
                                "\nv  View the selected file: " + newFiles_2[currFileInt] + " ("
                                + books[currFileInt].length + " records)" +
                                "\ns  Select a file to view" +
                                "\nx  Exit" +
                                "\n--------------------------------------" +
                                "\n\nEnter your choice: ");
                        choice = input.next().toLowerCase();
                        if (choice.equals("x")) {
                            input.close();
                            System.out.println("Thank you so much for using the program! Bye!");
                            System.exit(0);
                        } else if (choice.equals("s")) {
                            int file;
                            String message = ("\n--------------------------------------" +
                                    "\n           File Sub-Menu             " +
                                    "\n--------------------------------------\n");
                            for (int i = 0; i < books.length; i++) {
                                message += i + 1 + " " + newFiles_2[i] + " (" + books[i].length + " records)\n";
                            }
                            message += newFiles_2.length + 1 + " Exit" +
                                    "\n--------------------------------------" +
                                    "\n\nEnter your choice: ";
                            System.out.print(message);
                            if (input.hasNextInt()) {
                                file = input.nextInt() - 1;
                                if (file == newFiles_2.length) {
                                    input.close();
                                    System.out.println("Thank you so much for using the program! Bye!");
                                    System.exit(0);
                                } else if (file < newFiles_2.length && file > 1) {
                                    currFileInt = file;
                                    continue;
                                } else {
                                    System.out.println("Invalid input, please try again");
                                    continue;
                                }
                            } else {
                                System.out.println("Invalid input, please try again");
                                continue;
                            }
                        } else if (choice.equals("v")) {
                            int curr = 0;
                            int n = -10000000;
                            while (n != 0) {
                                System.out.print("\nviewing: "+newFiles_2[currFileInt]+" ("+books[currFileInt].length+" records)" +
                                "\nInput: ");

                                if (input.hasNextInt()) {
                                    n = input.nextInt();
                                    if (n > 0) {
                                        if (curr + n >= books[currFileInt].length) {
                                            System.out.println("EOF has been reached");
                                        } else {
                                            for (int i = curr; i < curr+n; i++) {
                                                System.out.println((i+1)+" - "+books[currFileInt][i]);
                                            }
                                            curr = curr + n-1;
                                        }
                                    } else if (n < 0) {
                                        if (curr + n+1 < 0) {
                                            System.out.println("BOF has been reached");
                                        } else {
                                            for (int i = curr + n+1; i <= curr; i++) {
                                                System.out.println((i+1)+" - "+books[currFileInt][i]);
                                            }
                                            curr = curr + n+1;
                                        }
                                    } else {
                                        break;
                                    }
                                } else {
                                    System.out.println("Invalid input, please try again");
                                    n = 0;
                                }

                            }
                            
                        }
                    }
    }
    // Custom appendInvalidBook method to facilitate the process. It takes as parameters the title and fields of the record, since
    // it's not always a valid book, the file where the error was found, the array that the book should be appended to, and an integer
    // representing the error message.

    // It first checks if the array is full, and if so, it increases its size by creating a temp array,
    // copying every element using the copy constructor, and then returns this temp array replacing the original array, after appending 
    // the new book.

    public static String[] appendInvalidBook(String title, String[] fields, String file, String[] array, int error) {
        int firstEmptyIndex = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                firstEmptyIndex = i;
                break;
            }
        }
        if (array.length - firstEmptyIndex <= 1) {
            String[] temp = new String[array.length + 1];
            for (int i = 0; i < array.length; i++) {
                temp[i] = array[i];
            }
            array = temp;
        }
        String errorMessage = "";
        if (fields.length > 5) {
            errorMessage = "too many fields";
        } else if (fields.length < 5) {
            errorMessage = "not enough fields";
        } else if (error >= 0 && error <= 5) {
            String missingField = "";
            switch (error) {
                case 0:
                    missingField = "title";
                    break;
                case 1:
                    missingField = "authors";
                    break;
                case 2:
                    missingField = "price";
                    break;
                case 3:
                    missingField = "isbn";
                    break;
                case 4:
                    missingField = "genre";
                    break;
                case 5:
                    missingField = "year";
                    break;
            }
            errorMessage = "missing " + missingField;
        } else if (error == 6) {
            errorMessage = "invalid genre";
        } else if (error == 7) {
            errorMessage = "invalid year";
        } else if (error == 8) {
            errorMessage = "invalid price";
        } else if (error == 9) {
            errorMessage = "invalid ISBN-10";
        } else if (error == 10) {
            errorMessage = "invalid ISBN-13";
        } else if (error == 11) {
            errorMessage = "invalid ISBN";
        }
        String record = "syntax error in file: " + file +
                "\n--------------------------------" +
                "\nError: " + errorMessage +
                "\nRecord: " + title;
        for (String str : fields) {
            record += "," + str;
        }
        record += "\n\n";
        array[firstEmptyIndex] = record;
        return array;
    }


    // similar to the appendInvalidBook method, this one appends valid records, making the process simpler since it does not have any sort of
    // error messages.
    public static Book[] appendBook(Book[] array, Book book) {
        int firstEmptyIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                firstEmptyIndex = i;
                break;
            }
        }
        if (array.length - firstEmptyIndex <= 1) {
            Book[] temp = new Book[array.length + 1];
            for (int i = 0; i < array.length; i++) {
                temp[i] = array[i];
            }
            array = temp;
        }
        array[firstEmptyIndex] = book;
        return array;
    }

}

// Creating custom exceptions

class TooManyFieldsException extends Exception {
    public TooManyFieldsException() {
        super("Too many fields were found");
    }
}

class TooFewFieldsException extends Exception {
    public TooFewFieldsException() {
        super("Not enough fields were found");
    }
}

class MissingFieldException extends Exception {
    public MissingFieldException() {
        super("At least one field was empty");
    }
}

class BadIsbnException extends Exception {
    public BadIsbnException() {
        super("Invalid ISBN code");
    }
}

class BadIsbn10Exception extends Exception {
    public BadIsbn10Exception() {
        super("Invalid 10 digit ISBN code");
    }
}

class BadIsbn13Exception extends Exception {
    public BadIsbn13Exception() {
        super("Invalid 13 digit ISBN code");
    }
}

class BadPriceException extends Exception {
    public BadPriceException() {
        super("Invalid price");
    }

}

class BadYearException extends Exception {
    public BadYearException() {
        super("Invalid Year");
    }
}

class BadGenreException extends Exception {
    public BadGenreException() {
        super("Invalid genre");
    }
}