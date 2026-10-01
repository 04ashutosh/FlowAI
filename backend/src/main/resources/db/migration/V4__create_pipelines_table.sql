CREATE TABLE pipelines (
                           id UUID PRIMARY KEY,
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           source_id UUID NOT NULL REFERENCES sources(id) ON DELETE RESTRICT,
                           destination_id UUID NOT NULL REFERENCES destinations(id) ON DELETE RESTRICT,
                           name VARCHAR(255) NOT NULL,
                           description TEXT,
                           status VARCHAR(50) NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           UNIQUE(user_id, name)
);

CREATE INDEX idx_pipelines_user_id ON pipelines(user_id);