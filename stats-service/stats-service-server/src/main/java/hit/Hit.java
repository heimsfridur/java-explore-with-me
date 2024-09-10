package hit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hit {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    @NotNull
    private LocalDateTime timestamp;
}

