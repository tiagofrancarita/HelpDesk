package br.com.franca.helpdesk.controller;


import br.com.franca.helpdesk.usecases.RelatorioUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/v1/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioUseCase relatorioUseCase;

    @GetMapping("/gerarRelatorioClientes")
    public ResponseEntity<byte[]> gerarRelatorioClientes() {
        try {

            // Formatar a data
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String formattedDate = now.format(formatter);

            // Nome do arquivo com a data formatada
            String filename = "rel-clientes-" + formattedDate + ".xlsx";


            ByteArrayInputStream in = relatorioUseCase.gerarRelatorioClientes();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(in.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/gerarRelatorioTecnicos")
    public ResponseEntity<byte[]> gerarRelatorioTecnicos() {
        try {

            // Formatar a data
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String formattedDate = now.format(formatter);

            // Nome do arquivo com a data formatada
            String filename = "rel-tecnicos-" + formattedDate + ".xlsx";

            ByteArrayInputStream in = relatorioUseCase.gerarRelatorioTecnicos();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(in.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}