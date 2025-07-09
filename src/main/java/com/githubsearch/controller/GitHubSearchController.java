@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubSearchController {
    private final GitHubSearchService searchService;
    private final GitHubDownloadService downloadService;
    private final GitHubAuthService authService;
    
    @GetMapping("/search")
    public CompletableFuture<SearchResponse> search(
        @RequestParam String query,
        @RequestParam String org,
        @RequestParam(required = false) List<String> excludeExtensions,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestHeader("Authorization") String authHeader) {
        
        var request = new SearchRequest(query, org, excludeExtensions);
        return searchService.search(request, page, size, authHeader);
    }
    
    @GetMapping("/download")
    public ResponseEntity<Resource> download(
        @RequestParam String query,
        @RequestParam String org,
        @RequestParam(required = false) List<String> excludeExtensions,
        @RequestHeader("Authorization") String authHeader) {
        
        var request = new SearchRequest(query, org, excludeExtensions);
        var resource = downloadService.downloadSearchResults(request, authHeader);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=github_search_results.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .contentLength(resource.contentLength())
            .body(resource);
    }
    
    @GetMapping("/auth/callback")
    public ResponseEntity<AuthResponse> oauthCallback(@RequestParam String code) {
        return ResponseEntity.ok(authService.authenticate(code));
    }
}