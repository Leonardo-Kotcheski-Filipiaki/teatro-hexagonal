package com.teatro.theater.ports.input;

import com.teatro.theater.domain.model.Theater;

public interface CreateTheaterUseCase {
    Theater execute(Theater theater);
}
