package Server;

import Help.Message;
import Help.Protocol;
import Help.User;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;


public class ServerThread extends Thread{
    private final Socket socket; // сокет, через который сервер общается с клиентом,

    private BufferedInputStream in;
    private BufferedOutputStream out;
    private boolean flag = false;

    public ServerThread(Socket socket) {
        this.socket = socket;
        start(); // вызываем run()
    }

    @Override
    public void run() {

        register();

        try {
            while (true) {

                in = new BufferedInputStream(socket.getInputStream());

                Message mesage = new Protocol(in).convertPacketToMsg();

                String aa = mesage.getNickname();

                for (User vr : Server.serverList) {
                    ServerThread v = vr.getServerThread();
                    if (v == this) {
                        aa = vr.getNickname();
                    }
                }

                if (Objects.equals(mesage.getText(), "stop")) {
                    aa = "Пользователь " + aa + " отключился от чата";
                    for (User vr : Server.serverList) {
                        ServerThread v = vr.getServerThread();
                        if (v != this) {
                            v.send(null, null, aa, null); // отослать принятое сообщение с привязанного клиента всем остальным включая его
                        }
                    }
                    serverMessage(null , aa);
                    downService();
                    break;
                } else {
                    for (User vr : Server.serverList) {
                        ServerThread v = vr.getServerThread();
                        v.send(aa, vr.getTimezone(), mesage.getText(), mesage.getFile()); // отослать принятое сообщение с привязанного клиента всем остальным включая его
                    }
                    serverMessage(aa , mesage.getText());
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void serverMessage(String aa , String text){
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getDefault());

        String mes = "";
        if (!Objects.equals(df.format(date), "")) {
            mes = mes + "<" + df.format(date) + ">";
        }
        if (!Objects.equals(aa , "") && !Objects.equals(aa ,null)) {
            mes = mes + " [" + aa + "]";
        }
        System.out.println((mes + " " + text).trim());
    }

    private void send(String msg, String time_zone, String text, byte[] file) {
        try {
            if (time_zone != null) {
                Date date = new Date();
                DateFormat df = new SimpleDateFormat("HH:mm");
                df.setTimeZone(TimeZone.getTimeZone(time_zone));
                time_zone = df.format(date);
            }

            Message message = new Message(msg, time_zone, text, file); // создаем сообщение

            out = new BufferedOutputStream(socket.getOutputStream());
            out.write(new Protocol(message).getPacket());
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (User vr : Server.serverList) {
                    ServerThread v = vr.getServerThread();
                    if (v.equals(this)) {
                        Server.serverList.remove(vr);
                        v.interrupt();
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void register() {
        try {
            in = new BufferedInputStream(socket.getInputStream());
            Message mesage = new Protocol(in).convertPacketToMsg();
            String nickname = mesage.getNickname();
            String timezone = mesage.getTime();

            for (int g = 0; g < Server.serverList.size(); g++) {

                User user = Server.serverList.get(g);

                if (Objects.equals(user.getNickname(), nickname)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                for (int j = 0; j < Server.serverList.size(); j++) {
                    User user = Server.serverList.get(j);
                    if (user.getServerThread() == this) {
                        user.setNickname(nickname);
                        user.setTimezone(timezone);
                    }
                }

                for (User vr : Server.serverList) {
                    ServerThread v = vr.getServerThread();
                    v.send(null, null, "Пользователь " + nickname + " вошел в чат", null);
                    // отослать принятое сообщение с привязанного клиента всем остальным включая его
                }
                serverMessage(null , "Пользователь " + nickname + " вошел в чат");
            } else {
                this.send(null, null, "Такой пользователь уже существует", null);
                downService();
            }
        } catch (IOException ignored) {
        }
    }
}
