package MainBackendService.service;

public enum JwtClaims {
    USER_EMAIL("user_email"),
    User("user"),
    USER_NAME("user_name");
    private final String claimName;

    JwtClaims(String claimName) {
        this.claimName = claimName;
    }

    public String getClaimName() {
        return claimName;
    }
}
