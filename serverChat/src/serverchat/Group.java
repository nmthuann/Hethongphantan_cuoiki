package serverchat;

import java.io.File;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
public class Group {
    private String name;
    private String password;
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    private ArrayList<ServerThread> threads ;

    public Group(String name, String password) {
        this.name = name;
        this.password = password;
        this.file = new File(this.password+".dat");
        this.threads = new ArrayList<>();
    }

    public Group() {
        this.password = null;
        this.threads = new ArrayList<>();
    }

    public ArrayList<ServerThread> getThreads() {
        return threads;
    }

    public void setThreads(ArrayList<ServerThread> threads) {
        threads = threads;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
