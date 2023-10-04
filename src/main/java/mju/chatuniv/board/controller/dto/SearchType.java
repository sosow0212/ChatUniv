package mju.chatuniv.board.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SearchType {

    TITLE,
    CONTENT,
    ALL;

    @JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static SearchType fromJson(final String type) {
        return valueOf(type);
    }
}
