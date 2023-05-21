package zerobase.weatherprojectstudy.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "memo") // memo라는 테이블에서 가져올 것
public class Memo {
    @Id
    // 키 생성을 DB에 맡김
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;

}
