package ru.practicum.shareit.request.model;

import lombok.Data;

@Data
public class GetRequest {
    private Integer userId;
    private int from;
    private int size;

    public static GetRequest of(Integer userId, int from, int size) {
        GetRequest request = new GetRequest();
        request.setUserId(userId);
        request.setFrom(from);
        request.setSize(size);

        return request;
    }
}
