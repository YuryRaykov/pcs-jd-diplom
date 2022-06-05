import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
//        System.out.println(engine.search("бизнес"));

        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989
        // отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        System.out.println("server started");
        int port = 8989;
        ServerSocket serverSocket = new ServerSocket(port);
        try (Socket clientSocket = serverSocket.accept()) {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.printf("New connection accepted. Port: %d%n", clientSocket.getPort());

            final String name = in.readLine();
            out.println(String.format(gson.toJson(engine.search(name))));

        } catch (IOException e) {
            System.err.println(e);
        }
    }
}