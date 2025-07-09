@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/auth/callback").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/api/github/search", true)
                .authorizationEndpoint(auth -> auth
                    .baseUri("/oauth2/authorization")
                )
                .tokenEndpoint(token -> token
                    .accessTokenResponseClient(accessTokenResponseClient())
                )
            )
            .addFilterBefore(new GitHubAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        var tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        tokenResponseClient.setRequestEntityConverter(new CustomRequestEntityConverter());
        return tokenResponseClient;
    }
    
    @Bean
    public GitHubBuilder gitHubBuilder() {
        return new GitHubBuilder();
    }
}

public class GitHubAuthFilter extends OncePerRequestFilter {
    private final GitHubAuthService authService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                authService.extractAccessToken(authHeader);
                filterChain.doFilter(request, response);
            } catch (SecurityException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}