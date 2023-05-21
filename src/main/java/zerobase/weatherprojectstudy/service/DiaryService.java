package zerobase.weatherprojectstudy.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weatherprojectstudy.domain.Diary;
import zerobase.weatherprojectstudy.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    @Value("${openweathermap.key}")
    private String apiKey;

    public void createDiary(LocalDate date, String text){
        //open weather map에서 날씨 데이터 가져옴
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        // 파싱된 데이터 + 일기 값 우리 db에 넣기
        saveDiary(date, text, parsedWeather);
    }

    public List<Diary> readDiary(LocalDate date){
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate){
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    private void saveDiary(LocalDate date, String text, Map<String, Object> parsedWeather) {
        Diary nowDiary = Diary.builder()
                .weather(parsedWeather.get("main").toString())
                .icon(parsedWeather.get("icon").toString())
                .temperature((double) parsedWeather.get("temp"))
                .text(text)
                .date(date)
                .build();
        diaryRepository.save(nowDiary);
    }

    public void updateDiary(LocalDate date, String text) {
        // 날짜중 첫번째 일기를 수정하는 것이라 가정해보자
        Diary firstByDate = diaryRepository.getFirstByDate(date);
        firstByDate.setText(text);

        // 이 경우엔 Id값을 그대로 둔 후, 덮어씌우게 됨
        diaryRepository.save(firstByDate);
    }

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
