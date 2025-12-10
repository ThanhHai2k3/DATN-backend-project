package com.backend.skillservice.service.impl;

import com.backend.skillservice.dto.request.CreateCategoryRequest;
import com.backend.skillservice.dto.request.UpdateCategoryRequest;
import com.backend.skillservice.dto.response.SkillCategoryResponse;
import com.backend.skillservice.entity.SkillCategory;
import com.backend.skillservice.enums.ErrorCode;
import com.backend.skillservice.exception.AppException;
import com.backend.skillservice.mapper.SkillCategoryMapper;
import com.backend.skillservice.repository.SkillCategoryRepository;
import com.backend.skillservice.repository.SkillRepository;
import com.backend.skillservice.service.SkillCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillCategoryServiceImpl implements SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;
    private final SkillCategoryMapper skillCategoryMapper;
    private final SkillRepository skillRepository;

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public SkillCategoryResponse createCategory(CreateCategoryRequest request){
        if(skillCategoryRepository.findByNameIgnoreCase(request.getName()).isPresent()){
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXIST);
        }

        SkillCategory entity = skillCategoryMapper.toEntity(request);
        SkillCategory saved = skillCategoryRepository.save(entity);

        return skillCategoryMapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request){
        SkillCategory category = skillCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if(request.getName() != null &&
                skillCategoryRepository.findByNameIgnoreCase(request.getName())
                        .filter(c -> !c.getId().equals(id)).isPresent()){
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXIST);
        }

        skillCategoryMapper.updateEntityFromDto(request, category);

        skillCategoryRepository.save(category);
        return skillCategoryMapper.toResponse(category);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void deleteCategory(UUID id) {
        SkillCategory category = skillCategoryRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        long skillCount = skillRepository.countByCategoryId(id);
        if (skillCount > 0) {
            throw new AppException(ErrorCode.CATEGORY_HAS_SKILLS);
        }

        skillCategoryRepository.delete(category);
    }

    @Override
    public SkillCategoryResponse getCategory(UUID id) {
        SkillCategory category = skillCategoryRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return skillCategoryMapper.toResponse(category);
    }

    @Override
    public List<SkillCategoryResponse> getAllCategories(){
        return skillCategoryRepository.findAll()
                .stream()
                .map(skillCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}
