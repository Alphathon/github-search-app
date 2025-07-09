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