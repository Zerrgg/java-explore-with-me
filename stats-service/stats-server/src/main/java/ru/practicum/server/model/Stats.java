package ru.practicum.server.model;


import lombok.*;
import lombok.experimental.FieldDefaults;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stats", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "app_name", nullable = false)
    String app;

    @Column(nullable = false)
    String uri;

    @Column(name = "user_ip", nullable = false, length = 39)
    String ip;

    @Column(name = "created", nullable = false)
    LocalDateTime timestamp;
}
