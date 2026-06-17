package com.shreya.farmflow_api.service;

import com.shreya.farmflow_api.dto.WeatherDataResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public WeatherService(RestTemplate restTemplate,
                          @Value("${weather.api.key}") String apiKey,
                          @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/forecast}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public List<WeatherDataResponse> getWeather(double lat, double lon) {
        String url = String.format(
                "%s?lat=%s&lon=%s&units=metric&appid=%s",
                apiUrl,
                URLEncoder.encode(String.valueOf(lat), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(lon), StandardCharsets.UTF_8),
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
        );

        URI uri = URI.create(url);
        OpenWeatherForecastResponse response = restTemplate.getForObject(uri, OpenWeatherForecastResponse.class);
        if (response == null || response.list() == null) {
            throw new IllegalStateException("Unable to fetch weather data from provider.");
        }

        Map<LocalDate, OpenWeatherItem> forecastByDate = new LinkedHashMap<>();
        for (OpenWeatherItem item : response.list()) {
            LocalDate date = Instant.ofEpochSecond(item.dt()).atZone(ZoneOffset.UTC).toLocalDate();
            if (!forecastByDate.containsKey(date)) {
                forecastByDate.put(date, item);
            } else {
                OpenWeatherItem current = forecastByDate.get(date);
                int targetHour = 12;
                int itemHour = Instant.ofEpochSecond(item.dt()).atZone(ZoneOffset.UTC).getHour();
                int currentHour = Instant.ofEpochSecond(current.dt()).atZone(ZoneOffset.UTC).getHour();
                if (Math.abs(itemHour - targetHour) < Math.abs(currentHour - targetHour)) {
                    forecastByDate.put(date, item);
                }
            }
        }

        return forecastByDate.values().stream()
                .limit(5)
                .map(this::toWeatherDataResponse)
                .collect(Collectors.toList());
    }

    private WeatherDataResponse toWeatherDataResponse(OpenWeatherItem item) {
        Main main = item.main();
        WeatherDescription desc = item.weather() != null && !item.weather().isEmpty() ? item.weather().get(0) : new WeatherDescription("", "", "");
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(item.dt()), ZoneOffset.UTC);
        return new WeatherDataResponse(
                dateTime.toLocalDate(),
                main.temp(),
                (int) Math.round(main.temp()),
                main.humidity(),
                item.wind() != null ? item.wind().speed() : 0,
                item.pop(),
                desc.main(),
                desc.description(),
                dateTime
        );
    }

    private static record OpenWeatherForecastResponse(@JsonProperty("list") List<OpenWeatherItem> list) {
    }

    private static record OpenWeatherItem(long dt, Main main, Wind wind, List<WeatherDescription> weather, double pop) {
    }

    private static record Main(double temp, int humidity) {
    }

    private static record Wind(double speed) {
    }

    private static record WeatherDescription(String main, String description, String icon) {
    }
}
