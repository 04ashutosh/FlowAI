CREATE TABLE dead_letter_queue (
                                   id UUID PRIMARY KEY,
                                   pipeline_id UUID NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
                                   destination_table_name VARCHAR(255) NOT NULL,
                                   data_payload TEXT NOT NULL,
                                   error_message TEXT NOT NULL,
                                   resolved BOOLEAN NOT NULL DEFAULT FALSE,
                                   created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                   updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_dlq_pipeline_id ON dead_letter_queue(pipeline_id);