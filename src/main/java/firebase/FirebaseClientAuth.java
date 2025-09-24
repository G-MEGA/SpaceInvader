package firebase;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FirebaseClientAuth {

    private static final String WEB_API_KEY = "YOUR_WEB_API_KEY"; // Firebase 콘솔에서 복사한 키
    private static final Gson gson = new Gson();

    /**
     * Firebase에 이메일/비밀번호로 회원가입을 요청하고 ID 토큰을 반환합니다.
     */
    public String signUp(String email, String password) throws Exception {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + WEB_API_KEY;
        return requestAuth(url, email, password);
    }

    /**
     * Firebase에 이메일/비밀번호로 로그인을 요청하고 ID 토큰을 반환합니다.
     */
    public String signIn(String email, String password) throws Exception {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + WEB_API_KEY;
        return requestAuth(url, email, password);
    }

    private String requestAuth(String urlString, String email, String password) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // 요청 본문(Payload) 생성
        String jsonPayload = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
        );

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                Map<String, String> result = gson.fromJson(reader, Map.class);
                return result.get("idToken"); // 성공 시 ID 토큰 반환
            }
        } else {
            // 에러 처리 (예: 이미 존재하는 이메일, 잘못된 비밀번호 등)
            // conn.getErrorStream()을 읽어 Firebase의 에러 메시지를 확인할 수 있습니다.
            throw new RuntimeException("Firebase Auth Failed: " + responseCode);
        }
    }
}