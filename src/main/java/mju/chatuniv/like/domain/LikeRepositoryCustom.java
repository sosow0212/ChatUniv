package mju.chatuniv.like.domain;

import mju.chatuniv.member.domain.Member;

public interface LikeRepositoryCustom {

    BoardLike findLike(Long boardId, Member member);
}
