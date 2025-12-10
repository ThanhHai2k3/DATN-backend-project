package com.backend.skillservice.service.impl;

import com.backend.skillservice.dto.request.CreateSkillRequest;
import com.backend.skillservice.dto.request.UpdateSkillRequest;
import com.backend.skillservice.dto.response.SkillResponse;
import com.backend.skillservice.entity.Skill;
import com.backend.skillservice.entity.SkillCategory;
import com.backend.skillservice.enums.ErrorCode;
import com.backend.skillservice.exception.AppException;
import com.backend.skillservice.mapper.SkillCategoryMapper;
import com.backend.skillservice.mapper.SkillMapper;
import com.backend.skillservice.repository.SkillCategoryRepository;
import com.backend.skillservice.repository.SkillRepository;
import com.backend.skillservice.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillCategoryRepository categoryRepository;
    private final SkillMapper skillMapper;
    private final SkillCategoryMapper categoryMapper;

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public SkillResponse createSkill(CreateSkillRequest request){
        if(skillRepository.findByNameIgnoreCase(request.getName()).isPresent()){
            throw new AppException(ErrorCode.SKILL_ALREADY_EXIST);
        }

        SkillCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Skill skill = skillMapper.toEntity(request);
        skill.setCategory(category);

        Skill saved = skillRepository.save(skill);
        return skillMapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public SkillResponse updateSkill(UUID id, UpdateSkillRequest request){
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND));

        if (request.getName() != null &&
                skillRepository.findByNameIgnoreCase(request.getName())
                        .filter(s -> !s.getId().equals(id))
                        .isPresent()) {
            throw new AppException(ErrorCode.SKILL_ALREADY_EXIST);
        }

        skillMapper.updateEntityFromDto(request, skill);

        if (request.getCategoryId() != null) {
            SkillCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            skill.setCategory(category);
        }

        skillRepository.save(skill);
        return skillMapper.toResponse(skill);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void deleteSkill(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND));

        skillRepository.delete(skill);
    }

    @Override
    public SkillResponse getSkill(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND));
        return skillMapper.toResponse(skill);
    }

    @Override
    public List<SkillResponse> searchSkills(String keyword) {
        return skillRepository.search(keyword)
                .stream()
                .map(skillMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillResponse> getSkillsByCategory(UUID categoryId) {
        if(!categoryRepository.existsById(categoryId)){
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return skillRepository.findByCategoryId(categoryId)
                .stream()
                .map(skillMapper::toResponse)
                .collect(Collectors.toList());
    }
}
