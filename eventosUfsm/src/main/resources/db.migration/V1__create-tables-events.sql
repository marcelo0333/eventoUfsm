CREATE TABLE events (
                        event_id bigint,
                        image_event varchar(255),
                        type_event varchar(255),
                        description varchar(255),
                        name_event varchar(255),
                        date_initial timestamp,
                        date_final timestamp,
                        fk_user_user_id bigint,
                        data_publish timestamp,
                        PRIMARY KEY (event_id)
);