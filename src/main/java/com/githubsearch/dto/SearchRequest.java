// Request DTOs
public record SearchRequest(
    String query,
    String org,
    List<String> excludedExtensions
) {}

// Response DTOs
public record SearchResponse(
    List<SearchResultItem> items,
    int totalCount,
    int totalPages,
    int currentPage
) {}

public record SearchResultItem(
    String repoName,
    String filePath,
    String htmlUrl,
    String repoOwner,
    List<PullRequestInfo> pullRequests
) {}

public record PullRequestInfo(
    String title,
    String url,
    String author,
    Date lastUpdated,
    Boolean mergeable
) {}

public record AuthResponse(
    String accessToken,
    String refreshToken,
    int expiresIn,
    String userEmail
) {}