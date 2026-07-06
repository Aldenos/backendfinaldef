package com.upc.learntrack.course.controller;

import com.upc.learntrack.course.dto.CollectionGroupDto;
import com.upc.learntrack.course.dto.LinkCollectionGroupRequest;
import com.upc.learntrack.course.dto.MoveGroupRequest;
import com.upc.learntrack.course.service.CollectionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/collection-groups")
@RequiredArgsConstructor
public class CollectionGroupController {

    private final CollectionGroupService collectionGroupService;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<CollectionGroupDto>> findAllByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(collectionGroupService.findAllByGroup(groupId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Void> linkCollectionToGroup(@Valid @RequestBody LinkCollectionGroupRequest request) {
        collectionGroupService.linkByName(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Void> delete(@RequestParam Long collectionId, @RequestParam Long groupId) {
        collectionGroupService.delete(collectionId, groupId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/move")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Void> moveGroupToCollection(@RequestBody MoveGroupRequest request) {
        collectionGroupService.moveGroup(request.getGroupId(), request.getTargetCollectionId());
        return ResponseEntity.ok().build();
    }
}