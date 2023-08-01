package ru.practicum.ewm.category.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import static ru.practicum.ewm.dto.GlobalConstants.MAX_LENGTH_CATEGORY;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = MAX_LENGTH_CATEGORY)
    String name;
}
