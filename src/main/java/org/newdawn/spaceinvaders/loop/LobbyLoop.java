package org.newdawn.spaceinvaders.loop;

import networking.Network;
import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.*;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.gui.Button;
import org.newdawn.spaceinvaders.game_object.gui.IButtonListener;
import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.map_load.MapInfo;

import java.awt.*;
import java.util.ArrayList;

//- 방 내부
//	- 내가 참가했던 게임 중 가장 하이스코어, 참가 인원
//	- 리더보드
//		- 순위, 점수, 참가 인원
//	- 맵선택(방장만 가능)
//	- 레디 버튼
//클라가 하이스코어와 랭킹 파베에서 끌어오기
//클라 : 방 나가기, 레디, p2p연결 완료(따른 모든 플레이어와 p2p연결 완료)
//서버 :
//방 나가기 결과,
//방 정보 전송(방 진입 및 방 정보(맵, 플레이어 목록, 플레이어 레디) 바뀔 때마다 브로드캐스팅),
//올레디-(시드및 플레이어목록 배부 및 각 플레이어 아이디 할당)
//게임 시작(방 내의 모든 클라로부터 p2p연결 완료 수신시... 서버 내부적으로는 레디 전부 풀어버림)(클라가 이거 받으면 게임루프플레이어루프로 넘어감)

public class LobbyLoop extends Loop{
    GameObject2D lobbyGUI;

    TextRenderer lobbyInfoText;  // 로비 ID, 로비 이름, 로비 최대 플레이어

    TextRenderer playerListText;  // 플레이어 목록(레디 여부 포함)

    GameObject2D mapSelectionGUI;
    TextRenderer mapInfoTextRenderer;
    GameObject2D mapSelectionButtonContainer;
    ArrayList<Button> mapSelectionButtons;  // 맵 목록(방장만 터치 가능)

    Button readyButton;
    Button exitButton;

    boolean waitingForServer = false;

    public LobbyLoop(Game game) {
        super(game);
//
//        mapSelectionGUI = new GameObject2D(this);
//
//        //region mapSelectionGUI 구성
//        mapInfoTextRenderer = new TextRenderer(this,"맵을 선택하세요.", 20);
//        mapInfoTextRenderer.setPosX(160L << 16);
//        addGameObject(mapInfoTextRenderer);
//
//        mapSelectionButtonContainer =  new GameObject2D(this);
//        mapSelectionGUI.addChild(mapSelectionButtonContainer);
//        ArrayList<MapInfo> maps = game.getMapList().getList();
//        for (int i = 0; i < maps.size(); i++) {
//            final MapInfo mapInfo = maps.get(i);
//            Button button = new Button(this, new IButtonListener() {
//                @Override
//                public void buttonPressed() {
//                    String t = mapInfo.getTitle();
//                    t += "\n" + mapInfo.getPath();
//                    t += "\n" + mapInfo.getHash();
//
//                    mapInfoTextRenderer.setText(t);
//                }
//            }, 150, 40);
//            mapSelectionButtonContainer.addChild(button);
//
//            button.setPos(0, i * button.getHeightInFixedPoint());
//            TextRenderer textRenderer = button.addTextRenderer(mapInfo.getTitle(), 20, Color.WHITE, 0);
//            textRenderer.setPosX(10L << 16);
//        }
//        //endregion
//
//        //region gameRoomGUI 구성
//
//        //endregion
//
//        lobbyGUI.addChild(mapSelectionGUI);
////        lobbyGUI.addChild(gameRoomGUI);
//
//        addGameObject(lobbyGUI);
    }

    @Override
    public void process(ArrayList<LoopInput> inputs) {
        super.process(inputs);

        getGame().getRudpPeer().processReceivedData();

        processGameObjects();
    }

    @Override
    protected IRUDPPeerListener generateIRUDPPeerListener() {
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
                if (data instanceof PacketDataS2CLobbyInfoUpdated) {
                    return true;
                }
                else if (data instanceof PacketDataS2CPreprocessForGame) {
                    // GameLoopPlayerLoop로 넘겨
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

    void updateLobbyInfo(int mapID) {
        try {
            PacketDataC2SUpdateLobbyInfo d = new PacketDataC2SUpdateLobbyInfo();
            d.mapID = mapID;
            getGame().getRudpPeer().broadcastAboutTag("server", d);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void exitLobby(){
        waitingForServer = true;

        try {
            getGame().getRudpPeer().broadcastAboutTag("server", new PacketDataC2SExitLobby());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void ready(){
        waitingForServer = true;

        try {
            getGame().getRudpPeer().broadcastAboutTag("server", new PacketDataC2SReady());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
