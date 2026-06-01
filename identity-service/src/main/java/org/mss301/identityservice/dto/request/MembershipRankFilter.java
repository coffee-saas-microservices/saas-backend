package org.mss301.identityservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.identityservice.entity.enumeration.MembershipRankStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipRankFilter extends BaseFilter {

    String keyword;
    MembershipRankStatus status;
}
