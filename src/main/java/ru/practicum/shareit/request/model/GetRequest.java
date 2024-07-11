package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetRequest {
    Integer userId;
    int from;
    int size;

    public static GetRequest of(Integer userId, int from, int size) {
        GetRequest request = new GetRequest();
        request.setUserId(userId);
        request.setFrom(from);
        request.setSize(size);

        return request;
    }
}
