package firebase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class FirebaseClientAuth {

    private static final String WEB_API_KEY = "AIzaSyAx6jRdo05AbC0eKQqDFO-NQEWemVd7bsg";
    private static final OkHttpClient client = new OkHttpClient(); // 클라이언트는 재사용하는 것이 효율적
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public Map<String, Object> signUp(String email, String password) throws IOException {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + WEB_API_KEY;
        return requestAuth(url, email, password);
    }

    public Map<String, Object> signIn(String email, String password) throws IOException {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + WEB_API_KEY;
        return requestAuth(url, email, password);
    }

    private Map<String, Object> requestAuth(String url, String email, String password) throws IOException {
        // 1. 요청 본문(Payload) 생성
        String jsonPayload = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
        );
        RequestBody body = RequestBody.create(jsonPayload, JSON);

        // 2. HTTP 요청 생성
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 3. 요청 실행 및 응답 처리 (try-with-resources로 자동 close)
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 에러 발생 시 응답 본문을 포함하여 예외 발생
                throw new IOException("Unexpected code " + response + " with body: " + response.body().string());
            }

            // 성공 시 JSON 응답을 Map으로 변환하여 반환
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, new TypeToken<Map<String, Object>>(){}.getType());
        }
    }
}