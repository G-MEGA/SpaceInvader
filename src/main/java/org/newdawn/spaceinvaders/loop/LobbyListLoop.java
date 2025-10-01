package org.newdawn.spaceinvaders.loop;

import networking.Network;
import networking.rudp.Connection;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.PacketData.PacketData;
import networking.rudp.RUDPPeer;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.gui.Button;
import org.newdawn.spaceinvaders.game_object.gui.IButtonListener;
import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;
import org.newdawn.spaceinvaders.map_load.MapInfo;

import java.awt.*;
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
    GameObject2D mapSelectionGUI;

    GameObject2D mapSelectionButtonContainer;
    TextRenderer mapInfoTextRenderer;

    GameObject2D gameRoomGUI;

    public LobbyListLoop(Game game) {
        super(game);

        mapSelectionGUI = new GameObject2D(this);
        gameRoomGUI = new GameObject2D(this);

        //region mapSelectionGUI 구성
        mapInfoTextRenderer = new TextRenderer(this,"맵을 선택하세요.", 20);
        mapInfoTextRenderer.setPosX(160L << 16);
        addGameObject(mapInfoTextRenderer);

        mapSelectionButtonContainer =  new GameObject2D(this);
        mapSelectionGUI.addChild(mapSelectionButtonContainer);
        ArrayList<MapInfo> maps = game.getMapList().getList();
        for (int i = 0; i < maps.size(); i++) {
            final MapInfo mapInfo = maps.get(i);
            Button button = new Button(this, new IButtonListener() {
                @Override
                public void buttonPressed() {
                    String t = mapInfo.getTitle();
                    t += "\n" + mapInfo.getPath();
                    t += "\n" + mapInfo.getHash();

                    mapInfoTextRenderer.setText(t);
                }
            }, 150, 40);
            mapSelectionButtonContainer.addChild(button);

            button.setPos(0, i * button.getHeightInFixedPoint());
            TextRenderer textRenderer = button.addTextRenderer(mapInfo.getTitle(), 20, Color.WHITE, 0);
            textRenderer.setPosX(10L << 16);
        }
        //endregion

        //region gameRoomGUI 구성

        //endregion

        addGameObject(mapSelectionGUI);
//        addGameObject(gameRoomGUI);
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
                return false;
            }
        };
    }
}
