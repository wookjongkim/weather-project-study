package zerobase.weatherprojectstudy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import zerobase.weatherprojectstudy.domain.Diary;
import zerobase.weatherprojectstudy.service.DiaryService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class DiaryControllerTest {

    @Mock
    private DiaryService diaryService;

    @InjectMocks
    private DiaryController diaryController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(diaryController).build();
    }

    @Test
    @DisplayName("다이어리 생성 테스트")
    void createDiary() throws Exception{
        //given
        LocalDate testDate = LocalDate.parse("2023-05-22");
        Diary diary = Diary.builder()
                .date(testDate)
                .text("일기 내용")
                .build();

        given(diaryService.createDiary(any(LocalDate.class), any(String.class))).willReturn(diary);


        //when,then
        mockMvc.perform(post("/create/diary")
                .contentType(MediaType.TEXT_PLAIN)
                .param("date", "2023-05-22")
                .content("일기 내용"))
                .andExpect(status().isOk())
                .andDo(print());
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

        given(diaryService.readDiary(any(LocalDate.class))).willReturn(diaryList);


        // when, then
        mockMvc.perform(get("/read/diary")
                .param("date","2023-05-21")) // 이 날짜는 당연히 영향이 없음
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("해당 두 날짜 사이에 작성된 모든 일기데이터 가져오기")
    void readDiaries() throws Exception {
        LocalDate localDate = LocalDate.parse("2023-03-22");
        Diary diary1 = Diary.builder()
                .date(localDate)
                .build();
        Diary diary2 = Diary.builder()
                .date(localDate)
                .build();
        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary1); diaryList.add(diary2);

        given(diaryService.readDiaries(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(diaryList);

        mockMvc.perform(get("/read/diaries")
                .param("startDate","2022-03-03")
                .param("endDate", "2022-04-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("기존에 작성한 일기 내용 수정")
    void updateDiary() throws Exception{
        LocalDate testDate = LocalDate.parse("2023-05-22");
        Diary diary = Diary.builder()
                .date(testDate)
                .text("수정된 일기 내용")
                .build();

        given(diaryService.updateDiary(any(LocalDate.class), any(String.class))).willReturn(diary);

        //when,then
        mockMvc.perform(put("/update/diary")
                        .contentType(MediaType.TEXT_PLAIN)
                        .param("date", "2023-05-22")
                        .content("기존 일기 내용"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("기존에 작성한 일기 내용 삭제")
    void deleteDiary() throws Exception{
        //given
        // 일기를 삭제하라는 요청에 대해, deleteDiary 메서드가 호출되는지 검증
        doNothing().when(diaryService).deleteDiary(any(LocalDate.class));
        // when, then
        mockMvc.perform(delete("/delete/diary")
                .param("date", "2023-05-22"))
                .andExpect(status().isOk())
                .andDo(print());

        // delete Diary 메서드가 한번 호출되었는지 검증
        verify(diaryService, times(1)).deleteDiary(any(LocalDate.class));
    }
}