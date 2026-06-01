package org.mss301.identityservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.identityservice.entity.enumeration.MembershipRankStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRankResponse {
    private Long id;
    private String rankName;
    private Float pointRate;
    private Integer requiredPoints;
    private MembershipRankStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
