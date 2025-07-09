@RestController
@RequestMapping("/actuator/github")
public class GitHubMetricsController {
    
    private final MeterRegistry meterRegistry;
    private final GitHubApiClient apiClient;
    
    @GetMapping("/rate-limits")
    public ResponseEntity<Map<String, Object>> getRateLimits(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring("Bearer ".length());
            GitHub gitHub = new GitHubBuilder().withOAuthToken(token).build();
            GHRateLimit rateLimit = gitHub.getRateLimit();
            
            Map<String, Object> response = new HashMap<>();
            response.put("limit", rateLimit.limit);
            response.put("remaining", rateLimit.remaining);
            response.put("reset", new Date(rateLimit.reset.getTime()));
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}