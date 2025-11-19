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
import org.newdawn.spaceinvaders.network.LoopRUDPPeerListener;

import javax.swing.*;
import java.awt.*;
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

        // 1. Swing 다이얼로그(로비 생성 창) 초기화
        initLobbyCreationDialog();

        // 2. 게임 오브젝트 및 버튼 생성
        initGameObjects();

        // 3. 객체 계층 구조 조립 (addChild)
        buildSceneHierarchy();

        // 4. 위치 및 스타일 설정
        setupLayoutAndStyles();

        // 5. 초기 데이터 요청
        requestLobbyList();
    }

    private void initLobbyCreationDialog() {
        lobbyCreationDialog = new JDialog(getGame().getContainer(), "로비 생성");
        lobbyCreationDialog.setLocationRelativeTo(getGame().getContainer());
        lobbyCreationDialog.setResizable(false);
        lobbyCreationDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        lobbyCreationDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeLobbyDialog(); // 로직 분리
            }
        });

        // 다이얼로그 내부 UI 구성 메서드 호출
        composeDialogContent();
        lobbyCreationDialog.pack();
    }

    private void composeDialogContent() {
        JPanel mainPanel = (JPanel) lobbyCreationDialog.getContentPane();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 입력 필드 구성
        JPanel infoPanel = new JPanel(new GridLayout(2, 2));
        JTextField nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(200, 0));
        JSpinner maxPlayersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        maxPlayersSpinner.setValue(4);

        infoPanel.add(new JLabel("로비 제목"));
        infoPanel.add(nameText);
        infoPanel.add(new JLabel("최대 인원 수"));
        infoPanel.add(maxPlayersSpinner);

        // 버튼 구성
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createBtn = new JButton("생성");
        createBtn.addActionListener(e ->
                createLobby(nameText.getText(), (Integer) maxPlayersSpinner.getValue())
        );
        buttonPanel.add(createBtn);

        mainPanel.add(infoPanel);
        mainPanel.add(buttonPanel);
    }

    private void closeLobbyDialog() {
        lobbyCreationDialog.setVisible(false);
        creatingLobby = false;
    }

    private void initGameObjects() {
        lobbylistGUI = new GameObject2D(this);
        lobbyButtonContainer = new GameObject2D(this);
        lobbyButtons = new ArrayList<>();
        notificationText = new TextRenderer(this, "입장할 로비를 선택하세요.", 20, Color.white);

        createLobbyButton = new Button(this, this::onClickCreateLobby, 200, 50);
        refreshButton = new Button(this, this::onClickRefresh, 200, 50);
        exitButton = new Button(this, this::onClickExit, 200, 50);
    }

    // 버튼 콜백 메서드들 (람다 식 내부 로직 추출)
    private void onClickCreateLobby() {
        if (waitingForServer || creatingLobby) return;
        creatingLobby = true;
        lobbyCreationDialog.setVisible(true);
    }

    private void onClickRefresh() {
        if (waitingForServer || creatingLobby) return;
        requestLobbyList();
    }

    private void onClickExit() {
        if (waitingForServer || creatingLobby) return;
        getGame().changeLoop(new MainMenuLoop(getGame()));
    }

    private void buildSceneHierarchy() {
        addGameObject(lobbylistGUI);
        lobbylistGUI.addChild(lobbyButtonContainer);
        lobbylistGUI.addChild(notificationText);
        lobbylistGUI.addChild(createLobbyButton);
        lobbylistGUI.addChild(refreshButton);
        lobbylistGUI.addChild(exitButton);
    }

    private void setupLayoutAndStyles() {
        // 알림 텍스트
        notificationText.setPos(400L << 16, 500L << 16);
        notificationText.alignment = 1;

        // 로비 생성 버튼
        createLobbyButton.setPos(800L << 16, 550L << 16);
        createLobbyButton.alignment = 2;
        createLobbyButton.addTextRenderer("로비 생성", 20, Color.WHITE, 0);

        // 갱신 버튼
        refreshButton.setPos(400L << 16, 550L << 16);
        refreshButton.alignment = 1;
        refreshButton.addTextRenderer("로비 목록 갱신", 20, Color.WHITE, 0);

        // 나가기 버튼
        exitButton.setPos(0L << 16, 550L << 16);
        exitButton.alignment = 0;
        exitButton.addTextRenderer("나가기", 20, Color.WHITE, 0);
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
        return new  LobbyListLoopRUDPPeerListener(this);
    }

    void requestLobbyList() {
        try {
            getGame().getRudpPeer().broadcastAboutTag(Network.SERVER_TAG, new PacketDataC2SRequestLobbyList());
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void createLobby(String lobbyName, int maxPlayers){
        try {
            getGame().getRudpPeer().broadcastAboutTag(Network.SERVER_TAG, new PacketDataC2SCreateLobby(lobbyName,  maxPlayers));
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void enterLobby(int lobbyID){
        try {
            getGame().getRudpPeer().broadcastAboutTag(Network.SERVER_TAG, new PacketDataC2SEnterLobby(lobbyID));
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class LobbyListLoopRUDPPeerListener extends LoopRUDPPeerListener{
        LobbyListLoop loop;
        LobbyListLoopRUDPPeerListener(LobbyListLoop loop) {
            this.loop = loop;
        }

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
            boolean result = false;
            if(data instanceof PacketDataS2CLobbyList) {
                updateLobbyList((PacketDataS2CLobbyList) data);

                waitingForServer = false;
                result = true;
            }
            else if (data instanceof PacketDataS2CEnterLobbyFaild) {
                PacketDataS2CEnterLobbyFaild d = (PacketDataS2CEnterLobbyFaild) data;
                //로비 입장 실패 사유 출력
                notificationText.setText(d.reason);

                requestLobbyList();

                waitingForServer = false;
                result = true;
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
//                result = false;
            }
            else if (data instanceof PacketDataP2PInput) {
                // 만약 게임 결과 나온 후 로비로 돌아왔을 때 P2P 패킷이 여태까지 남아있으면 안되니
                // 의미없이 소모
                result = true;
            }

            return result;
        }
        private void updateLobbyList(PacketDataS2CLobbyList d){
            // 로비 목록 갱신
            for(Button b : lobbyButtons){
                lobbyButtonContainer.removeChild(b);
                b.destroy();
            }
            lobbyButtons.clear();

            for(int i = 0; i < d.lobbyIDs.size(); i++){
                final Integer lobbyID = d.lobbyIDs.get(i);
                String lobbyName = d.lobbyNames.get(i);
                Integer playersNum = d.playersNum.get(i);
                Integer maxPlayer = d.maxPlayers.get(i);
                Integer mapID = d.mapIDs.get(i);
                Boolean isPlaying = d.isPlaying.get(i);

                String mapTitle = getGame().getMapList().getList().get(mapID).getTitle();

                int height = 37;
                Button b = new Button(loop, ()->{
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
        }
    }
}
