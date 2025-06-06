package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.dto.compilation.annotation.Create;
import ru.practicum.dto.compilation.annotation.Update;

import java.util.List;

@Data
public class CompilationRequestDto {
    @Size(min = 1, max = 50, groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
