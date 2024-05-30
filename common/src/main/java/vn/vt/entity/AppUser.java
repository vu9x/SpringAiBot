package vn.vt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import vn.vt.enums.UserState;

import java.time.LocalDateTime;


@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="app_user")
@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String userName;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserState state;

}
