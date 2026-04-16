create table if not exists movies (
    movie_id uuid primary key,
    title varchar(255) not null
);

create table if not exists theaters(
    theater_id uuid primary key,
    open_time time not null,
    close_time time not null
);

create table if not exists screenings(
    screening_id uuid primary key,
    movie_id uuid not null,
    theater_id uuid not null,
    screening_date date not null,
    start_time time not null,
    end_time time not null,
    foreign key (movie_id) references movies(movie_id),
    foreign key (theater_id) references theaters(theater_id)
);

create table if not exists seats(
    seats_id uuid primary key,
    screening_id uuid not null,
    seat_number varchar(10) not null,
    foreign key (screening_id) references screenings(screening_id)
    unique(screening_id,seat_number)
);
