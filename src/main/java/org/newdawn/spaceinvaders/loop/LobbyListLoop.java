package org.newdawn.spaceinvaders.loop;

import networking.Network;
import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.*;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.gui.Button;
import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

//- 방목록
//	- 방 목록 겸 입장 버튼
//	- 방 만들기 버튼
//	- 메인메뉴로
//클라: 방 목록 내놔, 방 만들기(방이름, 최대 인원수), 방 입장(로비 ID),
//서버: 방 목록임, 방 진입 실패 사유,
//        - 방 생성창
//	- 방 제목
//	- 최대 인원수
//	- 확인, 취소
public class LobbyListLoop extends Loop{
    GameObject2D lobbylistGUI;

    GameObject2D lobbyButtonContainer;
    ArrayList<Button> lobbyButtons;  // 로비 목록 겸 입장 버튼 (로비ID, 로비 이름, 로비 최대 플레이어, 맵, isPlaying)

    TextRenderer notificationText;

    Button createLobbyButton;
    Button refreshButton;
    Button exitButton;

    boolean waitingForServer = false;
    boolean creatingLobby = false;

    JDialog lobbyCreationDialog;

    public LobbyListLoop(Game game) {
        super(game);
        lobbyCreationDialog = new JDialog(getGame().getContainer(),"로비 생성");
        lobbyCreationDialog.setLocationRelativeTo(getGame().getContainer());
        lobbyCreationDialog.setResizable(false);
        lobbyCreationDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        lobbyCreationDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                lobbyCreationDialog.setVisible(false);
                creatingLobby = false;
            }
        });
        JPanel lobbyCreationPanel = (JPanel) lobbyCreationDialog.getContentPane();
        lobbyCreationPanel.setLayout(new BoxLayout(lobbyCreationPanel, BoxLayout.Y_AXIS));

        JPanel lobbyinfoGridPanel = new JPanel(new GridLayout(2, 2));
        lobbyinfoGridPanel.add(new JLabel("로비 제목"));
        JTextField lobbynameText = new JTextField();
        lobbynameText.setPreferredSize(new Dimension(200, 0));
        lobbyinfoGridPanel.add(lobbynameText);
        lobbyinfoGridPanel.add(new JLabel("최대 인원 수"));
        JSpinner lobbyMaxPlayersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        lobbyMaxPlayersSpinner.setValue(4);
        lobbyinfoGridPanel.add(lobbyMaxPlayersSpinner);

        JPanel lobbyButtonsPanel = new JPanel(new FlowLayout());
        JButton buttonCreate = new JButton("생성");
        buttonCreate.addActionListener(e -> {
            createLobby(lobbynameText.getText(), (Integer) lobbyMaxPlayersSpinner.getValue());
        });
        lobbyButtonsPanel.add(buttonCreate);

        lobbyCreationPanel.add(lobbyinfoGridPanel);
        lobbyCreationPanel.add(lobbyButtonsPanel);

        lobbyCreationDialog.pack();

        lobbylistGUI = new GameObject2D(this);

        lobbyButtonContainer = new GameObject2D(this);
        lobbyButtons = new ArrayList<>();

        notificationText = new TextRenderer(this, "입장할 로비를 선택하세요.", 20, Color.white);

        createLobbyButton = new Button(this, () -> {
            if(waitingForServer || creatingLobby){return;}

            creatingLobby = true;
            lobbyCreationDialog.setVisible(true);
        }, 200, 50);
        refreshButton = new Button(this, () -> {
            if(waitingForServer || creatingLobby){return;}

            requestLobbyList();
        }, 200, 50);
        exitButton = new Button(this, () -> {
            if(waitingForServer || creatingLobby){return;}

            getGame().changeLoop(new MainMenuLoop(getGame()));
        }, 200, 50);

        addGameObject(lobbylistGUI);

        lobbylistGUI.addChild(lobbyButtonContainer);

        lobbylistGUI.addChild(notificationText);

        lobbylistGUI.addChild(createLobbyButton);
        lobbylistGUI.addChild(refreshButton);
        lobbylistGUI.addChild(exitButton);

        notificationText.setPos(400L << 16, 500L << 16);
        notificationText.alignment = 1;

        createLobbyButton.setPos(800L << 16, 550L << 16);
        createLobbyButton.alignment = 2;
        createLobbyButton.addTextRenderer("로비 생성", 20, Color.WHITE, 0);
        refreshButton.setPos(400L << 16, 550L << 16);
        refreshButton.alignment = 1;
        refreshButton.addTextRenderer("로비 목록 갱신", 20, Color.WHITE, 0);
        exitButton.setPos(0L << 16, 550L << 16);
        exitButton.alignment = 0;
        exitButton.addTextRenderer("나가기", 20, Color.WHITE, 0);

        requestLobbyList();
    }
    @Override
    public void onExitLoop(){
        super.onExitLoop();

        if(lobbyCreationDialog.isDisplayable()){
            lobbyCreationDialog.dispose();
        }
    }

    @Override
    public void process(ArrayList<LoopInput> inputs) {
        super.process(inputs);

        getGame().getRudpPeer().processReceivedData();

        processGameObjects();
    }

    @Override
    protected IRUDPPeerListener generateIRUDPPeerListener() {
        final Loop thisLoop = this;
        return new  IRUDPPeerListener() {
            @Override
            public boolean onConnected(RUDPPeer peer, Connection connection) {
                return false;
            }

            @Override
            public boolean onDisconnected(RUDPPeer peer, Connection connection) {
                if (connection.getAddress().getAddress().getHostAddress().equals(Network.SERVER_IP)) {
                    System.out.println(connection.getAddress().getAddress().getHostAddress() + " disconnected");
                    System.exit(0);
                }
                return true;
            }

            @Override
            public boolean onReceived(RUDPPeer peer, Connection connection, PacketData data) {
                if(data instanceof PacketDataS2CLobbyList) {
                    // 로비 목록 갱신
                    for(Button b : lobbyButtons){
                        lobbyButtonContainer.removeChild(b);
                        b.destroy();
                    }
                    lobbyButtons.clear();

                    PacketDataS2CLobbyList d = (PacketDataS2CLobbyList) data;
                    for(int i = 0; i < d.lobbyIDs.size(); i++){
                        final Integer lobbyID = d.lobbyIDs.get(i);
                        String lobbyName = d.lobbyNames.get(i);
                        Integer playersNum = d.playersNum.get(i);
                        Integer maxPlayer = d.maxPlayers.get(i);
                        Integer mapID = d.mapIDs.get(i);
                        Boolean isPlaying = d.isPlaying.get(i);

                        String mapTitle = getGame().getMapList().getList().get(mapID).getTitle();

                        int height = 37;
                        Button b = new Button(thisLoop, ()->{
                            if(waitingForServer || creatingLobby){return;}

                            enterLobby(lobbyID);
                        }, 700, height);
                        b.addTextRenderer(
                                "  [로비 ID:" + lobbyID + "] [인원:" + playersNum + "/"+ maxPlayer + "] [맵:" +  mapTitle + "] [게임 중:" + isPlaying + "] " + lobbyName
                                , (int)(10*(height/25.0)), Color.WHITE, 0);
                        b.setPos(50L << 16, i*(((long)height) << 16));

                        lobbyButtons.add(b);
                        lobbyButtonContainer.addChild(b);
                    }

                    waitingForServer = false;
                    return true;
                }
                else if (data instanceof PacketDataS2CEnterLobbyFaild) {
                    PacketDataS2CEnterLobbyFaild d = (PacketDataS2CEnterLobbyFaild) data;
                    //로비 입장 실패 사유 출력
                    notificationText.setText(d.reason);

                    requestLobbyList();

                    waitingForServer = false;
                    return true;
                }
                else if (data instanceof PacketDataS2CLobbyInfoUpdated) {
                    PacketDataS2CLobbyInfoUpdated d = (PacketDataS2CLobbyInfoUpdated) data;
                    if(d.lobbyID == -1){
                        // d.lobbyID가 -1이면 나감 처리 되었다는 뜻
                        // 로비 리스트에서는 아무 것도 할 필요 없음
                    }
                    else{
                        //LobbyLoop로 넘겨야함
                        getGame().changeLoop(new LobbyLoop(getGame()));
                    }
                    return false;
                }



                else if (data instanceof PacketDataP2PInput) {
                    // 만약 게임 결과 나온 후 로비로 돌아왔을 때 P2P 패킷이 여태까지 남아있으면 안되니
                    // 의미없이 소모
                    return true;
                }

                return false;
            }
        };
    }

    void requestLobbyList() {
        try {
            getGame().getRudpPeer().broadcastAboutTag("server", new PacketDataC2SRequestLobbyList());
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void createLobby(String lobbyName, int maxPlayers){
        try {
            getGame().getRudpPeer().broadcastAboutTag("server", new PacketDataC2SCreateLobby(lobbyName,  maxPlayers));
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void enterLobby(int lobbyID){
        try {
            getGame().getRudpPeer().broadcastAboutTag("server", new PacketDataC2SEnterLobby(lobbyID));
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
