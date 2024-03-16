package com.iprody08.inquiryservice.service;

import com.iprody08.inquiryservice.dao.InquiryRepository;
import com.iprody08.inquiryservice.dto.InquiryDto;
import com.iprody08.inquiryservice.dto.mapper.InquiryMapper;
import com.iprody08.inquiryservice.entity.Inquiry;
import com.iprody08.inquiryservice.exception_handlers.NoSuchDtoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InquiryServiceImpl implements InquiryService {
    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;

    public InquiryServiceImpl(final InquiryRepository inquiryRepository, final InquiryMapper inquiryMapper) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryMapper = inquiryMapper;
    }
    @Override
    public List<InquiryDto> findAll() {
        return inquiryRepository.findAll()
                .stream()
                .map(inquiryMapper::inquiryToInquiryDto)
                .collect(Collectors.toList());
    }

    @Override
    public void save(InquiryDto inquiryDto) {
        final Inquiry inquiry = inquiryMapper.inquiryDtoToInquiry(inquiryDto);
        inquiryRepository.save(inquiry);
    }

    @Override
    public InquiryDto findById(long id) {
        return  inquiryRepository
                .findById(id).map(inquiryMapper::inquiryToInquiryDto)
                .orElseThrow(() -> new NoSuchDtoException("There is no inquiry with id " + id));
    }

    @Override
    public void deleteById(long id) {
        inquiryRepository.deleteById(id);
    }
}
