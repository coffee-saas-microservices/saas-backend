package org.mss301.commonservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageMeta {
    int currentPage;
    int size;
    int lastPage;
    long totalElements;
}
