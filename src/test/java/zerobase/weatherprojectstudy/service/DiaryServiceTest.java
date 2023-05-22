package zerobase.weatherprojectstudy.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zerobase.weatherprojectstudy.domain.DateWeather;
import zerobase.weatherprojectstudy.domain.Diary;
import zerobase.weatherprojectstudy.repository.DateWeatherRepository;
import zerobase.weatherprojectstudy.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DateWeatherRepository dateWeatherRepository;

    @InjectMocks
    private DiaryService diaryService;

    @Test
    @DisplayName("다이어리 생성 테스트")
    void createDiary() throws Exception{
        //given
        LocalDate testDate = LocalDate.parse("2023-05-22");
        Diary diary = Diary.builder()
                .date(testDate)
                .text("일기 내용")
                .build();

        List<DateWeather> list = new ArrayList<>();
        list.add(DateWeather.builder()
                .date(testDate)
                .weather("temp")
                .icon("temp")
                .temperature(0.0d)
                .build()
        );

        given(dateWeatherRepository.findAllByDate(any(LocalDate.class)))
                .willReturn(list);
        given(diaryRepository.save(any(Diary.class))).willReturn(diary);

        //when
        Diary createdDiary = diaryService.createDiary(testDate, "일기 내용");

        //then
        assertThat(createdDiary.getDate()).isEqualTo(testDate);
        assertThat(createdDiary.getText()).isEqualTo("일기 내용");
    }

    @Test
    @DisplayName("해당 날짜에 작성된 모든 일기데이터 가져오기")
    void readDiary() throws Exception {
        //given
        LocalDate localDate = LocalDate.parse("2023-05-22");
        Diary diary1 = Diary.builder()
                .date(localDate)
                .build();
        Diary diary2 = Diary.builder()
                .date(localDate)
                .build();
        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1); diaryList.add(diary2);

        given(diaryRepository.findAllByDate(any(LocalDate.class)))
                .willReturn(diaryList);

        //when
        List<Diary> foundDiaries = diaryService.readDiary(localDate);

        //then
        assertThat(foundDiaries.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("해당 두 날짜 사이에 작성된 모든 일기데이터 가져오기")
    void readDiaries(){
        //given
        LocalDate startDate = LocalDate.parse("2023-05-22");
        LocalDate endDate = LocalDate.parse("2023-05-24");

        Diary diary1 = Diary.builder()
                .date(startDate.plusDays(1))
                .text("일기 내용 1")
                .build();

        Diary diary2 = Diary.builder()
                .date(startDate.plusDays(2))
                .text("일기 내용 2")
                .build();

        List<Diary> expectedDiaries = Arrays.asList(diary1,diary2);

        given(diaryRepository.findAllByDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(expectedDiaries);

        //when
        List<Diary> actualDiaries = diaryService.readDiaries(startDate, endDate);

        assertThat(actualDiaries).isEqualTo(expectedDiaries);
    }

    @Test
    @DisplayName("기존에 작성한 일기 내용 수정")
    void updateDiary() throws Exception{
        //given
        LocalDate testDate = LocalDate.parse("2023-05-22");
        Diary originalDiary = Diary.builder()
                .date(testDate)
                .text("원래 일기 내용")
                .build();

        Diary updatedDiary = Diary.builder()
                .date(testDate)
                .text("수정된 일기 내용")
                .build();

        given(diaryRepository.getFirstByDate(any())).willReturn(originalDiary);
        given(diaryRepository.save(any(Diary.class))).willReturn(updatedDiary);

        //when
        Diary resultDiary = diaryService.updateDiary(testDate, "수정된 일기 내용");

        //then
        assertThat(resultDiary.getText()).isEqualTo(updatedDiary.getText());
    }

    @Test
    @DisplayName("기존에 작성한 일기 내용 삭제")
    void deleteDiary() throws Exception{
        //given
        LocalDate testDate = LocalDate.parse("2023-05-22");

        doNothing().when(diaryRepository).deleteAllByDate(any(LocalDate.class));

        //when
        diaryService.deleteDiary(testDate);

        //then
        verify(diaryRepository,times(1)).deleteAllByDate(any(LocalDate.class));
    }


}