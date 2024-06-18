
CREATE TABLE IF NOT EXISTS messages
(
    beginstring       CHAR(8)       NOT NULL,
    sendercompid      VARCHAR(64)   NOT NULL,
    sendersubid       VARCHAR(64)   NOT NULL,
    senderlocid       VARCHAR(64)   NOT NULL,
    targetcompid      VARCHAR(64)   NOT NULL,
    targetsubid       VARCHAR(64)   NOT NULL,
    targetlocid       VARCHAR(64)   NOT NULL,
    session_qualifier VARCHAR(64)   NOT NULL,
    msgseqnum         INT           NOT NULL,
    message           VARCHAR(2048) NOT NULL,
    PRIMARY KEY (beginstring, sendercompid, sendersubid, senderlocid,
                 targetcompid, targetsubid, targetlocid, session_qualifier,
                 msgseqnum)
);

CREATE TABLE IF NOT EXISTS messages_log
(
    id                INT auto_increment PRIMARY KEY,
    time              DATETIME      NOT NULL,
    side              VARCHAR(3),
    msgtype           VARCHAR(8),
    msgseqnum         INT,
    beginstring       CHAR(8)       NOT NULL,
    sendercompid      VARCHAR(64)   NOT NULL,
    sendersubid       VARCHAR(64)   NOT NULL,
    senderlocid       VARCHAR(64)   NOT NULL,
    targetcompid      VARCHAR(64)   NOT NULL,
    targetsubid       VARCHAR(64)   NOT NULL,
    targetlocid       VARCHAR(64)   NOT NULL,
    session_qualifier VARCHAR(64)   NOT NULL,
    text              VARCHAR(2048) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_log
(
    id                INT auto_increment PRIMARY KEY,
    time              DATETIME      NOT NULL,
    sessionid         VARCHAR(256)  NOT NULL,
    type              CHAR(5)       NOT NULL,
    text              VARCHAR(2048) NOT NULL
);

CREATE TABLE IF NOT EXISTS sessions
(
    beginstring       CHAR(8)     NOT NULL,
    sendercompid      VARCHAR(64) NOT NULL,
    sendersubid       VARCHAR(64) NOT NULL,
    senderlocid       VARCHAR(64) NOT NULL,
    targetcompid      VARCHAR(64) NOT NULL,
    targetsubid       VARCHAR(64) NOT NULL,
    targetlocid       VARCHAR(64) NOT NULL,
    session_qualifier VARCHAR(64) NOT NULL,
    creation_time     DATETIME    NOT NULL,
    incoming_seqnum   INT         NOT NULL,
    outgoing_seqnum   INT         NOT NULL,
    PRIMARY KEY (beginstring, sendercompid, sendersubid, senderlocid,
                 targetcompid, targetsubid, targetlocid, session_qualifier)
);
