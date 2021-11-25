package Help;

import Help.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Protocol {
    private Help.Message msg;
    private BufferedInputStream in;

    public Protocol(Help.Message msg) {
        this.msg = msg;
    }

    public Protocol(BufferedInputStream in){
        this.in = in;
    }

    public byte[] getPacket(){
        byte[] nick = new byte[0];

        if(msg.getNickname() != null){
            nick = msg.getNickname().getBytes(StandardCharsets.UTF_8);
        }

        byte[] tex = new byte[0];
        if(msg.getText() != null) {
            tex = msg.getText() .getBytes(StandardCharsets.UTF_8);
        }

        byte[] tim = new byte[0];
        if(msg.getTime() != null) {
            tim = msg.getTime().getBytes(StandardCharsets.UTF_8);
        }

        byte[] file = new byte[0];
        if(msg.getFile() != null) {
            file = msg.getFile();
        }

        int nick_size = nick.length;
        int tex_size = tex.length;
        int tim_size = tim.length;
        int file_size = file.length;

        byte[] msg_size = new byte[2];
        msg_size[0] = (byte) nick_size;
        msg_size[1] = (byte) tim_size;

        byte[] byText = new byte[]{
                (byte) (tex_size >> 16),
                (byte) (tex_size  >> 8),
                (byte) tex_size
        };

        byte[] byFile = new byte[]{
                (byte) (file_size >> 16),
                (byte) (file_size  >> 8),
                (byte) file_size
        };

        byte[] result = Arrays.copyOf(msg_size , msg_size.length + byText.length + byText.length + nick_size + tex_size + tim_size + file_size);
        System.arraycopy(byText, 0, result, msg_size.length, byText.length);
        System.arraycopy(byFile, 0, result, msg_size.length + byText.length, byFile.length);
        System.arraycopy(nick, 0, result, msg_size.length + byText.length + byFile.length, nick.length);
        System.arraycopy(tim, 0, result, msg_size.length + byText.length + nick.length + byFile.length, tim.length);
        System.arraycopy(tex, 0, result, msg_size.length + byText.length + nick.length + tim.length + byFile.length, tex.length);
        System.arraycopy(file, 0, result, msg_size.length + byText.length + nick.length + tim.length + byFile.length + tex.length, file_size);
        return  result;
    }

    public Message convertPacketToMsg() throws IOException {
        byte[] bytes = new byte[8];
        in.read(bytes); //читаем длину сообщения

        int nick_name_size = bytes[0]& 0xff;
        int time_size = bytes[1]& 0xff;

        int tex_size = 0;

        for (int i = 2 ; i < 5 ; i++) { // b => 2
            tex_size = (tex_size << 8) + (bytes[i] & 0xff);
        }

        int file_size = 0;

        for (int i = 5 ; i < 8 ; i++) { // b => 5
            file_size = (file_size << 8) + (bytes[i] & 0xff);
        }

        byte[] bytes_nick = new byte[nick_name_size];
        in.read(bytes_nick , 0 , nick_name_size); //читаем сообщение


        byte[] bytes_time = new byte[time_size];
        in.read(bytes_time , 0 , time_size); //читаем сообщение

        byte[] bytes_tex = new byte[tex_size];
        in.read(bytes_tex, 0 , tex_size);


        byte[] bytes_file = new byte[file_size];

        if(file_size != 0) {
            for (int i = 0; i < file_size; i++) {
                bytes_file[i] = (byte) in.read();
            }
        }

        String time_string = new String(bytes_time, 0, time_size, StandardCharsets.UTF_8);

        return new Message(new String(bytes_nick, 0, nick_name_size, StandardCharsets.UTF_8),
                   time_string , new String(bytes_tex, 0, tex_size, StandardCharsets.UTF_8) , bytes_file);
    }
}
