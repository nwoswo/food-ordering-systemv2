CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS orden;
-- Create the outbox_events table
CREATE TABLE orden.outbox_events
(
    id uuid NOT NULL DEFAULT public.uuid_generate_v4(),
    aggregate_id uuid NOT NULL,
    aggregate_type character varying(255) NOT NULL,
    event_type character varying(255) NOT NULL,
    event_data jsonb NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    processed boolean NOT NULL DEFAULT false,
    CONSTRAINT outbox_events_pkey PRIMARY KEY (id)
);



CREATE INDEX "IDX_OUTBOX_AGGREGATE_ID" ON "orden".outbox_events USING btree
    (aggregate_id ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX "IDX_OUTBOX_PROCESSED" ON "orden".outbox_events USING btree
    (processed ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX "IDX_OUTBOX_CREATED_AT" ON "orden".outbox_events USING btree
    (created_at ASC NULLS LAST)
    TABLESPACE pg_default;