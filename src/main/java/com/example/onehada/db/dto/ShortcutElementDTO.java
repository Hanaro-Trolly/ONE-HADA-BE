package com.example.onehada.db.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortcutElementDTO {
	@JsonProperty("type")
	String type;

	@JsonProperty("myAccount")
	String myAccount;

	@JsonProperty("receiverAccount")
	String receiverAccount;

	@JsonProperty("amount")
	String amount;

	@JsonProperty("period")
	String period;

	@JsonProperty("transferType")
	String transferType;

	@JsonProperty("searchWord")
	String searchWord;
}
