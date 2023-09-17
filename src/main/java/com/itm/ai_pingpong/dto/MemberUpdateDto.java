package com.itm.ai_pingpong.dto;

import java.util.Optional;

public record MemberUpdateDto(Optional<String> mail, Optional<String> name, Optional<String> tel
) {

}

