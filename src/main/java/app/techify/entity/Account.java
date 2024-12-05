package app.techify.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotBlank(message = "Email is required")
    @Nationalized
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Size(max = 255)
    @Nationalized
    @Column(name = "password_hash")
    private String passwordHash;

    @Size(max = 40)
    @Nationalized
    @Column(name = "role", length = 40)
    private String role;

    @Size(max = 255)
    @Nationalized
    @Column(name = "refresh_token")
    private String refreshToken;

    @Size(max = 50)
    @Nationalized
    @Column(name = "google_id", length = 50)
    private String googleId;

    @Size(max = 50)
    @Nationalized
    @Column(name = "facebook_id", length = 50)
    private String facebookId;

}