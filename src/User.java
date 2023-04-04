public class User {

    // Thou art in the land of Instance Variables
    private String username;

    private String passHash;

    public User(String username, String passHash ) { // Constructor that assigns private variables to
        // accessible variables

        this.username = username;

        this.passHash = passHash;

    }

    public String getUsername() { // Getter for username variable

        return username;

    }

    public String getPassHash() { // Getter for passHash variable

        return passHash;

    }

}
