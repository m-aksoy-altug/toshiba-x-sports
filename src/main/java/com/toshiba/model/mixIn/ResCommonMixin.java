package com.toshiba.model.mixIn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ResCommonMixin {
	@JsonProperty("Status") 
	private int Status;

	@JsonProperty("ErrMessage") 
	private String ErrMessage;
}
