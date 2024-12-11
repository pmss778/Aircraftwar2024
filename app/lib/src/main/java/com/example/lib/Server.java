package com.example.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9999;
    private static final int MAX_PLAYERS = 2;
    private boolean inGame = false;
    private static final ExecutorService pool = Executors.newFixedThreadPool(8);
    private static final ConcurrentLinkedQueue<PlayerHandler> players = new ConcurrentLinkedQueue<>();
    private ServerSocket serverSocket;
    private Socket game1;
    private Socket game2;
    private int cnt = 0;

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (!inGame) {
                    String playerId = "Player " + (players.size() + 1);
                    System.out.println("connected: " + playerId);
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(playerId);
                    PlayerHandler playerHandler = new PlayerHandler(clientSocket, playerId);
                    players.add(playerHandler);
                    pool.execute(playerHandler);
                    System.out.println("Current players: " + players.size());
                    if (players.size() == MAX_PLAYERS) {
                        for (PlayerHandler player : players) {
                            inGame = true;
                            player.startGame();
                        }
                        players.clear();
                    }
                } else if (inGame) {
                    if (cnt == 0) {
                        game1 = clientSocket;
                        cnt++;
                    } else if (cnt == 1) {
                        game2 = clientSocket;
                        cnt = 0;
                        GameHandler gameHandler = new GameHandler(game1, game2);
                        pool.execute(gameHandler);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }

    private class PlayerHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String playerId; // Player's unique identifier

        public PlayerHandler(Socket socket, String playerId) {
            this.socket = socket;
            this.playerId = playerId;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String inputLine;
                while (true) {
                    if ((inputLine = in.readLine()) != null) {
                        if (inGame) {
                            out.println(inputLine);
                            System.out.println("Server:" + inputLine);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        public void startGame() {
            out.println("MATCH_FOUND");
        }
    }

    private class GameHandler implements Runnable {
        private Socket socket1;
        private Socket socket2;
        private BufferedReader in1;
        private PrintWriter out1;
        private BufferedReader in2;
        private PrintWriter out2;
        private int OverFLAG = 0;
        private boolean F1 = true;
        private boolean F2 = true;

        public GameHandler(Socket socket1, Socket socket2) throws IOException {
            this.socket1 = socket1;
            this.socket2 = socket2;
            in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            out1 = new PrintWriter(socket1.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            out2 = new PrintWriter(socket2.getOutputStream(), true);
        }

        @Override
        public void run() {
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String inputLine;
                        while (true) {
                            if ((inputLine = in1.readLine()) != null) {
                                if (inGame) {
                                    if (inputLine.equals("DIE") && F1) {
                                        OverFLAG++;
                                        F1 = false;
                                        System.out.println("Player1 DIED!");
                                        if (OverFLAG == 2) {
                                            System.out.println("ENDDD!");
                                            out1.println("END");
                                            out2.println("END");
                                            inGame = false;
                                        }
                                    } else {
                                        out2.println(inputLine);
                                        if (!inputLine.equals("DIE")){
                                            System.out.println("Server to p2:" + inputLine);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error handling client 1: " + e.getMessage());
                    } finally {
                        try {
                            socket1.close();
                        } catch (IOException e) {
                            System.err.println("Error closing client socket 1: " + e.getMessage());
                        }
                    }
                }
            });

            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String inputLine;
                        while (true) {
                            if ((inputLine = in2.readLine()) != null) {
                                if (inGame) {
                                    if (inputLine.equals("DIE") && F2) {
                                        OverFLAG++;
                                        F2 = false;
                                        System.out.println("Player2 DIED!");
                                        if (OverFLAG == 2) {
                                            System.out.println("ENDDD!");
                                            out1.println("END");
                                            out2.println("END");
                                            inGame = false;
                                        }
                                    } else {
                                        if (!inputLine.equals("DIE")) {
                                            System.out.println("Server to p1:" + inputLine);
                                            out1.println(inputLine);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error handling client 2: " + e.getMessage());
                    } finally {
                        try {
                            socket2.close();
                        } catch (IOException e) {
                            System.err.println("Error closing client socket 2: " + e.getMessage());
                        }
                    }
                }
            });

            thread1.start();
            thread2.start();
        }
    }
}