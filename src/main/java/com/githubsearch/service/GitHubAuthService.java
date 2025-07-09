@Service
public class GitHubAuthService {
    private static final String BEARER_PREFIX = "Bearer ";

    public String extractAccessToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        throw new SecurityException("Invalid authorization header");
    }

    public AuthResponse authenticate(String code) {
        // Implementation for OAuth token exchange
        // This would make a request to GitHub's OAuth endpoint
        // Return the access token and user info
        return new AuthResponse("access_token", "refresh_token", 3600, "user@example.com");
    }
}