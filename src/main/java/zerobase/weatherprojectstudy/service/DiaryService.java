package zerobase.weatherprojectstudy.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weatherprojectstudy.WeatherProjectStudyApplication;
import zerobase.weatherprojectstudy.domain.DateWeather;
import zerobase.weatherprojectstudy.domain.Diary;
import zerobase.weatherprojectstudy.repository.DateWeatherRepository;
import zerobase.weatherprojectstudy.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherProjectStudyApplication.class);
    @Value("${openweathermap.key}")
    private String apiKey;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi(){
        String weatherData = getWeatherString();
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        DateWeather dateWeather = DateWeather.builder()
                .date(LocalDate.now())
                .weather(parsedWeather.get("main").toString())
                .icon(parsedWeather.get("icon").toString())
                .temperature((double)parsedWeather.get("temp"))
                .build();
        return dateWeather;
    }


    @Transactional(readOnly = false)
    public void createDiary(LocalDate date, String text){
//        //open weather map에서 날씨 데이터 가져옴
//        String weatherData = getWeatherString();
//
//        // 받아온 날씨 json 파싱하기
//        Map<String, Object> parsedWeather = parseWeather(weatherData);
        logger.info("started to create diary");

        // 날씨 데이터 가져오기(API 가 아닌, DB에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        // 파싱된 데이터 + 일기 값 우리 db에 넣기
        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText(text);
        diaryRepository.save(diary);
        logger.info("end to create diary");
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0){
            // 새로 api에서 날씨 정보를 가져와야 함
            return getWeatherFromApi();
        }else{
            return dateWeatherListFromDB.get(0);
        }
    }

    public List<Diary> readDiary(LocalDate date){
        logger.debug("read diary");
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate){
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = false)
    public void saveDiary(LocalDate date, String text, Map<String, Object> parsedWeather) {
        Diary nowDiary = Diary.builder()
                .weather(parsedWeather.get("main").toString())
                .icon(parsedWeather.get("icon").toString())
                .temperature((double) parsedWeather.get("temp"))
                .text(text)
                .date(date)
                .build();
        diaryRepository.save(nowDiary);
    }

    @Transactional(readOnly = false)
    public void updateDiary(LocalDate date, String text) {
        // 날짜중 첫번째 일기를 수정하는 것이라 가정해보자
        Diary firstByDate = diaryRepository.getFirstByDate(date);
        firstByDate.setText(text);

        // 이 경우엔 Id값을 그대로 둔 후, 덮어씌우게 됨
        diaryRepository.save(firstByDate);
    }

    @Transactional(readOnly = false)
    public void deleteDiary(LocalDate date){
        diaryRepository.deleteAllByDate(date);
    }

    private String getWeatherString(){
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            HttpURLConnection connection = createConnection(apiUrl);
            return getResponse(connection);
        }catch(Exception e){
            return "failed to get response";
        }
    }

    private String getResponse(HttpURLConnection connection) throws IOException{
        // 응답 코드 받아옴
        int responseCode = connection.getResponseCode();
        BufferedReader br;
        if(responseCode == 200){
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }else{
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = br.readLine()) != null){
            response.append(inputLine);
        }
        br.close();

        return response.toString();
    }

    private HttpURLConnection createConnection(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        }catch(ParseException e){
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        //weather 안에 [] 형식, 중괄호 X
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }


}
