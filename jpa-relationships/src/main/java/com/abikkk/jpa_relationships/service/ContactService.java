package com.abikkk.jpa_relationships.service;

import com.abikkk.jpa_relationships.model.Contact;
import com.abikkk.jpa_relationships.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    // Read operations
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    // Update operation
    public Contact updateContact(Long id, Contact contactDetails) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setType(contactDetails.getType());
        contact.setValue(contactDetails.getValue());

        return contactRepository.save(contact);
    }
}
