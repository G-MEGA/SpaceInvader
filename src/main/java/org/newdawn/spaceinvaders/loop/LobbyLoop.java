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

        // 1. 정적 UI 요소(텍스트, 일반 버튼) 초기화
        initStaticUI();

        // 2. 동적 UI 요소(맵 선택 목록) 초기화
        initMapSelectionUI();

        // 3. 화면 배치 (좌표 및 정렬 설정)
        setupLayout();

        // 4. 계층 구조 조립 (addChild)
        buildSceneHierarchy();
    }

// --- 추출된 헬퍼 메서드들 ---

    private void initStaticUI() {
        lobbyGUI = new GameObject2D(this);

        // 정보 텍스트
        lobbyInfoText = new TextRenderer(this, "로비 정보 텍스트", 20, Color.WHITE, 1);
        playerListText = new TextRenderer(this, "플레이어 목록", 10);

        // 맵 관련 컨테이너 및 텍스트
        mapSelectionGUI = new GameObject2D(this);
        mapSelectionButtonContainer = new GameObject2D(this);
        mapSelectionButtons = new ArrayList<>();
        mapInfoTextRenderer = new TextRenderer(this, "맵 정보", 15);
        mapRankingsTextRenderer = new TextRenderer(this, "랭킹", 13, Color.pink, 2);

        // 하단 버튼 (람다 내부 로직을 메서드로 추출)
        readyButton = new Button(this, this::onClickReady, 100, 50);

        exitButton = new Button(this, this::onClickExit, 100, 50);
    }

    private void initMapSelectionUI() {
        // 맵 리스트를 순회하며 버튼 생성
        // 복잡한 루프 로직을 별도 메서드로 분리하여 생성자를 단순하게 유지
        ArrayList<MapInfo> mapList = getGame().getMapList().getList();
        for (int i = 0; i < mapList.size(); i++) {
            createAndAddMapButton(mapList.get(i), i);
        }
    }

    private void createAndAddMapButton(MapInfo mapInfo, int index) {
        // 람다/익명 클래스 대신 메서드 레퍼런스 또는 단순 호출 사용
        Button button = new Button(this, () -> onClickMapSelect(index), 100, 50);

        button.setPos(0L << 16, index * 50L << 16);
        button.addTextRenderer(mapInfo.getTitle(), 20, Color.WHITE, 0);

        mapSelectionButtonContainer.addChild(button);
        // (필요하다면 mapSelectionButtons.add(button); 도 여기에 추가)
    }

// --- 이벤트 핸들러 (Guard Clause 적용) ---

    private void onClickReady() {
        if (waitingForServer) return;
        ready();
    }

    private void onClickExit() {
        if (waitingForServer) return;
        exitLobby();
    }

    private void onClickMapSelect(int mapId) {
        if (waitingForServer) return;
        updateLobbyInfo(mapId);
    }

