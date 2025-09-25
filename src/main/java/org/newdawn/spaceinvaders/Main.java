package org.newdawn.spaceinvaders;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import firebase.FirebaseClientAuth;
import networking.Network;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    final String SERVER_IP = "34.67.77.26";
//    final String SERVER_IP = "127.0.0.1";

    Client client;

    JFrame authFrame;
    Container authContainer;

    Container loginContainer;
    Container registerContainer;

    public Main(){
        client =  new Client();
        Network.register(client);
        client.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                System.out.println("connected");
            }

            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof Network.S2CAuthOK) {
                    if (((Network.S2CAuthOK)object).ok)
                        authFrame.dispose();
                    startGame();
                }
                else if(object instanceof Network.Hello) {
                    System.out.print(connection.getID());
                    System.out.print(" Hello ");
                    System.out.println(((Network.Hello) object).content);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("disconnected");
                System.exit(0);
            }
        });

        client.start();
        try {
            client.connect(5000, SERVER_IP, Network.PORT_TCP, Network.PORT_UDP);
        } catch (IOException e) {
            System.out.println("서버에 연결할 수 없습니다.");
            System.out.println(e.getMessage());
            System.exit(0);
            throw new RuntimeException(e);
        }

        authFrame = new JFrame("로그인/회원가입");
        authFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        authContainer = authFrame.getContentPane();
        authContainer.setLayout(new BoxLayout(authContainer, BoxLayout.X_AXIS));

        loginContainer = new JPanel();
        loginContainer.setLayout(new BoxLayout(loginContainer, BoxLayout.Y_AXIS));
        authContainer.add(loginContainer);

        JPanel line = new JPanel();
        line.setMinimumSize(new Dimension(10, 1));
        line.setBackground(Color.LIGHT_GRAY);
        authContainer.add(line);

        registerContainer = new JPanel();
        registerContainer.setLayout(new BoxLayout(registerContainer, BoxLayout.Y_AXIS));
        authContainer.add(registerContainer);


        Dimension loginInfoLabelPrefferedSize = new Dimension(70, 30);
        Dimension registerInfoLabelPrefferedSize = new Dimension(70, 30);
        Dimension textFieldPrefferedSize = new Dimension(200, 30);
        //로그인 GUI 구성
        Panel loginEmailPanel = new Panel();
        loginEmailPanel.setLayout(new BoxLayout(loginEmailPanel, BoxLayout.X_AXIS));
        JLabel loginEmailLabel = new JLabel("이메일:");
        loginEmailLabel.setPreferredSize(new Dimension(70, 30));
        loginEmailPanel.add(loginEmailLabel);
        JTextField loginEmailText = new JTextField();
        loginEmailPanel.add(loginEmailText);
        loginEmailPanel.setPreferredSize(new Dimension(200, 30));
        loginContainer.add(loginEmailPanel);

        Panel loginPasswordPanel = new Panel();
        loginPasswordPanel.setLayout(new BoxLayout(loginPasswordPanel, BoxLayout.X_AXIS));
        JLabel loginPasswordLabel = new JLabel("비밀번호:");
        loginPasswordLabel.setPreferredSize(new Dimension(70, 30));
        loginPasswordPanel.add(loginPasswordLabel);
        JPasswordField loginPasswordText = new JPasswordField();
        loginPasswordPanel.add(loginPasswordText);
        loginPasswordPanel.setPreferredSize(new Dimension(200, 30));
        loginContainer.add(loginPasswordPanel);

        JLabel loginNotificationLabel = new JLabel();
        loginContainer.add(loginNotificationLabel);

        JPanel loginButtonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("로그인");
        loginButtonPanel.add(loginButton);

        loginContainer.add(loginButtonPanel);

        //회원가입 GUI 구성
        Panel registerEmailPanel = new Panel();
        registerEmailPanel.setLayout(new BoxLayout(registerEmailPanel, BoxLayout.X_AXIS));
        JLabel registerEmailLabel = new JLabel("이메일:");
        registerEmailLabel.setPreferredSize(new Dimension(100, 30));
        registerEmailPanel.add(registerEmailLabel);
        JTextField registerEmailText = new JTextField();
        registerEmailPanel.add(registerEmailText);
        registerEmailPanel.setPreferredSize(new Dimension(200, 30));
        registerContainer.add(registerEmailPanel);

        Panel registerPasswordPanel = new Panel();
        registerPasswordPanel.setLayout(new BoxLayout(registerPasswordPanel, BoxLayout.X_AXIS));
        JLabel registerPasswordLabel = new JLabel("비밀번호:");
        registerPasswordLabel.setPreferredSize(new Dimension(100, 30));
        registerPasswordPanel.add(registerPasswordLabel);
        JPasswordField registerPasswordText = new JPasswordField();
        registerPasswordPanel.add(registerPasswordText);
        registerPasswordPanel.setPreferredSize(new Dimension(200, 30));
        registerContainer.add(registerPasswordPanel);

        Panel registerPasswordCheckPanel = new Panel();
        registerPasswordCheckPanel.setLayout(new BoxLayout(registerPasswordCheckPanel, BoxLayout.X_AXIS));
        JLabel registerPasswordCheckLabel = new JLabel("비밀번호 확인:");
        registerPasswordCheckLabel.setPreferredSize(new Dimension(100, 30));
        registerPasswordCheckPanel.add(registerPasswordCheckLabel);
        JPasswordField registerPasswordConfirmationText = new JPasswordField();
        registerPasswordCheckPanel.add(registerPasswordConfirmationText);
        registerPasswordCheckPanel.setPreferredSize(new Dimension(200, 30));
        registerContainer.add(registerPasswordCheckPanel);

        JLabel registerNotificationLabel = new JLabel();
        registerContainer.add(registerNotificationLabel);

        JPanel registerButtonPanel = new JPanel(new FlowLayout());
        JButton registerButton = new JButton("회원가입");
        registerButtonPanel.add(registerButton);

        registerContainer.add(registerButtonPanel);

        FirebaseClientAuth auth = new FirebaseClientAuth();
        //버튼 동작
        loginNotificationLabel.setText("");
        loginNotificationLabel.setPreferredSize(new Dimension(300, 30));
        loginNotificationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> {
            String email = loginEmailText.getText().trim();
            String password = String.valueOf(loginPasswordText.getPassword()).trim();

            try {
                String authToken = auth.signIn(email, password);
                System.out.println("Firebase 로그인 완료 " + authToken);
                authFrame.setVisible(false);

                tryAuth(authToken);
            } catch (Exception ex) {
                //인증 실패시 실패사유 출력
                JsonObject json = JsonParser.parseString(ex.getMessage()).getAsJsonObject();

                loginNotificationLabel.setText(json.getAsJsonObject("error").get("message").getAsString());
                authFrame.pack();
            }
        });

        registerNotificationLabel.setText("");
        registerNotificationLabel.setPreferredSize(new Dimension(300, 30));
        registerNotificationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton.addActionListener(e -> {
            String email = registerEmailText.getText().trim();
            String password = String.valueOf(registerPasswordText.getPassword()).trim();
            String passwordConfirmation = String.valueOf(registerPasswordConfirmationText.getPassword()).trim();

//            if(!email.contains("@")){
//                registerNotificationLabel.setText("이메일의 형식이 아닙니다.");
//                return;
//            }
            if(!password.equals(passwordConfirmation)){
                registerNotificationLabel.setText("비밀번호가 확인 란과 일치하지 않습니다.");
                return;
            }
            if(password.length() < 6){
                registerNotificationLabel.setText("비밀번호는 6자리 이상이어야 합니다.");
                return;
            }

            try {
                String authToken = auth.signUp(email, password);
                System.out.println("Firebase 회원가입 완료 " + authToken);
                authFrame.setVisible(false);

                tryAuth(authToken);
            } catch (Exception ex) {
                //인증 실패시 실패사유 출력
                JsonObject json = JsonParser.parseString(ex.getMessage()).getAsJsonObject();

                registerNotificationLabel.setText(json.getAsJsonObject("error").get("message").getAsString());
                authFrame.pack();
            }
        });

        //GUI 표시
        authFrame.pack();
        authFrame.setResizable(false);
        authFrame.setLocationRelativeTo(null);//창을 디스플레이 가운데 배치
        authFrame.setVisible(true);
    }
    public void startGame(){
        Game g = new Game(60L << 16);
        g.loop();
    }
    public void tryAuth(String authToken){
        client.sendTCP(new Network.C2SAuth(authToken));
    }

    public static void main(String[] argv) {
        new Main();
    }
}
