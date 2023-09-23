package mju.chatuniv.member.controller.dto;

import mju.chatuniv.chat.domain.chat.Chat;

import java.util.List;

public class MembersChatRoomResponse {

    private List<Chat> myChats;

    private MembersChatRoomResponse(){
    }

    private MembersChatRoomResponse(final List<Chat> myChats) {
        this.myChats = myChats;
    }

    public static MembersChatRoomResponse from(final List<Chat> myChats) {
        return new MembersChatRoomResponse((myChats));
    }
}
