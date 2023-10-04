package mju.chatuniv.chat.controller.dto;

import mju.chatuniv.chat.infrastructure.repository.dto.ChatRoomSimpleResponse;

import java.util.List;

public class ChatRoomsSimpleResponse {

    private final List<ChatRoomSimpleResponse> chats;

    private ChatRoomsSimpleResponse(final List<ChatRoomSimpleResponse> chats) {
        this.chats = chats;
    }

    public static ChatRoomsSimpleResponse from(final List<ChatRoomSimpleResponse> chats) {
        return new ChatRoomsSimpleResponse(chats);
    }

    public List<ChatRoomSimpleResponse> getChats() {
        return chats;
    }
}
