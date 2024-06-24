package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.security.UserSS;
import br.com.franca.helpdesk.domains.Pessoa;
import br.com.franca.helpdesk.repositorys.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsUseCasesImpl implements UserDetailsService {

    private final PessoaRepository pessoaRepository;


    @Autowired
    public UserDetailsUseCasesImpl(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;

    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Pessoa> usuario = pessoaRepository.findByEmail(email);
        if (usuario.isPresent()) {
            return new UserSS(usuario.get().getId(), usuario.get().getEmail(), usuario.get().getSenha(), usuario.get().getPerfis());
        }


        throw new UsernameNotFoundException(email);
    }
}