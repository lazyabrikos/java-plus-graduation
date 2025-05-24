package ru.practicum.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.Request;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class RequestMapper {
    public RequestDto mapRequest(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequesterId());
        dto.setEvent(request.getEventId());
        dto.setCreated(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setStatus(request.getStatus());
        return dto;
    }

    public RequestDto mapRequestWithConfirmedAndRejected(List<RequestDto> confirmedRequests,
                                                         List<RequestDto> rejectedRequests) {
        RequestDto result = new RequestDto();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }
}