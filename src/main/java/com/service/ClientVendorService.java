package com.service;

import com.dto.ClientVendorDTO;

import java.util.List;

public interface ClientVendorService {

    ClientVendorDTO findById(Long id);

    List<ClientVendorDTO> listAllClientVendorsForLoggedInCompany();

    List<ClientVendorDTO> listAllVendorsForLoggedInCompany();

    List<ClientVendorDTO> listAllClientsForLoggedInCompany();

    void save(ClientVendorDTO clientVendorDTO);

    void update(ClientVendorDTO clientVendorDTO);

    void deleteById(Long id);

    boolean isThereAnyRelatedInvoice(Long id);

    boolean isNameAlreadyInUse(String name);

    boolean isNameNotPrevious(Long id, String name);
}
