package com.teatro.event.ports.input;

import com.teatro.event.domain.model.Theater;

public interface FindByIdTheaterCapacityUseCase {

    Theater execute(Long theaterId);
}
