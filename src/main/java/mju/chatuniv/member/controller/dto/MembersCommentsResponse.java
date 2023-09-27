package mju.chatuniv.member.controller.dto;

import mju.chatuniv.comment.domain.dto.MembersCommentResponse;

import java.util.List;

public class MembersCommentsResponse {

    private List<MembersCommentResponse> membersCommentResponses;

    private MembersCommentsResponse(){
    }

    private MembersCommentsResponse(final List<MembersCommentResponse>membersCommentResponses) {
        this.membersCommentResponses = membersCommentResponses;
    }

    public static MembersCommentsResponse from(final List<MembersCommentResponse> membersCommentResponses) {
        return new MembersCommentsResponse(membersCommentResponses);
    }

    public List<MembersCommentResponse> getMembersCommentResponses() {
        return membersCommentResponses;
    }
}
