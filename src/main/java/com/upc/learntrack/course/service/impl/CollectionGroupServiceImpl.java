package com.upc.learntrack.course.service.impl;

import com.upc.learntrack.course.dto.CollectionGroupDto;
import com.upc.learntrack.course.dto.LinkCollectionGroupRequest;
import com.upc.learntrack.course.exception.CollectionGroupNotFoundException;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.exception.LearningCollectionNotFoundException;
import com.upc.learntrack.course.mapper.CollectionGroupMapper;
import com.upc.learntrack.course.model.CollectionGroup;
import com.upc.learntrack.course.model.CollectionGroupId;
import com.upc.learntrack.course.model.Group;
import com.upc.learntrack.course.model.LearningCollection;
import com.upc.learntrack.course.repository.CollectionGroupRepository;
import com.upc.learntrack.course.repository.GroupRepository;
import com.upc.learntrack.course.repository.LearningCollectionRepository;
import com.upc.learntrack.course.service.CollectionGroupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionGroupServiceImpl implements CollectionGroupService {

    private final CollectionGroupRepository collectionGroupRepository;
    private final LearningCollectionRepository learningCollectionRepository;
    private final GroupRepository groupRepository;
    private final CollectionGroupMapper collectionGroupMapper;

    @Override
    public List<CollectionGroupDto> findAllByGroup(Long groupId) {
        return collectionGroupRepository.findAllByIdGroupId(groupId).stream()
                .map(collectionGroupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void linkByName(LinkCollectionGroupRequest request) {
        LearningCollection collection = learningCollectionRepository.findByName(request.getCollectionName())
                .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + request.getCollectionName()));

        Group group = groupRepository.findByName(request.getGroupName())
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado: " + request.getGroupName()));

        CollectionGroupId compositeKey = new CollectionGroupId(collection.getId(), group.getId());
        if (collectionGroupRepository.existsById(compositeKey)) {
            throw new IllegalArgumentException("La colección ya está vinculada a este grupo.");
        }

        CollectionGroup collectionGroup = new CollectionGroup();
        collectionGroup.setId(compositeKey);
        collectionGroup.setLearningCollection(collection);
        collectionGroup.setGroup(group);

        collectionGroupRepository.save(collectionGroup);
    }

    @Override
    public void delete(Long collectionId, Long groupId) {
        CollectionGroupId compositeKey = new CollectionGroupId(collectionId, groupId);

        if (!collectionGroupRepository.existsById(compositeKey)) {
            throw new CollectionGroupNotFoundException("La relación no existe.");
        }

        collectionGroupRepository.deleteById(compositeKey);
    }

    @Override
    @Transactional
    public void moveGroup(Long groupId, Long targetCollectionId) {
        // Buscar la vinculación actual del grupo
        List<CollectionGroup> currentLinks = collectionGroupRepository.findAllByIdGroupId(groupId);
        if (currentLinks.isEmpty()) {
            throw new CollectionGroupNotFoundException("El grupo no está vinculado a ninguna colección");
        }
        if (currentLinks.size() > 1) {
            throw new IllegalStateException("El grupo tiene múltiples vinculaciones. No se puede mover.");
        }

        CollectionGroup current = currentLinks.get(0);

        // Buscar la colección destino
        LearningCollection targetCollection = learningCollectionRepository.findById(targetCollectionId)
                .orElseThrow(() -> new LearningCollectionNotFoundException("Colección destino no encontrada"));

        // Eliminar la vinculación actual
        collectionGroupRepository.delete(current);

        // Crear la nueva vinculación
        CollectionGroupId newId = new CollectionGroupId(targetCollectionId, groupId);
        CollectionGroup newLink = new CollectionGroup();
        newLink.setId(newId);
        newLink.setLearningCollection(targetCollection);
        newLink.setGroup(current.getGroup());

        collectionGroupRepository.save(newLink);
    }
}