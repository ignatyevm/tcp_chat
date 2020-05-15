package polyndrom.tcp_chat.server;

import java.util.HashSet;
import java.util.Set;

public class Chat {

    private String ownerName;
    private String name;
    private String password;

    private Set<String> users = new HashSet<>();

    public Chat(String name, String ownerName, String password) {
        this.name = name;
        this.ownerName = ownerName;
        this.password = password;
        addUser(ownerName);
    }

    public Set<String> getUsers() {
        return users;
    }

    public void addUser(String userName) {
        users.add(userName);
    }

    public boolean containsUser(String userName) {
        return users.contains(userName);
    }

    public void removeUser(String userName) {
        users.remove(userName);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
