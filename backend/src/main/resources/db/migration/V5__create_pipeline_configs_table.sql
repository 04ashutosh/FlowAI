CREATE TABLE pipeline_configs (
                                  id UUID PRIMARY KEY,
                                  pipeline_id UUID NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
                                  mapping_json TEXT NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                  UNIQUE(pipeline_id)
);