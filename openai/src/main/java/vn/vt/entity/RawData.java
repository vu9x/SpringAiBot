package vn.vt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.telegram.telegrambots.meta.api.objects.Update;

//@Data Генерирует Getter, Setter, Equals, Hashcode, использовать только immutable поля. У нас
// При создание объекта в MainServiceImpl, мы сэттим в объект только update, а id при этом == null.
// При сохранении объекта в базу, СУБД сгенерит объекту id. И после этого Spring засэттит id в объект.
// То есть объект в течение сохранения изменится.
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="raw_data")
@Entity
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    private Update event;
}
