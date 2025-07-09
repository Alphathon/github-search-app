import org.kohsuke.github.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {
    private final GitHubBuilder gitHubBuilder;
    
    public SearchResponse searchCode(SearchRequest request, int page, int size, String accessToken) throws IOException {
        GitHub gitHub = gitHubBuilder.withOAuthToken(accessToken).build();
        
        GHContentSearchBuilder searchBuilder = gitHub.searchContent()
            .q(request.query())
            .org(request.org());
            
        // Add file extension exclusions if provided
        if (request.excludedExtensions() != null && !request.excludedExtensions().isEmpty()) {
            searchBuilder.extension(excludeExtensions(request.excludedExtensions()));
        }
        
        PagedSearchIterable<GHContent> searchResult = searchBuilder.list()
            .withPageSize(size)
            .withPage(page);
            
        List<SearchResultItem> items = searchResult.toList().parallelStream()
            .map(content -> processContentItem(content, gitHub))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
            
        return new SearchResponse(
            items,
            searchResult.getTotalCount(),
            calculateTotalPages(searchResult.getTotalCount(), size),
            page
        );
    }
    
    private String excludeExtensions(List<String> extensions) {
        return extensions.stream()
            .map(ext -> "-extension:" + ext)
            .collect(Collectors.joining(" "));
    }
    
    private Optional<SearchResultItem> processContentItem(GHContent content, GitHub gitHub) {
        try {
            List<PullRequestInfo> prInfos = getRelatedPullRequests(content, gitHub);
            return Optional.of(new SearchResultItem(
                content.getOwner().getName(),
                content.getPath(),
                content.getHtmlUrl(),
                content.getRepository().getOwnerName(),
                prInfos
            ));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
    
    private List<PullRequestInfo> getRelatedPullRequests(GHContent content, GitHub gitHub) throws IOException {
        GHRepository repo = gitHub.getRepository(content.getOwner().getFullName());
        return repo.queryPullRequests()
            .state(GHIssueState.OPEN)
            .list()
            .toList().parallelStream()
            .filter(pr -> isPrRelatedToContent(pr, content))
            .map(pr -> new PullRequestInfo(
                pr.getTitle(),
                pr.getHtmlUrl(),
                pr.getUser().getLogin(),
                pr.getUpdatedAt(),
                pr.getMergeable()
            ))
            .toList();
    }
    
    private boolean isPrRelatedToContent(GHPullRequest pr, GHContent content) {
        try {
            return pr.listFiles().toList().stream()
                .anyMatch(file -> file.getFilename().equals(content.getPath()));
        } catch (IOException e) {
            return false;
        }
    }
    
    private int calculateTotalPages(int totalCount, int pageSize) {
        return (int) Math.ceil((double) totalCount / pageSize);
    }
}