package zerobase.weatherprojectstudy.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zerobase.weatherprojectstudy.domain.Diary;
import zerobase.weatherprojectstudy.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @ApiOperation(value = "일기 텍스트와 날짜를 이용해서 DB에 일기 저장")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "일기를 작성할 날짜입니다.", example = "2020-02-02")LocalDate date,
                     @RequestBody @ApiParam(value = "일기 내용을 작성해주세요.") String text){
        diaryService.createDiary(date, text);
    }

    @ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회 일자", example = "2020-02-02")LocalDate date){
        return diaryService.readDiary(date);
    }

    @ApiOperation("선택한 기간중의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 첫번째날 입니다.", example = "2020-02-02")LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 마지막날 입니다.", example = "2020-02-02")LocalDate endDate
                            ){
        return diaryService.readDiaries(startDate,endDate);
    }

    @ApiOperation("기존의 작성한 일기 내용을 수정합니다.")
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "해당 일기 작성 일자", example = "2020-02-02") LocalDate date,
                     @RequestBody @ApiParam(value = "여기에 적은 text를 바탕으로 일기 내용이 수정됩니다.") String text ){
        diaryService.updateDiary(date, text);
    }

    @ApiOperation("기존에 작성한 일기를 제거합니다.")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "해당 일기 작성 일자", example = "2020-02-02") LocalDate date){
        diaryService.deleteDiary(date);
    }
}
