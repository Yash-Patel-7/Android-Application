package model;

import com.example.android10.PhotosLibrary;
import com.example.android10.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * class for handling data
 * @author Parth Patel, Yash Patel
 */
public final class Model {
    /**
     * stores all users
     */
    public static ArrayList<User> users;
    /**
     * current logged-in user
     */
    public static User currentUser;
    /**
     * stores data for transferring between views
     */
    public static ArrayList<Object> dataTransfer;
    /**
     * stores data for transferring between views
     */
    private static ArrayDeque<Object> dataSnapshots;

    /**
     * no-arg constructor
     */
    private Model() {}

    /**
     * sets up and loads data
     */
    @SuppressWarnings("unchecked casting")
    public static void init() {
        dataTransfer = new ArrayList<>();
        dataSnapshots = new ArrayDeque<>();
        File dir = new File(PhotosLibrary.getFilesDirectory(), "data");
        if(!dir.exists()) dir.mkdir();
        File serializedUsers = new File(dir, "users.txt");
        String path;
        if (serializedUsers.length() == 0) {
            users = new ArrayList<>();
            users.add(new User("stock"));
            try {
                setCurrentUser("stock");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                currentUser.addToTagPreset("person", false);
                currentUser.addToTagPreset("location", true);
            } catch (Exception e) {
                throw new RuntimeException("tag type could not be added to preset");
            }
            Album stockAlbum = new Album("stock");
            currentUser.albums.add(stockAlbum);
            try {
                path = "android.resource://com.example.android10/" + R.drawable.one;
                stockAlbum.addPhoto(path, "first");
                path = "android.resource://com.example.android10/" + R.drawable.two;
                stockAlbum.addPhoto(path, "second");
                path = "android.resource://com.example.android10/" + R.drawable.three;
                stockAlbum.addPhoto(path, "third");
                path = "android.resource://com.example.android10/" + R.drawable.four;
                stockAlbum.addPhoto(path, "fourth");
                path = "android.resource://com.example.android10/" + R.drawable.five;
                stockAlbum.addPhoto(path, "fifth");
                path = "android.resource://com.example.android10/" + R.drawable.six;
                stockAlbum.addPhoto(path, "sixth");
                path = "android.resource://com.example.android10/" + R.drawable.seven;
                stockAlbum.addPhoto(path, "seventh");
            } catch (Exception e) {
                throw new RuntimeException("stock photos could not be added");
            }
        }
        else {
            try {
                FileInputStream file = new FileInputStream(serializedUsers);
                ObjectInputStream input = new ObjectInputStream(file);
                users = (ArrayList<User>) input.readObject();
                if (users.size() == 0) {
                    users.add(new User("stock"));
                }
                try {
                    setCurrentUser("stock");
                } catch (Exception e) {
                    throw new RuntimeException("can not find stock user");
                }
                input.close();
                file.close();
            } catch (Exception e) {
                throw new RuntimeException("could not read serialized object");
            }
        }
    }

    /**
     * saves changes to any data in users.txt file
     */
    public static void persist() {
        try {
            File dir = new File(PhotosLibrary.getFilesDirectory(), "data");
            if(!dir.exists()) dir.mkdir();
            File fileD = new File(dir, "users.txt");
            FileOutputStream file = new FileOutputStream(fileD);
            ObjectOutputStream output = new ObjectOutputStream(file);
            output.writeObject(users);
            output.close();
            file.close();
        } catch (IOException e) {
            throw new RuntimeException("could not serialize the object");
        }
    }

    /**
     * sets a user for the program
     * @param username username of currentUser
     * @throws Exception if user does not exist
     */
    public static void setCurrentUser(String username) throws Exception {
        for (User user : Model.users) {
            if (user.username.equals(username)) {
                currentUser = user;
                return;
            }
        }
        throw new Exception("username does not exist");
    }

    /**
     * adds user to list of users
     * @param username username of user we are adding
     * @throws Exception if user is already in list of users
     */
    public static void addUser(String username) throws Exception {
        if (users.contains(new User(username))) {
            throw new Exception("User Already Exists");
        }
        users.add(new User(username));
    }

    /**
     * deletes user from list of users
     * @param username username of user we are deleting
     * @throws Exception if user is not in list of users
     */
    public static void deleteUser(String username) throws Exception {
        if (!users.contains(new User(username))) {
            throw new Exception("User Does Not Exist");
        }
        users.remove(new User(username));
    }

    /**
     * Call just before changing to previous scene
     */
    @SuppressWarnings("unchecked casting")
    public static void initPreviousScene() {
        if (dataSnapshots.isEmpty()) {
            throw new RuntimeException("there is no previous scene");
        }
        dataTransfer = (ArrayList<Object>) dataSnapshots.pop();
    }

    /**
     * Call just before adding objects to Model.dataTransfer for next scene
     * @param comeBack Whether the next scene can directly come back to the current scene in a linear transition
     *                 (usually only valid for scenes on the same stage because otherwise, the user can go back in multiple
     *                 stages at the same time)
     *                 (Valid: A to B to C, linear transition) (Invalid: C to A, C can not jump over B)
     */
    public static void initNextScene(boolean comeBack) {
        if (comeBack) {
            dataSnapshots.push(dataTransfer.clone());
        }
        dataTransfer.clear();
    }

    /**
     * logs out current user
     */
    public static void logOut() {
        dataSnapshots.clear();
        dataTransfer.clear();
    }
}


