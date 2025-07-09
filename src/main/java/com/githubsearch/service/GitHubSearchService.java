@Service
@RequiredArgsConstructor
public class GitHubSearchService {
    private final GitHubApiClient apiClient;
    private final Cache<String, SearchResponse> searchCache;
    private final GitHubAuthService authService;
    
    @Async
    public CompletableFuture<SearchResponse> search(SearchRequest request, int page, int size, String authHeader) {
        String accessToken = authService.extractAccessToken(authHeader);
        String cacheKey = generateCacheKey(request, page, size, accessToken);
        
        return CompletableFuture.completedFuture(
            searchCache.get(cacheKey, key -> {
                try {
                    return apiClient.searchCode(request, page, size, accessToken);
                } catch (IOException e) {
                    throw new GitHubApiException("GitHub search failed", e);
                }
            })
        );
    }
    
    private String generateCacheKey(SearchRequest request, int page, int size, String token) {
        return "%s-%s-%s-%d-%d-%s".formatted(
            request.query(),
            request.org(),
            request.excludedExtensions() != null ? String.join(",", request.excludedExtensions()) : "",
            page,
            size,
            token.hashCode() // For cache differentiation per user
        );
    }
}


