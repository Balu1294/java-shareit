package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestItem {

    Integer userId;
    int from;
    int size;
    String text;

    public static RequestItem of(Integer userId, int from, int size) {
        RequestItem item = new RequestItem();
        item.setUserId(userId);
        item.setFrom(from);
        item.setSize(size);

        return item;
    }

    public static RequestItem of(Integer userId, int from, int size, String text) {
        RequestItem item = new RequestItem();
        item.setUserId(userId);
        item.setFrom(from);
        item.setSize(size);
        item.setText(text);

        return item;
    }
}
