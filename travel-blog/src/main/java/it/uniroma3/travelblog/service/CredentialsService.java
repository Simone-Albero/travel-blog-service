package it.uniroma3.travelblog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.travelblog.model.Credentials;
import it.uniroma3.travelblog.repository.CredentialsRepository;


@Service
public class CredentialsService {
	
	@Autowired
    protected PasswordEncoder passwordEncoder;

	@Autowired
	protected CredentialsRepository credentialsRepository;
	
	@Transactional
	public Credentials findById(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credentials findByUsername(String username) {
		Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
		return result.orElse(null);
	}
		
    @Transactional
    public Credentials save(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }
    
    @Transactional
    public Credentials update(Credentials credentials) {
        return this.credentialsRepository.save(credentials);
    }

	public void promote(String username) {
		Credentials credentials = this.credentialsRepository.findByUsername(username).get();
		credentials.setRole(Credentials.ADMIN_ROLE);
		this.credentialsRepository.save(credentials);
	}

	public void deleteById(Long id) {
		this.credentialsRepository.deleteById(id);
	}
	
}
