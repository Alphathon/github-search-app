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