// --- 레이아웃 및 구조 ---

    private void setupLayout() {
        // 로비 정보
        lobbyInfoText.setPos(400L << 16, 0L << 16);
        lobbyInfoText.alignment = 1;

        // 플레이어 목록
        playerListText.setPos(800L << 16, 75L << 16);
        playerListText.alignment = 2;

        // 맵 UI 배치
        mapRankingsTextRenderer.setPos(100L << 16, 200L << 16);
        mapInfoTextRenderer.setPos(100L << 16, 0L << 16);
        mapSelectionGUI.setPos(0L << 16, 75L << 16);

        // 하단 버튼 배치
        readyButton.setPos(800L << 16, 550L << 16);
        readyButton.alignment = 2;
        readyButton.addTextRenderer("Ready", 20, Color.RED, 1);

        exitButton.setPos(0L << 16, 550L << 16);
        exitButton.addTextRenderer("나가기", 20, Color.white, 0);
    }

    private void buildSceneHierarchy() {
        // 맵 UI 조립
        mapSelectionGUI.addChild(mapSelectionButtonContainer);
        mapSelectionGUI.addChild(mapRankingsTextRenderer);
        mapSelectionGUI.addChild(mapInfoTextRenderer);

        // 전체 GUI 조립
        lobbyGUI.addChild(exitButton);
        lobbyGUI.addChild(readyButton);
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



    private class LobbyLoopRUDPPeerListener extends LoopRUDPPeerListener {

        @Override
        public boolean onConnected(RUDPPeer peer, Connection connection) {
            return false;
        }

        // [Refactoring] 메인 핸들러: 단순히 타입에 따라 하위 담당자에게 일을 넘김 (복잡도 3: if-else if)
        @Override
        public boolean onReceived(RUDPPeer peer, Connection connection, PacketData data) {
            boolean result = false;
            if (data instanceof PacketDataS2CLobbyInfoUpdated) {
                handleLobbyInfoUpdated((PacketDataS2CLobbyInfoUpdated) data);
                result = true;
            }

            if (data instanceof PacketDataS2CPreprocessForGame) {
                handleGameStart();
                result = false; // 보통 상태 전환 시엔 false를 리턴하거나 상황에 맞게 조정
            }

            if (data instanceof PacketDataP2PInput) {
                result = true; // P2P 패킷 소모
            }

            return result;
        }

        // [Extract] 로비 정보 업데이트 핸들러 (복잡도 2: if-else)
        private void handleLobbyInfoUpdated(PacketDataS2CLobbyInfoUpdated d) {
            if (d.lobbyID == -1) {
                // 나감 처리
                getGame().changeLoop(new LobbyListLoop(getGame()));
            } else {
                updateLobbyUI(d);
                waitingForServer = false; // 락 해제
            }
        }

        // [Extract] 게임 시작 핸들러 (복잡도 1)
        private void handleGameStart() {
            getGame().changeLoop(new GameLoopPlayerLoop(getGame()));
        }

        // [Refactoring] UI 업데이트 오케스트레이터 (복잡도 1)
        private void updateLobbyUI(PacketDataS2CLobbyInfoUpdated d) {
            updateBasicInfoUI(d);
            updatePlayerListUI(d);
            updateRankingUI(d.mapID);
        }

        // [Extract] 기본 텍스트 정보 업데이트 (복잡도 1)
        private void updateBasicInfoUI(PacketDataS2CLobbyInfoUpdated d) {
            lobbyInfoText.setText(
                    "[로비 ID:" + d.lobbyID + "] [최대 인원:" + d.maxPlayers + "] " + d.lobbyName
            );

            MapInfo mapInfo = getGame().getMapList().getList().get(d.mapID);
            mapInfoTextRenderer.setText(
                    "<<<" + mapInfo.getTitle() + ">>>\n" + mapInfo.getDescription()
            );
        }

        // [Extract] 플레이어 목록 업데이트 (복잡도 1)
        private void updatePlayerListUI(PacketDataS2CLobbyInfoUpdated d) {
            // 문자열 생성 로직을 별도로 분리하여 복잡도 격리
            String playerListString = buildPlayerListString(d);
            playerListText.setText(playerListString);
        }

        // [Extract] 플레이어 목록 문자열 생성 (복잡도 2: for loop)
        private String buildPlayerListString(PacketDataS2CLobbyInfoUpdated d) {
            StringBuilder sb = new StringBuilder("|플레이어 목록|\n\n");
            for (int i = 0; i < d.playersUID.size(); i++) {
                String playerUID = d.playersUID.get(i);
                boolean isReady = d.playerReadied.get(i);

                sb.append(playerUID);
                if (!isReady) {
                    sb.append(" *NOT READIED*");
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        // [Extract] 랭킹 정보 업데이트 (복잡도 2: try-catch)
        private void updateRankingUI(int mapID) {
            mapRankingsTextRenderer.setText("");
            try {
                String rankString = fetchAndBuildRankString(mapID);
                mapRankingsTextRenderer.setText(rankString);
            } catch (IOException e) {
                throw new RuntimeException("firebase로부터 랭킹 정보 가져오기 실패", e);
            }
        }

        // [Extract] 랭킹 데이터 통신 및 조합 (복잡도 1: 흐름 제어)
        private String fetchAndBuildRankString(int mapID) throws IOException {
            // 데이터 가져오기
            Map<String, Long> myBestRank = getGame().firebaseRankings.getMyBestRank(getGame().authToken, getGame().myUID, mapID);
            java.util.List<FirebaseRankings.GameResult> top10 = getGame().firebaseRankings.getTop10Rankings(getGame().authToken, mapID);

            // 문자열 조합
            StringBuilder sb = new StringBuilder();
            appendMyRank(sb, myBestRank);
            appendLeaderBoard(sb, top10);

            return sb.toString();
        }

        // [Extract] 내 랭킹 문자열 추가 (복잡도 2: if check)
        private void appendMyRank(StringBuilder sb, Map<String, Long> myBestRank) {
            if (!myBestRank.containsKey("error")) {
                sb.append("* 나의 랭킹 *\n")
                        .append(myBestRank.get("rank")).append("위 ")
                        .append(myBestRank.get("bestScore")).append("점\n\n");
            }
        }

        // [Extract] 리더보드 문자열 추가 (복잡도 2: for loop)
        private void appendLeaderBoard(StringBuilder sb, java.util.List<FirebaseRankings.GameResult> top10) {
            sb.append("* 리더 보드 *\n");
            for (int i = 0; i < top10.size(); i++) {
                FirebaseRankings.GameResult result = top10.get(i);
                sb.append(i + 1).append("위 ")
                        .append(result.score).append("점 ")
                        .append(result.players.toString()).append("\n");
            }
        }
    }
}
