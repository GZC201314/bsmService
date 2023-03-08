package org.bsm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurUser {
    private String userid;
    private String username;
    private boolean isFaceValid;
    private String token;
    private String role;
    private String emailaddress;
}
