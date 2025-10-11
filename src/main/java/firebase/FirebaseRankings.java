package firebase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

public class FirebaseRankings {

    private static final String DATABASE_URL = "https://spaceinvader-29187-default-rtdb.asia-southeast1.firebasedatabase.app/"; // 본인 DB URL
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * 게임 결과를 데이터베이스에 저장합니다.
     * @param idToken 로그인 시 받은 인증 토큰
     * @param gameSessionId 서버로부터 받은 이번 게임 세션 ID
     * @param mapId 맵 ID (예: 1, 2, 3...)
     * @param score 점수
     * @param playerUids 참여한 모든 플레이어의 UID 리스트
     */
    public void saveGameResult(String idToken, String gameSessionId, int mapId, int score, List<String> playerUids) throws IOException {
        // 2. 저장할 데이터 객체 생성
        Map<String, Object> gameData = new HashMap<>();
        gameData.put("score", score);
        gameData.put("timestamp", System.currentTimeMillis()); // 시간 기록
        gameData.put("players", playerUids);

        String jsonData = gson.toJson(gameData);
        RequestBody body = RequestBody.create(jsonData, JSON);

        // 3. 'scores_by_map' 경로에 데이터 저장 (PUT 요청)
        // PUT은 해당 경로의 데이터를 완전히 덮어씁니다.
        String mapUrl = String.format("%sscores_by_map/map_%d/%s.json?auth=%s",
                DATABASE_URL, mapId, gameSessionId, idToken);
        Request mapRequest = new Request.Builder().url(mapUrl).put(body).build();

        try (Response response = client.newCall(mapRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Map score save failed: " + response);
            }
            // 성공 시, 유저별 데이터도 저장
            updateUserScores(idToken, mapId, score, playerUids, gameSessionId);
        }
    }

    // 유저별 점수 데이터를 저장하는 내부 메소드
    private void updateUserScores(String idToken, int mapId, int score, List<String> playerUids, String gameSessionId) throws IOException {
        RequestBody scoreBody = RequestBody.create(String.valueOf(score), JSON);

        for (String uid : playerUids) {
            String userUrl = String.format("%sscores_by_user/%s/map_%d/%s.json?auth=%s",
                    DATABASE_URL, uid, mapId, gameSessionId, idToken);
            Request userRequest = new Request.Builder().url(userUrl).put(scoreBody).build();

            try (Response response = client.newCall(userRequest).execute()) {
                if (!response.isSuccessful()) {
                    // 여기서 실패하면 데이터 정합성이 깨질 수 있으나,
                    // 클라이언트 레벨에서는 일단 로그를 남기는 정도로 처리합니다.
                    System.err.println("Failed to save score for user: " + uid);
                }
            }
        }
    }

    /**
     * 특정 맵에서 나의 최고 점수와 순위를 가져옵니다.
     * @param idToken 로그인 시 받은 인증 토큰
     * @param myUid 나의 UID
     * @param mapId 맵 ID
     * @return 최고 점수와 순위 정보 (Map 형태)
     */
    public Map<String, Long> getMyBestRank(String idToken, String myUid, int mapId) throws IOException {
        // --- 1단계: 나의 최고 점수 찾기 ---
        String myScoresUrl = String.format("%sscores_by_user/%s/map_%d.json?auth=%s",
                DATABASE_URL, myUid, mapId, idToken);
        Request request = new Request.Builder().url(myScoresUrl).build();

        long bestScore = 0;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to get my scores: " + response);
            if (response.body() == null) throw new IOException("Empty response body");

            String responseBody = response.body().string();
            // 응답이 null일 경우 (기록이 없을 경우)
            if (responseBody.equals("null")) {
                return Collections.singletonMap("error", -1L); // 기록 없음
            }

            Type type = new TypeToken<Map<String, Long>>(){}.getType();
            Map<String, Long> myScores = gson.fromJson(responseBody, type);

            // 자바 스트림을 사용하여 최고 점수 찾기
            bestScore = myScores.values().stream().max(Long::compare).orElse(0L);
        }

        if (bestScore == 0) {
            return Collections.singletonMap("error", -1L); // 기록 없음
        }

        // --- 2단계: 순위 계산하기 (나보다 높은 점수 개수 세기) ---
        // startAt 파라미터는 JSON 문자열 내부의 값과 비교하므로, 숫자여도 ""가 필요 없습니다.
        String rankUrl = String.format("%sscores_by_map/map_%d.json?orderBy=\"score\"&startAt=%d&auth=%s",
                DATABASE_URL, mapId, bestScore + 1, idToken);
        Request rankRequest = new Request.Builder().url(rankUrl).build();

        long higherRankersCount = 0;
        try (Response response = client.newCall(rankRequest).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to get rank: " + response);
            if (response.body() == null) throw new IOException("Empty response body for rank");

            String responseBody = response.body().string();
            if (!responseBody.equals("null")) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> higherScores = gson.fromJson(responseBody, type);
                higherRankersCount = higherScores.size();
            }
        }

        long myRank = higherRankersCount + 1;

        Map<String, Long> result = new HashMap<>();
        result.put("bestScore", bestScore);
        result.put("rank", myRank);
        return result;
    }

    // JSON 응답을 객체로 변환하기 위한 도우미 클래스 (POJO)
    public static class GameResult {
        public long score;
        public List<String> players;
        // 필요하다면 timestamp 등 다른 필드도 추가


        @Override
        public String toString() {
            return score + " " + players;
        }
    }

    /**
     * 특정 맵의 상위 10위 랭킹을 가져옵니다.
     * @param idToken 로그인 시 받은 인증 토큰
     * @param mapId 맵 ID
     * @return 상위 10명의 GameResult 리스트
     */
    public List<GameResult> getTop10Rankings(String idToken, int mapId) throws IOException {
        // orderBy: "score"를 기준으로 정렬
        // limitToLast: 정렬된 결과의 마지막 10개를 가져옴 (즉, 가장 점수가 높은 10개)
        String top10Url = String.format("%sscores_by_map/map_%d.json?orderBy=\"score\"&limitToLast=10&auth=%s",
                DATABASE_URL, mapId, idToken);
        Request request = new Request.Builder().url(top10Url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to get top 10: " + response);
            if (response.body() == null) throw new IOException("Empty response body for top 10");

            String responseBody = response.body().string();
            if (responseBody.equals("null")) {
                return new ArrayList<>(); // 랭킹 데이터가 없음
            }

            Type type = new TypeToken<Map<String, GameResult>>(){}.getType();
            Map<String, GameResult> scoresMap = gson.fromJson(responseBody, type);

            // Map의 값들을 리스트로 변환
            List<GameResult> topScores = new ArrayList<>(scoresMap.values());

            // 중요: 점수가 높은 순서(내림차순)로 리스트를 정렬
            topScores.sort(Comparator.comparingLong(o -> ((GameResult)o).score).reversed());

            return topScores;
        }
    }
}
