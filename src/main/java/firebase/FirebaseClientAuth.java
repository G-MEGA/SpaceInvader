package firebase;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FirebaseClientAuth {

    private static final String WEB_API_KEY = "AIzaSyAx6jRdo05AbC0eKQqDFO-NQEWemVd7bsg"; // Firebase 콘솔에서 복사한 키
    private static final Gson gson = new Gson();

    /**
     * Firebase에 이메일/비밀번호로 회원가입을 요청
     */
    public Map<String, String> signUp(String email, String password) throws Exception {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + WEB_API_KEY;
        return requestAuth(url, email, password);
    }

    /**
     * Firebase에 이메일/비밀번호로 로그인을 요청
     */
    public Map<String, String> signIn(String email, String password) throws Exception {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + WEB_API_KEY;
        return requestAuth(url, email, password);
    }

    private Map<String, String> requestAuth(String urlString, String email, String password) throws Exception {
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
                return result;
            }
        } else {
            // 1. 에러 스트림을 가져옵니다.
            InputStream errorStream = conn.getErrorStream();
            if (errorStream == null) {
                throw new RuntimeException("Firebase Auth Failed with response code: " + responseCode);
            }

            // 2. 스트림의 내용을 문자열로 읽어들입니다.
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // 3. 실제 에러 메시지를 출력하고 예외를 발생시킵니다.
            String actualErrorMessage = response.toString();
            throw new RuntimeException(actualErrorMessage);
        }
    }
}