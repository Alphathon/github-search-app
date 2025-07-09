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

@Service
@RequiredArgsConstructor
public class GitHubDownloadService {
    private final GitHubApiClient apiClient;
    private final GitHubAuthService authService;
    
    public ByteArrayResource downloadSearchResults(SearchRequest request, String authHeader) {
        String accessToken = authService.extractAccessToken(authHeader);
        List<SearchResultItem> allItems = new ArrayList<>();
        int page = 1;
        final int pageSize = 100; // GitHub's max page size
        
        try {
            while (true) {
                SearchResponse response = apiClient.searchCode(request, page, pageSize, accessToken);
                allItems.addAll(response.items());
                
                if (page * pageSize >= response.totalCount()) {
                    break;
                }
                page++;
            }
            
            return generateCsv(allItems);
        } catch (IOException e) {
            throw new GitHubApiException("Failed to download search results", e);
        }
    }
    
    private ByteArrayResource generateCsv(List<SearchResultItem> items) {
        try (var outputStream = new ByteArrayOutputStream();
             var writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            
            // Header
            writer.writeNext(new String[]{
                "Repository", "File Path", "URL", "Repo Owner", 
                "PR Title", "PR URL", "PR Author", "PR Last Updated", "PR Mergeable"
            });
            
            // Data
            for (var item : items) {
                if (item.pullRequests().isEmpty()) {
                    writer.writeNext(new String[]{
                        item.repoName(),
                        item.filePath(),
                        item.htmlUrl(),
                        item.repoOwner(),
                        "", "", "", "", ""
                    });
                } else {
                    for (var pr : item.pullRequests()) {
                        writer.writeNext(new String[]{
                            item.repoName(),
                            item.filePath(),
                            item.htmlUrl(),
                            item.repoOwner(),
                            pr.title(),
                            pr.url(),
                            pr.author(),
                            pr.lastUpdated().toString(),
                            String.valueOf(pr.mergeable())
                        });
                    }
                }
            }
            
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new GitHubApiException("CSV generation failed", e);
        }
    }
}

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