package api;

import lombok.AllArgsConstructor;

public class Credentials {
    private String email;
    private String password;
    private String domain;

    public Credentials(String email, String password, String domain) {
        this.email = email;
        this.password = password;
        this.domain = domain;
    }
}
