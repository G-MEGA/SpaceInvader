package org.newdawn.spaceinvaders.loop;

import firebase.FirebaseRankings;
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
import org.newdawn.spaceinvaders.network.LoopRUDPPeerListener;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
    TextRenderer mapRankingsTextRenderer;
    GameObject2D mapSelectionButtonContainer;
    ArrayList<Button> mapSelectionButtons;  // 맵 목록(방장만 터치 가능)

    Button readyButton;
    Button exitButton;

    boolean waitingForServer = false;

    public LobbyLoop(Game game) {
        super(game);

        lobbyGUI = new GameObject2D(this);

        lobbyInfoText = new TextRenderer(this, "로비 정보 텍스트", 20, Color.WHITE, 1);  // 로비 ID, 로비 이름, 로비 최대 플레이어

        playerListText = new TextRenderer(this, "플레이어 목록", 10);  // 플레이어 목록(레디 여부 포함)

        mapSelectionGUI = new  GameObject2D(this);
        mapSelectionButtonContainer = new GameObject2D(this);
        mapSelectionButtons = new ArrayList<>();  // 맵 목록(방장만 터치 가능)
        mapInfoTextRenderer = new TextRenderer(this, "맵 정보", 15);
        mapRankingsTextRenderer = new TextRenderer(this, "랭킹", 13, Color.pink, 2);

        readyButton = new Button(this, () -> {
            if(waitingForServer){return;}

            ready();
        }, 100, 50);
        exitButton = new Button(this, () -> {
            if(waitingForServer){return;}

            exitLobby();
        }, 100, 50);

        lobbyInfoText.setPos(400L << 16, 0L << 16);
        lobbyInfoText.alignment = 1;

        playerListText.setPos(800L << 16, 75L << 16);
        playerListText.alignment = 2;

        for(int i = 0; i < getGame().getMapList().getList().size(); i++){
            MapInfo mapInfo = getGame().getMapList().getList().get(i);

            final int mapID = i;
            Button b = new Button(this, new IButtonListener() {
                @Override
                public void buttonPressed() {
                    if(waitingForServer){return;}

                    updateLobbyInfo(mapID);
                }
            }, 100, 50 );
            b.setPos(0L << 16, i * 50L << 16);
            b.addTextRenderer(mapInfo.getTitle(), 20, Color.WHITE, 0);
            mapSelectionButtonContainer.addChild(b);
        }
        mapRankingsTextRenderer.setPos(100L << 16, 200L << 16);
        mapInfoTextRenderer.setPos(100L << 16, 0L << 16);
        mapSelectionGUI.setPos(0L << 16, 75L << 16);

        readyButton.setPos(800L << 16, 550L << 16);
        readyButton.alignment = 2;
        readyButton.addTextRenderer("Ready", 20, Color.RED, 1);
        exitButton.setPos(0L << 16, 550L << 16);
        exitButton.addTextRenderer("나가기", 20, Color.white, 0);



        lobbyGUI.addChild(exitButton);
        lobbyGUI.addChild(readyButton);

        mapSelectionGUI.addChild(mapSelectionButtonContainer);
        mapSelectionGUI.addChild(mapRankingsTextRenderer);
        mapSelectionGUI.addChild(mapInfoTextRenderer);
        lobbyGUI.addChild(mapSelectionGUI);

        lobbyGUI.addChild(playerListText);

        lobbyGUI.addChild(lobbyInfoText);

        addGameObject(lobbyGUI);
    }

    @Override
    public void process(ArrayList<LoopInput> inputs) {
        super.process(inputs);

        getGame().getRudpPeer().processReceivedData();

        processGameObjects();
    }

    @Override
    protected IRUDPPeerListener generateIRUDPPeerListener() {
        return new LobbyLoopRUDPPeerListener();
    }

    void updateLobbyInfo(int mapID) {
        try {
            PacketDataC2SUpdateLobbyInfo d = new PacketDataC2SUpdateLobbyInfo();
            d.mapID = mapID;
            getGame().getRudpPeer().broadcastAboutTag(Network.SERVER_TAG, d);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void exitLobby(){
        try {
            getGame().getRudpPeer().broadcastAboutTag(Network.SERVER_TAG, new PacketDataC2SExitLobby());
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void ready(){
        try {
            getGame().getRudpPeer().broadcastAboutTag(Network.SERVER_TAG, new PacketDataC2SReady());
            waitingForServer = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private class LobbyLoopRUDPPeerListener extends LoopRUDPPeerListener{
        @Override
        public boolean onConnected(RUDPPeer peer, Connection connection) {
            return false;
        }

        @Override
        public boolean onReceived(RUDPPeer peer, Connection connection, PacketData data) {
            if (data instanceof PacketDataS2CLobbyInfoUpdated) {
                PacketDataS2CLobbyInfoUpdated d = (PacketDataS2CLobbyInfoUpdated) data;
                if(d.lobbyID == -1){
                    // d.lobbyID가 -1이면 나감 처리 되었다는 뜻
                    getGame().changeLoop(new LobbyListLoop(getGame()));
                }
                else{
                    updateLobbyUI(d);
                    // 락 풀어주기
                    waitingForServer = false;
                }
                return true;
            }
            else if (data instanceof PacketDataS2CPreprocessForGame) {
                // GameLoopPlayerLoop로 넘겨
                getGame().changeLoop(new GameLoopPlayerLoop(getGame()));
                return false;
            }



            else if (data instanceof PacketDataP2PInput) {
                // 만약 게임 결과 나온 후 로비로 돌아왔을 때 P2P 패킷이 여태까지 남아있으면 안되니
                // 의미없이 소모
                return true;
            }

            return false;
        }

        private void updateLobbyUI(PacketDataS2CLobbyInfoUpdated d) {
            //받은 정보로 UI 업뎃
            lobbyInfoText.setText(
                    "[로비 ID:" + d.lobbyID + "] [최대 인원:" + d.maxPlayers + "] " + d.lobbyName
            );


            MapInfo mapInfo = getGame().getMapList().getList().get(d.mapID);
            mapInfoTextRenderer.setText(
                    "<<<" + mapInfo.getTitle() + ">>>\n" + mapInfo.getDescription()
            );

            String playerlistString = "|플레이어 목록|\n\n";
            for(int i = 0; i < d.playersUID.size(); i++){
                String playerUID = d.playersUID.get(i);
                boolean playerReadied = d.playerReadied.get(i);
                if(playerReadied){
                    playerlistString += playerUID + "\n";
                }
                else {
                    playerlistString += playerUID + " *NOT READIED*\n";
                }
            }
            playerListText.setText(playerlistString);

            // 랭킹 정보 가져오기
            mapRankingsTextRenderer.setText("");
            try {

                Map<String, Long> myBestRank = getGame().firebaseRankings.getMyBestRank(getGame().authToken, getGame().myUID, d.mapID);
                java.util.List<FirebaseRankings.GameResult> top10 = getGame().firebaseRankings.getTop10Rankings(getGame().authToken, d.mapID);

                String rankString = "";
                if(!myBestRank.containsKey("error")){
                    rankString += "* 나의 랭킹 *\n" +
                            myBestRank.get("rank") + "위 "+ myBestRank.get("bestScore") + "점\n\n";
                }

                rankString += "* 리더 보드 *\n";
                for(int i = 0; i < top10.size(); i++){
                    rankString += i + 1 + "위 " + top10.get(i).score + "점 " + top10.get(i).players.toString() + "\n";
                }

                mapRankingsTextRenderer.setText(rankString);
            } catch (IOException e) {
                throw new RuntimeException("firebase로부터 랭킹 정보 가져오기 실패", e);
            }
        }
    }
}
