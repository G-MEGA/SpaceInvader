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
        // 1단계: 나의 최고 점수 가져오기 (메서드 추출)
        long bestScore = fetchBestScore(idToken, myUid, mapId);

        // Guard Clause: 점수가 없거나 0점이면 즉시 반환
        if (bestScore <= 0) {
            return Collections.singletonMap("error", -1L);
        }

        // 2단계: 나보다 높은 점수 개수 가져오기 (메서드 추출)
        long higherRankersCount = fetchHigherRankersCount(idToken, mapId, bestScore);

        // 3단계: 결과 반환
        Map<String, Long> result = new HashMap<>();
        result.put("bestScore", bestScore);
        result.put("rank", higherRankersCount + 1);
        return result;
    }

    private long fetchBestScore(String idToken, String myUid, int mapId) throws IOException {
        // 1. 요청 생성
        String url = String.format("%sscores_by_user/%s/map_%d.json?auth=%s",
                DATABASE_URL, myUid, mapId, idToken);
        Request request = new Request.Builder().url(url).build();

        // 2. 네트워크 실행 및 응답 문자열 확보 (메서드 추출)
        String responseBody = executeAndGetBody(request);

        // 3. 파싱 및 계산 (메서드 추출)
        return parseMaxScore(responseBody);
    }

    private String executeAndGetBody(Request request) throws IOException {
        // try-with-resources와 유효성 검사만 담당
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to get my scores: " + response);
            if (response.body() == null) throw new IOException("Empty response body");

            return response.body().string();
        }
    }

    private long parseMaxScore(String responseBody) {
        // 순수 비즈니스 로직만 담당
        if (responseBody.equals("null")) {
            return -1L;
        }

        Type type = new TypeToken<Map<String, Long>>(){}.getType();
        Map<String, Long> myScores = gson.fromJson(responseBody, type);

        return myScores.values().stream()
                .max(Long::compare)
                .orElse(0L);
    }

    private long fetchHigherRankersCount(String idToken, int mapId, long bestScore) throws IOException {
        // 1. 요청 생성
        String url = String.format("%sscores_by_map/map_%d.json?orderBy=\"score\"&startAt=%d&auth=%s",
                DATABASE_URL, mapId, bestScore + 1, idToken);
        Request request = new Request.Builder().url(url).build();

        // 2. 네트워크 실행 (공통 메서드 재사용 권장)
        String responseBody = executeAndGetBody(request);

        // 3. 파싱 및 카운트 계산 (메서드 추출)
        return countRankersFromJson(responseBody);
    }

    // 순수 데이터 변환 로직
    private long countRankersFromJson(String responseBody) {
        if (responseBody.equals("null")) {
            return 0L; // 나보다 높은 사람이 없음
        }

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> higherScores = gson.fromJson(responseBody, type);

        return (long) higherScores.size();
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
        // 1. URL 및 요청 생성
        String top10Url = String.format("%sscores_by_map/map_%d.json?orderBy=\"score\"&limitToLast=10&auth=%s",
                DATABASE_URL, mapId, idToken);
        Request request = new Request.Builder().url(top10Url).build();

        // 2. 네트워크 실행 (공통 메서드 재사용)
        String responseBody = executeAndGetBody(request);

        // 3. 파싱 및 정렬 (순수 로직 분리)
        return parseAndSortRankings(responseBody);
    }

    private List<GameResult> parseAndSortRankings(String responseBody) {
        // 데이터가 없으면 빈 리스트 반환 (Guard Clause)
        if (responseBody.equals("null")) {
            return new ArrayList<>();
        }

        // JSON 파싱
        Type type = new TypeToken<Map<String, GameResult>>(){}.getType();
        Map<String, GameResult> scoresMap = gson.fromJson(responseBody, type);

        // 변환 및 정렬
        List<GameResult> topScores = new ArrayList<>(scoresMap.values());

        // 람다식 내부 캐스팅 제거 및 깔끔하게 정리
        // (GameResult 객체의 score 필드에 접근한다고 가정)
        topScores.sort((o1, o2) -> Long.compare(o2.score, o1.score));
        // 또는 Java 8 스타일: topScores.sort(Comparator.comparingLong((GameResult o) -> o.score).reversed());

        return topScores;
    }
}
