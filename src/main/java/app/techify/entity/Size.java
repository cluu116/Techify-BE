package app.techify.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "size")
public class Size {
    @Id
    @Column(name = "id", columnDefinition = "tinyint not null")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @jakarta.validation.constraints.Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "size_json", nullable = false)
    private String sizeJson;

}
