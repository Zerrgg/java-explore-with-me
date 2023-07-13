package ru.practicum.stats_client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Slf4j
@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findBookingsByUser(Long userId, State state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> createBooking(Long userId, BookingRequestDTO requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> findBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }


    public ResponseEntity<Object> findBookingsByItemsOwner(Long userId, State state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
}
