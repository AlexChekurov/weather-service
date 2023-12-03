CREATE SEQUENCE IF NOT EXISTS weather_record_id_sec;
CREATE TABLE IF NOT EXISTS
weather (
   id bigint PRIMARY KEY DEFAULT nextval('weather_record_id_sec'),
   temperature REAL NOT NULL,
   wind_speed REAL NOT NULL DEFAULT 0,
   atmosphere_pressure REAL NOT NULL,
   humidity int2 NOT NULL,
   weather_conditions VARCHAR(50) NOT NULL,
   location VARCHAR(255) NOT NULL,
   last_update_epoch timestamp NOT NULL
);
ALTER TABLE weather ADD CONSTRAINT location_last_update_epoch_u1 UNIQUE (location, last_update_epoch);
