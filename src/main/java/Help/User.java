package Help;

import Server.ServerThread;

public class User {

    private String nickname;
    private final ServerThread serverThread;
    private String timezone;
    public User(ServerThread serverThread, String nickname , String timezone){
        this.serverThread = serverThread;
        this.nickname = nickname;
        this.timezone = timezone;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setNickname(String nickname){this.nickname = nickname;}

    public ServerThread getServerThread() {
        return serverThread;
    }

    @Override
    public String toString() {
        return nickname + " , " + serverThread + " , " + timezone;
    }
}
