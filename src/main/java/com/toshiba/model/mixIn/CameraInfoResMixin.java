package com.toshiba.model.mixIn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.toshiba.model.DeviceInfo;

@JacksonXmlRootElement(localName = "Response") // Maps to <Response> in XML
public abstract class CameraInfoResMixin {
	@JsonProperty("DevInfo") // Maps to <DevInfo> in XML
    private DeviceInfo devInfo;
}
