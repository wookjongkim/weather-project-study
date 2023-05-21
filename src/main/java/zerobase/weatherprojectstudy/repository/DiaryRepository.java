package zerobase.weatherprojectstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weatherprojectstudy.domain.Diary;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    Diary getFirstByDate(LocalDate date);

    @Transactional // 여기 Transactional 없으면 데이터가 안지워짐
    void deleteAllByDate(LocalDate date);
}
