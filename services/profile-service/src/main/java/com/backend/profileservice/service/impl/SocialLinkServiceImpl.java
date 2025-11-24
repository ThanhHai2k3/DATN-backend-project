package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.student.social.SocialLinkCreateRequest;
import com.backend.profileservice.dto.request.student.social.SocialLinkUpdateRequest;
import com.backend.profileservice.dto.response.student.SocialLinkResponse;
import com.backend.profileservice.entity.SocialLink;
import com.backend.profileservice.entity.Student;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.SocialLinkMapper;
import com.backend.profileservice.repository.SocialLinkRepository;
import com.backend.profileservice.repository.StudentRepository;
import com.backend.profileservice.service.SocialLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialLinkServiceImpl implements SocialLinkService {

    private final SocialLinkRepository socialLinkRepository;
    private final SocialLinkMapper socialLinkMapper;
    private final StudentRepository studentRepository;

    @Override
    public SocialLinkResponse create(UUID userId, SocialLinkCreateRequest request) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        SocialLink link = socialLinkMapper.toEntity(request);
        link.setStudent(student);

        SocialLink saved = socialLinkRepository.save(link);
        return socialLinkMapper.toResponse(saved);
    }

    @Override
    public SocialLinkResponse update(UUID userId, UUID linkId, SocialLinkUpdateRequest request) {
        SocialLink link = socialLinkRepository.findById(linkId)
                .orElseThrow(() -> new AppException(ErrorCode.SOCIAL_LINK_NOT_FOUND));

        if (!link.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        socialLinkMapper.updateEntity(link, request);

        SocialLink saved = socialLinkRepository.save(link);
        return socialLinkMapper.toResponse(saved);
    }

    @Override
    public List<SocialLinkResponse> getAllByStudent(UUID userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return socialLinkRepository.findByStudentId(student.getId())
                .stream()
                .map(socialLinkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID userId, UUID linkId) {
        SocialLink link = socialLinkRepository.findById(linkId)
                .orElseThrow(() -> new AppException(ErrorCode.SOCIAL_LINK_NOT_FOUND));

        if (!link.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        socialLinkRepository.delete(link);
    }
}
