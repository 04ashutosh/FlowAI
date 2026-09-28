CREATE TABLE destinations (
                              id UUID PRIMARY KEY,
                              user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              name VARCHAR(255) NOT NULL,
                              type VARCHAR(50) NOT NULL,
                              host VARCHAR(255) NOT NULL,
                              port INTEGER NOT NULL,
                              database_name VARCHAR(255) NOT NULL,
                              username VARCHAR(255) NOT NULL,
                              password VARCHAR(255) NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              UNIQUE(user_id, name)
);

CREATE INDEX idx_destinations_user_id ON destinations(user_id);