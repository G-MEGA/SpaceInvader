package org.newdawn.spaceinvaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import firebase.FirebaseClientAuth;
import networking.Network;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.PacketData;
import networking.rudp.PacketData.PacketDataC2SAuth;
import networking.rudp.PacketData.PacketDataS2CAuthOK;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.network.LoopRUDPPeerListener;
import serializer.GameLoopSerializer;

import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;
import java.util.Map;

public class Main {
    static final String ID_TOKEN = "idToken";

    RUDPPeer rudpPeer;

    JFrame authFrame;
    Container authContainer;

    Container loginContainer;
    JLabel loginNotificationLabel;
    JButton loginButton;
    Container registerContainer;
    JLabel registerNotificationLabel;
    JButton registerButton;

    Map<String, Object> authInfo;
    boolean authenticated = false;

    IRUDPPeerListener rudpPeerListener = new LoopRUDPPeerListener() {
        @Override
        public boolean onConnected(RUDPPeer peer, networking.rudp.Connection connection) {
            System.out.println(connection.getAddress().getAddress().getHostAddress() + " connected");
            if (connection.getAddress().getAddress().getHostAddress().equals(Network.SERVER_IP)) {
                connection.tag = "server";
            }
            return true;
        }

        @Override
        public boolean onReceived(RUDPPeer peer, networking.rudp.Connection connection, PacketData data) {
            if(data instanceof PacketDataS2CAuthOK) {
//                if(authenticated) {
//                    return true;
//                }

                System.out.println("서버 측 인증 결과 " + ((PacketDataS2CAuthOK)data).ok);
                if (((PacketDataS2CAuthOK)data).ok) {
                    authenticated = true;
                    authFrame.dispose();
                    startGame();
                }
                else{
                    onServerAuthFailed();
                }
            }
            return true;
        }
    };

    public Main() throws Exception {
        GameLoopSerializer.getInstance();// GameLoopSerializer 초기화

        rudpPeer = new RUDPPeer(Network.PEER_UDP_PORT + (int)(System.currentTimeMillis() % 1000));

        rudpPeer.addListener(rudpPeerListener);

        rudpPeer.start();

        InetSocketAddress serverAddress = new InetSocketAddress(Network.SERVER_IP, Network.SERVER_UDP_PORT);
        int max = 5;
        for(int i = 0; i < max; i++){
            if(rudpPeer.isConnected(serverAddress)) break;

            System.out.println("서버와 연결 시도..." + (i+1) + "/ " + max);
            rudpPeer.connect(serverAddress);

            Thread.sleep(1000);
        }

        if(!rudpPeer.isConnected(serverAddress)){
            System.err.println("서버와의 연결에 실패했습니다.");
            System.exit(0);
        }

        authFrame = new JFrame("로그인/회원가입");
        authFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

        loginNotificationLabel = new JLabel();
        loginContainer.add(loginNotificationLabel);

        JPanel loginButtonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("로그인");
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

        registerNotificationLabel = new JLabel();
        registerContainer.add(registerNotificationLabel);

        JPanel registerButtonPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("회원가입");
        registerButtonPanel.add(registerButton);

        registerContainer.add(registerButtonPanel);

        FirebaseClientAuth auth = new FirebaseClientAuth();
        //버튼 동작
        loginNotificationLabel.setText("");
        loginNotificationLabel.setPreferredSize(new Dimension(300, 30));
        loginNotificationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> {
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);

            String email = loginEmailText.getText().trim();
            String password = String.valueOf(loginPasswordText.getPassword()).trim();

            try {
                authInfo = auth.signIn(email, password);
                System.out.println("Firebase 로그인 완료 " + authInfo.get(ID_TOKEN));
                authFrame.setVisible(false);

                tryAuth((String) authInfo.get(ID_TOKEN));
            } catch (Exception ex) {
                //인증 실패시 실패사유 출력
                JsonObject json = JsonParser.parseString(ex.getMessage()).getAsJsonObject();

                loginNotificationLabel.setText(json.getAsJsonObject("error").get("message").getAsString());
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                authFrame.pack();
            }
        });

        registerNotificationLabel.setText("");
        registerNotificationLabel.setPreferredSize(new Dimension(300, 30));
        registerNotificationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton.addActionListener(e -> {
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);

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
                authInfo = auth.signUp(email, password);
                System.out.println("Firebase 회원가입 완료 " + authInfo.get(ID_TOKEN));
                authFrame.setVisible(false);

                tryAuth((String) authInfo.get(ID_TOKEN));
            } catch (Exception ex) {
                //인증 실패시 실패사유 출력
                JsonObject json = JsonParser.parseString(ex.getMessage()).getAsJsonObject();

                registerNotificationLabel.setText(json.getAsJsonObject("error").get("message").getAsString());
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                authFrame.pack();
            }
        });

        //아이디 비번 기본값
        loginEmailText.setText("s@s.ss");
        loginPasswordText.setText("111111");

        //GUI 표시
        authFrame.pack();
        authFrame.setResizable(false);
        authFrame.setLocationRelativeTo(null);//창을 디스플레이 가운데 배치
        authFrame.setVisible(true);

        while(!authenticated){
            Thread.sleep(100);
            rudpPeer.processReceivedData();
        }
    }
    public void startGame(){
        rudpPeer.removeListener(rudpPeerListener);
        Game g = new Game(60L << 16, rudpPeer, (String) authInfo.get("localId"), (String) authInfo.get(ID_TOKEN));
        g.loop();
    }
    public void tryAuth(String authToken) throws Exception {
        if(authenticated) return;

        rudpPeer.broadcastAboutTag("server", new PacketDataC2SAuth(authToken));
    }

    private void onServerAuthFailed(){
        loginButton.setEnabled(true);
        loginNotificationLabel.setText("서버측 인증 실패");
        registerButton.setEnabled(true);
        registerNotificationLabel.setText("서버측 인증 실패");
        authFrame.pack();
        authFrame.setVisible(true);
    }

    public static void main(String[] argv) throws Exception {
        new Main();
    }
}



















