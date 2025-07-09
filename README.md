# github-search-app
github-search-app/
├── src/
│   ├── main/
│   │   ├── java/com/githubsearch/
│   │   │   ├── config/
│   │   │   │   ├── AsyncConfig.java
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── GitHubConfig.java ----- NOT NEEDED
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   └── GitHubSearchController.java
│   │   │   ├── service/
│   │   │   │   ├── GitHubSearchService.java
│   │   │   │   ├── GitHubDownloadService.java
│   │   │   │   └── GitHubAuthService.java
│   │   │   ├── client/
│   │   │   │   └── GitHubApiClient.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   └── SearchRequest.java
│   │   │   │   ├── response/
│   │   │   │   │   ├── SearchResponse.java
│   │   │   │   │   ├── SearchResultItem.java
│   │   │   │   │   └── PullRequestInfo.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── GitHubApiException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── filter/
│   │   │   │   └── GitHubAuthFilter.java
│   │   │   └── GitHubSearchApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/ (for frontend if needed)
│   └── test/ (test files would go here)
├── build.gradle.kts
├── settings.gradle.kts
├── Dockerfile
└── README.md
