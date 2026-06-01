package org.mss301.identityservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.identityservice.dto.request.MembershipRankFilter;
import org.mss301.identityservice.dto.request.MembershipRankRequest;
import org.mss301.identityservice.dto.response.MembershipRankResponse;
import org.mss301.identityservice.service.MembershipRankService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membership-ranks")
@RequiredArgsConstructor
public class MembershipRankController {

    private final MembershipRankService membershipRankService;

    @PostMapping
    public ResponseEntity<MembershipRankResponse> createRank(
            @Valid @RequestBody MembershipRankRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipRankService.createMembershipRank(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MembershipRankResponse>> getRanks(
            @ParameterObject @ModelAttribute MembershipRankFilter filter) {
        return ResponseEntity.ok(membershipRankService.getMembershipRanks(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipRankResponse> getRank(@PathVariable Long id) {
        MembershipRankResponse response = membershipRankService.getRankById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembershipRankResponse> updateRank(
            @PathVariable Long id,
            @Valid @RequestBody MembershipRankRequest request
    ) {
        MembershipRankResponse response = membershipRankService.updateRank(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRank(@PathVariable Long id) {
        membershipRankService.deleteRank(id);
        return ResponseEntity.ok("Xóa hạng thành viên thành công");
    }
}
