package com.service.implementation;

import com.dto.ClientVendorDTO;
import com.dto.CompanyDTO;
import com.entity.ClientVendor;
import com.enums.ClientVendorType;
import com.exception.ClientVendorNotFoundException;
import com.mapper.MapperUtil;
import com.repository.ClientVendorRepository;
import com.service.ClientVendorService;
import com.service.InvoiceService;
import com.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapper;
    private final SecurityService securityService;
    private final InvoiceService invoiceService;


    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapper, SecurityService securityService, InvoiceService invoiceService) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapper = mapper;
        this.securityService = securityService;
        this.invoiceService = invoiceService;
    }

    @Override
    public ClientVendorDTO findById(Long id) {

        return mapper.convert(clientVendorRepository.findById(id).orElseThrow(() -> new ClientVendorNotFoundException("there is no client with this id: " + id)), ClientVendorDTO.class);
    }

    @Override
    public List<ClientVendorDTO> listAllClientVendorsForLoggedInCompany() {

        return dtoMapper(clientVendorRepository.findByCompany_IdOrderByClientVendorTypeAsc(getLoggedCompany().getId()));
    }

    @Override
    public List<ClientVendorDTO> listAllVendorsForLoggedInCompany() {

        return dtoMapper(clientVendorRepository.findByCompany_IdAndClientVendorTypeOrderByClientVendorNameAsc(getLoggedCompany().getId(), ClientVendorType.VENDOR.name()));
    }

    @Override
    public List<ClientVendorDTO> listAllClientsForLoggedInCompany() {

        return dtoMapper(clientVendorRepository.findByCompany_IdAndClientVendorTypeOrderByClientVendorNameAsc(getLoggedCompany().getId(), ClientVendorType.CLIENT.name()));
    }

    @Override
    public void save(ClientVendorDTO clientVendorDTO) {

        clientVendorDTO.setCompany(getLoggedCompany());

        ClientVendor clientVendor = mapper.convert(clientVendorDTO, ClientVendor.class);

        clientVendorRepository.save(clientVendor);
    }

    @Override
    public void update(ClientVendorDTO clientVendorDTO) {

        clientVendorRepository.findById(clientVendorDTO.getId()).orElseThrow();

        clientVendorDTO.setClientVendorName(clientVendorDTO.getClientVendorName().trim());

        clientVendorRepository.save(mapper.convert(clientVendorDTO, ClientVendor.class));
    }

    @Override
    public void deleteById(Long id) {

        ClientVendor clientVendor = clientVendorRepository.findById(id).orElseThrow();

        clientVendor.setIsDeleted(true);

        clientVendorRepository.save(clientVendor);
    }

    @Override
    public boolean isThereAnyRelatedInvoice(Long id) {
        return invoiceService.existsByClientVendorId(id);
    }


    @Override
    public boolean isNameAlreadyInUse(String name) {

        return clientVendorRepository.existsByCompany_IdAndClientVendorNameIgnoreCase(getLoggedCompany().getId(), name.trim());
    }

    @Override
    public boolean isNameNotPrevious(Long id, String name) {

        return !clientVendorRepository.findById(id).orElseThrow().getClientVendorName().equals(name.trim()) && isNameAlreadyInUse(name);
    }

    private CompanyDTO getLoggedCompany() {
        return securityService.getLoggedInUser().getCompany();
    }

    private List<ClientVendorDTO> dtoMapper(List<ClientVendor> clientVendors) {

        return clientVendors.stream()
                .map(clientVendor -> mapper.convert(clientVendor, ClientVendorDTO.class))
                .collect(Collectors.toList());
    }
}


