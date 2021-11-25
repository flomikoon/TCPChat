package Help;

public class Message {
    private final String nickname;
    private final String time;
    private final String text;
    private final byte[] file;

    public Message(String nickname , String time , String text , byte[] file){
        this.nickname = nickname;
        this.time = time;
        this.text = text;
        this.file = file;
    }

    public String getNickname() {
        return nickname;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public byte[] getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "Message{" +
                "time='" + time + '\'' +
                ", name='" + nickname + '\'' +
                ", text='" + text + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
