package it.tinna.smartdoc.shared.dto.job;

import com.google.gson.annotations.Expose;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigurazioneJobDto
{

    @Expose
    private String   cronExpression;

    @Expose
    private JobNames jobName;

}

