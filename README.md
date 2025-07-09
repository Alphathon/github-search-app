# github-search-app
github-search-app/
├── src/
│   ├── main/
│   │   ├── java/com/githubsearch/
│   │   │   ├── config/
│   │   │   │   ├── AsyncConfig.java
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── GitHubConfig.java
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

# OAuth Configuration
spring.security.oauth2.client.registration.github.client-id=your-client-id
spring.security.oauth2.client.registration.github.client-secret=your-client-secret
spring.security.oauth2.client.registration.github.scope=repo

# Application Configuration
server.port=8080
github.api.cache.size=1000
github.api.cache.expiry=15m

# Actuator Endpoints
management.endpoints.web.exposure.include=health,metrics,github
management.endpoint.health.show-details=always
