package br.com.franca.helpdesk.util;

import br.com.franca.helpdesk.domains.Pessoa;
import br.com.franca.helpdesk.repositorys.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private final PessoaRepository pessoaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordMigrationRunner(PessoaRepository pessoaRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.pessoaRepository = pessoaRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        Iterable<Pessoa> users = pessoaRepository.findAll();
        for (Pessoa user : users) {
            if (!user.getSenha().startsWith("$2a$")) {
                System.out.println("Senha" +user.getSenha());
                user.setSenha(bCryptPasswordEncoder.encode(user.getSenha()));
                System.out.println("Senha criptografada" + user.getSenha());
                pessoaRepository.save(user);
            }
        }
    }
}