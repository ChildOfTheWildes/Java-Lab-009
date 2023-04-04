/**
 *
 * @author Trevor Hartman
 * @author Jeff Grimm
 *
 * @since Version 1.0
 */

import org.apache.commons.codec.digest.Crypt;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class Crack {
    private final User[] users;
    private final String dictionary;
    private int lineCountSf;


    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {

        this.dictionary = dictionary;

        this.users = Crack.parseShadow(shadowFile); // holds the return value of userArray from parseShadow

    }

    // Method that takes contents of users array storing user objects, removes salt from the
    // hashed passwords, then using Crypt, hashes the contents of the dictionary file with the
    // removed salt, and compares the two to see if they are the same
    public void crack() throws FileNotFoundException {

        Scanner in = new Scanner(new FileInputStream(dictionary)); // Reads dictionary file

        // "While there are lines to read"
        while (in.hasNextLine()) {

            // Creates the variable to store and examine each line read from dictionary
            String word = in.nextLine();

            // "For each user in the user array named users, I want to do something"
            for (User user : users) {

                // "If the user object stored in the users array contains a $, crack that password
                // eliminates service user accounts designated by "*"
                // $ designates where hash's salt ends
                if  (user.getPassHash().contains("$")) {

                    // Crypt method removes salt from user.getPassHash
                    // and uses that salt with the contents of dictionary (word)
                    //to create new hash
                    String hash = Crypt.crypt(word, user.getPassHash());

                    // Compares the old hash and the newly created hash to see if they are
                    // the same
                    if (hash.equals((user.getPassHash()))) {

                        System.out.printf("Found password %s for user %s", word, user.getUsername() );
                        System.out.println(); // Line break for output

                    }

                }

            }

        }
    }

    public static int getLineCount(String path) {

        int lineCount = 0;

        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {

            lineCount = (int) stream.count();

        } catch (IOException ignored) {

        }

        return lineCount;
    }

    private static String[] splitLine(String line) { // method for splitting each line into a string array
        if (line != null && !line.isEmpty()) {
            String[] lineArray = line.split(":");
            return lineArray;
        } else {
            return null;
        }
    }

    /* parseShadow reads the shadowFile and creates an array called users
    * the loop loads the array named users with "user objects" that have the first two elements from the shadowFile
    * the username and hash. the method then returns that array */
    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {

        /* Creates an int variable called lineCount that stores the results of the counted lines in shadowFile */

        int lineCount = getLineCount(shadowFile); // Gets number of lines from the shadowFile

        // New Scanner object that takes a new FileInputStream that examines the shadowFile
        Scanner in = new Scanner(new FileInputStream(shadowFile));

        String line = "a"; // New variable for accepting Scanner input and storing each line in loop

        String[] lineSplitArray; // Declares new array for storing split lines based on : from file

        User[] users = new User[lineCount];  // Creates the User Array object with the length of lineCount

        /* With i = 0, and i less than the number of lines in the file, and i + 1 every iteration
         * if lines is not equal to null and is not empty */
        for (int i = 0; i < users.length; i++) {

            line = in.nextLine(); //scan stream of characters for a newline

            if (line != null && !line.isEmpty()) {

                lineSplitArray = line.split(":"); // Splits each line based on :

                //System.out.println(lineSplitArray[0]); // Tests contents of lineSplitArray

                User user = new User(lineSplitArray[0], lineSplitArray[1]); // Creates new user with first two
                // elements of lineSplitArray - lsa[0] = username - lsp[1] = passHash

                users[i] = user; // Creates a new user per iteration of loop (i)
                // Stores >!user objects!< in Array, users
                // userArray stores user objects made up of username and hash!!!

            }
        }

        in.close(); // closes scanner stream
        // returns userArray out of method


        return users;

    }

    public static void main(String[] args) throws FileNotFoundException {

        Scanner sc = new Scanner(System.in);
        System.out.print("Type the path to your shadow file: ");
        String shadowPath = sc.nextLine();
        System.out.print("Type the path to your dictionary file: ");
        String dictPath = sc.nextLine();

        Crack c = new Crack(shadowPath, dictPath);
        c.crack();





    }
}
