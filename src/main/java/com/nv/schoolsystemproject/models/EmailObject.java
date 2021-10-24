package com.nv.schoolsystemproject.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class EmailObject {

	private String to;
	private String subject;
	private String text;
}
