package vn.vt.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="appOpenaiResponses")
@Entity
public class AppOpenAiResponses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="app_user_id", nullable = false)
    private AppUser appUser;

    @Column(columnDefinition = "text")
    private String userRequest;

    @Column(columnDefinition = "text")
    private String openAiResponse;
}
