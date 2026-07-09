CREATE TABLE session_logs (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    campaign_id BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    title       VARCHAR(100) NOT NULL,
    body        VARCHAR(10000) NOT NULL DEFAULT '',
    created_on  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_session_logs_campaign ON session_logs(campaign_id);
CREATE INDEX idx_session_logs_created_on ON session_logs(created_on DESC);
