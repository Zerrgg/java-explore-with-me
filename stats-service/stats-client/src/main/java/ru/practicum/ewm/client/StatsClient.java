package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.practicum.ewm.dto.GlobalConstants.*;

@Service
public class StatsClient extends BaseClient {
    private final String serverUrl;

    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        super(new RestTemplate());
        this.serverUrl = serverUrl;
    }

        public void addHit(EndpointHitDto endpointHit) {
            HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHit);
            rest.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, Object.class);
        }

    public List<ViewStatsDto> getStats(String start,
                                    String end,
                                    boolean unique,
                                    String[] uris) {

        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "unique", unique,
                "uris", uris
        );
        String path = serverUrl + "/stats" + "?start={start}&end={end}&uris={uris}&unique={unique}";


        ResponseEntity<List<ViewStatsDto>> serverResponse = rest.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        }, parameters);
        return serverResponse.getBody();
    }
}

