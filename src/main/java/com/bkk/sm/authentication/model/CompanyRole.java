package com.bkk.sm.authentication.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class CompanyRole implements Serializable {
    @Serial
    private static final long serialVersionUID = 2405172041950251807L;

    private String companyCode;
    private String companyName;
    private Role role;
}
