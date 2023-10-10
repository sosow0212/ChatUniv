create table if not exists MEMBER
(
    id       bigint auto_increment
        primary key,
    email    varchar(200) not null,
    password varchar(255) not null,
    constraint UK_c289a9yd2r43ketljjwtt16w7
        unique (email)
);

create table if not exists BOARD
(
    id         bigint auto_increment
        primary key,
    createdAt  datetime    not null,
    modifiedAt datetime    not null,
    content    longtext    not null,
    title      varchar(50) not null,
    member_id  bigint      null,
    constraint FKe8tvf1ogratr7q9gmxigybn7g
        foreign key (member_id) references MEMBER (id)
            on delete cascade
);

create table if not exists COMMENT
(
    DTYPE      varchar(31)  not null,
    id         bigint auto_increment
        primary key,
    createdAt  datetime     not null,
    modifiedAt datetime     not null,
    content    varchar(500) not null,
    member_id  bigint       null,
    constraint FKrsiylx1pb16nh11306rpxmtcb
        foreign key (member_id) references MEMBER (id)
            on delete cascade
);

create table if not exists BOARD_COMMENT
(
    id       bigint not null
        primary key,
    board_id bigint null,
    constraint FKas7ma4cq9icfp9hka2ohqmi5e
        foreign key (id) references COMMENT (id),
    constraint FKrd2sa0ti5fi256h4capinuwcx
        foreign key (board_id) references BOARD (id)
            on delete cascade
);

create table if not exists CHAT
(
    chat_id    bigint auto_increment
        primary key,
    createdAt  datetime not null,
    modifiedAt datetime not null,
    member_id  bigint   null,
    constraint FKtko756cg9exnbfbeq64wmfj6e
        foreign key (member_id) references MEMBER (id)
            on delete cascade
);

create table if not exists CONVERSATION
(
    conversation_id bigint auto_increment
        primary key,
    createdAt       datetime not null,
    modifiedAt      datetime not null,
    answer          longtext not null,
    ask             longtext not null,
    chat_chat_id    bigint   null,
    constraint FKhhyg9dvnfenl5b4pbapgx6h3l
        foreign key (chat_chat_id) references CHAT (chat_id)
            on delete cascade
);

create table if not exists CONVERSATION_COMMENT
(
    id              bigint not null
        primary key,
    conversation_id bigint null,
    constraint FKae267lp57dsw01flbr57brdd1
        foreign key (id) references COMMENT (id),
    constraint FKfn2j7pftqjpqgy52yqys0hvw3
        foreign key (conversation_id) references CONVERSATION (conversation_id)
            on delete cascade
);

create table if not exists WORD
(
    word_id         bigint auto_increment
        primary key,
    createdAt       datetime     not null,
    modifiedAt      datetime     not null,
    frequency       int          not null,
    total_frequency int          not null,
    word            varchar(255) not null,
    constraint UK_8agpej4c44wg0yhrhkd7enkdk
        unique (word)
);
