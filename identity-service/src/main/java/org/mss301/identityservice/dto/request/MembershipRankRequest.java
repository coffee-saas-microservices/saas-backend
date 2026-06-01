package org.mss301.identityservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.mss301.identityservice.entity.enumeration.MembershipRankStatus;

@Data
public class MembershipRankRequest {
    @NotBlank(message = "Tên hạng thành viên không được để trống")
    private String rankName;
    private Float pointRate;
    private Integer requiredPoints;
    private MembershipRankStatus status;
}
